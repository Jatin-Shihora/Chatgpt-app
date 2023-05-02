package com.jatin.chatgpt.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jatin.chatgpt.model.*
import com.jatin.chatgpt.model.enums.Role
import com.jatin.chatgpt.repository.GptRepository
import com.jatin.template.common.utils.L
import kotlinx.coroutines.launch


/**
 * Main ViewModel
 *
 * @author Jatin
 * @time 30/04/2023
 */
class MainPageViewModel(private val _gptRepository: GptRepository) : ViewModel() {

    //current session
    private val _currentSession = MutableLiveData<Session>()
    val currentSession: LiveData<Session> = _currentSession

    //conversation list
    private val _sessionList = MutableLiveData<List<Session>>()
    val sessionList: LiveData<List<Session>> = _sessionList

    //message list
    private val _messageList = MutableLiveData<List<Message>>()
    var messageList: LiveData<List<Message>> = _messageList

    //template list
    private val _templateList = MutableLiveData<List<Template>>()
    var templateList: LiveData<List<Template>> = _templateList

    init {
        //Get the last session session
        queryLeastSession()
        //get all templates
        queryAllTemplate()
    }

    //***Message***//

    private fun updateMessageList(sessionId: Int, onUpdate: (MutableList<Message>) -> Unit) {
        //Determine whether the session id of the current message is the same as the id of _currentSession
        if (getCurrentSessionId() == sessionId) {
            _messageList.value = _messageList.value?.toMutableList()?.apply {
                onUpdate.invoke(this)
            }
        } else {
            //If it is not the same, it will not be processed
        }

    }

    private fun insertMessage(message: Message) {
        viewModelScope.launch {
            //Also update the session log
            if (message.role == Role.USER.roleName) {
                updateSessionTitle(message.sessionId, message.content)
            }
            //Last time of update session
            updateSessionTime(message.sessionId)
            _gptRepository.insertMessage(message).onSuccess {
                val sessionId = message.sessionId
                if (message.role == Role.USER.roleName) {
                    updateMessageList(sessionId) {
                        it.add(message)
                    }
                    //add an empty response
                    updateMessageList(sessionId) {
                        it.add(Message(content = "", role = Role.ASSISTANT.roleName))
                    }
                } else {
                    updateMessageList(sessionId) {
                        if (it.size > 1) {
                            //delete the last
                            it.removeAt(it.size - 1)
                        }
                    }
                    updateMessageList(sessionId) {
                        it.add(message)
                    }
                }
                L.d("insert message $message ${getCurrentSessionId()}")
            }.onFailure { }
        }
    }

    fun deleteMessage(message: Message) {
        viewModelScope.launch {
            _gptRepository.deleteMessage(message).onSuccess {
                _messageList.value = _messageList.value?.toMutableList()?.apply {
                    this.remove(message)
                }
            }
        }
    }

    //***Session***//


    // open a new session
    fun startNewSession() {
        setSessionDetail(null)
    }

    fun switchSession(session: Session?) {
        setSessionDetail(session)
    }


    //query all sessions
    private fun queryAllSession() {
        viewModelScope.launch {
            //Query the latest session
            _gptRepository.queryAllSession().onSuccess {
                _sessionList.value = it
            }
        }
    }

    private fun setSessionDetail(session: Session?) {
//        messageTask?.cancel()
        viewModelScope.launch {
            if (session == null) {
                //It means that there is no session record yet, create a new one
                val newSession = Session(0, "", System.currentTimeMillis())
                _gptRepository.createSession(newSession).onSuccess {
                    //Overwrite the generated id on the entity
                    _currentSession.value = newSession.copy(id = it.toInt())
                    _messageList.value = listOf()
                }
            } else {
                //If you have it, just use it directly
                _currentSession.value = session!!
                _gptRepository.queryMessageBySID(session.id).onSuccess { messages ->
                    _messageList.value = messages
                }
            }
            queryAllSession()
        }
    }


    private fun getCurrentSessionId(): Int {
        return _currentSession.value!!.id
    }

    //Update session operation time
    private suspend fun updateSessionTime(sessionId: Int) {
        viewModelScope.launch {
            val time = System.currentTimeMillis()
            _gptRepository.updateSessionTime(id = sessionId, time).onSuccess {
                _currentSession.value = _currentSession.value!!.copy(lastSessionTime = time)
            }
        }
    }

    //update session title
    private suspend fun updateSessionTitle(sessionId: Int, title: String) {
        viewModelScope.launch {
            _gptRepository.updateSessionTitle(id = sessionId, title).onSuccess {
                _currentSession.value = _currentSession.value!!.copy(title = title)
                queryAllSession()
            }
        }
    }

    private fun queryLeastSession() {
        viewModelScope.launch {
            //Query the latest session
            _gptRepository.queryLeastSession().onSuccess {
                L.d("success")
                setSessionDetail(it)
            }.onFailure {
                L.d("fail")
            }
        }
    }


    fun deleteCurrentSession() {
        viewModelScope.launch {
            _gptRepository.clear(_currentSession.value!!).onSuccess {
                queryLeastSession()
            }.onFailure {
            }
        }
    }

    //***Template***//

    //Send a message
    fun sendMessage(content: String) {
//        messageTask?.cancel()
        viewModelScope.launch {
            val sessionId = getCurrentSessionId()
            val message = Message(
                sessionId = sessionId,
                content = content,
                role = Role.USER.roleName
            )
            _gptRepository.fetchMessage(mutableListOf<MessageDTO>().apply {
                //Bring the previous chat records without system prompts
                _messageList.value?.filter { it.role != Role.SYSTEM.roleName }?.forEach {
                    add(it.toDTO())
                }
                //Put what you write in the database
                insertMessage(message = message)
                add(message.toDTO())
            }).onSuccess { it ->
                repDataProcess(it, sessionId)
            }.onFailure {
                //Error message
                insertMessage(
                    Message(
                        sessionId = sessionId,
                        content = it.toString(),
                        role = Role.SYSTEM.roleName
                    )
                )
            }
        }
    }

    private fun repDataProcess(gtpResponse: GptResponse, sessionId: Int) {
        //mistake
        if (gtpResponse.error != null) {
            insertMessage(
                Message(
                    content = gtpResponse.error.message,
                    role = Role.SYSTEM.roleName,
                    sessionId = sessionId,
                )
            )
        } else {
            gtpResponse.choices.forEach {
                //After getting the data, insert it into the database
                insertMessage(
                    Message(
                        content = filterDrayMessage(it.message.content), role = it.message.role,
                        sessionId = sessionId,
                    )
                )
            }
        }

    }

    fun saveTemplate(name: String, message: List<Message>) {
        val list = message.filter { it.role != Role.SYSTEM.roleName }
        val listJson = Gson().toJson(list)
        L.d("listJson $listJson")
        viewModelScope.launch {
            _gptRepository.saveTemplate(name, listJson).onSuccess {
                queryAllTemplate()
            }.onFailure {

            }
        }
    }

    fun deleteTemplate(id: Int) {
        viewModelScope.launch {
            _gptRepository.deleteTemplate(id).onSuccess {
                //may need to update the list
                queryAllTemplate()
            }.onFailure {

            }
        }
    }

    fun updateTemplateName(name: String, id: Int) {
        viewModelScope.launch {
            _gptRepository.updateTemplateName(name, id).onSuccess {
                queryAllTemplate()
            }.onFailure {

            }
        }
    }

    fun loadTemplate(template: Template) {
        val list = Gson().fromJson<List<Message>>(
            template.tempContent,
            object : TypeToken<List<Message>>() {}.type
        )
        viewModelScope.launch {
            val newSession = Session(0, list[0].content, System.currentTimeMillis())
            _gptRepository.createSession(newSession).onSuccess { id ->
                //Overwrite the generated id on the entity
                _currentSession.value = newSession.copy(id = id.toInt())
                //replace id
                val newMessageList = mutableListOf<Message>().apply {
                    list.forEach {
                        add(it.copy(sessionId = id.toInt()))
                    }
                }
                //insert message
                _gptRepository.insertMessages(newMessageList).onSuccess {
                    _messageList.value = list
                    //The session list needs to be updated
                    queryLeastSession()
                }
            }
        }
    }


    fun queryAllTemplate() {
        viewModelScope.launch {
            _gptRepository.queryAllTemplate().onSuccess {
                //may need to update the list
                _templateList.value = it
                L.d("queryAllTemplate $it")
            }.onFailure {

            }
        }
    }


    //Filter data starting with \n
    private fun filterDrayMessage(message: String): String {
        var newMessage = message
        val nextLineSymbol = "\n"
        while (newMessage.startsWith(nextLineSymbol)) {
            val index = newMessage.indexOf("\n")
            newMessage = newMessage.substring(index + nextLineSymbol.length, newMessage.length)
        }
        return newMessage
    }


}
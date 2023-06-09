package com.jatin.chatgpt.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.jatin.chatgpt.R
import com.jatin.chatgpt.model.Session
import com.jatin.chatgpt.model.enums.Role
import com.jatin.chatgpt.viewmodel.MainPageViewModel
import com.jatin.chatgpt.widget.TextCursorBlinking
import kotlinx.coroutines.delay

/**
 * home page
 *
 * @author Jatin
 * @time 30/04/2023
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainPage(viewModel: MainPageViewModel) {
    val messageList by viewModel.messageList.observeAsState(emptyList())
    val sessionList by viewModel.sessionList.observeAsState(emptyList())
    val templateList by viewModel.templateList.observeAsState(emptyList())
    val currentSession by viewModel.currentSession.observeAsState(
        Session(
            title = "",
            lastSessionTime = System.currentTimeMillis()
        )
    )
    var isOpenPop by remember { mutableStateOf(false) }

    var text by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (topR, listR, bottomR) = createRefs()

            //top bar
            SmallTopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val scrollState = rememberScrollState(0)
                        Box(modifier = Modifier.horizontalScroll(scrollState)) {
                            val title =
                                currentSession.title.ifEmpty { stringResource(id = R.string.app_title) }
                            Text(
                                title,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(16) // 每隔16毫秒滚动一次
                                if (scrollState.value == scrollState.maxValue) {
                                    delay(1000)
                                    scrollState.scrollTo(0)
                                    delay(1000)
                                } else {
                                    scrollState.scrollBy(1f)

                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { isVisible = !isVisible }) {
                        Icon(painter = painterResource(id = R.drawable.ic_more), "More")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isOpenPop = !isOpenPop
                    }) {
                        Icon(Icons.Filled.PlayArrow, "Info")
                    }
                },
                modifier = Modifier.constrainAs(topR) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            //the list
            Box(modifier = Modifier
                .constrainAs(listR) {
                    top.linkTo(topR.bottom)
                    bottom.linkTo(bottomR.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                }
                .paint(
                    painterResource(id = R.drawable.mainbg),
                    contentScale = ContentScale.FillBounds)
            ) {
                val scrollState = rememberLazyListState()
                LazyColumn(state = scrollState) {
                    items(messageList.size) { position ->
                        Spacer(modifier = Modifier.height(10.dp))
                        val message = messageList[position]
                        if (message.role == Role.ASSISTANT.roleName) {
                            LeftView(message.content)
                        } else if (message.role == Role.USER.roleName) {
                            RightView(message.content)
                        } else if (message.role == Role.SYSTEM.roleName) {
                            TipsView(message.content)
                        }
                        if (position == messageList.size - 1) {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
                if (messageList.isNotEmpty()) {
                    LaunchedEffect(key1 = messageList) {
                        // Automatically scroll to the bottom when the list is updated
                        scrollState.animateScrollToItem(messageList.size - 1)
                    }
                }
            }
            val keyboardController = LocalSoftwareKeyboardController.current

            //bottom BAR
            ConstraintLayout(modifier = Modifier.constrainAs(bottomR) {
                bottom.linkTo(parent.bottom, margin = 0.dp)
                start.linkTo(parent.start, margin = 0.dp)
                width = Dimension.fillToConstraints
                end.linkTo(parent.end)
            }) {
                val (textR, sendR) = createRefs()
                TextField(value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .constrainAs(textR) {
                            start.linkTo(parent.start)
                            end.linkTo(sendR.start)
                            width = Dimension.fillToConstraints
                        }
                        .padding(start = 5.dp, end = 5.dp),
                    maxLines = 3)
                Button(
                    onClick = {
                    keyboardController?.hide()
                    viewModel.sendMessage(text)
                    text = ""
                },
                    colors = ButtonDefaults.buttonColors(contentColor = Color(0xFFFFFFFF)),
                    modifier = Modifier
                    .constrainAs(sendR) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .padding(end = 5.dp)
                    .width(90.dp)) {
                    Text(text = stringResource(id = R.string.btn_send))

                }
            }
        }

        // sidebar content
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
        ) {
            MainSideBar(sessionList, currentSession, {
                viewModel.startNewSession()
                isVisible = !isVisible
            }, { isVisible = !isVisible }, { session ->
                viewModel.switchSession(session)
            }, {
                viewModel.deleteCurrentSession()
            })
        }


        //popwindow
        if (isOpenPop) {
            MainPop(
                templateList,
                messageList.size,
                onCloseCallback = { isOpenPop = false },
                onSaveTemplate = {
                    viewModel.saveTemplate(it, messageList)
                },
                onTemplateDelete = {
                    viewModel.deleteTemplate(it.id)
                }, onTemplateLoad = {
                    viewModel.loadTemplate(it)
                })
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeftView(content: String) {
    Row(modifier = Modifier.padding(start = 10.dp, end = 60.dp)) {
        Image(
            painter = painterResource(id = R.drawable.ic_gpt),
            contentDescription = "",
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        SelectionContainer() {
            Card(modifier = Modifier.padding(start = 7.dp)) {
                if (content.isEmpty()) {
                    TextCursorBlinking(
                        text = content,
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth()
                    )
                } else {
                    Text(
                        text = content, color = Color.White, modifier = Modifier.padding(15.dp)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RightView(content: String) {
    ConstraintLayout(
        modifier = Modifier
            .padding(end = 10.dp)
            .fillMaxWidth()
    ) {
        val (head, text) = createRefs()
        SelectionContainer(modifier = Modifier.constrainAs(text) {
            top.linkTo(head.top)
            end.linkTo(head.start)
        }) {
            Card(modifier = Modifier.padding(start = 108.dp, end = 7.dp)) {
                Text(
                    text = content, color = Color.White, modifier = Modifier.padding(15.dp)
                )
            }
        }
        Image(
            painter = painterResource(id = R.drawable.ic_me),
            contentDescription = "",
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape)
                .constrainAs(head) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                },
            contentScale = ContentScale.Crop
        )
    }
    Row(
        modifier = Modifier
            .padding(end = 10.dp, start = 60.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {


    }
}

@Composable
fun TipsView(content: String) {
    val tips = stringResource(id = R.string.error_tips)
    Box(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp), contentAlignment = Alignment.Center
    ) {
        SelectionContainer() {
            Text(
                text = "$tips $content",
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                color = Color.Red,
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RightView("55AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA5")
}
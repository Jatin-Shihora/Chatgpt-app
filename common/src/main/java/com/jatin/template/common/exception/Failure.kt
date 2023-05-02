package com.jatin.template.common.exception

/**
 * custom error
 */
sealed class Failure : Throwable() {

    /**
     *
     * Mainly set the initial state for State
     */
    object None : Failure()

    /**
     * Network Error
     */
    object NetworkError : Failure() {
        override val message = "Network UnAvailable"
    }

    /**
     * Business error returned by the server
     */
    data class ServerError(val code: Int = 0, val msg: String? = "") : Failure()

    /**
     * For example, errors in data processing, http protocol errors
     */
    data class OtherError(val throwable: Throwable? = null) : Failure()

    /**
     * The data field returned by the interface is empty
     */
    object EmptyData : Failure()

}

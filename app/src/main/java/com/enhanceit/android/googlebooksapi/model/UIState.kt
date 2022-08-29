package com.enhanceit.android.googlebooksapi.model

import com.enhanceit.android.googlebooksapi.model.remote.BookResponse

sealed interface UIState
data class Response(val data: BookResponse): UIState

data class Loading(val isLoading: Boolean = true): UIState

data class Failure(val reason: String): UIState

object Empty: UIState

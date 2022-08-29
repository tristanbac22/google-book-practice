package com.enhanceit.android.googlebooksapi.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enhanceit.android.googlebooksapi.model.Empty
import com.enhanceit.android.googlebooksapi.model.Repository
import com.enhanceit.android.googlebooksapi.model.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(private val repository: Repository):ViewModel() {

    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(Empty)
    val uiState: StateFlow<UIState> get() =_uiState

    fun searchBook(query: String,
                   bookQuerySize: String,
                   printType: String){
        viewModelScope.launch {
            repository.getBookByName(query,
                bookQuerySize,
                printType).collect{
                    _uiState.value = it
            }


        }
    }
}
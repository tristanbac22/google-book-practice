package com.enhanceit.android.googlebooksapi.model

import com.enhanceit.android.googlebooksapi.model.remote.BookAPI
import com.enhanceit.android.googlebooksapi.model.remote.BookService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RepositoryImpl constructor (private val bookService: BookAPI) : Repository{
    override fun getBookByName(bookTitle: String, bookSize: String, printType: String): Flow<UIState> {
        return flow{
            //todo verify if user is authenticated...
            //todo verify if device is ONLINE
            //request data, using bookService.getBookTitle(booktitle)
            //emit Loading
            // evaluate if data is success emit Success
            //else emit Error
            emit(Loading())
            delay(600)
            val response = bookService.api.getBook(bookTitle, bookSize, printType)

            if(response.isSuccessful){
                response.body()?.let {emit(Response(it))} ?: emit(Empty)
            } else{
                emit(Failure("Error Message."))
            }
        }
    }
}
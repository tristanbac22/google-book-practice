package com.enhanceit.android.googlebooksapi.model.remote

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class BookResponse(
    val items: List<BookItem>
)

@Parcelize
data class BookItem(
    val volumeInfo: BookVolumeInfo

):Parcelable

@Parcelize
data class BookVolumeInfo(
    val title: String,
    val authors: List<String>?,
    val imageLinks: BookImageLinks?,
    val description: String?,
    val publishedDate: String?

):Parcelable

@Parcelize
data class BookImageLinks(
    val smallThumbnail: String,
    val thumbnail: String
):Parcelable

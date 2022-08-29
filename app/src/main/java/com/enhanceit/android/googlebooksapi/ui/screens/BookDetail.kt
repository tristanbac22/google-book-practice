package com.enhanceit.android.googlebooksapi.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.enhanceit.android.googlebooksapi.R
import com.enhanceit.android.googlebooksapi.model.remote.BookItem

@ExperimentalUnitApi
@Composable
fun BookDetail(bookItem: BookItem) {
    //todo implement remember.

    Column(modifier = Modifier.fillMaxWidth()) {
        BookDetailHeader(bookItem)
        Spacer(modifier = Modifier.fillMaxWidth(0.5f))
        BookDetailBody(bookItem)
    }
}

@Composable
fun BookDetailBody(bookItem: BookItem) {
    Card(shape = RoundedCornerShape(corner = CornerSize(8.dp)),
    border = BorderStroke(3.dp, Color.DarkGray),
        elevation = 0.dp
    ) {
        Text(text = bookItem.volumeInfo.authors.toString(),
        modifier = Modifier
            .border(BorderStroke(3.dp, Color.DarkGray))
            .padding(5.dp))
        Text(text = bookItem.volumeInfo.description.toString(),
            modifier = Modifier
                .border(BorderStroke(3.dp, Color.DarkGray))
                .padding(5.dp))
    }
}

@ExperimentalUnitApi
@Composable
fun BookDetailHeader(bookItem: BookItem) {
    Card(
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(3.dp, Color.DarkGray),
        elevation = 0.dp
    ) {Row{
        Column{

            Text(
                text = bookItem.volumeInfo.title,
                fontSize = TextUnit(value = 20f, type = TextUnitType.Sp),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(5.dp)
                )
            Text(
                text = bookItem.volumeInfo.publishedDate.toString(),
                modifier = Modifier.padding(5.dp),
                fontWeight = FontWeight.Light
                )
            }

        AsyncImage(model = ImageRequest.Builder(LocalContext.current)
            .data(bookItem.volumeInfo.imageLinks?.smallThumbnail)
            .crossfade(true)
            .build(),
            contentDescription = stringResource(id = R.string.ic_book_content_cover_description),
            contentScale = ContentScale.Inside, modifier = Modifier.clip(RectangleShape),
            placeholder = painterResource(id = R.drawable.ic_baseline_library_books_24)
            )
        }
    }
}

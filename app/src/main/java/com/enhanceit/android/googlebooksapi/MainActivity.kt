package com.enhanceit.android.googlebooksapi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.enhanceit.android.googlebooksapi.ui.theme.GoogleBooksApiTheme
import com.enhanceit.android.googlebooksapi.viewmodel.BookViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp {
                val viewModel: BookViewModel by viewModels()
                SearchScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainApp(content: @Composable () -> Unit){
    GoogleBooksApiTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            content()
        }
    }
}

@Composable
fun SearchScreen(bookViewModel: BookViewModel) {
    var bookQuery by remember{
        mutableStateOf("")
    }
    var bookQuerySize by remember{
        mutableStateOf("")
    }
    var selectedPrintType by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(50.dp),
    shape = RoundedCornerShape(20.dp),
    elevation = 14.dp) {
        Column {
            TextField(value = bookQuery,
                onValueChange ={
                    bookQuery = it
                               },
                    label = {Text(text = "Search by book title")}
                )
            TextField(value = bookQuerySize,
                onValueChange = {bookQuerySize = it
                },
                label = {Text(text = "Number of results to return")}
            )

            BookPrintType(){printType ->
                selectedPrintType = printType
            }

            Button(onClick = {
                Toast.makeText(
                    context,
                    "Searching",
                    Toast.LENGTH_SHORT
                ).show()
                bookViewModel.searchBook(bookQuery, bookQuerySize, selectedPrintType)
            }) {
                Text(text = "Search")
            }
        }

    }
}

@Composable
fun BookPrintType(selected: (String) -> Unit ) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var selection by remember {
        mutableStateOf("")
    }
    val dropDownIcon = if (isExpanded) Icons.Filled.KeyboardArrowDown
    else Icons.Filled.KeyboardArrowUp
    Column(modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Icon(dropDownIcon, contentDescription = "A dropdown Icon",
                modifier = Modifier.clickable { isExpanded = !isExpanded }
            )
            Text(
                text = selection
            )
        }

        DropdownMenu(expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
            }) {
            val context = LocalContext.current
            context.resources.getStringArray(R.array.book_print_type).forEach { printType ->
                DropdownMenuItem(onClick = {
                    isExpanded = false
                    selected(printType)
                    selection = printType
                }) {
                    Text(text = printType)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GoogleBooksApiTheme {
        Greeting("Android")
    }
}

@Preview(showBackground = false)
@Composable
fun SearchScreenPreview() {
    val bookViewModel: BookViewModel = BookViewModel()
    MainApp {
        SearchScreen(bookViewModel)
    }
}
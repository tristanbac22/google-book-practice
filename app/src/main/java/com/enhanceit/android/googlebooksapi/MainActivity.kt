package com.enhanceit.android.googlebooksapi

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.enhanceit.android.googlebooksapi.model.*
import com.enhanceit.android.googlebooksapi.model.remote.BookItem
import com.enhanceit.android.googlebooksapi.model.remote.BookResponse
import com.enhanceit.android.googlebooksapi.ui.screens.BookDetail
import com.enhanceit.android.googlebooksapi.ui.theme.GoogleBooksApiTheme
import com.enhanceit.android.googlebooksapi.util.ARG_BOOK
import com.enhanceit.android.googlebooksapi.util.DETAILS_SCREEN
import com.enhanceit.android.googlebooksapi.util.SEARCH_SCREEN
import com.enhanceit.android.googlebooksapi.viewmodel.BookViewModel
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalUnitApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp {
                val viewModel: BookViewModel by viewModels()
                val navController = rememberNavController()
                NavHost( navController =  navController,
                    startDestination = SEARCH_SCREEN){
                    composable(
                        route = SEARCH_SCREEN
                    ){
                        SearchScreenStateful(viewModel = viewModel, navigation = navController){
                            logOutCurrentUser()
                        }
                    }
                    composable(route = DETAILS_SCREEN,
                        arguments = listOf(
                            navArgument(ARG_BOOK){
                                    type = NavType.ParcelableType<BookItem>(BookItem::class.java)
                            }
                        )
                    ){ navBackStackEntry ->
                        BookDetail(bookItem = navBackStackEntry.arguments?.getParcelable<BookItem>(
                            ARG_BOOK) ?: throw Exception("Data is not ready"))

                    }
                }
                //It will be main container in your activity
                //used to render ToolBar/BottomAppBar

            }
        }

        FirebaseMessaging
            .getInstance()
            .token
            .addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = "FBM token: $token"
            Log.d(TAG, msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
    }
    private fun logOutCurrentUser(){

        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                Log.d(TAG, "logoutCurrentUser: ${it.result}")
            }
    }
}

@Composable
fun SearchScreenStateful(viewModel: BookViewModel,
    navigation: NavController, logOutCurrentUser: () -> Unit) {
    Scaffold(topBar = {MainTopBar{
        logOutCurrentUser()
    }
    } ) {
        it.toString()
        //val viewModel = BookViewModel(RepositoryImpl(BookAPI().api))

        /** object: ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
        super.create(modelClass)
        return BookViewModel( RepositoryImpl( BookAPI().api ) ) as T
        }
        }**/

        val uiState = viewModel.uiState.collectAsState().value
        SearchScreen(viewModel)

        when (uiState) {
            is Response -> {
                BookResponseScreen(uiState.data){bookItem ->  
                    navigation.navigate("$DETAILS_SCREEN$bookItem")
                }

            }
            is Failure -> {
                ErrorScreen(uiState.reason)
            }
            is Loading -> {
                LoadingScreen(uiState.isLoading)
            }
            is Empty -> {}
        }
    }
}

@Composable
fun MainTopBar(logOutCurrentUser: () -> Unit) {
    var menuLogOutIsExpanded: Boolean by remember {
        mutableStateOf(false)
    }
    Log.d(TAG, "MainTopBar: Photo URL ${FirebaseAuth.getInstance().currentUser?.photoUrl}")
    Log.d(TAG, "MainTopBar: Username ${FirebaseAuth.getInstance().currentUser?.displayName}")
    var context = LocalContext.current
    TopAppBar(
        title = {
            LocalContext.current.getString(R.string.app_name)
        },
        actions = {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = LocalContext.current.getString(R.string.menu),
                modifier = Modifier.clickable { menuLogOutIsExpanded = true }
            )
            DropdownMenu(
                expanded = menuLogOutIsExpanded,
                onDismissRequest = { menuLogOutIsExpanded = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        menuLogOutIsExpanded = false
                        logOutCurrentUser()
                    }
                ) {
                    Text(text = LocalContext
                        .current
                        .getString(R.string.log_out))
                }
            }
        }
    )
}




@Composable
fun LoadingScreen(loading: Boolean) {
    Log.d(TAG, "LoadingScreen: Current State")
}
@Composable
fun ErrorScreen(reason: String) {
    Log.d(TAG, "ErrorScreen: Current State")

}
@Composable
fun BookResponseScreen(data: BookResponse, openDetails:(BookItem) -> Unit) {
    LazyColumn{
        items(data.items.size){position ->
            BookItemStateless(data.items[position], openDetails)
        }
    }
}


@Composable
fun BookItemStateless(book: BookItem, openDetails:(BookItem) -> Unit){
    Box(modifier = Modifier
        .padding(
            start = 10.dp,
            end = 5.dp,
            top = 2.dp,
            bottom = 2.dp
        )
        .fillMaxWidth()) {
        Row() {
            AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                .data(book.volumeInfo.imageLinks?.smallThumbnail)
                .crossfade(true)
                .build(),
                contentDescription = stringResource(id = R.string.ic_book_content_cover_description),
            contentScale = ContentScale.Inside, modifier = Modifier.clip(RectangleShape),
                placeholder = painterResource(id = R.drawable.ic_baseline_library_books_24))
            Column {
                Text(text = book.volumeInfo.title,
                modifier = Modifier.clickable { openDetails(book) })
                Row{
                    Text(text = book.volumeInfo.authors.toString())
                    book.volumeInfo.publishedDate?.let { Text(text = it) }
                }
                book.volumeInfo.description?.let { Text(text = it) }
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
//    val bookViewModel: BookViewModel = BookViewModel()
  //  MainApp {
    //    SearchScreen(bookViewModel)
    //}
}
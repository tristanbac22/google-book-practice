package com.enhanceit.android.googlebooksapi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.enhanceit.android.googlebooksapi.ui.theme.GoogleBooksApiTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val TAG = "RegisterPage"

@ExperimentalUnitApi
class RegisterPage: ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
      onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            //navigate to Search Screen
            // ...

            Log.d(TAG, "onSignInResult: $user")
            navigateSearchScreen()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            response?.let { resp ->
                Log.e(TAG, "onSignInResult: ${resp.error?.errorCode}",)
            } ?: kotlin.run { Log.e(TAG, "onSignInResult: User Cancled",) }
        }
    }

    private fun navigateSearchScreen() {
        val navigate = Intent()
        navigate.setClass(this,MainActivity::class.java)
        startActivity(navigate)
    }

    private fun createFirebaseUILogon() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth

        setContent {
            RegisterApp {

            }
        }
        //createlogin providers
        //trigger the implicit intent for loginUI
        //register activity for result
        //handle 2 scenarios()
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if(currentUser == null){
            createFirebaseUILogon()
        }
        else{
            navigateSearchScreen()
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            createFirebaseUILogon()
        }
    }

    override fun onStop() {
        super.onStop()
    }
}

    @Composable
    fun RegisterApp(content: @Composable () -> Unit) {
        GoogleBooksApiTheme {
            //surface container using the background colour from thee theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                content()
            }

        }

    }




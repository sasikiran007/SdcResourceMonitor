package com.example.sdcresourcemonitor.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class MainActivityLoginViewModel: ViewModel() {
    enum class AuthenticationState{
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    var authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            LoginViewModel.AuthenticationState.AUTHENTICATED
        } else {
            LoginViewModel.AuthenticationState.UNAUTHENTICATED
        }
    }

    val currentUser = FirebaseUserLiveData().map {user ->
        Log.i("LoginViewModel","Currentuser mapped")
        user
    }

}
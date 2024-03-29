package com.example.sdcresourcemonitor.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class LoginViewModel : ViewModel() {
//    var authenticationState = MutableLiveData<AuthenticationState>()
    enum class AuthenticationState{
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    var authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

    val currentUser = FirebaseUserLiveData().map {user ->
        Log.i("LoginViewModel","Currentuser mapped")
        user
    }


}
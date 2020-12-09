package com.example.sdcresourcemonitor.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.signature.AndroidResourceSignature
import com.example.sdcresourcemonitor.model.local.AlertDatabase
import com.example.sdcresourcemonitor.model.local.User
import com.example.sdcresourcemonitor.model.network.UserService
import com.example.sdcresourcemonitor.model.network.model.UserLogin
import com.example.sdcresourcemonitor.model.network.model.UserSignup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response

class UserSignupViewModel(application: Application) : BaseViewModel(application) {

    val userSignupResponse = MutableLiveData<Response<ResponseBody>>()
    val isLoading = MutableLiveData<Boolean>()
    val hasError = MutableLiveData<Boolean>()

    private val userApi = UserService()
    private val database = AlertDatabase.invoke(application)
    private val disposable = CompositeDisposable()

    private val TAG = "UserLogin"
    fun signup(userSignup: UserSignup){
        Log.i(TAG,"Signup function is called")
        val d1 = userApi.signup(userSignup)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Response<ResponseBody>>(){
                override fun onSuccess(t: Response<ResponseBody>) {

                    userSignupResponse.value = t
                }

                override fun onError(e: Throwable) {
                    hasError.value = true
                    Log.i("UserLogIn",e.toString())
                }

            })
        disposable.add(d1)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }




}
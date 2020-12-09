package com.example.sdcresourcemonitor.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.sdcresourcemonitor.model.local.AlertDatabase
import com.example.sdcresourcemonitor.model.network.UserService
import com.example.sdcresourcemonitor.model.network.model.UserLogin
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response

class UserLoginViewModel(application: Application) : BaseViewModel(application) {
    val loginResponse = MutableLiveData<Response<ResponseBody>>()
    val isLoading = MutableLiveData<Boolean>()
    val hasError = MutableLiveData<Boolean>()
//
    private val userApi = UserService()
    private val database = AlertDatabase.invoke(application)
    private val disposable = CompositeDisposable()
//
    private val TAG = "UserLoginViewmodel"


    fun login(userLogin: UserLogin) {
        Log.i(TAG,"User Login function is called")
        val d1 = userApi.login(userLogin)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Response<ResponseBody>>() {
                override fun onSuccess(response: Response<ResponseBody>) {
                    loginResponse.value = response
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
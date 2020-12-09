package com.example.sdcresourcemonitor.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sdcresourcemonitor.R
import com.example.sdcresourcemonitor.databinding.FragmentLoginBinding
import com.example.sdcresourcemonitor.model.network.model.UserLogin
import com.example.sdcresourcemonitor.model.network.model.UserSignup
import com.example.sdcresourcemonitor.viewModel.LoginViewModel
import com.example.sdcresourcemonitor.viewModel.UserSignupViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import org.json.JSONObject
import com.example.sdcresourcemonitor.viewModel.UserLoginViewModel as User


class LoginFragment : Fragment() {

    companion object {
        const val TAG = "LoginFragment"
        const val SIGN_IN_RESULT_CODE = 1001
    }

    //    private val userSignupViewModel by viewModels<UserSignupViewModel>()
    private lateinit var userSignupViewModel: UserSignupViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var userLoginViewModel: User
    private var email = ""
    private var uid = ""

//    private lateinit var googleSignInClient: GoogleSignInClient


    private lateinit var binding: FragmentLoginBinding

    private lateinit var fireBaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("LogInOut", "LoginFragment called")
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false
        )
        observeFirebaseUserDataAndToken()
//        binding.authButton.setOnClickListener { launchSignInFlow() }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userSignupViewModel = ViewModelProvider(this).get(UserSignupViewModel::class.java)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        userLoginViewModel = ViewModelProvider(this).get(User::class.java)

        // [START config_signin]
        // Configure Google Sign In
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
        // [END config_signin]
//        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder()
                .build()
        )
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false)
            .setAvailableProviders(providers)
            .build()

        startActivityForResult(intent, SIGN_IN_RESULT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in user.
                Log.i(
                    TAG,
                    "Successfully signed in user " +
                            "${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
            } else {
                // Sign in failed. If response is null the user canceled the sign-in flow using
                // the back button. Otherwise check response.getError().getErrorCode() and handle
                // the error.
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }

    private fun observeFirebaseUserDataAndToken() {
        Log.i(TAG, "observeFirebaseUserDataAndToken called")
        loginViewModel.currentUser.observe(viewLifecycleOwner, Observer { currentUser ->
            if (currentUser != null && currentUser.email != null) {
                fireBaseUser = currentUser
                email = currentUser.email!!
                uid = currentUser.uid
                Log.d(TAG, "current user is not null (Email: $email, uid :$uid)")
                var token: String
                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w(TAG, "getInstanceId failed", task.exception)
                            return@OnCompleteListener
                        }

                        // Get new Instance ID token
                        token = task.result?.token!!

                        Log.i(TAG, "Got the token")
                        val userSignup = UserSignup(email, uid, token)
                        signup(userSignup)
                    })
            }
            else {
                Log.i("LogInOut", "Error while sign in with gmail")
                binding.authButton.visibility = View.VISIBLE
                binding.authButton.setOnClickListener {
                    launchSignInFlow()
                }
            }
        })
    }

    private fun signup(userSignup: UserSignup) {
        observeUserSignupViewModel()
        userSignupViewModel.signup(userSignup)
    }

    private fun observeUserSignupViewModel() {
        userSignupViewModel.userSignupResponse.observe(viewLifecycleOwner, Observer { userSignupResponse ->
            if (userSignupResponse != null) {
                if (userSignupResponse.body() != null && userSignupResponse.message().equals("ok", true)) {
                    val json: String = userSignupResponse.body()?.string()?.removeSurrounding("\"") ?: ""
                    if(json == "") {
                        Log.i(TAG,"Something is wrong..")
                    }else {
                        val jsonObject = JSONObject(json)
                        val jsonArray1 = jsonObject.getJSONArray("projects")
                        val jsonArray2 = jsonObject.getJSONArray("roles")
                        //Todo Save/update user data into Sharedprefs or database
                        login(UserLogin(email, uid))
                    }

                }
                else {
                    Log.i(
                        "LogInOut",
                        "This email is not authorized , please contact the support team"
                    )

                    //Todo user should be reomved from Firebase
                    //TOdo Display appropriate message
                    AuthUI.getInstance().signOut(requireContext()).addOnCompleteListener {
                        loginViewModel.authenticationState.observe(
                            requireActivity(),
                            Observer { authenticationState ->
                                if (authenticationState != LoginViewModel.AuthenticationState.AUTHENTICATED) {
//                                    fireBaseUser.delete().addOnCompleteListener { task ->
//                                        if (task.isSuccessful) {
                                    binding.authButton.visibility = View.VISIBLE
                                    binding.authButton.setOnClickListener {
                                        launchSignInFlow()
                                    }
//                                            }
//                                        } else {
//                                            Log.i(TAG, "Something wrong ....")
//                                        }
                                }
                            })
                    }


                }
            } else {
                Log.i("LogInOut", "Network error please check your internet")
                binding.authButton.visibility = View.VISIBLE
                binding.authButton.setOnClickListener {
                    launchSignInFlow()
                }
            }
        })
        userSignupViewModel.hasError.observe(viewLifecycleOwner, Observer { hasError ->
            if (hasError)
                Log.i("UserLogin", hasError.toString())
        })

    }

    private fun login(userLogin: UserLogin) {
        userLoginViewModel.login(userLogin)
        observeUserLoginResponse()
    }

    private fun observeUserLoginResponse() {
        userLoginViewModel.loginResponse.observe(viewLifecycleOwner, Observer { loginResponse ->
            if (loginResponse != null) {
                if (loginResponse.message().equals("ok", true) &&
                    loginResponse.headers().get("Authorization")?.startsWith("Bearer", true) == true
                ) {
                    val authorizationToken = loginResponse.headers().get("Authorization")
                    //Todo Store this token
                    binding.authButton.visibility = View.GONE
                    val navController = findNavController()
                    navController.navigate(LoginFragmentDirections.actionLoginFragmentToAlertDashBoard22())


                } else {
                    Log.i(
                        "LogInOut",
                        "YOU are unauthorized to access this request"
                    )
                    //Todo user should be removed from Firebase
                    //TOdo Display appropriate message
                    binding.authButton.visibility = View.VISIBLE
                    binding.authButton.setOnClickListener {
                        launchSignInFlow()

                    }
                }
            } else {
                Log.i("LogInOut", "Network error please check your internet")
                binding.authButton.visibility = View.VISIBLE
                binding.authButton.setOnClickListener {
                    launchSignInFlow()
                }
            }

        })
    }

}
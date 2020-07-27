package com.example.sdcresourcemonitor.view

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.sdcresourcemonitor.R
import com.example.sdcresourcemonitor.databinding.NavHeaderMainBinding
import com.example.sdcresourcemonitor.view.adapter.AlertListAdapter
import com.example.sdcresourcemonitor.viewModel.FirebaseUserLiveData
import com.example.sdcresourcemonitor.viewModel.LoginViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navController: NavController
    private val appBarConfiguration by lazy {
        AppBarConfiguration(
            setOf(
                R.id.alertDashBoard2
            ),drawerLayout
        )
    }
    private lateinit var headerBinding: NavHeaderMainBinding

//    private var loginViewModel : LoginViewModel? = null
    private val loginViewModel by viewModels<LoginViewModel>()
    private val TAG = "mainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar1))
        setupNavigation()
        setupDataBinding()
        observeViewModel()
        setupViews()
        setupFirebaseToken()
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController,null)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setupNavigation(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController,drawerLayout)
        setupWithNavController(toolbar1, navController, appBarConfiguration)

        navController.addOnDestinationChangedListener{ _, destination, _  ->
            if(destination.id in arrayOf(R.id.alertDashBoard2,R.id.alertListTabbedFragment2) ) {
                toolbar1.visibility = View.VISIBLE
            } else {
                toolbar1.visibility = View.GONE
            }
        }
    }

    private fun setupDataBinding() {
        headerBinding =DataBindingUtil.inflate(
            layoutInflater,R.layout.nav_header_main,navView,false)

        navView.addHeaderView(headerBinding.root)
    }

    private fun observeViewModel() {
//        val viewModelProvider = ViewModelProvider(navController.getViewModelStoreOwner(
//            R.id.alert_navigation),ViewModelProvider.AndroidViewModelFactory(application))
//        loginViewModel = viewModelProvider.get(LoginViewModel::class.java)
        loginViewModel.currentUser.observe(this, Observer { currentUser ->
            if(currentUser != null) {
                headerBinding.viewModel = loginViewModel
                Log.i("NavHeader" , "$currentUser : ${currentUser.displayName}, ${currentUser.email}, ${currentUser.photoUrl}")
            }
        })
//        headerBinding.viewModel = loginViewModel

    }

    private fun setupViews() {
        navView.setNavigationItemSelectedListener(this)
    }

    private fun setupFirebaseToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
//                val msg = getString(R.string.msg_token_fmt, token)
                val msg  = token
                Log.d(TAG, msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.lagout -> {

            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}

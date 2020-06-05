package com.example.sdcresourcemonitor.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sdcresourcemonitor.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    lateinit var adapter: AlertListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        mainRv.layoutManager = LinearLayoutManager(applicationContext)
        val texts = ArrayList<String>()
        texts.add("sasi")
        texts.add("kiran")
        texts.add("kilari")
        adapter = AlertListAdapter(texts)
        mainRv.adapter = adapter
        adapter.update(texts)*/

        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar1))
        navController = Navigation.findNavController(
            this,
            R.id.fragment3
        )
        NavigationUI.setupWithNavController(toolbar1, navController, null)
    }
    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController,null)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}

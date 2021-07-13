package io.github.geekcarl.webview.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.alibaba.fastjson.JSON
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import io.github.geekcarl.bridge.JsonHandler
import io.github.geekcarl.bridge.JsonHandlerProxy

class MainActivity : AppCompatActivity() {

    val mGson: Gson by lazy { Gson() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        JsonHandlerProxy.setProxy(object : JsonHandler {
            override fun toJson(obj: Any?): String? {
//                return mGson.toJson(obj)
                return JSON.toJSONString(obj)
            }

            override fun <T> fromJson(json: String, clazz: Class<T>): T? {
//                return mGson.fromJson<T>(json, clazz)
                return JSON.parseObject(json, clazz)
            }
        })

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
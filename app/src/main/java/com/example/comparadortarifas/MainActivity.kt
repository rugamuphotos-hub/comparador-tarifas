package com.example.comparadortarifas

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var resultView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultView = findViewById(R.id.resultView)
        val settingsBtn: Button = findViewById(R.id.settingsBtn)

        settingsBtn.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        findViewById<Button>(R.id.openUberBtn).setOnClickListener {
            openApp("com.ubercab", "Uber")
        }
        findViewById<Button>(R.id.openBoltBtn).setOnClickListener {
            openApp("ee.mtakso.client", "Bolt")
        }
        findViewById<Button>(R.id.openCabifyBtn).setOnClickListener {
            openApp("com.cabify.rider", "Cabify")
        }

        startPolling()
    }

    private fun openApp(packageName: String, label: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            resultView.text = "$label no está instalada en este móvil."
        }
    }

    private fun startPolling() {
        handler.post(object : Runnable {
            override fun run() {
                val data = FareStore.snapshot()
                resultView.text = if (data.isEmpty()) {
                    "Todavía no se ha capturado ningún precio."
                } else {
                    data.entries.joinToString("\n") { (app, price) -> "$app: $price" }
                }
                handler.postDelayed(this, 1000)
            }
        })
    }
}

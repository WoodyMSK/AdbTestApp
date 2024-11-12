package com.example.adbtestapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.adbtestapp.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        with(binding) {
            bVolumeInc.setOnClickListener {
                runAdb("input keyevent 24", this@MainActivity)
            }
            bVolumeDec.setOnClickListener {
                runAdb("input keyevent 25", this@MainActivity)
            }
            bInstall.setOnClickListener {
                runAdb(
                    "pm install -r -d -i org.telegram.messenger.web --user 0 /storage/emulated/0/Download/Telegram.apk",
                    this@MainActivity
                )
            }
            bUninstall.setOnClickListener {
                runAdb(
                    "pm uninstall -k --user 0 ru.rutube.app",
                    this@MainActivity
                )
            }
            bUninstall.setOnClickListener {
                runAdb(
                    "rm /storage/emulated/0/Download/1.jpg",
                    this@MainActivity
                )
            }
        }

    }
}

private fun runAdb(command: String, context: Context) {

    try {
        val process = Runtime.getRuntime().exec(command)
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
    } catch (e: Exception) {
        val toastText = e.message.toString()
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
    }
}
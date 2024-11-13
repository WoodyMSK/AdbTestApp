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
                Toast.makeText(this@MainActivity, "VolumeInc", Toast.LENGTH_SHORT).show()
            }
            bVolumeDec.setOnClickListener {
                runAdb("input keyevent 25", this@MainActivity)
                Toast.makeText(this@MainActivity, "VolumeDec", Toast.LENGTH_SHORT).show()
            }
            bInstall.setOnClickListener {
                runAdb(
                    "pm install -r -d -i org.telegram.messenger.web --user 0 /storage/emulated/0/Download/Telegram.apk",
                    this@MainActivity
                )
                Toast.makeText(this@MainActivity, "Install Telegram", Toast.LENGTH_SHORT).show()
            }
            bUninstall.setOnClickListener {
                runAdb(
                    "pm uninstall -k --user 0 ru.rutube.app",
                    this@MainActivity
                )
                Toast.makeText(this@MainActivity, "Uninstall RuTube", Toast.LENGTH_SHORT).show()
            }
            bUninstallPicture.setOnClickListener {
                runAdb(
                    "rm -r /storage/emulated/0/Download/1.jpg",
                    this@MainActivity
                )
                Toast.makeText(this@MainActivity, "Uninstall 1.jpg", Toast.LENGTH_SHORT).show()
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
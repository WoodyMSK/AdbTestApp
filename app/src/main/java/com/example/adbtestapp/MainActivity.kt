package com.example.adbtestapp

import android.os.Bundle
import android.util.Log
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
                val process = Runtime.getRuntime().exec("input keyevent 24")
                val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
                Toast.makeText(this@MainActivity, "VolumeInc", Toast.LENGTH_SHORT).show()
            }
            bVolumeDec.setOnClickListener {
                val process = Runtime.getRuntime().exec("input keyevent 25")
                val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
                Toast.makeText(this@MainActivity, "VolumeDec", Toast.LENGTH_SHORT).show()
            }
            bInstall.setOnClickListener {
                runAdb("pm install -r -d -i org.telegram.messenger.web --user 0 /storage/emulated/0/Download/Telegram.apk")
                Toast.makeText(this@MainActivity, "Install Telegram", Toast.LENGTH_SHORT).show()
            }
            bUninstall.setOnClickListener {
                runAdb("pm uninstall -k ru.rutube.app")
                Toast.makeText(this@MainActivity, "Uninstall RuTube", Toast.LENGTH_SHORT).show()
            }
            bUninstallPicture.setOnClickListener {
                runAdb("rm -r /storage/emulated/0/Download/1.jpg")
                Toast.makeText(this@MainActivity, "Uninstall 1.jpg", Toast.LENGTH_SHORT).show()
            }
            bSuperUser.setOnClickListener {
                runAdb("su")
                Toast.makeText(this@MainActivity, "SuperUser", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private fun runAdb(command: String) {

    try {
        val process = Runtime.getRuntime().exec(command)
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))

        // Grab the results
        val log = StringBuilder()
        var line: String?
        line = bufferedReader.readLine()
        while (line != null) {
            log.append(line + "\n")
            line = bufferedReader.readLine()
        }
        val Reader = BufferedReader(
            InputStreamReader(process.errorStream)
        )

        // if we had an error during ex we get here
        val error_log = StringBuilder()
        var error_line: String?
        error_line = Reader.readLine()
        while (error_line != null) {
            error_log.append(error_line + "\n")
            error_line = Reader.readLine()
        }
        if (error_log.toString() != "")
            Log.i("ADB_COMMAND", "command : $command $log error $error_log")
        else
            Log.i("ADB_COMMAND", "command : $command $log")
    } catch (e: Exception) {
        Log.i("ADB_COMMAND", e.message.toString())
    }
}
package com.example.adbtestapp

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageInstaller
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val volumeInc: Button = findViewById<Button?>(R.id.bVolumeInc).apply {
            setOnClickListener {
                val process = Runtime.getRuntime().exec("input keyevent 24")
                val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
                Toast.makeText(this@MainActivity, "VolumeInc", Toast.LENGTH_SHORT).show()
            }
        }

        val volumeDec: Button = findViewById<Button?>(R.id.bVolumeDec).apply {
            setOnClickListener {
                val process = Runtime.getRuntime().exec("input keyevent 25")
                val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
                Toast.makeText(this@MainActivity, "VolumeDec", Toast.LENGTH_SHORT).show()
            }
        }

        val install: Button = findViewById<Button?>(R.id.bInstall).apply {
            setOnClickListener {
                runAdb("pm install -r -d -i org.telegram.messenger.web --user 0 /storage/emulated/0/Download/Telegram.apk")
                Toast.makeText(this@MainActivity, "Install Telegram", Toast.LENGTH_SHORT).show()
            }
        }

        val unInstall: Button = findViewById<Button?>(R.id.bUninstall).apply {
            setOnClickListener {
                runAdb("pm uninstall -k ru.rutube.app")
                Toast.makeText(this@MainActivity, "Uninstall RuTube", Toast.LENGTH_SHORT).show()
            }
        }

        val unInstallByPM: Button = findViewById<Button?>(R.id.bUninstallByPM).apply {
            setOnClickListener {
                uninstallApp("pm uninstall -k ru.rutube.app", this@MainActivity)
                Toast.makeText(this@MainActivity, "Uninstall RuTube", Toast.LENGTH_SHORT).show()
            }
        }

        val unInstallByPicture: Button = findViewById<Button?>(R.id.bUninstallPicture).apply {
            setOnClickListener {
                runAdb("rm -r /storage/emulated/0/Download/1.jpg")
                Toast.makeText(this@MainActivity, "Uninstall 1.jpg", Toast.LENGTH_SHORT).show()
            }
        }

        val superUser: Button = findViewById<Button?>(R.id.bSuperUser).apply {
            setOnClickListener {
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

fun uninstallApp(packageName: String, context: Context) {
    Log.d("ADB_COMMAND", "Uninstalling package $packageName")

    try {
        val packageInstaller: PackageInstaller = context.packageManager.packageInstaller
        packageInstaller.uninstall(
            packageName,
            createUninstallIntentSender(context, packageName)
        )
    } catch (e: Exception) {
        Log.i("ADB_COMMAND", e.message.toString())
        e.printStackTrace()
    }
}


fun createUninstallIntentSender(
    context: Context,
    packageName: String
): IntentSender {
    val intent = Intent("1")
    intent.putExtra(Intent.EXTRA_PACKAGE_NAME, packageName)
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0,
        intent, PendingIntent.FLAG_IMMUTABLE
    )
    return pendingIntent.intentSender
}
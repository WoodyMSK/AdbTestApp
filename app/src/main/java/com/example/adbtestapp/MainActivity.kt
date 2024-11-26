package com.example.adbtestapp

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL


class MainActivity : AppCompatActivity() {
    val TAG: String = "com.example.adbtestapp.MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val installApk: Button = findViewById<Button?>(R.id.bInstall).apply {
            setOnClickListener {

                download(
                    urlLink = "https://support.mobileiron.com/MIClient-latest.apk",
                    fileName = "MIClient.apk",
                    fileNameExtension = "apk",
                    context = this@MainActivity,
                )
            }
        }

        val installCertificates: Button = findViewById<Button?>(R.id.bInstallCer).apply {
            setOnClickListener {

                download(
                    urlLink = "https://gu-st.ru/content/Other/doc/russian_trusted_root_ca.cer",
                    fileName = "russian_trusted_root_ca.cer",
                    fileNameExtension = "cer",
                    context = this@MainActivity,
                )
            }
        }
    }

    fun download(
        urlLink: String,
        fileName: String,
        fileNameExtension: String,
        context: Context,
    ) {
        Log.d(TAG, "Start - install")
        val thread = Thread {
            Log.d(TAG, "Start Thread - install")
            val url: URL = URL(urlLink)
            val dataTmp: String = "/data/local/tmp/%s".format(fileName)
            var outPath: String = getExternalFilesDir(null).toString() + "/" + fileName
            val file: File = File(outPath)
            if (!file.exists() || fileNameExtension == "cer") {
                val cx = url.openConnection()
                cx.connect()

                val lengthFile = cx.contentLength
                val input: InputStream = BufferedInputStream(url.openStream())
                val output: OutputStream = FileOutputStream(outPath)
                val data = ByteArray(1024)

                Log.d(TAG, "Start Download - install")
                // Download file.
                var count: Int = 0
                var total: Int = 0
                while ((input.read(data, 0, 1024).also { count = it }) != -1) {
                    output.write(data, 0, count)
                    total += count
                    Log.d(TAG, "bytes: $total - install")
                }
                // Close streams.
                output.flush()
                output.close()
                input.close()
                Log.d(TAG, "End Download - install")
            } else {
                Log.d(TAG, "File exists")
                // TODO: Storage Access Framework (SAF)
                // TODO: необязательное копирование в /data/data/com.example.adbtestapp/cache/installХХХХХapk

                // Open an InputStream to read the APK file
                var inputStream: InputStream = FileInputStream(outPath)

                // Create a temporary file to save the APK
                var tempFile: File =
                    File.createTempFile("install", fileNameExtension, getCacheDir())

                // Copy the APK file to the temporary file
                var outputStream: FileOutputStream? = null
                try {
                    outputStream = FileOutputStream(tempFile)
                    val buffer: ByteArray = ByteArray(4096)
                    var bytesRead: Int
                    var total: Int = 0
                    while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        total += bytesRead
                        Log.d(TAG, "bytes: $total - copy")
                    }
                    outputStream.flush()
                    inputStream.close()
                    outputStream.close()
                    outPath = tempFile.getCanonicalPath()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    try {
                        if (outputStream != null) {
                            outputStream.close()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            Log.d(TAG, "End Thread - install. outPath = $outPath")

            when(fileNameExtension) {
                "apk" -> installApk(outPath, context)
                "cer" -> installCert(outPath)
            }

        }.start()

        Log.d(TAG, "End - install")
    }

    private fun installCert(inPath: String) {

        Log.d(TAG, "Cert - install. inPath = $inPath")

        Log.d(TAG, "Cert - Start Thread. Thread name = ${Thread.currentThread().name}")

        try {
            val certificateFile = File(inPath)
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = FileProvider.getUriForFile(
                this,
                this.applicationContext.packageName + ".provider",
                certificateFile
            )
            intent.setDataAndType(uri, "application/x-x509-ca-cert")
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent)
        } catch (e: Exception) {
            Log.d(TAG, "Cert - Exception. inPath = ${e.message}")
        }

    }

    private fun installApk(apkPath: String, context: Context): Boolean {
        Log.d(TAG, "Start %s - install".format(apkPath))
        try {
            //val apkUri: Uri = Uri.fromFile(File(apkPath))
            //context.packageManager.installPackage(apkUri, null, PackageManager.INSTALL_REPLACE_EXISTING, TAG);

            Log.d(TAG, "01. %s - install".format(apkPath))
            // Install the APK using the PackageInstaller
            val packageInstaller: PackageInstaller = getPackageManager().getPackageInstaller()
            val params: PackageInstaller.SessionParams = PackageInstaller.SessionParams(
                PackageInstaller.SessionParams.MODE_FULL_INSTALL
            )
            params.setAppPackageName("com.mobileiron")
            val sessionId: Int = packageInstaller.createSession(params)
            val session: PackageInstaller.Session = packageInstaller.openSession(sessionId)

            Log.d(TAG, "02. %s - install".format(apkPath))
            // Create a OutputStream to write the APK to the PackageInstaller session
            val packageOutputStream: OutputStream = session.openWrite("COSU", 0, -1)

            Log.d(TAG, "03. %s - install".format(apkPath))
            // Copy the APK file to the PackageInstaller session
            var inputStream: InputStream = FileInputStream(apkPath)
            val buffer: ByteArray = ByteArray(4096)
            var bytesRead: Int
            while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
                packageOutputStream.write(buffer, 0, bytesRead)
            }
            packageOutputStream.flush()
            inputStream.close()
            packageOutputStream.close()

            Log.d(TAG, "04. %s - install".format(apkPath))

            // Create a PendingIntent and use it to generate the IntentSender
            val intent = Intent(context, InstallReceiver::class.java)
            intent.action = InstallReceiver.ACTION_INSTALL
            val pi = PendingIntent.getBroadcast(
                context,
                InstallReceiver::class.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            Log.d(TAG, "05. %s - install".format(apkPath))
            // Commit the PackageInstaller session
            session.commit(pi.intentSender)
            session.close()
            Log.d(TAG, "End %s - install finished".format(apkPath))
            return true;
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d(TAG, "End %s - install failed".format(apkPath))
        return false
    }
}
package com.example.adbtestapp

import android.content.BroadcastReceiver
import android.content.pm.PackageInstaller
import android.util.Log
import android.content.Context
import android.content.Intent

class InstallReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_INSTALL: String = "com.example.adbtestapp.InstallReceiver.ACTION_INSTALL"
    }
    val TAG: String = "com.example.adbtestapp.InstallReceiver"

    override fun onReceive(context: Context, intent: Intent) {

        if(intent.action == ACTION_INSTALL){

            val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -2)
            val message: String? = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
            val pname = intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME)
            val sessionId = intent.getIntExtra(PackageInstaller.EXTRA_SESSION_ID, 0)
            val sessionInfo: PackageInstaller.SessionInfo? = intent.getParcelableExtra(PackageInstaller.EXTRA_SESSION)
            Log.d(TAG, "Status: $status, Message: $message, Package Name: $pname, Session ID: $sessionId")

            when (status) {
                PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                    // This test app isn't privileged, so the user has to confirm the install.
                    Log.d(TAG, "Install needs user interaction")
                }

                PackageInstaller.STATUS_SUCCESS -> {
                    Log.d(TAG, "Installed successfully")
                }
                PackageInstaller.STATUS_FAILURE,
                PackageInstaller.STATUS_FAILURE_ABORTED,
                PackageInstaller.STATUS_FAILURE_BLOCKED,
                PackageInstaller.STATUS_FAILURE_CONFLICT,
                PackageInstaller.STATUS_FAILURE_INCOMPATIBLE,
                PackageInstaller.STATUS_FAILURE_INVALID,
                PackageInstaller.STATUS_FAILURE_STORAGE /*,
                PackageInstaller.STATUS_FAILURE_TIMEOUT */-> {
                    val message: String? = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
                    Log.d(TAG, "Install failure, Status: $status, Message: $message")
                }

                else -> {
                    //Unknown status
                    Log.d(TAG, "Unknown install status $status")
                }
            }
        }else{
            Log.d(TAG, "Unknown action ${intent.action}")
        }
    }
}
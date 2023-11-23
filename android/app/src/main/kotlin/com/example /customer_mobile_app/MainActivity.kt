package com.poonawallafincorp.loan.emi.payment.card.upi.mf

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Debug
import android.provider.Settings
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class MainActivity : FlutterFragmentActivity() {

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "example.com/channel").setMethodCallHandler { call, result ->
            if (call.method == "getDeviceModel") {

                result.success(isFridaDetected(this))
                // isFridaDetected();
            } else {
                result.notImplemented()
            }
        }
    }

    fun isFridaDetected(context: Context): Boolean {

        var isFridaDetected = false
        // Check if the app is running on a rooted device

        if (isDeviceRooted()) {
            isFridaDetected = true
        }

        // Check if the Frida server process is running
        else if (isFridaServerRunning(context)) {
            isFridaDetected = true
         }

        // Check if the app is being debugged
         else if (isDeveloperOptionEnabled()) {
             isFridaDetected = true;

        }else if (isFridaProcessName()) {
            isFridaDetected = true
        }

        // Add more detection techniques if desired
        return isFridaDetected
    }

    private fun isRooted(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun isFridaServerRunning(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (activityManager != null) {
            for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
                if ("com.frida.server" == service.service.className) {
                    return true
                }
            }
        }
        return false
    }

    private fun isDeveloperOptionEnabled():Boolean{
        val adb: Int = Settings.Secure.getInt(this.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0)
        return adb==1
    }

    private fun isDebuggerAttached(context: Context): Boolean {
        return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) == 1
        /*Log.e("DEbugger", Debug.isDebuggerConnected().toString());
        Log.e("DEbugger", Debug.waitingForDebugger().toString());
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger()*/
    }

    private fun isFridaProcessName(): Boolean {
        val processName = getProcessName()
        return processName != null && (processName.contains("frida") || processName.contains("re.frida"))
    }

    private fun areNativeLibrariesTampered(): Boolean {
        return try {
            System.loadLibrary("frida")
            false // The library loaded successfully, indicating it's not tampered
        } catch (e: UnsatisfiedLinkError) {
            true // Failed to load the library, suggesting tampering
        }
    }

    private fun getProcessName(): String? {
        val pid = android.os.Process.myPid()
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (manager != null) {
            for (processInfo in manager.runningAppProcesses) {
                if (processInfo.pid == pid) {
                    return processInfo.processName
                }
            }
        }
        return null
    }


    fun isDeviceRooted(): Boolean {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3()
    }

    private fun checkRootMethod1(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun checkRootMethod2(): Boolean {
        val paths = arrayOf("/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su")
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }

    private fun checkRootMethod3(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val `in` = BufferedReader(InputStreamReader(process.inputStream))
            if (`in`.readLine() != null) true else false
        } catch (t: Throwable) {
            false
        } finally {
            process?.destroy()
        }
    }
}

package com.anjez.utils

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.anjez.R
import java.util.concurrent.Executor

object SecurityHelper {
    
    fun isBiometricAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or 
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }
    
    fun requestBiometricAuth(
        activity: FragmentActivity,
        onAuthResult: (success: Boolean) -> Unit
    ) {
        if (!isBiometricAvailable(activity)) {
            Toast.makeText(
                activity,
                "المصادقة البيومترية غير متاحة على هذا الجهاز",
                Toast.LENGTH_LONG
            ).show()
            onAuthResult(false)
            return
        }
        
        val executor: Executor = ContextCompat.getMainExecutor(activity)
        
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    onAuthResult(false)
                }
                
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    onAuthResult(true)
                }
                
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onAuthResult(false)
                }
            }
        )
        
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("قفل التطبيق")
            .setSubtitle("استخدم البصمة أو PIN لإلغاء قفل التطبيق")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .setConfirmationRequired(true)
            .build()
        
        biometricPrompt.authenticate(promptInfo)
    }
    
    fun setAppLockEnabled(context: Context, enabled: Boolean) {
        val sharedPrefs = context.getSharedPreferences(
            "anjez_prefs",
            Context.MODE_PRIVATE
        )
        sharedPrefs.edit()
            .putBoolean("app_lock_enabled", enabled)
            .apply()
    }
    
    fun isAppLockEnabled(context: Context): Boolean {
        val sharedPrefs = context.getSharedPreferences(
            "anjez_prefs",
            Context.MODE_PRIVATE
        )
        return sharedPrefs.getBoolean("app_lock_enabled", false)
    }
}

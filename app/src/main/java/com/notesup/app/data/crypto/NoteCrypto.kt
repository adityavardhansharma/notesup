package com.notesup.app.data.crypto

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AES-GCM key in the Android Keystore. User authentication is required to use
 * the key (biometric or device credential), matching LockGateScreen.
 */
@Singleton
class NoteCrypto @Inject constructor() {
    fun encrypt(plain: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key())
        val iv = cipher.iv
        val packed = cipher.doFinal(plain)
        return byteArrayOf(iv.size.toByte()) + iv + packed
    }

    fun decrypt(blob: ByteArray): ByteArray {
        require(blob.isNotEmpty()) { "empty cipher" }
        val ivLen = blob[0].toInt() and 0xFF
        require(ivLen in 12..16 && blob.size > 1 + ivLen) { "bad cipher" }
        val iv = blob.copyOfRange(1, 1 + ivLen)
        val packed = blob.copyOfRange(1 + ivLen, blob.size)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, iv))
        return cipher.doFinal(packed)
    }

    private fun key(): SecretKey {
        val ks = KeyStore.getInstance(PROVIDER).apply { load(null) }
        (ks.getKey(ALIAS, null) as? SecretKey)?.let { return it }
        val gen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVIDER)
        val builder = KeyGenParameterSpec.Builder(
            ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(true)
            .setRandomizedEncryptionRequired(true)
        if (Build.VERSION.SDK_INT >= 30) {
            builder.setUserAuthenticationParameters(
                0,
                KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL,
            )
        } else {
            @Suppress("DEPRECATION")
            builder.setUserAuthenticationValidityDurationSeconds(-1)
        }
        gen.init(builder.build())
        return gen.generateKey()
    }

    private companion object {
        const val PROVIDER = "AndroidKeyStore"
        const val ALIAS = "notesup_lock"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}

package com.example.tapgopay.data

import android.util.Log
import com.example.tapgopay.MainActivity
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import java.io.File
import java.math.BigInteger
import java.security.AlgorithmParameters
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.*
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Generates a key from password using Argon2 KDF (Key derivation function)
 * Note: To get a deterministic key every time, you must provide this with
 * the same salt. Or none at all - If you're feeling lucky :)
 *
 * @return 32 byte derived key
 */
private fun deriveKey(password: String, salt: ByteArray, keyLen: Int = 32): ByteArray {
    val params = Argon2Parameters.Builder(Argon2Parameters.ARGON2_id).withSalt(salt)
        .withParallelism(2) // Number of threads
        .withMemoryAsKB(64 * 1024) // 64 MB
        .withIterations(3).build()

    val generator = Argon2BytesGenerator()
    generator.init(params)

    val result = ByteArray(keyLen)
    generator.generateBytes(password.toByteArray(), result, 0, result.size)
    return result
}

/**
 * Generates a deterministic private and public keypair based on
 * provided seed phrase
 *
 * @return ECDSA private and public keypair
 */
private fun generateKeyPair(
    seed: ByteArray,
    curveName: String = "secp256r1"
): KeyPair {
    val keyPairGenerator = KeyPairGenerator.getInstance("EC")
    val ecSpec = ECGenParameterSpec(curveName)

    // Modern DRBG-based SecureRandom
    val random = SecureRandom.getInstanceStrong()
    random.setSeed(seed) // deterministic: same seed -> same sequence

    keyPairGenerator.initialize(ecSpec, random)
    return keyPairGenerator.generateKeyPair()
}

/**
 * Generates a deterministic private and public keypair based on
 * provided seed phrase, encrypts the private key using seed phrase,
 * and saves salt + iv + ciphertext material onto the provided file
 *
 * @return ECDSA private and public keys that were saved to file
 */
fun generateAndSaveKeyPair(
    password: String,
    privKeyFile: File,
    pubKeyFile: File
): KeyPair? {
    try {
        val secureRandom = SecureRandom()
        val salt = ByteArray(16).also { secureRandom.nextBytes(it) }
        val iv = ByteArray(12).also { secureRandom.nextBytes(it) }

        // Generate a stronger key from password using KDF
        val derivedKey = deriveKey(password, salt)

        // Use key to deterministically generate a private key
        val keypair: KeyPair = generateKeyPair(derivedKey)

        // Encrypt private key
        val aesKey = SecretKeySpec(derivedKey, "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, GCMParameterSpec(128, iv))
        val ciphertext: ByteArray = cipher.doFinal(keypair.private.encoded)

        // Save private key to file
        privKeyFile.writeBytes(salt + iv + ciphertext)

        // Save public key to file
        pubKeyFile.writeBytes(keypair.public.pemEncode())

        return keypair

    } catch (e: Exception) {
        Log.e(MainActivity.TAG, "Error generating and saving ECDSA private key; ${e.message}")
        return null
    }
}

fun PublicKey.pemEncode(): ByteArray {
    val encoded = this.encoded
    val base64 = Base64.getMimeEncoder(64, "\n".toByteArray()).encodeToString(encoded)
    return "-----EC PUBLIC KEY-----\n$base64\n-----END PUBLIC KEY-----".toByteArray()
}

fun loadAndDecryptPrivateKey(password: String, file: File): PrivateKey? {
    try {
        val data = file.readBytes()
        val salt = data.copyOfRange(0, 16)
        val iv = data.copyOfRange(16, 28)
        val ciphertext = data.copyOfRange(28, data.size)

        // Regenerate the key that was used to encrypt private key
        val derivedKey = deriveKey(password, salt)

        // Decrypt ciphertext
        val aesKey = SecretKeySpec(derivedKey, "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, aesKey, GCMParameterSpec(128, iv))
        val encodedKey = cipher.doFinal(ciphertext)

        // Parse private key bytes into private key
        val keySpec = PKCS8EncodedKeySpec(encodedKey)
        val keyFactory = KeyFactory.getInstance("EC")
        return keyFactory.generatePrivate(keySpec)

    } catch (e: Exception) {
        Log.e(MainActivity.TAG, "Error loading private key; ${e.message}")
        return null
    }
}

fun signData(data: ByteArray, privateKey: PrivateKey): ByteArray? {
    try {
        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()

    } catch (e: Exception) {
        Log.e(MainActivity.TAG, "Error signing data; ${e.message}")
        return null
    }
}



package com.example.summarizer

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TokenManager {
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    fun putToken(tokenCount: Long) {
        val userDocRef = db.collection("users").document(user?.uid ?: "")
        userDocRef.set(mapOf("tokenCount" to tokenCount))
            .addOnSuccessListener {
                // Token count stored successfully
            }
            .addOnFailureListener { e ->
                // Handle any errors
                Log.i(Utils.ERROR_TAG, e.message.toString())
            }
    }

    fun updateToken(newTokenCount: Long) {
        val userDocRef = db.collection("users").document(user?.uid ?: "")
        userDocRef.update("tokenCount", newTokenCount)
            .addOnSuccessListener {
                // Token count updated successfully
            }
            .addOnFailureListener { e ->
                // Handle any errors
                Log.i(Utils.ERROR_TAG, e.message.toString())
            }
    }


    fun getToken(callback: (Long?) -> Unit) {
        val userDocRef = db.collection("users").document(user?.uid ?: "")
        userDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val tokenCount = documentSnapshot.getLong("tokenCount")
                    callback(tokenCount)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                callback(null)
                Log.i(Utils.ERROR_TAG, e.message.toString())
            }
    }
}

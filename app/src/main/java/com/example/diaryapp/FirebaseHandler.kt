package com.example.diaryapp

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.diaryapp.model.Diary
import com.example.diaryapp.model.User
import com.example.diaryapp.ui.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class FirebaseHandler {
    private val auth = Firebase.auth
    private val database = Firebase.database.reference
    private val storage = Firebase.storage.reference

    fun updateUI(context: Context){
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    fun signIn(email: String, password: String, callback: (Boolean, String?) -> Unit){
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{ task->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message) // Login failed
                }
            }
    }

    fun signUp(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun signOut(){
        auth.signOut()
    }

    private fun getUID():String{
        return auth.currentUser?.uid ?: "NULL"
    }

    fun insertUser(user : User) {
        database.child("user").child(getUID()).setValue(user)
            .addOnFailureListener{
                Log.w(TAG, "insertUser:onFailure", it)
            }
    }

    fun getUserData(callback: (Boolean, User?) -> Unit) {
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userData = dataSnapshot.getValue(User::class.java)
                callback(userData != null, userData)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false, null)
                Log.w(TAG, "getUserData:onCancelled", databaseError.toException())
            }
        }

        database.child("user").child(getUID()).addListenerForSingleValueEvent(userListener)
    }


    fun insertDiary(diary: Diary, callback: (Boolean) -> Unit){
        database.child("diaries").child(getUID()).child(diary.id).setValue(diary)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener{
                callback(false)
                Log.w(TAG, "insertDiary:onFailure", it)
            }
    }

    fun getDiaries(callback: (Boolean, List<Diary>?) -> Unit) {
        val diariesListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val diaries = dataSnapshot.children.mapNotNull { ds ->
                    ds.getValue(Diary::class.java)
                }
                callback(true, diaries)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false, null)
                Log.w(TAG, "getDiaries:onCancelled", databaseError.toException())
            }
        }

        database.child("diaries").child(getUID()).addListenerForSingleValueEvent(diariesListener)
    }

    fun getDiary(diaryId: String, callback: (Boolean, Diary?) -> Unit){
        val diaryListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val diary = dataSnapshot.getValue(Diary::class.java)
                callback(diary != null, diary)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                callback(false, null)
                Log.w(TAG, "getDiary:onCancelled", databaseError.toException())
            }
        }

        database.child("diaries").child(getUID()).child(diaryId).addListenerForSingleValueEvent(diaryListener)
    }

    fun uploadDiaryImage(imageUri : Uri, diaryId : String, callback: (Boolean) -> Unit){
        storage.child(diaryId).putFile(imageUri)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener{
                callback(false)
                Log.w(TAG, "uploadDiaryImage:onFailure", it)
            }
    }

    fun getDiaryImage(diaryId : String, callback: (Boolean, Bitmap?) -> Unit){
        val ONE_MEGABYTE: Long = 1024 * 1024
        storage.child(diaryId).getBytes(ONE_MEGABYTE)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                callback(true, bitmap)
            }.addOnFailureListener {
                callback(false, null)
                Log.w(TAG, "getDiaryImage:onFailure", it)
            }
    }
}
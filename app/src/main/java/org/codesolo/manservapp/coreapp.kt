package org.codesolo.manservapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object firemanager {
    val authentication: FirebaseAuth? = FirebaseAuth.getInstance()
    var database:FirebaseDatabase? = FirebaseDatabase.getInstance()
    val firestore:FirebaseFirestore? = FirebaseFirestore.getInstance()
    val firestorage =  FirebaseStorage.getInstance()

}


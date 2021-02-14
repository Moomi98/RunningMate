package org.techtown.runningmate

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SortRanking {
    private val rankingCoroutine = CoroutineScope(Dispatchers.IO)
    private val db = Firebase.firestore

    fun findAllUserDistanceInfo() {
        rankingCoroutine.launch {

            db.collection("userInfo").get().addOnSuccessListener {
                Log.d("sortRanking", "Success")
                Log.d("sortRanking", it.isEmpty.toString())
                for (i in it) {
                    Log.d("sortRanking", i.id + i.get("totalDistance").toString())

                }
            }.addOnFailureListener {
                Log.d("sortRanking", "sort Failed")
            }.addOnCanceledListener {
                Log.d("sortRanking", "sort Canceled")
            }


        }
    }
}
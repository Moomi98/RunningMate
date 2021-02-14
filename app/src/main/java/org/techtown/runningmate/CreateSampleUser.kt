package org.techtown.runningmate

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CreateSampleUser {
    private val createCoroutine = CoroutineScope(Dispatchers.IO)
    private var year : Int = 0
    private var month : Int = 0
    private var day : Int = 0

    private fun getDate(){
        val cal = Calendar.getInstance() // 오늘 날짜 설정
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH) + 1
        day = cal.get(Calendar.DATE)
    }

    private fun randomDistance() : Double{
//        return (random.nextInt(1000) / 100).toDouble()
        return ((100..1000).random() / 100).toDouble()
    }

    fun createUser(){
        getDate()
        createCoroutine.launch {
            val db = Firebase.firestore
            for(i in 1 until 31){
                val inputData = hashMapOf("totalDistance" to randomDistance())
                db.collection("userInfo").document("testEmail$i@gmail.com").collection(year.toString()).document(month.toString()).set(inputData).addOnCompleteListener {
                    Log.d("createUser", inputData.toString() + "is Complete")
                }.addOnFailureListener {
                    Log.d("createUser", inputData.toString() + "is Fail")
                }
            }
        }
    }
}
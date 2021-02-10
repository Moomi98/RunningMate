package org.techtown.runningmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import io.realm.RealmResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.techtown.runningmate.databinding.RunningresultBinding
import org.w3c.dom.Document
import java.util.*

class RunningResult : AppCompatActivity(), DeleteDialog.AddOnDeleteDialogListener {
    private lateinit var binding: RunningresultBinding
    private var min: Int = 0
    private var sec: Int = 0
    private var distance: Double = 0.0
    private var kcal: Double = 0.0
    private var pace: String = ""
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var startTime: String = ""
    private var endTime: String = ""
    private lateinit var pathList: MutableList<LatLng>
    private var saveCoroutine = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RunningresultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getRunningInfo()
        setResultInfo()
        setButton()
    }

    private fun getRunningInfo() { // StartRunning 으로 부터 결과 정보를 가져온다.
        min = intent.getIntExtra("min", 0)
        sec = intent.getIntExtra("sec", 0)
        distance = intent.getDoubleExtra("distance", 0.0)
        kcal = intent.getDoubleExtra("kcal", 0.0)
        pace = intent.getStringExtra("pace")!!
        startTime = intent.getStringExtra("startTime")!!
        endTime = intent.getStringExtra("endTime")!!
        val list = intent.getParcelableExtra<LatLngSet>("pathList")
        pathList = list!!.pathList
    }

    private fun setResultInfo() { // 가져온 정보로 UI 업데이트
        val showDistance = "$distance km"
        val showTime = "$min:$sec"
        val showKcal = "$kcal kcal"
        binding.mainContentShowKm.text = showDistance
        binding.mainContentShowCal.text = showKcal
        binding.mainContentShowTime.text = showTime
        binding.mainContentShowPace.text = pace
    }

    private fun setButton() { // 저장, 삭제 버튼 설정
        binding.saveButton.setOnClickListener {
            saveRunningData()
            Snackbar.make(binding.root, "활동이 저장 되었습니다", Snackbar.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        binding.deleteButton.setOnClickListener {
            val dialog = DeleteDialog(this)
            dialog.startDialog()
            dialog.setOnDeleteDialogListener(this)
        }
    }

    private fun getCurrentTime() {
        val cal = Calendar.getInstance() // 오늘 날짜 설정
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH) + 1
        day = cal.get(Calendar.DATE)
    }

    private fun saveRunningData() {
        getCurrentTime() // 저장 시점의 날짜 정보 얻기
        val query: RunningList? =
            MyRealm.realm.where(RunningList::class.java) // 저장 시점의 날짜에 다른 정보가 있는지 확인
                .equalTo("date", "${year}/${month}/${day}").findFirst()
        Log.d("saveInfo", "${year}/${month}/${day}")

        if (query != null) { // 해당 날짜에 이미 저장된 정보가 있었다면 추가 정보를 바로 저장
            MyRealm.realm.executeTransaction {
                with(it.createObject(RunningOfDay::class.java)) {
                    this.distance = this@RunningResult.distance
                    this.min = this@RunningResult.min
                    this.sec = this@RunningResult.sec
                    this.pace = this@RunningResult.pace
                    this.kcal = this@RunningResult.kcal
                    this.memo = binding.memoEditText.text.toString()
                    query.runningDayList.add(this)
                }
            }
        } else { // 없다면 해당 날짜를 가질 수 있는 클래스를 만들어 저장
            MyRealm.realm.executeTransaction {
                with(it.createObject(RunningList::class.java)) {
                    val newRunningOfDay = RunningOfDay()
                    newRunningOfDay.distance = this@RunningResult.distance
                    newRunningOfDay.min = this@RunningResult.min
                    newRunningOfDay.sec = this@RunningResult.sec
                    newRunningOfDay.pace = this@RunningResult.pace
                    newRunningOfDay.kcal = this@RunningResult.kcal
                    newRunningOfDay.memo = binding.memoEditText.text.toString()

                    this.runningDayList.add(newRunningOfDay)
                    this.date = "${year}/${month}/${day}"
                }
            }
        }

        saveInfoOnServer()
    }

    private fun saveInfoOnServer() { // 서버에 정보 저장
        val query: LoginInfo = MyRealm.realm.where(LoginInfo::class.java).findFirst()
        val user = hashMapOf(
            "distance" to distance,
            "min" to min,
            "sec" to sec,
            "pace" to pace,
            "kcal" to kcal,
            "memo" to binding.memoEditText.text.toString(),
            "path" to pathList
        )
        val initDistance = hashMapOf("totalDistance" to 0.0)
        var totalDistance = 0.0
        val date = "${year}/${month}/${day}"
        val time = "$startTime ~ $endTime"
        val db = Firebase.firestore
        lateinit var findTotalDistance: Task<DocumentSnapshot>
        saveCoroutine.launch {

            val saveDayInfo =
                db.collection("userInfo").document(query.userId).collection(date).document(time)
            val saveMonthInfo =
                db.collection("userInfo").document(query.userId).collection(year.toString())
                    .document(month.toString())

            findTotalDistance =
                db.collection("userInfo").document(query.userId).collection(year.toString())
                    .document(month.toString()).get().addOnCompleteListener {

                        db.runTransaction {
                            totalDistance = it.get(saveMonthInfo)["totalDistance"] as Double


                            initDistance["totalDistance"] = totalDistance + distance

                            if (!findTotalDistance.result.exists()) {
                                it.set(saveMonthInfo, initDistance)
                                Log.d("checkFire", "not exist")
                            }
                            it.set(saveDayInfo, user)
                            it.update(saveMonthInfo, initDistance as Map<String, Any>)


                        }

                        Toast.makeText(this@RunningResult, "서버에 저장되었습니다", Toast.LENGTH_SHORT).show()

                    }.addOnFailureListener {
                        Toast.makeText(
                            this@RunningResult,
                            "서버에서 정보를 가져오는데 실패했습니다",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("checkFire", it.stackTraceToString())
                    }

//            db.runTransaction {
//                totalDistance = it.get(saveMonthInfo)["totalDistance"] as Double
//
//
//                initDistance["totalDistance"] = totalDistance + distance
//
//                if (!findTotalDistance.result.exists()) {
//                    it.set(saveMonthInfo, initDistance)
//                    Log.d("checkFire", "not exist")
//                }
//                it.set(saveDayInfo, user)
//                it.update(saveMonthInfo, initDistance as Map<String, Any>)
//
//
//            }.addOnSuccessListener {
//                Toast.makeText(this@RunningResult, "서버에 저장되었습니다", Toast.LENGTH_SHORT).show()
//            }.addOnFailureListener {
//                Toast.makeText(this@RunningResult, "서버 저장에 실패하였습니다", Toast.LENGTH_SHORT)
//                    .show()
//                Log.d("checkFire", it.stackTraceToString())
//            }


        }


//
//            db.collection("userInfo").document(query.userId).collection(date).document(time).set(user)
//                .addOnSuccessListener {
//                    Toast.makeText(this@RunningResult, "서버에 저장되었습니다", Toast.LENGTH_SHORT).show()
//                }.addOnCanceledListener {
//                    Toast.makeText(this@RunningResult, "서버 저장에 실패하였습니다", Toast.LENGTH_SHORT).show()
//                }
//
//            Log.d("checkFire", (totalDistance + distance).toString())
//            db.collection("userInfo").document(query.userId).collection(year.toString()).document(month.toString()).update("totalDistance", totalDistance + distance)
    }


    override fun onYesClicked() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        Snackbar.make(binding.root, "활동이 삭제 되었습니다", Snackbar.LENGTH_SHORT).show()
    }

    override fun onBackPressed() { // 뒤로가기 버튼 막기
        return
    }
}

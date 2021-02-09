package org.techtown.runningmate

import android.app.Dialog
import android.content.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.*
import org.techtown.runningmate.databinding.RunningfragmentBinding
import org.techtown.runningmate.databinding.StartrunningFinishdialogBinding
import java.time.LocalDateTime
import kotlin.math.round
import kotlin.math.roundToInt

class RunningFragment(
    private val startRunning: StartRunning
) : Fragment(), FinishDialog.AddOnDialogReturnListener {
    private lateinit var binding: RunningfragmentBinding
    private lateinit var dialogBinding : StartrunningFinishdialogBinding
    private var timerThread = CoroutineScope(Dispatchers.Main)
    private var distanceThread = CoroutineScope(Dispatchers.Main)
    private var min: Int = 0
    private var sec: Int = 0
    private var distance: Double = 0.0
    private var kcal : Double = 0.0
    private var pace : String = ""
    private var startTime : String = ""
    private var endTime : String = ""
    private var pathList = mutableListOf<LatLng>()
    private val serviceReceiver: ServiceReceiver = ServiceReceiver(this)
    private val intentFilter = IntentFilter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RunningfragmentBinding.inflate(inflater, container, false)
        dialogBinding = StartrunningFinishdialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        registerMyReceiver() // broadCastReceiver 등록
        startRunning() // service 실행
        setButton() // 각 버튼별 listener 설정
        setStartTime()

    }

    private fun setButton(){ // 버튼 설정

        binding.startButton.setOnClickListener { // 시작 버튼
            if (binding.startButton.isEnabled)
                binding.startButton.isEnabled = false // 시작 버튼 비활성화
            binding.pauseButton.isEnabled = true // 중지 버튼 활성화
            startRunning() // 서비스 실행
            startRunning.mBound = true
        }

        binding.pauseButton.setOnClickListener { // 중지 버튼
            super.onStop()
            if (startRunning.mBound) {
                startRunning.unbindService(startRunning.mConnection) // 서비스 unbind
                startRunning.stopService(startRunning.runningServiceIntent)
                binding.pauseButton.isEnabled = false // 중지 버튼 비활성화
                binding.startButton.isEnabled = true // 시작 버튼 활성화
                startRunning.mBound = false
            }

        }

        binding.finishButton.setOnClickListener { // 종료 버튼
            startRunning.unbindService(startRunning.mConnection) // 서비스 unbind
            startRunning.stopService(startRunning.runningServiceIntent)
            val dialog = FinishDialog(startRunning)
            dialog.startDialog()
            dialog.setOnDialogReturnListener(this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setStartTime(){
        val time: LocalDateTime = LocalDateTime.now()
        startTime = time.toString().slice(11..18)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setEndTime(){
        val time: LocalDateTime = LocalDateTime.now()
        endTime = time.toString().slice(11..18)
    }

    override fun onNoClicked() { // finishDialog 에서 '아니오' 를 눌렀을 경우 서비스 재실행
        startRunning() // 재실행
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onYesClicked() { // finishDialog 에서 '예' 를 눌렀을 경우 결과창 출력
        setEndTime() // 종료 시간 확인

        val intent = Intent(startRunning, RunningResult::class.java)
        intent.putExtra("min", min)
        intent.putExtra("sec", sec)
        intent.putExtra("distance", distance)
        intent.putExtra("kcal", kcal)
        intent.putExtra("pace", pace)
        intent.putExtra("startTime", startTime)
        intent.putExtra("endTime", endTime)

        val list = LatLngSet(pathList)
        intent.putExtra("pathList", list)
        startRunning.startActivity(intent)
    }



    fun setTimer(time: String?) { // UI에 타이머 업데이트
        min = time?.slice(0..1)?.toInt() ?: 0
        sec = time?.slice(3..4)?.toInt() ?: 0
        binding.RunningShowTimer.text = time
    }

    fun setDistance(distance: Double?) { // UI에 거리 정보 업데이트
        this.distance = distance!!
        binding.RunningShowKm.text = distance.toString()
    }
    fun setPace(){
        if(distance != 0.0) {
            val paceMin: Double = (min.toDouble() * 60 + sec.toDouble()) / 60
            val totalPaceMin: Int = (paceMin / distance).toInt()
            val totalPaceSec: Int = (paceMin % distance * 60).roundToInt()
            val changeTotalMin = if(totalPaceMin <10){
                "0$totalPaceMin"
            }
            else
                totalPaceMin
            val changeTotalSec = if(totalPaceSec < 10){
                "0$totalPaceSec"
            }
            else
                totalPaceSec
            pace = "${changeTotalMin}:${changeTotalSec}"
            binding.RunningShowFace.text = pace
        }
    }

    fun setCal(){
        kcal = distance * 62.5
        binding.RunningShowCal.text = kcal.toString()
    }

    fun setPathList(pathList : MutableList<LatLng>){
        this.pathList = pathList
    }

    private fun startRunning() { // 서비스 실행
        startRunning.startRunning(min, sec, distance)
    }

    override fun onStop() {
        Log.d("mapCycle", "Fragment_onStop")
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        timerThread.cancel()
        distanceThread.cancel()
        startRunning.unregisterReceiver(serviceReceiver) // broadCastReceiver 등록 해제

    }

    private fun registerMyReceiver(){ // broadCastReceiver 등록
        intentFilter.addAction("TimerService")
        intentFilter.addAction("DistanceService")
        intentFilter.addAction("PathListService")
        startRunning.registerReceiver(serviceReceiver, intentFilter)
    }

}

class ServiceReceiver(private val runningFragment: RunningFragment) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) { // 시간, 거리 정보를 업데이트 마다 수신받기

        if (intent != null) {
            Log.d("pathList", intent.action.toString())
            when (intent.action) { // 액션 종류에 따라 분류
                "TimerService" -> { // 타이머 액션 업데이터
                    val time = intent.getStringExtra("time")
                    runningFragment.setTimer(time)
                }

                "DistanceService" -> { // 거리 액션 업데이트
                    val distance = intent.getDoubleExtra("distance", 0.0)
                    runningFragment.setDistance(distance)
                    runningFragment.setPace()
                    runningFragment.setCal()
                }

                "PathListService" ->{ // 경로 정보 세팅
                    val pathList = intent.getParcelableExtra<LatLngSet>("pathList")
                    runningFragment.setPathList(pathList!!.pathList)
                    Log.d("pathList", pathList.pathList.toString())
                }
            }
        }

    }
}

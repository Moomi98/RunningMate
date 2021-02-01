package org.techtown.runningmate

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.*
import org.techtown.runningmate.databinding.RunningfragmentBinding

class RunningFragment(
    private val startRunning: StartRunning
) : Fragment() {
    private lateinit var binding: RunningfragmentBinding
    private var timerThread = CoroutineScope(Dispatchers.Main)
    private var distanceThread = CoroutineScope(Dispatchers.Main)

    private var min: Int = 0
    private var sec: Int = 0
    private var distance: Double = 0.0
    private val serviceReceiver: ServiceReceiver = ServiceReceiver(this)
    private val intentFilter = IntentFilter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RunningfragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        registerMyReceiver() // broadCastReceiver 등록
        startRunning() // service 실행
        setButton() // 각 버튼별 listener 설정
    }

    private fun setButton(){
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
                binding.pauseButton.isEnabled = false // 중지 버튼 비활성화
                binding.startButton.isEnabled = true // 시작 버튼 활성화
                startRunning.mBound = false
            }

        }
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
        startRunning.registerReceiver(serviceReceiver, intentFilter)
    }

}

class ServiceReceiver(private val runningFragment: RunningFragment) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) { // 시간, 거리 정보를 업데이트 마다 수신받기

        if (intent != null) {
            when (intent.action) { // 액션 종류에 따라 분류
                "TimerService" -> { // 타이머 액션 업데이터
                    val time = intent.getStringExtra("time")
                    runningFragment.setTimer(time)
                }

                "DistanceService" -> { // 거리 액션 업데이트
                    val distance = intent.getDoubleExtra("distance", 0.0)
                    runningFragment.setDistance(distance)
                }
            }

        }

    }
}

package org.techtown.runningmate

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import org.techtown.runningmate.databinding.RunningfragmentBinding

class RunningFragment(
    private val startRunning: StartRunning
) : Fragment() {
    private lateinit var binding: RunningfragmentBinding
    private var timerThread = CoroutineScope(Dispatchers.Main)
    private var distanceThread = CoroutineScope(Dispatchers.Main)
    private var timerJob: Job = launchTimer()
    private var distanceJob: Job = launchDistance()
    private var min: Int = 0
    private var sec: Int = 0
    private var distance: Double = 0.0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RunningfragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        startRunning()


        binding.startButton.setOnClickListener { // 시작 버튼
            if (binding.startButton.isEnabled)
                binding.startButton.isEnabled = false // 시작 버튼 비활성화
            binding.pauseButton.isEnabled = true // 중지 버튼 활성화

            startRunning()
            if(timerJob.isActive.not())
                timerJob = launchTimer()


            if(distanceJob.isActive.not())
                distanceJob = launchDistance()


            startRunning.mBound = true
        }

        binding.pauseButton.setOnClickListener { // 중지 버튼
            super.onStop()
            if (startRunning.mBound) {
                startRunning.unbindService(startRunning.mConnection)
                binding.pauseButton.isEnabled = false // 중지 버튼 비활성화
                binding.startButton.isEnabled = true // 시작 버튼 활성화
                startRunning.mBound = false
                timerJob.cancel()
                distanceJob.cancel()
            }

        }
    }


    private fun launchTimer() = timerThread.launch {
        while (true) {
            delay(200)
            min = startRunning.mService.getMin()
            sec = startRunning.mService.getSec()
            val changeSec: String = if (sec < 10) {
                "0$sec"
            } else
                sec.toString()

            val changeMin: String = if (min < 10) {
                "0$min"
            } else
                min.toString()

            val time = "$changeMin:$changeSec"

            binding.RunningShowTimer.text = time

        }
    }



    private  fun launchDistance() = distanceThread.launch {
        while (true) {
            delay(500)
            distance = startRunning.mService.getDistance()
            binding.RunningShowKm.text = distance.toString()
        }
    }





    private fun startRunning() {
        startRunning.startRunning(min, sec, distance)
    }


    override fun onDestroy() {
        super.onDestroy()
        timerThread.cancel()
        distanceThread.cancel()

    }

}
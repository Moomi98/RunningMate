package org.techtown.runningmate

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.*
import kotlin.concurrent.timer
import kotlin.math.*

import kotlin.math.roundToLong

class RunningService : Service() { // 백그라운드에서도 달리기 정보를 유지시켜주는 서비스

    private val binder = MyBinder()
    private var min: Int = 0
    private var sec: Int = 0
    private var distance : Double = 0.0
    var flag: Boolean = true
    private val timerThread = CoroutineScope(Dispatchers.Main)

    inner class MyBinder : Binder() {
        fun getService(): RunningService {
            return this@RunningService
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d("mapCycle", "onBind")
        min = intent.getIntExtra("min", 0)
        sec = intent.getIntExtra("sec", 0)
        distance = intent.getDoubleExtra("distance", 0.0)
        flag = true
        launchTimer()
        return binder
    }

    fun getMin(): Int {
        return min
    }

    fun getSec(): Int {
        return sec
    }

    fun setDistance(distance : Double){ // m 단위 거리를 km로 전환하여 저장
        val changeDistance = round(distance * 0.1)/100
        this.distance = changeDistance
    }

    fun getDistance() : Double{
        return distance
    }

    private fun launchTimer() { // 1초 간격으로 시간을 업데이트 하는 코루틴
        timerThread.launch {
            while (true) {
                delay(1000)
                Log.d("min", min.toString())
                Log.d("min", sec.toString())
                sec += 1
                if (sec == 60) {
                    min += 1
                    sec = 0
                }

            }

        }

    }

    override fun onUnbind(intent: Intent?): Boolean {
        timerThread.cancel()
        Log.d("mapCycle", "onUnbind")
        flag = false
        return false
    }
}
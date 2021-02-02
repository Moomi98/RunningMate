package org.techtown.runningmate

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.PathOverlay
import kotlinx.coroutines.*
import kotlin.math.*

class RunningService : Service() { // 백그라운드에서도 달리기 정보를 유지시켜주는 서비스
    private val CHANNEL_ID = "ForegroundServiceChannel";
    private val binder = MyBinder()
    private var min: Int = 0
    private var sec: Int = 0
    private var distance: Double = 0.0
    var flag: Boolean = true
    private val timerThread = CoroutineScope(Dispatchers.Main) // 타이머 코루틴을 위한 객체
    private val mapThread = CoroutineScope(Dispatchers.Main)
    private val timerIntent = Intent() // timer 정보를 전달하기 위한 intent 객체
    private val distanceIntent = Intent() // 거리 정보를 전달하기 위한 intent 객체
    private val pathList = mutableListOf<LatLng>() // 경로 저장 리스트
    private lateinit var path: PathOverlay
    private lateinit var naverMap: NaverMap // 네이버 맵 객체

    inner class MyBinder : Binder() {
        fun getService(): RunningService {
            return this@RunningService
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        createNotificationChannel()
        val notificationIntent = Intent(this, StartRunning::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Foreground Service")
                .setContentText("")
                .setSmallIcon(R.drawable.appicon).setContentIntent(pendingIntent)
                .build()
        startForeground(1, notification)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d("mapCycle", "onBind")
        min = intent.getIntExtra("min", 0)
        sec = intent.getIntExtra("sec", 0)
        distance = intent.getDoubleExtra("distance", 0.0)
        registerIntent() // intent action 설정
        launchTimer()

        return binder
    }

    private fun registerIntent() {
        timerIntent.action = "TimerService"
        distanceIntent.action = "DistanceService"
    }

    private fun setDistance(distance: Double) { // m 단위 거리를 km로 전환하여 저장
        this.distance += distance
        val changeDistance = round(this.distance * 0.1) / 100
        getDistance(changeDistance)
    }

    private fun launchTimer() = timerThread.launch {
        while (true) {
            delay(1000)
            sec += 1
            if (sec == 60) {
                min += 1
                sec = 0
            }

            val changeSec: String = if (sec < 10) {
                "0$sec"
            } else
                sec.toString()

            val changeMin: String = if (min < 10) {
                "0$min"
            } else
                min.toString()

            val time = "$changeMin:$changeSec"
            getTimer(time)
        }
    }

    private fun launchMap() = mapThread.launch {

        naverMap.addOnLocationChangeListener {
            if (flag) {
                if (pathList.size < 2) { // 최소 2개 이상의 좌표를 가지고 있어야 하므로 최초에는 2개를 저장
                    pathList.add(LatLng(it.latitude, it.longitude))
                    pathList.add(LatLng(it.latitude, it.longitude))
                } else
                    pathList.add(LatLng(it.latitude, it.longitude))
                drawPath() // 경로 그리기
                val changedistance = calDistance() // 이동 거리 구하기
                setDistance(changedistance)
                Log.d("mapCycle", distance.toString())
            }

        }

    }

    private fun getTimer(time: String) { // Timer 정보 전달
        timerIntent.putExtra("time", time)
        sendBroadcast(timerIntent)
    }

    private fun getDistance(distance: Double) { // 거리 정보 전달
        distanceIntent.putExtra("distance", distance)
        sendBroadcast(distanceIntent)
    }


    fun setNaverMapListener(naverMap: NaverMap, path: PathOverlay) { // 서비스에서 실행할 naverMap 설정

        this.naverMap = naverMap
        this.path = path



        launchMap()

    }

    private fun drawPath() { // 이동 경로 그리기
        Log.d("listSize", pathList.size.toString())
        path.coords = pathList
        path.color = Color.parseColor("#b5b2ff")
        if (pathList.size > 2) {
            path.map = naverMap
        }
    }

    private fun calDistance(): Double { // 경로 거리 저장
        return pathList[pathList.size - 1].distanceTo(pathList[pathList.size - 2])
    }

    private fun createNotificationChannel() { // notificationChannel 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager: NotificationManager = getSystemService(NotificationManager::class.java)!!
            manager.createNotificationChannel(
                serviceChannel
            )
        }
    }


    override fun onUnbind(intent: Intent?): Boolean {
        timerThread.cancel()
        mapThread.cancel()
        Log.d("mapCycle", "onUnbind")
        flag = false
        return false
    }
}
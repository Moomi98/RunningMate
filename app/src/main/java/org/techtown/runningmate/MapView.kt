package org.techtown.runningmate

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.UiSettings
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import org.techtown.runningmate.databinding.RunningMapviewBinding

class MapView(
    private val startRunning: StartRunning
) : Fragment(), OnMapReadyCallback { // 지도를 보여주는 뷰
    private lateinit var binding: RunningMapviewBinding
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap // 네이버 맵 객체
    private lateinit var mapView: MapView // xml mapView 객체
    private val path = PathOverlay() // 경로를 그리기 위해 필요한 객체
//    private val pathList = mutableListOf<LatLng>() // 경로 저장 리스트
//    private var distance: Double = 0.0 // 이동 거리


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RunningMapviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("mapCycle", "onCreate")
        mapView = binding.mapView

        mapView.getMapAsync(this@MapView)
        locationSource =
            FusedLocationSource(this@MapView, LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("mapCycle", "onCreate")

        super.onCreate(savedInstanceState)
        binding = RunningMapviewBinding.inflate(layoutInflater)
    }


    override fun onMapReady(mNaverMap: NaverMap) {
        Log.d("mapCycle", "onMapReady")

        naverMap = mNaverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true

        val uiSettings: UiSettings = naverMap.uiSettings
        uiSettings.isLocationButtonEnabled = true
        uiSettings.isZoomControlEnabled = true

        if(startRunning.mService.flag)
            startRunning.mService.setNaverMapListener(naverMap, path)
//        naverMap.addOnLocationChangeListener { // 좌표의 변화가 생겼을 때 발생하는 리스너
//
//            if (startRunning.mService.flag) {
//                if (pathList.size < 2) { // 최소 2개 이상의 좌표를 가지고 있어야 하므로 최초에는 2개를 저장
//                    pathList.add(LatLng(it.latitude, it.longitude))
//                    pathList.add(LatLng(it.latitude, it.longitude))
//                } else
//                    pathList.add(LatLng(it.latitude, it.longitude))
//                drawPath() // 경로 그리기
//                calDistance() // 이동 거리 구하기
//                Log.d("mapCycle", distance.toString())
//                startRunning.mService.setDistance(distance)
//            }
//
//        }
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onRequestPermissionsResult( // 위치 권한 요청 함수
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions,
                grantResults
            )
        ) {
            if (!locationSource.isActivated) { // 권한 거부됨
                Log.d("naver", "거부")
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

//    private fun drawPath() {
//        Log.d("listSize", pathList.size.toString())
//        path.coords = pathList
//        path.color = Color.parseColor("#b5b2ff")
//        if (pathList.size > 2) {
//            path.map = naverMap
//        }
//    }
//
//    private fun calDistance() { // 경로 거리 저장
//        distance += pathList[pathList.size - 1].distanceTo(pathList[pathList.size - 2])
//    }

    override fun onStart() {

        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {

        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        Log.d("mapCycle", "onStop")
//        startRunning.mService.setNaverMapListener()
        super.onStop()

    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        Log.d("mapCycle", "onDestroy")
        super.onDestroy()
        mapView.onDestroy()
    }


}

class ScrollAwareMapView : MapView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(event)
    }
}
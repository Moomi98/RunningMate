package org.techtown.runningmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.techtown.runningmate.databinding.MaincontentBinding
import java.util.*

class MainContent : Fragment() {
    private var _binding : MaincontentBinding? = null
    private val binding get() = _binding!!
    private var year : Int = 0
    private var month : Int = 0
    private var day : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MaincontentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setButton()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        showTodayRunning()
    }

    private fun setButton(){
        binding.startRunningButton.setOnClickListener {
            Log.d("btn", "clicked")
            val intent = Intent(activity, StartRunning::class.java)
            startActivity(intent)
        }
    }

    private fun getCurrentTime(){
        val cal = Calendar.getInstance() // 오늘 날짜 설정
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH) + 1
        day = cal.get(Calendar.DATE)
    }


    private fun showTodayRunning(){
        getCurrentTime() // 현재 날짜 정보 얻기
        val query: RunningList? = MyRealm.realm.where(RunningList::class.java) // 저장 시점의 날짜에 다른 정보가 있는지 확인
            .equalTo("date", "${year}/${month}/${day}").findFirst()
        Log.d("saveInfo",  "${year}/${month}/${day}")

        var todayDistance : Double = 0.0
        var todayMin : Int = 0
        var todaySec : Int = 0
        var todayKcal : Double = 0.0

        if(query != null){
            Log.d("saveInfo", "query is not Null")
            with(query.runningDayList){
                for(i in this){
                    todayDistance += i.distance
                    todayMin += i.min
                    todaySec += i.sec
                    todayKcal += i.kcal
                }
            }
            Log.d("saveInfo", "$todayDistance :$todayMin :$todaySec :$todayKcal")
        }

        val changeMin  : String = if(todayMin < 10){
            "0$todayMin"
        }
        else
            todayMin.toString()

        val changeSec : String = if(todaySec < 10){
            "0$todaySec"
        }
        else
            todaySec.toString()
        val showDistance = "$todayDistance km"
        val showKcal = "$todayKcal kcal"
        val showTime = "$changeMin:$changeSec"
        binding.mainContentShowTime.text = showTime
        binding.mainContentShowKm.text = showDistance
        binding.mainContentShowCal.text = showKcal
    }
}
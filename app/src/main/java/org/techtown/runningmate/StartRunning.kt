package org.techtown.runningmate

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout

import org.techtown.runningmate.databinding.StartRunningBinding

class StartRunning : AppCompatActivity() {
    private lateinit var binding: StartRunningBinding

    lateinit var runningServiceIntent: Intent
    lateinit var mService: RunningService
    var mBound: Boolean = false
    private var fragmentFlag : Boolean = false
    val mConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RunningService.MyBinder
            mService = binder.getService()
            mBound = true

            if(!fragmentFlag){
                settingFragment()
                fragmentFlag = true
            }
            Log.d("mapCycle", "onServiceConnected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
            Log.d("mapCycle", "onServiceDisconnected")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StartRunningBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startRunning(0, 0, 0.0)
    }


    fun startRunning(min : Int, sec : Int, distance : Double){
        runningServiceIntent = Intent(this, RunningService::class.java)
        runningServiceIntent.putExtra("min", min)
        runningServiceIntent.putExtra("sec", sec)
        runningServiceIntent.putExtra("distance", distance)
        bindService(runningServiceIntent, mConnection, Context.BIND_AUTO_CREATE)
    }

    private fun settingFragment(){
        binding.runningTabLayout.addTab(binding.runningTabLayout.newTab().setIcon(R.drawable.runningicon))
        binding.runningTabLayout.addTab(binding.runningTabLayout.newTab().setIcon(R.drawable.mapicon))

        val pagerAdapter = FragmentPagerAdapter(supportFragmentManager , 2)

        binding.runningViewPager.adapter = pagerAdapter
        binding.runningViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.runningTabLayout))

        binding.runningTabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{ // 탭과 화면이 같이 전환
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.runningViewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

   inner class FragmentPagerAdapter(fragmentManager: FragmentManager, private val tabCount : Int) : FragmentStatePagerAdapter(fragmentManager){
        override fun getCount(): Int {
            return tabCount
        }

        override fun getItem(position: Int): Fragment {

            Log.d("hello", "getItem")
            return when(position){
                0 -> RunningFragment(this@StartRunning)
                else -> MapView(this@StartRunning)
            }
        }
    }

}










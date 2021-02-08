package org.techtown.runningmate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import org.techtown.runningmate.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

       mainBinding.mainViewPager.adapter = PageAdapter(this) // viewPager 어댑터 설정

        val tabIconList = arrayListOf(R.drawable.running_shoe, R.drawable.competitionicon, R.drawable.profileicon)

        TabLayoutMediator(mainBinding.mainTabView, mainBinding.mainViewPager){ tab, position ->
            tab.setIcon(tabIconList[position])
        }.attach()
        mainBinding.mainViewPager.isUserInputEnabled = false

    }

}

class PageAdapter(
    fragment: FragmentActivity,
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MainContent()
            1 -> CompetitionView()
            else -> ProfileVIew()
        }
    }
}
package org.techtown.runningmate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import org.techtown.runningmate.databinding.CompetitionviewBinding
import org.techtown.runningmate.databinding.RankingbarBinding

class RankingView : Fragment() {
    private var _binding : CompetitionviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CompetitionviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButton()
        setRanking()
    }

    private fun setButton(){
        binding.generateUser.setOnClickListener {
            val create = CreateSampleUser()
            create.createUser()
        }
    }

    private fun setRanking(){
        val sortRanking = SortRanking()
        sortRanking.findAllUserDistanceInfo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


class RankingRecyclerView(private val inflater : LayoutInflater, private val binding : RankingbarBinding):
    RecyclerView.Adapter<RankingRecyclerView.ViewHolder>(){

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val rankNum = binding.rankNumber
        val rankerName = binding.rankerName
        val distance = binding.distance
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.rankingbar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 10
    }

}
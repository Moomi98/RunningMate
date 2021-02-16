package org.techtown.runningmate

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import org.techtown.runningmate.databinding.CompetitionviewBinding
import org.techtown.runningmate.databinding.RankingbarBinding

class RankingView(private val activity: Activity) : Fragment() {
    private var _binding: CompetitionviewBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore
    private val sortCoroutine = CoroutineScope(Dispatchers.IO)
    private val list = mutableListOf<HashMap<String, Any>>()
    private val viewCreatedCoroutine = CoroutineScope(Dispatchers.Main)
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

        sortRanking()
        setRecyclerView()



    }

    private fun setRecyclerView() {
        val adapter = RankingRecyclerView(LayoutInflater.from(activity))

        binding.rankingRecyclerView.adapter = adapter
        binding.rankingRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    private fun setButton() {
//        binding.refreshButton.setOnClickListener {
//
//            binding.rankingRecyclerView.adapter = RankingRecyclerView(
//                LayoutInflater.from(activity),
//                RankingbarBinding.inflate(activity.layoutInflater),
//                list
//            )
//            binding.rankingRecyclerView.layoutManager = LinearLayoutManager(activity)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sortRanking() {
        val job = sortCoroutine.launch {
            db.collection("CountryRanking").get().addOnSuccessListener {
                for (doc in it.documents) {
                    val map = doc.data
                    map?.set("rank", doc.id)
                    list.add(map as HashMap<String, Any>)
                }
                Log.d("recycle", "sort Finish")
                binding.rankingRecyclerView.layoutManager = LinearLayoutManager(activity)
            }.addOnFailureListener {
                Log.d("recycle", "fail")
            }
        }

    }

    inner class RankingRecyclerView(
        private val inflater: LayoutInflater,
    ) :
        RecyclerView.Adapter<RankingRecyclerView.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var rankNum: TextView = itemView.findViewById(R.id.rankNumber)
            val rankerName: TextView = itemView.findViewById(R.id.rankerName)
            val distance: TextView = itemView.findViewById(R.id.distance)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = inflater.inflate(R.layout.rankingbar, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            Log.d("recycle", rankingList[position]["rank"].toString())
//            Log.d("recycle", rankingList[position]["NickName"].toString())
//            Log.d("recycle", rankingList[position]["distance"].toString())

            if(list.size == 9){
                holder.rankNum.text = list[position]["rank"].toString()
                when(holder.rankNum.text){
                    "1" -> holder.rankNum.setTextColor(Color.rgb(255, 215, 0))
                    "2" -> holder.rankNum.setTextColor(Color.rgb(192, 192, 192))
                    "3" -> holder.rankNum.setTextColor(Color.rgb(164, 124, 109))
                    else -> holder.rankNum.setTextColor(Color.WHITE)
                }
                holder.rankerName.text = list[position]["NickName"].toString()
                holder.distance.text = list[position]["distance"].toString() + "km"
            }


            Log.d("recycle", "ranking : " + holder.rankNum.text.toString())
            Log.d("recycle", "name : " + holder.rankerName.text.toString())
            Log.d("recycle", "distance : " + holder.distance.text.toString())
        }

        override fun getItemCount(): Int {
            return 9
        }

    }
}

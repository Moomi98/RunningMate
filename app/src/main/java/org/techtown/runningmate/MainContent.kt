package org.techtown.runningmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.techtown.runningmate.databinding.MaincontentBinding

class MainContent : Fragment() {
    private var _binding : MaincontentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MaincontentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.startRunningButton.setOnClickListener {
            Log.d("btn", "clicked")
            val intent = Intent(activity, StartRunning::class.java)
            startActivity(intent)
        }
        super.onViewCreated(view, savedInstanceState)
    }
}
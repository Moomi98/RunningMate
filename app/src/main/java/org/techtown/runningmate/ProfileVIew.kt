package org.techtown.runningmate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.techtown.runningmate.databinding.ProfileviewBinding

class ProfileVIew : Fragment(){
    private lateinit var _binding : ProfileviewBinding
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileviewBinding.inflate(inflater, container, false)
        return binding.root
    }

}
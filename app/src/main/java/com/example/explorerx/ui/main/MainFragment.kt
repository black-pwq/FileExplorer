package com.example.explorerx.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.explorerx.databinding.FragmentMainBinding

private const val TAG = "MainFragment"

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.list.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.toList())
        }
        binding.tree.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.toTree())
        }
        binding.dup.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.toDup())
        }
        binding.picture.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.toPictures())
        }
        binding.movies.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.toMovies())
        }
        binding.music.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.toMusic())
        }
        binding.download.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.toDownload())
        }
    }
}
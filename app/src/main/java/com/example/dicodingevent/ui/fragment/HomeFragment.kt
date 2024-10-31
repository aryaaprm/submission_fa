package com.example.dicodingevent.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.databinding.FragmentHomeBinding
import com.example.dicodingevent.ui.adapter.EventAdapter
import com.example.dicodingevent.ui.model.EventViewModel
import com.example.dicodingevent.ui.model.EventViewModelFactory
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var eventViewModel: EventViewModel
    private lateinit var upcomingEventAdapter: EventAdapter
    private lateinit var finishedEventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = EventViewModelFactory.getInstance(requireContext())
        eventViewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]

        binding.rvUpcomingEvents.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        upcomingEventAdapter = EventAdapter(emptyList())
        binding.rvUpcomingEvents.adapter = upcomingEventAdapter

        binding.rvFinishedEvents.layoutManager = LinearLayoutManager(requireContext())
        finishedEventAdapter = EventAdapter(emptyList())
        binding.rvFinishedEvents.adapter = finishedEventAdapter

        binding.progressBar.visibility = View.VISIBLE


        eventViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            Handler(Looper.getMainLooper()).postDelayed({
                binding.progressBar.visibility = View.GONE
                upcomingEventAdapter.updateList(events.take(5))
            }, 500)
        }

        eventViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            Handler(Looper.getMainLooper()).postDelayed({
                binding.progressBar.visibility = View.GONE
                finishedEventAdapter.updateList(events.take(5))
            }, 500)
        }

        //error message handling
        eventViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                if (it.isNotEmpty()) {
                    Snackbar.make(binding.coordinatorLayout, it, Snackbar.LENGTH_LONG).show()
                    eventViewModel.clearErrorMessage()
                }
            }
        }
    }
}

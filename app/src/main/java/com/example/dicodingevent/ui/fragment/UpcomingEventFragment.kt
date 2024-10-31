package com.example.dicodingevent.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.data.entity.EventEntity
import com.example.dicodingevent.databinding.FragmentUpcomingEventBinding
import com.example.dicodingevent.ui.adapter.EventAdapter
import com.example.dicodingevent.ui.model.EventViewModel
import com.example.dicodingevent.ui.model.EventViewModelFactory
import com.google.android.material.snackbar.Snackbar

class UpcomingEventFragment : Fragment() {

    private var _binding: FragmentUpcomingEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventViewModel: EventViewModel
    private var originalEventList: List<EventEntity> = emptyList()
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //viewModel
        val factory = EventViewModelFactory.getInstance(requireContext())
        eventViewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]

        //recyclerView setup
        binding.rvUpcomingEvent.layoutManager = LinearLayoutManager(context)
        adapter = EventAdapter(originalEventList)
        binding.rvUpcomingEvent.adapter = adapter

        binding.progressBar.visibility = View.VISIBLE

        eventViewModel.upcomingEvents.observe(viewLifecycleOwner) { eventList ->
            Handler(Looper.getMainLooper()).postDelayed({
                binding.progressBar.visibility = View.GONE // Hide ProgressBar after delay
                originalEventList = eventList
                adapter.updateList(originalEventList)
                binding.rvUpcomingEvent.visibility = if (eventList.isNotEmpty()) View.VISIBLE else View.GONE
            }, 500) // Set delay to 500ms for ProgressBar visibility
        }

        //error handling
        eventViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                if (it.isNotEmpty()) {
                    Snackbar.make(binding.coordinatorLayout, it, Snackbar.LENGTH_LONG).show()
                    eventViewModel.clearErrorMessage()
                }
            }
        }

        setupSearchView()
    }

    private fun setupSearchView() {
        val searchHandler = Handler(Looper.getMainLooper())
        val searchDelay = 500L

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchHandler.removeCallbacksAndMessages(null)

                val searchRunnable = Runnable {
                    binding.progressBar.visibility = View.VISIBLE
                    if (newText.isNullOrEmpty()) {
                        eventViewModel.upcomingEvents.observe(viewLifecycleOwner) { eventList ->
                            binding.progressBar.visibility = View.GONE // Sembunyikan ProgressBar
                            adapter.updateList(eventList)
                        }
                    } else {
                        eventViewModel.searchEvents(active = 1, keyword = newText).observe(viewLifecycleOwner) { eventList ->
                            binding.progressBar.visibility = View.GONE // Sembunyikan ProgressBar
                            adapter.updateList(eventList)
                        }
                    }
                }
                searchHandler.postDelayed(searchRunnable, searchDelay)
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

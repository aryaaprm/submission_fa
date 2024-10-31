package com.example.dicodingevent.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.data.entity.EventEntity
import com.example.dicodingevent.databinding.FragmentFinishedEventBinding
import com.example.dicodingevent.ui.adapter.EventAdapter
import com.example.dicodingevent.ui.model.EventViewModel
import com.example.dicodingevent.ui.model.EventViewModelFactory
import com.google.android.material.snackbar.Snackbar

class FinishedEventFragment : Fragment() {

    private var _binding: FragmentFinishedEventBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory.getInstance(requireContext())
    }

    private var originalEventList: List<EventEntity> = emptyList()
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.progressBar.visibility = View.VISIBLE

        eventViewModel.finishedEvents.observe(viewLifecycleOwner) { eventList ->
            Handler(Looper.getMainLooper()).postDelayed({
                binding.progressBar.visibility = View.GONE // Hide ProgressBar after delay
                originalEventList = eventList
                adapter.updateList(originalEventList)
                binding.rvFinishedEvent.visibility = if (eventList.isNotEmpty()) View.VISIBLE else View.GONE
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

        setupSearchView()
    }

    private fun setupRecyclerView() {
        binding.rvFinishedEvent.layoutManager = LinearLayoutManager(context)
        adapter = EventAdapter(originalEventList)
        binding.rvFinishedEvent.adapter = adapter
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
                        eventViewModel.finishedEvents.observe(viewLifecycleOwner) { eventList ->
                            binding.progressBar.visibility = View.GONE // Sembunyikan ProgressBar
                            adapter.updateList(eventList)
                        }
                    } else {
                        eventViewModel.searchEvents(active = 0, keyword = newText).observe(viewLifecycleOwner) { eventList ->
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

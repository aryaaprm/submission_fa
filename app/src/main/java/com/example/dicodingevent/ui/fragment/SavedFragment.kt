package com.example.dicodingevent.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.databinding.FragmentSavedBinding
import com.example.dicodingevent.ui.adapter.EventAdapter
import com.example.dicodingevent.ui.model.EventViewModel
import com.example.dicodingevent.ui.model.EventViewModelFactory

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventViewModel: EventViewModel
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = EventViewModelFactory.getInstance(requireContext())
        eventViewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]

        eventViewModel.bookmarkedEvents.observe(viewLifecycleOwner) { bookmarkedEvents ->
            adapter.updateList(bookmarkedEvents)

            if (bookmarkedEvents.isEmpty()) {
                binding.tvNoData.visibility = View.VISIBLE
                binding.rvSavedEvent.visibility = View.GONE
            } else {
                binding.tvNoData.visibility = View.GONE
                binding.rvSavedEvent.visibility = View.VISIBLE
            }
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter(emptyList())  // Adapter dengan daftar kosong
        binding.rvSavedEvent.layoutManager = LinearLayoutManager(context)
        binding.rvSavedEvent.adapter = adapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

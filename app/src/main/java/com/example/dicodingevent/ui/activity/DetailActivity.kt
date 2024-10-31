package com.example.dicodingevent.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.dicodingevent.R
import com.example.dicodingevent.databinding.ActivityDetailBinding
import com.example.dicodingevent.ui.model.EventViewModel
import com.example.dicodingevent.ui.model.EventViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var eventViewModel: EventViewModel
    private var eventId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = EventViewModelFactory.getInstance(this)
        eventViewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]

        binding.progressBar.visibility = ProgressBar.VISIBLE

        eventId = intent.getIntExtra(EXTRA_EVENT_ID, -1)
        val mediaCover = intent.getStringExtra(EXTRA_MEDIA_COVER)
        val name = intent.getStringExtra(EXTRA_NAME)
        val ownerName = intent.getStringExtra(EXTRA_OWNER_NAME)
        val beginTime = intent.getStringExtra(EXTRA_BEGIN_TIME)
        val quota = intent.getIntExtra(EXTRA_QUOTA, 0)
        val registrants = intent.getIntExtra(EXTRA_REGISTRANTS, 0)
        val description = intent.getStringExtra(EXTRA_DESCRIPTION)
        val registerLink = intent.getStringExtra(EXTRA_REGISTER_LINK)

        loadEventDetails(mediaCover, name, ownerName, beginTime, quota, registrants, description, registerLink)

        eventViewModel.getBookmarkStatus(eventId).observe(this) { bookmark ->
            val isBookmarked = bookmark?.isBookmarked ?: false
            updateBookmarkIcon(isBookmarked)

            binding.bookmarkButton.setOnClickListener {
                val newStatus = !isBookmarked
                eventViewModel.toggleBookmark(eventId, newStatus)
                updateBookmarkIcon(newStatus)
            }
        }

        eventViewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                if (it.isNotEmpty()) {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                    eventViewModel.clearErrorMessage()
                }
            }
        }

        binding.progressBar.visibility = ProgressBar.GONE
    }

    private fun loadEventDetails(mediaCover: String?, name: String?, ownerName: String?, beginTime: String?, quota: Int, registrants: Int, description: String?, registerLink: String?) {
        Glide.with(this).load(mediaCover).into(binding.imgItemDetailPhoto)
        binding.tvItemName.text = name
        binding.tvOwnerName.text = ownerName
        binding.tvQuotaLeft.text = calculateQuotaLeft(quota, registrants, beginTime)
        binding.tvBeginTime.text = formatDate(beginTime)

        if (description != null) {
            binding.tvDescription.text = HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }

        binding.buttonRegister.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(registerLink))
            startActivity(intent)
        }
    }

    private fun updateBookmarkIcon(isBookmarked: Boolean) {
        val iconRes = if (isBookmarked) R.drawable.saved_icon else R.drawable.save_icon
        binding.bookmarkButton.setImageResource(iconRes)
    }

    private fun calculateQuotaLeft(quota: Int, registrants: Int, beginTime: String?): String {
        if (beginTime != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val eventDate = dateFormat.parse(beginTime)
            val currentDate = Date()

            if (eventDate != null && currentDate.after(eventDate)) {
                return "Closed"
            }
        }
        return if (quota - registrants > 0) {
            (quota - registrants).toString()
        } else {
            "Closed"
        }
    }

    private fun formatDate(dateString: String?): String {
        return if (dateString != null) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } else {
            "Date not available"
        }
    }

    companion object {
        const val EXTRA_MEDIA_COVER = "extra_media_cover"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_OWNER_NAME = "extra_owner_name"
        const val EXTRA_BEGIN_TIME = "extra_begin_time"
        const val EXTRA_QUOTA = "extra_quota"
        const val EXTRA_REGISTRANTS = "extra_registrants"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_REGISTER_LINK = "extra_register_link"
        const val EXTRA_EVENT_ID = "extra_event_id"
    }
}

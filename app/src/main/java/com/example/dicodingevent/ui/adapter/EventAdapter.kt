package com.example.dicodingevent.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dicodingevent.data.entity.EventEntity
import com.example.dicodingevent.databinding.ItemEventBinding
import com.example.dicodingevent.ui.activity.DetailActivity

class EventAdapter(
    private var eventList: List<EventEntity>,
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(eventList[position])
    }

    override fun getItemCount(): Int = eventList.size

    fun updateList(newList: List<EventEntity>) {
        val diffCallback = EventDiffCallback(eventList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        eventList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    inner class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventEntity) {
            binding.tvItemName.text = event.name
            binding.tvItemSummary.text = event.summary
            Glide.with(binding.root.context)
                .load(event.imageLogo)
                .into(binding.imgItemPhoto)

            itemView.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_MEDIA_COVER, event.imageLogo)
                    putExtra(DetailActivity.EXTRA_NAME, event.name)
                    putExtra(DetailActivity.EXTRA_OWNER_NAME, event.ownerName)
                    putExtra(DetailActivity.EXTRA_BEGIN_TIME, event.beginTime)
                    putExtra(DetailActivity.EXTRA_QUOTA, event.quota)
                    putExtra(DetailActivity.EXTRA_REGISTRANTS, event.registrants)
                    putExtra(DetailActivity.EXTRA_DESCRIPTION, event.description)
                    putExtra(DetailActivity.EXTRA_REGISTER_LINK, event.link)

                    putExtra(DetailActivity.EXTRA_EVENT_ID, event.id)
                }
                context.startActivity(intent)
            }
        }
    }

    class EventDiffCallback(
        private val oldList: List<EventEntity>,
        private val newList: List<EventEntity>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}

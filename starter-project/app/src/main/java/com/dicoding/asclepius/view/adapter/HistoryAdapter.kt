package com.dicoding.asclepius.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.databinding.HistoryItemBinding
import com.dicoding.asclepius.utils.formatToString

class HistoryAdapter(
    private val list: List<HistoryEntity>,
    private val navigateToResultPage: (uri: String, result: String, score: Float) -> Unit,
    private val onDeleteClick: (position: Int) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            previewImage.setImageURI(item.uri.toUri())
            tvResult.text = item.result
            val scorePercentage = item.score.coerceIn(0f, 1f)
            val progress = (scorePercentage * 100).toInt()
            progressScoreBar.progress = progress
            score.text = (item.score * 100).formatToString().plus(" %")
            tvCreatedAt.text = item.createdAt
        }

        holder.itemView.setOnClickListener {
            navigateToResultPage.invoke(item.uri, item.result, item.score)
        }
    }
}

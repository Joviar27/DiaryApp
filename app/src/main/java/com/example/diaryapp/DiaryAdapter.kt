package com.example.diaryapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.model.Diary

class DiaryAdapter(
    private val dataList: List<Diary>,
    private val onClickedListener : (Diary) -> Unit,
    ) : RecyclerView.Adapter<DiaryAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName = itemView.findViewById<TextView>(R.id.tv_item_name)
        val tvDate = itemView.findViewById<TextView>(R.id.tv_item_date)
        val tvContent = itemView.findViewById<TextView>(R.id.tv_item_content)

        fun bind(diary: Diary){
            tvName.text = diary.title
            tvDate.text = diary.date
            tvContent.text = diary.content

            itemView.setOnClickListener{
                onClickedListener(diary)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_diary, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
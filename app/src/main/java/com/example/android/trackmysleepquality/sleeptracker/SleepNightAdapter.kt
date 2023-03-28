package com.example.android.trackmysleepquality.sleeptracker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding
import kotlinx.android.synthetic.main.text_item_view.view.*

class SleepNightAdapter( val clickListener: SleepNightListener) :
    androidx.recyclerview.widget.ListAdapter<SleepNight ,SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item =getItem(position)
        holder.bind(item, clickListener)
        holder.bind(getItem(position)!!,clickListener)
    }

 class ViewHolder private constructor(val binding: ListItemSleepNightBinding) : RecyclerView.ViewHolder(binding.root){

     fun bind(item: SleepNight, clickListener: SleepNightListener) {
         //هنا بعمل binding للrecyclerView نفسها
         binding.sleep=item
         binding.clickListener=clickListener
         //وهنا بقوله هات الobject اللي اتعمل في ال binding
         binding.executePendingBindings()
     }

        companion object {
             fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class SleepNightDiffCallback :DiffUtil.ItemCallback<SleepNight>(){
    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
       return oldItem.nightId ==newItem.nightId
    }

    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem ==newItem
    }

}

class SleepNightListener(val clickListener: (sleepId: Long) ->Unit){
   fun onClick(night: SleepNight)=clickListener(night.nightId)
}
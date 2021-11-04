package com.example.gruppeinndeling

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class groupsAdapter(
    private var groups: MutableList<groupClass>
) : RecyclerView.Adapter<groupsAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var groupName: TextView
        var childRV: RecyclerView

        init {
            groupName = itemView.findViewById(R.id.textView)
            childRV = itemView.findViewById(R.id.membersView)
        }
    }

    fun addGroup(group: groupClass){
        groups.add(group)
        notifyItemInserted(groups.size - 1)
    }

    fun deleteGroup(name: String){
        groups.removeAll { temp ->
            temp.name == name
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.groups_recycler,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.groupName.text = groups[position].name

        val rvAdapter = RecyclerViewAdapter(mutableListOf())
        holder.childRV.adapter = rvAdapter
        holder.childRV.layoutManager = LinearLayoutManager(MainActivity())

        for (i in groups[position].list.indices) {
            rvAdapter.addName(groups[position].list[i])
        }
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    fun getItems(): MutableList<groupClass>{
        return groups.toMutableList()
    }

    fun reset(){
        groups = mutableListOf()
        notifyDataSetChanged()
    }
}

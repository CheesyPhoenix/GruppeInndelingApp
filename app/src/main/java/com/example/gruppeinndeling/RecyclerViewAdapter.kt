package com.example.gruppeinndeling

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class RecyclerViewAdapter(
    private var names: MutableList<String>
    ) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            // Define click listener for the ViewHolder's View.
            textView = view.findViewById(R.id.tvname)
        }
    }

    fun addName(name: String){
        names.add(name)
        notifyItemInserted(names.size - 1)
    }
    fun setItems(items: ArrayList<String>?){
        if (items != null) {
            names = items
            notifyDataSetChanged()
        }
    }

    fun deleteName(name: String){
        names.removeAll { temp ->
            temp == name
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recyclerview_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.textView.text = names[position]
    }

    override fun getItemCount(): Int {
        return names.size
    }

    fun getItems(): MutableList<String> {
        return names.toMutableList()
    }

    fun reset(){
        names = mutableListOf()
        notifyDataSetChanged()
    }
}
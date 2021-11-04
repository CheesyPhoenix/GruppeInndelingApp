package com.example.gruppeinndeling

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.ArrayList

class PresetsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var presets: MutableList<groupClass>
    var selected = ""

    lateinit var rvAdapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presets)

        val spinner: Spinner = findViewById(R.id.spinner)
        spinner.onItemSelectedListener = this

        presets = loadPresets()
        spinnerAdaptation()


        rvAdapter = RecyclerViewAdapter(mutableListOf())

        val rv = findViewById<View>(R.id.presetsRecView) as RecyclerView

        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(this)
    }

    fun spinnerAdaptation(){
        val spinner: Spinner = findViewById(R.id.spinner)

        val names = mutableListOf<String>()

        for (i in 0 until presets.size){
            names.add(presets[i].name)
        }

        val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, names)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    fun loadPresets(): MutableList<groupClass>{
        val sharedPref = getSharedPreferences("tk.cheesyphoenix.groups.presets", Context.MODE_PRIVATE) ?: return mutableListOf()
        if (sharedPref.all.isEmpty()) return mutableListOf()
        val data = Gson().fromJson<MutableList<groupClass>>(sharedPref.all["tk.cheesyphoenix.groups.presets"].toString(), object : TypeToken<MutableList<groupClass>>() {}.type)
        return data
    }

    fun deletePreset(view: View?){
        val newList = loadPresets().filter { i -> i.name != selected }
        val json = Gson().toJson(newList)

        val sharedPref = getSharedPreferences("tk.cheesyphoenix.groups.presets", Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("tk.cheesyphoenix.groups.presets", json)
            apply()
        }

        presets = loadPresets()
        spinnerAdaptation()
    }

    fun selectPreset(view: View?){
        val intent = Intent(this, MainActivity::class.java).apply {
            putStringArrayListExtra("preset",
                loadPresets().find { i -> i.name == selected }?.list as ArrayList<String>?
            )
        }
        startActivity(intent)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            selected = parent.getItemAtPosition(position).toString()

            rvAdapter.setItems(loadPresets().find { name -> name.name == selected }?.list as ArrayList<String>)
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        return
    }
}
package com.example.gruppeinndeling

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.Exception
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.random.Random

class MainActivity : AppCompatActivity()  {
    lateinit var rvAdapter: RecyclerViewAdapter
    lateinit var gAdapter: groupsAdapter

    lateinit var presets: MutableList<groupClass>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvAdapter = RecyclerViewAdapter(mutableListOf())
        gAdapter = groupsAdapter(mutableListOf())

        val rv = findViewById<View>(R.id.RecyclerView) as RecyclerView

        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(this)

        val gv = findViewById<View>(R.id.groupsRV) as RecyclerView

        gv.adapter = gAdapter
        gv.layoutManager = LinearLayoutManager(this)

        presets = loadPresets()

        rvAdapter.setItems(intent.getStringArrayListExtra("preset"))
    }

    var shouldReset = false

    fun submitName(view: View){
        if ((findViewById<View>(R.id.textInput) as EditText).text.toString()
                .filter { !it.isWhitespace() }.isEmpty()
        ) return
        rvAdapter.addName((findViewById<View>(R.id.textInput) as EditText).text.toString())
    }

    fun groupify(view: View){
        try {
            gAdapter.reset()
            val names = rvAdapter.getItems()

            fun namesCount(): Int {
                var count = 0
                for (i in names.indices) {
                    count++
                }
                return count
            }
            val totalNames = namesCount()

            if ((findViewById<View>(R.id.numberPerGroup) as EditText).text.toString()
                    .filter { !it.isWhitespace() }.isEmpty()
            ) return

            val groupCount =  (totalNames / (findViewById<View>(R.id.numberPerGroup) as EditText).text.toString().toInt())
            val namesPerGroup = (findViewById<View>(R.id.numberPerGroup) as EditText).text.toString().toInt()

            val groups = mutableListOf<groupClass>()
            if (namesCount() == 0) return
            for (i in 0 until floor(groupCount.toDouble()).toInt()){
                groups.add(groupClass("Group $i", mutableListOf()))
                for (x in 0 until namesPerGroup){
                    val randi = Random.nextInt(0, namesCount())
                    groups[i].list.add(names[randi])
                    names.removeAt(randi)
                }
            }
            if (namesCount() != 0){
                groups.add(groupClass("Group ${ceil(groupCount.toDouble()).toInt()}", mutableListOf()))
                for (i in 0 until totalNames % (findViewById<View>(R.id.numberPerGroup) as EditText).text.toString().toInt()){
                    val randi = Random.nextInt(0, namesCount())
                    groups[ceil(groupCount.toDouble()).toInt()].list.add(names[randi])
                    names.removeAt(randi)
                }
            }

            for (i in groups.indices){
                gAdapter.addGroup(groups[i])
            }

            shouldReset = true
        }catch (e: Exception){
            println(e)
        }
    }

    fun copyToClipboard(view: View){
        val items = gAdapter.getItems()

        var formattedString = ""

        for (i in 0 until items.size){
            formattedString += items[i].name + ": " + items[i].list + "\n"
        }

        val clipboardManager =  getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Groups", formattedString)
        clipboardManager.setPrimaryClip(clipData)
    }


    fun savePreset(view: View){
        val temp = groupClass((findViewById<View>(R.id.groupNameInput) as EditText).text.toString(), rvAdapter.getItems())
        presets = loadPresets()
        if (presets.find { i -> i.name == temp.name } == null){
            presets.add(temp)
        }
        else{
            presets[presets.indexOf(presets.find { i -> i.name == temp.name })] = temp
        }

        val json = Gson().toJson(presets)

        val sharedPref = getSharedPreferences("tk.cheesyphoenix.groups.presets", Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("tk.cheesyphoenix.groups.presets", json)
            apply()
        }
    }

    fun loadPresets(): MutableList<groupClass>{
        val sharedPref = getSharedPreferences("tk.cheesyphoenix.groups.presets", Context.MODE_PRIVATE) ?: return mutableListOf()
        if (sharedPref.all.isEmpty()) return mutableListOf()
        val data = Gson().fromJson<MutableList<groupClass>>(sharedPref.all["tk.cheesyphoenix.groups.presets"].toString(), object : TypeToken<MutableList<groupClass>>() {}.type)
        return data
    }

    fun presets(view: View){
        val intent = Intent(this, PresetsActivity::class.java)
        startActivity(intent)
    }

    fun clear(view: View){
        rvAdapter.reset()
    }
}




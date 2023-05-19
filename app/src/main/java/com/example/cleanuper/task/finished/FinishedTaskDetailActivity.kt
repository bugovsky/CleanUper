package com.example.cleanuper.task.finished

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import com.example.cleanuper.databinding.ActivityFinishedTaskDetailBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap


class FinishedTaskDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFinishedTaskDetailBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var taskRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishedTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.backButton.setOnClickListener {
            onBackPressed()
            finish()
        }
        val intent = intent
        if (intent.hasExtra("title") && intent.hasExtra("description")) {
            val title = intent.getStringExtra("title")
            val description = intent.getStringExtra("description")
            binding.title.text = Editable.Factory.getInstance().newEditable(title)
            binding.description.text = Editable.Factory.getInstance().newEditable(description)
        }
        val daySectionToReps: LinkedHashMap<String, Float> =
            getDaysSectionsToReps(intent.getSerializableExtra("completes") as ArrayList<Long>)
        val list: ArrayList<BarEntry> = ArrayList()
        var step = 0f
        var maxRep = -1f
        var minRep = 200f
        for (sectionReps in daySectionToReps) {
            list.add(BarEntry(step, sectionReps.value))
            step += 2f
            if (sectionReps.value > maxRep) {
                maxRep = sectionReps.value
            }
            if (sectionReps.value < minRep) {
                minRep = sectionReps.value
            }
        }
        var best = arrayListOf<String>()
        var worst = arrayListOf<String>()
        for (sectionReps in daySectionToReps) {
            if (sectionReps.value == maxRep) {
                best.add(sectionReps.key.lowercase())
            }
            if (sectionReps.value == minRep) {
                worst.add(sectionReps.key.lowercase())
            }
        }
        binding.report.text = "Рекомендуемое время суток для выполнения задачи: ${best.joinToString()}\n" +
                "Худшее время суток для выполнения задачи: ${worst.joinToString()}"
        val barChart = binding.resultChart
        val barDataSet = BarDataSet(list, "Время суток")
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255)
        barDataSet.valueTextColor = Color.BLACK
        val barData = BarData(barDataSet)
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        val legendEntries = ArrayList<LegendEntry>()
        var i = 0
        for (sectionReps in daySectionToReps) {
            val label = sectionReps.key
            val color = barDataSet.getColor(i)
            val legendEntry = LegendEntry()
            legendEntry.label = label
            legendEntry.formColor = color
            legendEntries.add(legendEntry)
            ++i
        }
        val legend = barChart.legend
        legend.setCustom(legendEntries)
        legend.form = Legend.LegendForm.SQUARE
        legend.formSize = 8f
        legend.formLineWidth = 1f
        legend.textColor = Color.BLACK
        legend.textSize = 12f
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

        barChart.setFitBars(true)
        barChart.data = barData
        barChart.description.text = ""
        barChart.animateY(2000)
    }

    private fun getDaysSectionsToReps(times: ArrayList<Long>): LinkedHashMap<String, Float> {
        val daysSectionsToReps = linkedMapOf<String, Float>()
        for (i in times.indices) {
            val daySection = getDaySection(times[i])
            daysSectionsToReps.putIfAbsent(daySection, 0.0F)
            daysSectionsToReps[daySection] =
                daysSectionsToReps.getOrDefault(daySection, -1.0F) + 1.0F
        }
        return daysSectionsToReps
    }

    private fun getDaySection(time: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        val timePeriod = when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..4 -> "Ночь"
            in 5..11 -> "Утро"
            in 12..17 -> "День"
            in 18..22 -> "Вечер"
            else -> "Ночь"
        }
        println(timePeriod)
        return timePeriod
    }
}
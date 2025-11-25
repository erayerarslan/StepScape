package com.erayerarslan.stepscape.ui.home

import android.graphics.Color
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.erayerarslan.stepscape.R
import com.erayerarslan.stepscape.databinding.FragmentHomeBinding
import com.erayerarslan.stepscape.data.health.HealthConnectManager
import com.erayerarslan.stepscape.data.local.entity.StepLog
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var healthConnectManager: HealthConnectManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
        requestHealthConnectPermissions()
        
        viewModel.refreshData()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.todaySteps.collect { steps ->
                    updateProgressBar(steps.toInt(), viewModel.goalSteps.value)
                    binding.tvCurrentSteps.text = steps.toString()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.goalSteps.collect { goal ->
                    binding.tvGoalSteps.text = "/$goal"
                    updateProgressBar(viewModel.todaySteps.value.toInt(), goal)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.motivationalMessage.collect { message ->
                    binding.tvMotivationalMessage.text = message
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weeklySteps.collect { logs ->
                    updateStats(logs)
                    updateGraph(logs)
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnLogs.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_logsFragment)
        }

        binding.btnDay.setOnClickListener {
            selectTab(binding.btnDay)
            viewModel.loadDataForPeriod("day")
        }

        binding.btnWeek.setOnClickListener {
            selectTab(binding.btnWeek)
            viewModel.loadDataForPeriod("week")
        }

        binding.btnMonth.setOnClickListener {
            selectTab(binding.btnMonth)
            viewModel.loadDataForPeriod("month")
        }

        binding.btn6Month.setOnClickListener {
            selectTab(binding.btn6Month)
            viewModel.loadDataForPeriod("6month")
        }

        binding.btnYear.setOnClickListener {
            selectTab(binding.btnYear)
            viewModel.loadDataForPeriod("year")
        }
        
        setupGraph()
    }

    private fun selectTab(selectedButton: com.google.android.material.button.MaterialButton) {
        val buttons = listOf(
            binding.btnDay,
            binding.btnWeek,
            binding.btnMonth,
            binding.btn6Month,
            binding.btnYear
        )
        buttons.forEach { button ->
            button.backgroundTintList = if (button == selectedButton) {
                resources.getColorStateList(R.color.tab_selected, null)
            } else {
                resources.getColorStateList(R.color.tab_unselected, null)
            }
        }
    }

    private fun updateProgressBar(currentSteps: Int, goalSteps: Int) {
        binding.progressBar.max = goalSteps
        binding.progressBar.progress = currentSteps.coerceAtMost(goalSteps)
    }

    private fun updateStats(logs: List<com.erayerarslan.stepscape.data.local.entity.StepLog>) {
        if (logs.isNotEmpty()) {
            val totalSteps = logs.sumOf { it.steps }
            binding.tvStepsCount.text = "$totalSteps Steps"

            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val startDate = Date(logs.first().date)
            val endDate = Date(logs.last().date)
            binding.tvDateRange.text = "${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}"
        }
    }

    private fun setupGraph() {
        val lineChart = binding.lineChart
        
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.setDragEnabled(true)
        lineChart.setScaleEnabled(false)
        lineChart.setPinchZoom(false)
        lineChart.setDrawGridBackground(false)
        lineChart.legend.isEnabled = false
        
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(true)
        xAxis.gridColor = Color.parseColor("#E0E0E0")
        xAxis.textColor = Color.parseColor("#757575")
        xAxis.textSize = 10f
        xAxis.setDrawAxisLine(false)
        
        val leftAxis = lineChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.gridColor = Color.parseColor("#E0E0E0")
        leftAxis.textColor = Color.parseColor("#757575")
        leftAxis.textSize = 10f
        leftAxis.setDrawAxisLine(false)
        leftAxis.axisMinimum = 0f
        
        lineChart.axisRight.isEnabled = false
        
        lineChart.setBackgroundColor(Color.WHITE)
    }
    
    private fun updateGraph(logs: List<StepLog>) {
        if (logs.isEmpty()) {
            binding.lineChart.clear()
            binding.lineChart.invalidate()
            return
        }
        
        val entries = mutableListOf<Entry>()
        val sortedLogs = logs.sortedBy { it.date }

        sortedLogs.forEachIndexed { index, log ->
            val stepsPerHour = log.steps / 24f
            for (hour in 0 until 24) {
                entries.add(Entry(hour.toFloat(), stepsPerHour))
            }
        }
        
        val latestLog = sortedLogs.lastOrNull()
        if (latestLog != null && sortedLogs.size == 1) {
            val stepsPerHour = latestLog.steps / 24f
            entries.clear()
            for (hour in 0 until 24) {
                val multiplier = when {
                    hour in 6..10 -> 1.5f // Morning
                    hour in 11..15 -> 1.8f // Midday
                    hour in 16..20 -> 1.6f // Afternoon
                    hour in 21..23 -> 0.8f // Evening
                    else -> 0.3f // Night
                }
                entries.add(Entry(hour.toFloat(), stepsPerHour * multiplier))
            }
        }
        
        val dataSet = LineDataSet(entries, "Steps")
        dataSet.color = Color.parseColor("#2196F3") // Blue color
        dataSet.lineWidth = 3f
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.cubicIntensity = 0.2f
        dataSet.setDrawFilled(true)
        dataSet.fillColor = Color.parseColor("#80C8E6C9")
        dataSet.fillAlpha = 100
        
        val lineData = LineData(dataSet)
        binding.lineChart.data = lineData
        
        val xAxis = binding.lineChart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val hour = value.toInt()
                return when (hour) {
                    0 -> "00"
                    3 -> "3"
                    10 -> "10"
                    17 -> "17"
                    24 -> "24"
                    else -> ""
                }
            }
        }
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 24f
        xAxis.setLabelCount(5, false) // Show 5 labels: 00, 3, 10, 17, 24
        
        val leftAxis = binding.lineChart.axisLeft
        leftAxis.axisMinimum = 0f
        val maxValue = entries.maxOfOrNull { it.y } ?: 100f
        leftAxis.axisMaximum = (maxValue * 1.2f).coerceAtLeast(100f) // Add 20% padding
        leftAxis.setLabelCount(4, true) // Show 4 labels
        
        binding.lineChart.invalidate()
    }
    
    private fun requestHealthConnectPermissions() {
        if (healthConnectManager.isAvailable) {
            viewLifecycleOwner.lifecycleScope.launch {
                if (!healthConnectManager.hasAllPermissions()) {
                    android.util.Log.d("HomeFragment", "Health Connect permissions not granted")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

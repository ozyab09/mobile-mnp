package com.example.mnpdetector.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mnpdetector.R
import com.example.mnpdetector.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    
    private val viewModel: SettingsViewModel by viewModels()
    
    private lateinit var tvDbStatus: TextView
    private lateinit var etSourceUrl: EditText
    private lateinit var btnUpdateNow: Button
    private lateinit var switchAutoUpdate: Switch
    private lateinit var spinnerUpdateFrequency: Spinner
    private lateinit var cbWifiOnly: CheckBox
    private lateinit var cbChargingOnly: CheckBox
    private lateinit var etOverlayTimeout: EditText
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        initViews()
        setupObservers()
        setupClickListeners()
        setupSpinner()
    }
    
    private fun initViews() {
        tvDbStatus = findViewById(R.id.tvDbStatus)
        etSourceUrl = findViewById(R.id.etSourceUrl)
        btnUpdateNow = findViewById(R.id.btnUpdateNow)
        switchAutoUpdate = findViewById(R.id.switchAutoUpdate)
        spinnerUpdateFrequency = findViewById(R.id.spinnerUpdateFrequency)
        cbWifiOnly = findViewById(R.id.cbWifiOnly)
        cbChargingOnly = findViewById(R.id.cbChargingOnly)
        etOverlayTimeout = findViewById(R.id.etOverlayTimeout)
    }
    
    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                etSourceUrl.setText(state.sourceUrl)
                switchAutoUpdate.isChecked = state.isAutoUpdateEnabled
                cbWifiOnly.isChecked = state.wifiOnly
                cbChargingOnly.isChecked = state.chargingOnly
                etOverlayTimeout.setText(state.overlayTimeout)
                
                // Update database status
                tvDbStatus.text = "Status: Updated ${state.lastUpdate}, ${state.recordCount} records"
                
                // Set spinner selection
                val frequencyOptions = viewModel.getUpdateFrequencyOptions()
                val position = frequencyOptions.indexOf(state.updateFrequency)
                if (position >= 0) {
                    spinnerUpdateFrequency.setSelection(position)
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        etSourceUrl.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val url = etSourceUrl.text.toString().trim()
                if (url.isNotEmpty()) {
                    viewModel.updateSourceUrl(url)
                }
            }
        }
        
        switchAutoUpdate.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleAutoUpdate(isChecked)
        }
        
        cbWifiOnly.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setWifiOnly(isChecked)
        }
        
        cbChargingOnly.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setChargingOnly(isChecked)
        }
        
        etOverlayTimeout.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val timeout = etOverlayTimeout.text.toString().trim()
                if (timeout.isNotEmpty()) {
                    viewModel.setOverlayTimeout(timeout)
                }
            }
        }
        
        btnUpdateNow.setOnClickListener {
            // This would trigger a manual update
            Toast.makeText(this, "Manual update would be triggered here", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupSpinner() {
        val frequencyOptions = viewModel.getUpdateFrequencyOptions()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frequencyOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateFrequency.adapter = adapter
        
        spinnerUpdateFrequency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = parent.getItemAtPosition(position).toString()
                viewModel.setUpdateFrequency(selected)
            }
            
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}
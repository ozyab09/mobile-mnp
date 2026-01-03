package ru.mobile.mnp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import ru.mobile.mnp.R
import ru.mobile.mnp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private val viewModel: MainViewModel by viewModels()
    
    private lateinit var etPhoneNumber: EditText
    private lateinit var btnCheck: Button
    private lateinit var resultContainer: LinearLayout
    private lateinit var tvResult: TextView
    private lateinit var tvLastUpdate: TextView
    private lateinit var tvRecordCount: TextView
    private lateinit var fabSettings: com.google.android.material.floatingactionbutton.FloatingActionButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupObservers()
        setupClickListeners()
    }
    
    private fun initViews() {
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        btnCheck = findViewById(R.id.btnCheck)
        resultContainer = findViewById(R.id.resultContainer)
        tvResult = findViewById(R.id.tvResult)
        tvLastUpdate = findViewById(R.id.tvLastUpdate)
        tvRecordCount = findViewById(R.id.tvRecordCount)
        fabSettings = findViewById(R.id.fabSettings)
    }
    
    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                if (state.isLoading) {
                    btnCheck.text = "Checking..."
                    btnCheck.isEnabled = false
                } else {
                    btnCheck.text = getString(R.string.check_number)
                    btnCheck.isEnabled = true
                }
                
                state.result?.let { result ->
                    tvResult.text = getString(R.string.result_ported, result.operator)
                    resultContainer.visibility = android.view.View.VISIBLE
                } ?: run {
                    if (!state.isLoading && state.result == null) {
                        tvResult.text = getString(R.string.result_not_ported)
                        resultContainer.visibility = android.view.View.VISIBLE
                    }
                }
                
                if (state.lastUpdate.isNotEmpty()) {
                    tvLastUpdate.text = getString(R.string.last_update, state.lastUpdate)
                }
                
                tvRecordCount.text = getString(R.string.number_of_records, state.recordCount)
                
                state.error?.let { error ->
                    Toast.makeText(this@MainActivity, error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        btnCheck.setOnClickListener {
            val phoneNumber = etPhoneNumber.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                viewModel.checkNumber(phoneNumber)
            } else {
                Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show()
            }
        }
        
        fabSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
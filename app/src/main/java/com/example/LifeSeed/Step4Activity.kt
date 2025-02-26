package com.example.LifeSeed

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.LifeSeed.databinding.ActivityStep4Binding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions

class Step4Activity : AppCompatActivity() {
    private lateinit var binding: ActivityStep4Binding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityStep4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Back Button
        binding.backButton.setOnClickListener {
            finish()
        }

        // Set up Specific Treatment Type Spinner
        val treatmentTypes = arrayOf("IVF", "ICSI", "IUI", "Frozen Embryo Transfer", "Other")
        val treatmentAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, treatmentTypes)
        treatmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.treatmentTypeSpinner.adapter = treatmentAdapter

        // Set up Egg Source Spinner
        val eggSources = arrayOf("Own Eggs", "Donor Eggs")
        val eggSourceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, eggSources)
        eggSourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.eggSourceSpinner.adapter = eggSourceAdapter

        // Set up Sperm Source Spinner
        val spermSources = arrayOf("Own Sperm", "Donor Sperm")
        val spermSourceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spermSources)
        spermSourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spermSourceSpinner.adapter = spermSourceAdapter

        // Finish Button to Save Data and Navigate to ResultsActivity
        binding.nextButton.setOnClickListener {
            saveTreatmentDetailsToFirestore()
        }
    }

    private fun saveTreatmentDetailsToFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        val treatmentType = binding.treatmentTypeSpinner.selectedItem.toString()
        val electiveSingleEmbryoTransfer = if (binding.singleEmbryoToggle.isChecked) "Yes" else "No"
        val eggSource = binding.eggSourceSpinner.selectedItem.toString()
        val spermSource = binding.spermSourceSpinner.selectedItem.toString()

        // Validate Inputs
        if (treatmentType.isEmpty() || eggSource.isEmpty() || spermSource.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        // Calculate final result (Yes/No)
        val finalResult = calculateResult(treatmentType, electiveSingleEmbryoTransfer, eggSource, spermSource)

        // Data to Save
        val treatmentData = mapOf(
            "treatmentType" to treatmentType,
            "electiveSingleEmbryoTransfer" to electiveSingleEmbryoTransfer,
            "eggSource" to eggSource,
            "spermSource" to spermSource,
            "finalResult" to finalResult // Save the calculated result
        )

        // Merge Data into User Document
        db.collection("User").document(userId)
            .set(treatmentData, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Treatment details saved!", Toast.LENGTH_SHORT).show()
                goToResultsPage(finalResult)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun calculateResult(treatmentType: String, electiveTransfer: String, eggSource: String, spermSource: String): String {
        // Apply your logic to determine the result (YES/NO)
        return if (treatmentType == "IVF" && electiveTransfer == "Yes") "Yes" else "No"
    }

    private fun goToResultsPage(finalResult: String) {
        val intent = Intent(this, ResultsActivity::class.java)
        intent.putExtra("finalResult", finalResult) // Pass the result
        startActivity(intent)
    }
}

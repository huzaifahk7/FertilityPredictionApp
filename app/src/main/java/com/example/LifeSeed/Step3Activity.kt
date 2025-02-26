package com.example.LifeSeed

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.LifeSeed.databinding.ActivityStep3Binding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions

class Step3Activity : AppCompatActivity() {
    private lateinit var binding: ActivityStep3Binding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityStep3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Back Button
        binding.backButton.setOnClickListener {
            finish()
        }

        // Set up Cause of Infertility Spinner
        val infertilityCauses = arrayOf("Unexplained", "Male Factor", "Female Factor", "Combined Factors", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, infertilityCauses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.causeInfertilitySpinner.adapter = adapter

        // Next Button Click Listener
        binding.nextButton.setOnClickListener {
            saveInfertilityDataToFirestore()
        }
    }

    private fun saveInfertilityDataToFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        val infertilityCause = binding.causeInfertilitySpinner.selectedItem.toString()
        val ivfCycles = binding.ivfCyclesEditText.text.toString().toIntOrNull() ?: 0
        val diCycles = binding.diCyclesEditText.text.toString().toIntOrNull() ?: 0
        val previousPregnancies = binding.previousPregnanciesEditText.text.toString().toIntOrNull() ?: 0

        // Data to Save
        val infertilityData = mapOf(
            "infertilityCause" to infertilityCause,
            "ivfCycles" to ivfCycles,
            "diCycles" to diCycles,
            "previousPregnancies" to previousPregnancies
        )

        // Merge Data into User Document
        db.collection("User").document(userId)
            .set(infertilityData, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Infertility history saved!", Toast.LENGTH_SHORT).show()
                goToNextStep()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun goToNextStep() {
        val intent = Intent(this, Step4Activity::class.java)
        startActivity(intent)
    }
}

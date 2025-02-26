package com.example.LifeSeed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.LifeSeed.databinding.ActivityResultsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ResultsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultsBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore and Firebase Auth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Retrieve and Display Result
        retrieveResultFromFirestore()

        // Back Button Listener
        binding.backButton.setOnClickListener {
            finish() // Go back to the previous screen
        }

        // Home Button Listener
        binding.homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun retrieveResultFromFirestore() {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            binding.resultValue.text = "Unknown"
            return
        }

        // 🔥 Fetch the result from Firestore
        db.collection("User")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val successRate = document.getDouble("successRate") ?: 0.0

                    // Display success rate as a percentage
                    binding.resultValue.text = "Success Rate: ${"%.2f".format(successRate)}%"

                    // Recommend clinics based on success rate
                    recommendClinics(successRate)
                } else {
                    binding.resultValue.text = "No Result Available"
                    Toast.makeText(this, "No result found for this user.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                binding.resultValue.text = "Error"
                Toast.makeText(this, "Error retrieving result: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("FirestoreError", "Error fetching result: ${e.message}")
            }
    }

    private fun recommendClinics(successRate: Double) {
        val clinics = listOf(
            "Toronto Fertility Clinic - +1 416-123-4567",
            "Maple Leaf IVF Center - +1 416-987-6543",
            "Downtown Reproductive Health - +1 416-222-3333"
        )

        val recommendation = when {
            successRate >= 80 -> "Great chances! Here’s a clinic to consider:\n${clinics[0]}"
            successRate in 50.0..79.9 -> "Good chances! You might want to consult:\n${clinics[1]}"
            else -> "Consider expert advice. Try:\n${clinics[2]}"
        }


    }
}

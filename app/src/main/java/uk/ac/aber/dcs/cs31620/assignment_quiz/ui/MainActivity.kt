package uk.ac.aber.dcs.cs31620.assignment_quiz.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import uk.ac.aber.dcs.cs31620.assignment_quiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            val intent = Intent(this, SelectBankForStudent::class.java)
            startActivity(intent)
        }



        binding.editButton.setOnClickListener {
            val intent = Intent(this, BankList::class.java)
            startActivity(intent)
        }
    }


}
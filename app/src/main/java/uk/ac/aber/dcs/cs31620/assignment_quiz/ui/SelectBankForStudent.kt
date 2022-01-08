package uk.ac.aber.dcs.cs31620.assignment_quiz.ui

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import uk.ac.aber.dcs.cs31620.assignment_quiz.databinding.ActivitySelectBankForStudentBinding
import uk.ac.aber.dcs.cs31620.assignment_quiz.datasource.DbHandler

class SelectBankForStudent : AppCompatActivity() {

    private lateinit var binding: ActivitySelectBankForStudentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectBankForStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val context = this
        val db = DbHandler(context)

        displayBankListForStudent(db)
        val intent = Intent(this, QuizPlay::class.java)
        binding.bankListForStudent.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedBank = parent.getItemAtPosition(position).toString()

                if (db.getQuestionsBody(db,selectedBank).isNotEmpty()) {
                    intent.putExtra("selectedbank", selectedBank)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        context,
                        "This bank is empty, try different one",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }



    private fun displayBankListForStudent(db: DbHandler) {
        val bankNameList = db.getBankNames(db)
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, bankNameList)
        binding.bankListForStudent.adapter = adapter

    }
}
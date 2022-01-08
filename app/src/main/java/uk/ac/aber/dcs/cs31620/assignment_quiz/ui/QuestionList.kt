package uk.ac.aber.dcs.cs31620.assignment_quiz.ui

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import uk.ac.aber.dcs.cs31620.assignment_quiz.databinding.ActivityQuestionListBinding
import uk.ac.aber.dcs.cs31620.assignment_quiz.datasource.DbHandler
import uk.ac.aber.dcs.cs31620.assignment_quiz.datasource.Question


class QuestionList : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityQuestionListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val context = this
        val db = DbHandler(context)
        val edit = binding.inputQuestion
        displayQuestionsList(db)
        val superiorBankName: String = intent.getStringExtra("bankname").toString()
        val intent = Intent(this, AnswerInserter::class.java)
        binding.addNewQuestion.setOnClickListener {

            if (edit.text.toString().isNotEmpty()) {

                val question = Question(edit.text.toString())

                if (db.isThisQuestionExist(question, superiorBankName)) {
                    db.insertNewQuestion(question, superiorBankName)
                    intent.putExtra("questionid", db.findQuestionId(question.bodyOfQuestion, superiorBankName))
                    edit.text.clear()
                    displayQuestionsList(db)
                    startActivity(intent)


                } else {

                    Toast.makeText(context, "Provide different question body", Toast.LENGTH_SHORT)
                        .show()
                    edit.text.clear()
                    displayQuestionsList(db)

                }
            } else {
                Toast.makeText(context, "Please Fill All Data's", Toast.LENGTH_SHORT).show()


            }
        }



        binding.questionsListView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->

                val questionBody = parent.getItemAtPosition(position).toString()
                val id = db.findQuestionId(questionBody, superiorBankName)
                intent.putExtra("questionid",id)
                startActivity(intent)

            }

        binding.questionsListView.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { parent, view, position, _ ->
                val popupMenu = PopupMenu(this, view)
                val questionBody = parent.getItemAtPosition(position).toString()
                val selectedQuestionIdForDeletion = db.findQuestionId(questionBody, superiorBankName)
                popupMenu.menuInflater.inflate(
                    uk.ac.aber.dcs.cs31620.assignment_quiz.R.menu.popup_menu,
                    popupMenu.menu
                )
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        uk.ac.aber.dcs.cs31620.assignment_quiz.R.id.delete -> Toast.makeText(
                            this@QuestionList,
                            "You Successfully " + item.title + "d: " + questionBody,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    db.deleteQuestion(selectedQuestionIdForDeletion)
                    displayQuestionsList(db)
                    true
                }
                popupMenu.show()
                true

            }


    }

    private fun displayQuestionsList(db: DbHandler) {

        val nameBank: String = intent.getStringExtra("bankname").toString()
        val questionBodyList = db.getQuestionsBody(db, nameBank)
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, questionBodyList)
        binding.questionsListView.adapter = adapter
    }


}
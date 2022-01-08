package uk.ac.aber.dcs.cs31620.assignment_quiz.ui

import android.R
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import uk.ac.aber.dcs.cs31620.assignment_quiz.databinding.ActivityAnswerInserterBinding

import uk.ac.aber.dcs.cs31620.assignment_quiz.datasource.Answer
import uk.ac.aber.dcs.cs31620.assignment_quiz.datasource.DbHandler

class AnswerInserter : AppCompatActivity() {


    private lateinit var binding: ActivityAnswerInserterBinding

    private var flag: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnswerInserterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val context = this
        val db = DbHandler(context)

        displayAnswerList(db)
        val superiorQuestion: Int = intent.getIntExtra("questionid", 0)
        val edit = binding.inputAnswer


        check(db,superiorQuestion)


        binding.addNewAnswer.setOnClickListener {


            if (edit.text.toString().isNotEmpty()) {


                val answer = Answer(edit.text.toString())

                if (db.getAnswersBody(superiorQuestion).size < 10) {
                    db.insertNewAnswer(answer, superiorQuestion)

                    edit.text.clear()
                    displayAnswerList(db)
                   // flag  = true
                    check(db,superiorQuestion)



                } else Toast.makeText(context, "Limit of 10 answers", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(context, "Please Fill All Data's", Toast.LENGTH_SHORT).show()
        }





        binding.answerListView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, _ ->

                val selectedItem = parent.getItemAtPosition(position).toString()
                if (selectedItem.isNotEmpty()) {
                    db.setAnswerToTrue(db.findAnswerId(selectedItem), superiorQuestion)
                    view.setBackgroundColor(Color.green(50))
                    Toast.makeText(context, "$selectedItem : Is true answer!", Toast.LENGTH_SHORT)
                        .show()
                    check(db,superiorQuestion)

                }else{
                    Toast.makeText(context, "Select an answer", Toast.LENGTH_SHORT).show()
                }
            }










        binding.answerListView.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { parent, view, position, _ ->
                val popupMenu = PopupMenu(this, view)
                val answerBody = parent.getItemAtPosition(position).toString()
                val selectedAnswerIdForDeletion = db.findAnswerId(answerBody)
                popupMenu.menuInflater.inflate(
                    uk.ac.aber.dcs.cs31620.assignment_quiz.R.menu.popup_menu,
                    popupMenu.menu
                )
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        uk.ac.aber.dcs.cs31620.assignment_quiz.R.id.delete -> Toast.makeText(
                            this@AnswerInserter,
                            "You Successfully " + item.title + "d: " + answerBody,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    db.deleteAnswer(selectedAnswerIdForDeletion)
                    displayAnswerList(db)
                    flag = false
                    true
                }
                popupMenu.show()
                true

            }



    }

    private fun displayAnswerList(db: DbHandler) {

        val questionId: Int = intent.getIntExtra("questionid", 0)
        val answerBodyList = db.getAnswersBody(questionId)
        val adapter = ArrayAdapter(this, R.layout.select_dialog_singlechoice, answerBodyList)
        binding.answerListView.adapter = adapter

    }

//

    override fun onBackPressed() {
        if (flag)
        super.onBackPressed()
        else Toast.makeText(this@AnswerInserter, "You must input an answers\n, and select correct one! " , Toast.LENGTH_LONG).show()
    }

    private fun check(db:DbHandler, superiorQuestion: Int){
        val temp = db.listOfCorrectId(superiorQuestion)
        flag = db.getAnswersBody(superiorQuestion).isNotEmpty() && (temp.isNotEmpty() && temp.contains(1))


    }





}
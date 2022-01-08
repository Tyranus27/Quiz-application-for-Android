package uk.ac.aber.dcs.cs31620.assignment_quiz.ui

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import uk.ac.aber.dcs.cs31620.assignment_quiz.databinding.ActivityQuizPlayBinding
import uk.ac.aber.dcs.cs31620.assignment_quiz.datasource.DbHandler

var correctAnswers: Int = 0
var numOfQuestions: Int = 0

class QuizPlay : AppCompatActivity() {

    private lateinit var binding: ActivityQuizPlayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityQuizPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val context = this
        val db = DbHandler(context)
        val selectedBankName = intent.getStringExtra("selectedbank").toString()
        val backToBankSelection = Intent(this, SelectBankForStudent::class.java)
        val questions: MutableList<String> = db.getQuestionsBody(db, selectedBankName)

        numOfQuestions = questions.size



        playGame(questions, db, selectedBankName)

        binding.nextButton.setOnClickListener {
            if (questions.isEmpty()) {

                Toast.makeText(
                    context,
                    "You scored $correctAnswers points out of $numOfQuestions possible",
                    Toast.LENGTH_LONG
                ).show()
                correctAnswers = 0
                startActivity(backToBankSelection)
                finish()

            } else {
                playGame(questions, db, selectedBankName)
            }
        }
    }

    private fun displayAnswerList(db: DbHandler, questionBody: String, superiorBankName : String) {
        val questionId = db.findQuestionId(questionBody,superiorBankName)
        val answerBodyList = db.getAnswersBody(questionId)
        val adapter = ArrayAdapter(this, R.layout.select_dialog_singlechoice, answerBodyList)
        binding.answersListForPlaying.adapter = adapter
    }


    private fun playGame(
        questions: MutableList<String>,
        db: DbHandler,
        selectedBankName: String
    ): Int {

        val randomQuestion = questions.random()

        binding.questionBankName.text = selectedBankName
        binding.questionBodyForStudent.text = randomQuestion
        displayAnswerList(db, randomQuestion, selectedBankName)
        binding.answersListForPlaying.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->

                val selectedAnswer = parent.getItemAtPosition(position).toString()
                val seletedQuestionId = db.findQuestionId(randomQuestion, selectedBankName)
                val selectedAnswerId = db.findAnswerId(selectedAnswer)
                if (db.isTrue(selectedAnswerId, seletedQuestionId)) {
                    correctAnswers++
                }
            }
        questions.remove(randomQuestion)
        return questions.size
    }
}
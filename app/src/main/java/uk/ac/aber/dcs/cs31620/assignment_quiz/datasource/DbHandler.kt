package uk.ac.aber.dcs.cs31620.assignment_quiz.datasource

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

const val DATABASENAME = "QUIZ DATABASE"

class DbHandler(var context: Context) : SQLiteOpenHelper(context, DATABASENAME, null, 1) {


    private val QUESTIONBANKNAME = "Bank"
    private val QB_COL_NAME_ID = "question_bank_id"
    private val ANSWERTABLENAME = "Answers"
    private val COL_ANSWERBODY = "answer"
    private val COL_ANSWERID = "id_ans"
    private val COL_ISTRUE = "correct_answer"
    private val QUESTIONTABLENAME = "Questions"
    private val COL_QUESTIONBODY = "question"
    private val COL_QUESITONID = "id_que"

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        if (!db.isReadOnly) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON")
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createQuestionBankTable =
            "CREATE TABLE $QUESTIONBANKNAME ($QB_COL_NAME_ID TEXT PRIMARY KEY)"
        db?.execSQL(createQuestionBankTable)

        val createQuestionTable =
            "CREATE TABLE $QUESTIONTABLENAME($COL_QUESITONID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_QUESTIONBODY TEXT, $QB_COL_NAME_ID TEXT, FOREIGN KEY ($QB_COL_NAME_ID) REFERENCES $QUESTIONBANKNAME($QB_COL_NAME_ID ) ON DELETE CASCADE )"
        db?.execSQL(createQuestionTable)

        val createAnswerTable =
            "CREATE TABLE $ANSWERTABLENAME($COL_ANSWERID INTEGER PRIMARY KEY,$COL_ANSWERBODY TEXT,$COL_ISTRUE INT,$COL_QUESITONID INTEGER, FOREIGN KEY ($COL_QUESITONID) REFERENCES $QUESTIONTABLENAME($COL_QUESITONID ) ON DELETE CASCADE )"
        db?.execSQL(createAnswerTable)


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //  onCreate(db);
    }

    fun insertNewBank(bank: QuestionBank) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(QB_COL_NAME_ID, bank.questionBankId)
        val result = db.insert(QUESTIONBANKNAME, null, contentValues)
        if (result == (0).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
        db.close()


    }

    fun isThisBankExist(bank: QuestionBank): Boolean {
        val db = this.readableDatabase
        val testQuery =
            "SELECT * FROM $QUESTIONBANKNAME WHERE $QB_COL_NAME_ID =" + "\"" + bank.questionBankId + "\""
        val test = db.rawQuery(testQuery, null)
        if (test.moveToFirst()) {
            test.close()
            db.close()
            return false
        }
        db.close()
        return true

    }

    fun isThisQuestionExist(question: Question, superiorBankName: String): Boolean {
        val db = this.readableDatabase
        val testQuery =
            "SELECT * FROM $QUESTIONTABLENAME WHERE $COL_QUESTIONBODY =" + "\"" + question.bodyOfQuestion + "\"" + "AND $QB_COL_NAME_ID =" + "\"" + superiorBankName + "\""
        val test = db.rawQuery(testQuery, null)
        if (test.moveToFirst()) {
            test.close()
            db.close()
            return false
        }
        db.close()
        return true
    }

    fun insertNewQuestion(question: Question, superiorBankName: String) {
        val database = this.writableDatabase
        val query1 =
            "SELECT $QB_COL_NAME_ID FROM $QUESTIONBANKNAME WHERE $QB_COL_NAME_ID =\"$superiorBankName\""
        val result1 = database.rawQuery(query1, null)
        result1.moveToLast()
        val id = result1.getString(result1.getColumnIndexOrThrow(QB_COL_NAME_ID))
        val contentValues = ContentValues()
        contentValues.put(COL_QUESTIONBODY, question.bodyOfQuestion)
        contentValues.put(QB_COL_NAME_ID, id)
        val result = database.insert(QUESTIONTABLENAME, null, contentValues)
        if (result == (0).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
        result1.close()
        database.close()
    }

    fun insertNewAnswer(answer: Answer, superiorQuestionId: Int) {

        val db = this.writableDatabase
        val query1 =
            "SELECT $COL_QUESITONID FROM $QUESTIONTABLENAME WHERE $COL_QUESITONID =\"$superiorQuestionId\""
        val result1 = db.rawQuery(query1, null)
        result1.moveToLast()
        val id = result1.getLong(result1.getColumnIndexOrThrow(COL_QUESITONID))
        val contentValues = ContentValues()
        contentValues.put(COL_ANSWERBODY, answer.bodyOfAnswer)
        contentValues.put(COL_QUESITONID, id)
        val result = db.insert(ANSWERTABLENAME, null, contentValues)
        if (result == (0).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
        result1.close()
        db.close()


    }

    fun getBankNames(db: DbHandler): MutableList<String> {
        val listOfBankNames: MutableList<String> = ArrayList()
        val db = this.readableDatabase
        val selectBankNames = "SELECT * FROM $QUESTIONBANKNAME"
        val result = db.rawQuery(selectBankNames, null)
        if (result.moveToFirst())
            do {
                val temp = result.getString(result.getColumnIndexOrThrow(QB_COL_NAME_ID))
                listOfBankNames.add(temp)
            } while (result.moveToNext())
        result.close()
        db.close()

        return listOfBankNames
    }

    fun getQuestionsBody(db: DbHandler, bankName: String): MutableList<String> {
        val listOfQuestions: MutableList<String> = ArrayList()
        val db = this.readableDatabase
        val selectQuestionsFromBank =
            "SELECT $COL_QUESTIONBODY FROM $QUESTIONTABLENAME where $QB_COL_NAME_ID = \"$bankName\""
        val result = db.rawQuery(selectQuestionsFromBank, null)
        if (result.moveToFirst())
            do {
                val temp = result.getString(result.getColumnIndexOrThrow(COL_QUESTIONBODY))
                listOfQuestions.add(temp)
            } while (result.moveToNext())
        result.close()
        db.close()
        return listOfQuestions
    }

    fun getAnswersBody(selectedQuestionId: Int): MutableList<String> {
        val listOfAnswers: MutableList<String> = ArrayList()
        val db = this.readableDatabase
        val selectAnswersFromQuestion =
            "SELECT $COL_ANSWERBODY FROM $ANSWERTABLENAME where $COL_QUESITONID = $selectedQuestionId"
        val result = db.rawQuery(selectAnswersFromQuestion, null)
        if (result.moveToFirst())
            do {
                val temp = result.getString(result.getColumnIndexOrThrow(COL_ANSWERBODY))
                listOfAnswers.add(temp)
            } while (result.moveToNext())
        result.close()
        db.close()
        return listOfAnswers
    }


    fun findQuestionId(question: String, superiorBank : String): Int {
        val db = this.readableDatabase
        val findQueId =
            "SELECT $COL_QUESITONID FROM $QUESTIONTABLENAME WHERE $COL_QUESTIONBODY =  \"$question\" AND $QB_COL_NAME_ID = \"$superiorBank\" "
        val result = db.rawQuery(findQueId, null)
        result.moveToFirst()
        val id = result.getInt(result.getColumnIndexOrThrow(COL_QUESITONID))
        result.close()
        db.close()
        return id
    }

    fun findAnswerId(answer: String): Int {

        val db = this.readableDatabase
        val finAnsId =
            "SELECT $COL_ANSWERID FROM $ANSWERTABLENAME WHERE $COL_ANSWERBODY =  \"$answer\""
        val result = db.rawQuery(finAnsId, null)
        result.moveToFirst()
        val id = result.getInt(result.getColumnIndexOrThrow(COL_ANSWERID))
        result.close()
        db.close()
        return id
    }


    fun deleteQuestionBank(bankNameToDeleting: String) {
        val db = this.writableDatabase
        val deleteQuery =
            "DELETE FROM $QUESTIONBANKNAME WHERE $QB_COL_NAME_ID =\"$bankNameToDeleting\""
        db?.execSQL(deleteQuery)

    }

    fun deleteQuestion(questionId: Int) {
        val db = this.writableDatabase
        val deleteQuestionQuery =
            "DELETE FROM $QUESTIONTABLENAME WHERE $COL_QUESITONID =\"$questionId\""
        db?.execSQL(deleteQuestionQuery)
        db.close()
    }

    fun deleteAnswer(answerId: Int) {
        val db = this.writableDatabase
        val deleteAnswerQuery =
            "DELETE FROM $ANSWERTABLENAME WHERE $COL_ANSWERID =\"$answerId\""
        db?.execSQL(deleteAnswerQuery)
        db.close()
    }

    fun setAnswerToTrue(answerId: Int, superiorQuestionId: Int) {
        val db = this.writableDatabase
        val updateAnswerToTrue =
            "UPDATE $ANSWERTABLENAME SET $COL_ISTRUE = true WHERE $COL_ANSWERID = \"$answerId\" AND $COL_QUESITONID = \"$superiorQuestionId\""
        val updateAnswersToFalse =
            "UPDATE $ANSWERTABLENAME SET $COL_ISTRUE = false WHERE $COL_ANSWERID != \"$answerId\" AND $COL_QUESITONID = \"$superiorQuestionId\""
        db?.execSQL(updateAnswerToTrue)
        db?.execSQL(updateAnswersToFalse)
        db.close()
    }

    fun isTrue(answerId: Int, superiorQuestionId: Int): Boolean {
        val db = this.readableDatabase
        val isTrueQuery =
            "SELECT * FROM $ANSWERTABLENAME WHERE $COL_ANSWERID = \"$answerId\" AND $COL_QUESITONID = \"$superiorQuestionId\""
        val result = db.rawQuery(isTrueQuery, null)
        result.moveToFirst()
        val trueOrNotTrue = result.getInt(result.getColumnIndexOrThrow(COL_ISTRUE))
        result.close()
        db.close()
        return trueOrNotTrue == 1
    }

    fun listOfCorrectId (superiorQuestionId: Int): MutableList<Int>{
        val db = this.readableDatabase
        val listOfCorrectAns : MutableList<Int> = ArrayList()
        val listOfCorrectQuery = "SELECT $COL_ISTRUE FROM $ANSWERTABLENAME WHERE $COL_QUESITONID = \"$superiorQuestionId\""
        val result = db.rawQuery(listOfCorrectQuery, null)
        if (result.moveToFirst())
            do {
                val temp = result.getInt(result.getColumnIndexOrThrow(COL_ISTRUE))
                listOfCorrectAns.add(temp)
            } while (result.moveToNext())
        result.close()
        db.close()
        return listOfCorrectAns
    }


}
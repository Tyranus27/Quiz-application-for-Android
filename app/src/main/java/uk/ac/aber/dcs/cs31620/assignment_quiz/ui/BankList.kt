package uk.ac.aber.dcs.cs31620.assignment_quiz.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import uk.ac.aber.dcs.cs31620.assignment_quiz.R
import uk.ac.aber.dcs.cs31620.assignment_quiz.databinding.ActivityBankListBinding
import uk.ac.aber.dcs.cs31620.assignment_quiz.datasource.DbHandler
import uk.ac.aber.dcs.cs31620.assignment_quiz.datasource.QuestionBank

class BankList : AppCompatActivity() {

    private lateinit var binding: ActivityBankListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBankListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val context = this
        val db = DbHandler(context)
        displayBankList(db)
        val edit = binding.inputNewBank

        val intent = Intent(this, QuestionList::class.java)

        binding.addNewBank.setOnClickListener {

            if (edit.text.toString().isNotEmpty()) {

                val bank = QuestionBank(edit.text.toString())
                if (db.isThisBankExist(bank)) {

                    db.insertNewBank(bank)
                    intent.putExtra("bankname", edit.text.toString())
                    edit.text.clear()
                    displayBankList(db)
                    startActivity(intent)

                } else {

                    Toast.makeText(context, "Provide different bank name", Toast.LENGTH_SHORT)
                        .show()
                    edit.text.clear()
                    displayBankList(db)

                }

            } else {
                Toast.makeText(context, "Please Fill All Data's", Toast.LENGTH_SHORT).show()
            }
        }
        binding.bankListView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedItemText = parent.getItemAtPosition(position).toString()
                intent.putExtra("bankname", selectedItemText)
                startActivity(intent)
            }

        binding.bankListView.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { parent, view, position, _ ->
                val popupMenu = PopupMenu(this, view)
                val selectedBankNameForDeletion = parent.getItemAtPosition(position).toString()
                popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.delete -> Toast.makeText(
                            this@BankList,
                            "You Successfully " + item.title + "d: " + selectedBankNameForDeletion,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    db.deleteQuestionBank(selectedBankNameForDeletion)
                    displayBankList(db)
                    true
                }
                popupMenu.show()
                true

            }

    }


     private fun displayBankList(db: DbHandler) {
        val bankNameList = db.getBankNames(db)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, bankNameList)
        binding.bankListView.adapter = adapter

    }


}
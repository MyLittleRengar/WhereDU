package com.project.wheredu

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.project.wheredu.dialog.CustomAccountDialog
import com.project.wheredu.dialog.CustomDialog
import java.text.DecimalFormat

class DutchPayActivity : AppCompatActivity() {

    private lateinit var addIv: ImageView
    private lateinit var totalCalculateBtn: Button
    private lateinit var totalShareBtn: Button

    private lateinit var personLayout: LinearLayout
    private lateinit var addCalLayout: LinearLayout
    private var editTextCnt: Int = 0
    private var addNameAll: Boolean = false
    private lateinit var existingLayout: View

    private val editTextValues = mutableListOf<String>()
    private val totalEditTextValues = mutableListOf<EditText>()
    private val checkBoxStates = mutableListOf<MutableList<Boolean>>()

    private lateinit var calResultTotalAmount: TextView
    private lateinit var calResultPerPerson: TextView
    private lateinit var calResultPerPerson2: TextView

    private var xmlCnt: Int = 0
    @SuppressLint("InflateParams", "SetTextI18n", "DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dutch_pay)

        showDialog()

        addCalLayout = findViewById(R.id.addCalLayout)
        addIv = findViewById(R.id.addIv)

        totalCalculateBtn = findViewById(R.id.totalCalculateBtn)
        totalShareBtn = findViewById(R.id.totalShareBtn)
        calResultTotalAmount = findViewById(R.id.calResultTotalAmount)
        calResultPerPerson = findViewById(R.id.calResultPerPerson)
        calResultPerPerson2 = findViewById(R.id.calResultPerPerson2)

        personLayout = findViewById(R.id.personLayout)

        totalShareBtn.setOnClickListener {
            accountDialog()
        }

        addIv.setOnClickListener {
            if (addNameAll) {
                xmlCnt += 1
                existingLayout = layoutInflater.inflate(R.layout.cal_sample, null)
                addCalLayout.addView(existingLayout)
                val checkBoxList = mutableListOf<Boolean>()

                for (i in 0 until editTextValues.size) {
                    checkBoxList.add(false)
                    val checkBoxId = resources.getIdentifier("checkBox${i + 1}", "id", packageName)
                    val checkBox: CheckBox = existingLayout.findViewById(checkBoxId)
                    checkBox.text = editTextValues[i]
                }

                checkBoxStates.add(checkBoxList)

                if (editTextValues.size < 5) {
                    val remainCheckBoxes = listOf(
                        existingLayout.findViewById<CheckBox>(R.id.checkBox3),
                        existingLayout.findViewById(R.id.checkBox4),
                        existingLayout.findViewById(R.id.checkBox5)
                    )
                    remainCheckBoxes.takeLast(5 - editTextValues.size).forEach {
                        it.visibility = View.GONE
                    }
                }

                val editText2 = existingLayout.findViewById<EditText>(R.id.editTextText2)
                totalEditTextValues.add(editText2)
            } else {
                Toast.makeText(this, "이름을 먼저 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        totalCalculateBtn.setOnClickListener {
            val editTextPrice = totalEditTextValues.map { it.text.toString().toInt() }
            val totalAmount = editTextPrice.sum()
            val decimal = DecimalFormat("#,###")
            totalShareBtn.isEnabled = true

            calResultTotalAmount.visibility = View.VISIBLE
            calResultTotalAmount.text = "총 금액: " + decimal.format(totalAmount) + "원"

            calResultPerPerson.visibility = View.VISIBLE
            calResultPerPerson2.visibility = View.VISIBLE

            val totalItems = (0 until xmlCnt).map { i ->
                val layout = addCalLayout.getChildAt(i)
                val checkBoxList = checkBoxStates[i]
                for (j in 1..editTextValues.size) {
                    val checkBox = layout.findViewById<CheckBox>(
                        resources.getIdentifier("checkBox$j", "id", packageName)
                    )
                    checkBoxList[j - 1] = checkBox.isChecked
                }
                Pair(editTextPrice[i], checkBoxStates[i])
            }

            val userCounts = checkBoxStates[0].size

            val personAmounts = MutableList(5) { 0 }

            for (i in 0 until xmlCnt) {
                val xmlPerAmount = totalItems[i].first

                for (j in 0 until userCounts) {
                    val isChecked = totalItems[i].second[j]
                    if (isChecked) {
                        personAmounts[j] += xmlPerAmount / totalItems[i].second.count { it }
                    }
                }
            }

            val personAmountStrings = mutableListOf<String>()
            for (i in 0 until userCounts) {
                personAmountStrings.add("${editTextValues[i]}: ${decimal.format(personAmounts[i])}원")
            }
            calResultPerPerson.text = personAmountStrings.joinToString("\n")
        }
    }

    private fun accountDialog() {
        CustomAccountDialog(this) { data ->
            val sharedData = calResultPerPerson.text.toString()
            val sharedData2 = calResultTotalAmount.text.toString()
            userDialog(sharedData2+"\n"+sharedData, data)
        }.show()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showDialog() {
        CustomDialog(this) { count ->
            for (i in 0 until count.toInt()) {
                val createEditText = EditText(this)
                val layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1f
                }

                createEditText.layoutParams = layoutParams
                createEditText.hint = "이름"
                createEditText.textSize = 16f
                createEditText.textCursorDrawable = resources.getDrawable(R.drawable.edittextcur)
                createEditText.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#71BF45"))
                personLayout.addView(createEditText)
                editTextCnt++
            }

            val createButton = Button(this).apply {
                val layoutParam = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 0f
                }

                layoutParams = layoutParam
                text = "확인"
                setTextColor(Color.parseColor("#ffffff"))
                backgroundTintList = ColorStateList.valueOf(Color.parseColor("#71BF45"))

                val customFont =
                    ResourcesCompat.getFont(this@DutchPayActivity, R.font.nanum_bold)
                typeface = customFont

                setOnClickListener {
                    for (i in 0 until editTextCnt) {
                        val editTexts = personLayout.getChildAt(i) as EditText
                        val textValue = editTexts.text.toString()
                        editTextValues.add(textValue)
                    }

                    val allNonNull = editTextValues.none { it.isEmpty() }
                    if (allNonNull) {
                        for (i in 0 until editTextCnt) {
                            val editTexts = personLayout.getChildAt(i) as EditText
                            editTexts.isEnabled = false
                        }

                        visibility = View.GONE
                        addNameAll = true
                    } else {
                        Toast.makeText(
                            this@DutchPayActivity,
                            "이름을 모두 넣어주세요",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            personLayout.addView(createButton)
        }.show()
    }


    private fun userDialog(data: String, account: String) {
        val splitAccount = account.split("/")
        val builder = AlertDialog.Builder(this)
            .setTitle("공유하기")
            .setMessage("$data\n\n${splitAccount[0]} ${splitAccount[1]}")
            .setPositiveButton("공유") { _, _ ->
                sharedContent("$data\n\n${splitAccount[0]} ${splitAccount[1]}")
            }
            .setNegativeButton("취소") {_, _ -> }
        val dialogs = builder.create()
        dialogs.show()
        dialogs.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#6096FD"))
        dialogs.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#FB7B8E"))
    }
    private fun sharedContent(data: String) {
        val editData = "WhereDu Calculator\n\n$data"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, editData)
        }
        startActivity(Intent.createChooser(intent, "공유"))
    }
}
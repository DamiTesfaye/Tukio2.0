package io.neolution.eventify.View.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.Transaction
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Utils.AppUtils

class PromoteEventActivity : AppCompatActivity() {

    //On First StartUp Activity
    private lateinit var onFirstStartLayout: RelativeLayout
    private lateinit var closeFirstStartBtn: ImageButton
    private lateinit var doneFirstStartBtn: Button

    //Normal Activity Views
    private lateinit var accountNumberLayout: TextInputLayout
    private lateinit var accountNumberEdit: TextInputEditText
    private lateinit var expYearLayout: TextInputLayout
    private lateinit var expYearEdit: TextInputEditText
    private lateinit var expMonthLayout: TextInputLayout
    private lateinit var expMonthEdit: TextInputEditText
    private lateinit var cvvLayout: TextInputLayout
    private lateinit var cvvEdit: TextInputEditText
    private lateinit var backBtn: ImageButton

    private lateinit var selectPlanLayout: RelativeLayout
    private lateinit var selectPlanText: TextView
    private lateinit var selectPlanImage: ImageView

    private lateinit var closeBottomSheet: ImageButton
    private lateinit var bigAudienceLayout: LinearLayout
    private lateinit var mediumAudienceLayout: LinearLayout
    private lateinit var smallAudienceLayout: LinearLayout

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var planBottomSheet: LinearLayout

    private lateinit var promoteEventContainer: RelativeLayout
    private lateinit var promoteEventText: TextView
    private lateinit var promoteProgressBar: ProgressBar
    private lateinit var progressText: TextView

    //Activity Variables
    private lateinit var accountNumber: String
    private lateinit var expMonth: String
    private lateinit var expYear: String
    private lateinit var cvv: String
    private var amountPaid: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promote_event)

        onFirstStartLayout = findViewById(R.id.promote_event_instro_layout)
        closeFirstStartBtn = findViewById(R.id.promote_event_close_btn)
        doneFirstStartBtn = findViewById(R.id.promote_event_done_btn)

        accountNumberLayout = findViewById(R.id.promote_event_acc_number_layout)
        accountNumberEdit = findViewById(R.id.promote_event_acc_number_edit)
        expYearLayout = findViewById(R.id.promote_event_exp_year_layout)
        expYearEdit = findViewById(R.id.promote_event_exp_year_edit)
        expMonthLayout = findViewById(R.id.promote_event_exp_month_layout)
        expMonthEdit = findViewById(R.id.promote_event_exp_month_edit)
        cvvLayout = findViewById(R.id.promote_event_cvv_layout)
        cvvEdit = findViewById(R.id.promote_event_cvv_edit)
        backBtn = findViewById(R.id.promote_event_back_btn)

        progressText = findViewById(R.id.progress_text)

        backBtn.setOnClickListener {
            accountNumber = accountNumberEdit.text.toString().trim()
            expMonth = expMonthEdit.text.toString().trim()
            expYear = expYearEdit.text.toString().trim()
            cvv = cvvEdit.text.toString().trim()

            if (accountNumber.isNotEmpty() || expMonth.isNotEmpty() || expYear.isNotEmpty() || cvv.isNotEmpty() || amountPaid != null){

                val dialog = AlertDialog.Builder(this, R.style.MyTimePickerDialogTheme)
                dialog.setMessage("Do you want to discard your changes?")
                dialog.setPositiveButton("DISCARD") { _, _ ->

                    val intent = Intent()
                    intent.putExtra("promotedEvent", 0L)
                    intent.putExtra("paymentStatus", "failed")
                    setResult(Activity.RESULT_OK, intent)

                    finish()
                }

                dialog.setNegativeButton("CANCEL") { dialogInterface, _ ->

                    dialogInterface.dismiss()
                }

                val alert = dialog.create()
                alert.show()

            }else{
                val intent = Intent()
                intent.putExtra("promotedEvent", 0L)
                setResult(Activity.RESULT_OK, intent)

                finish()

            }
        }

        selectPlanLayout = findViewById(R.id.promote_event_select_plan)
        selectPlanText = findViewById(R.id.promote_event_add_plan_text)
        selectPlanImage = findViewById(R.id.promote_event_selected_plan_image)

        planBottomSheet = findViewById(R.id.promoting_plan_bottomsheet)
        bottomSheetBehavior = BottomSheetBehavior.from(planBottomSheet)

        closeBottomSheet = findViewById(R.id.promote_plan_close)
        bigAudienceLayout = findViewById(R.id.promote_plan_big_audience)
        mediumAudienceLayout = findViewById(R.id.promote_plan_medium_audience)
        smallAudienceLayout = findViewById(R.id.promote_plan_small_audience)

        promoteEventContainer = findViewById(R.id.promote_event_promote_event_container)
        promoteEventText = findViewById(R.id.promote_event_promote_event_text)
        promoteProgressBar = findViewById(R.id.promote_event_promote_event_progress)

        checkForFirstTime()

        selectPlanLayout.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        closeBottomSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bigAudienceLayout.setOnClickListener {
            selectPlanLayout.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
            selectPlanText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            selectPlanText.text = "Big Audience Plan: N10,000.00"
            selectPlanImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_people))
            amountPaid = 10000

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        mediumAudienceLayout.setOnClickListener {

            selectPlanLayout.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
            selectPlanText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            selectPlanText.text = "Medium Audience Plan: N5,000.00"
            selectPlanImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_people))
            amountPaid = 5000

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        smallAudienceLayout.setOnClickListener {

            selectPlanLayout.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
            selectPlanText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            selectPlanText.text = "Small Audience Plan: N1,000.00"
            selectPlanImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_people))
            amountPaid = 1000

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        }

        promoteEventText.setOnClickListener {

            accountNumber = accountNumberEdit.text.toString().trim()
            expMonth = expMonthEdit.text.toString().trim()
            expYear = expYearEdit.text.toString().trim()
            cvv = cvvEdit.text.toString().trim()

            if (accountNumber.isNotEmpty() && expMonth.isNotEmpty() && expYear.isNotEmpty() && cvv.isNotEmpty() && amountPaid != null){

                payPromotionMoney(accountNum = accountNumber,
                    expMonth = expMonth, expYear = expYear, cvv = cvv, amount = amountPaid!!)

            }else{
                if (accountNumber.isEmpty()){
                    accountNumberLayout.isErrorEnabled = true
                    accountNumberLayout.error = "Please input a valid account number"

                }else if (expMonth.isEmpty()){
                    expMonthLayout.isErrorEnabled = true
                    expMonthLayout.error = "Please input a valid expiry month"

                }else if (expYear.isEmpty()){
                    expYearLayout.isErrorEnabled = true
                    expYearLayout.error = "Please input a valid expiry year"

                }else if (cvv.isEmpty()){
                    cvvLayout.isErrorEnabled = true
                    cvvLayout.error = "Please input a valid cvv"

                }else if (amountPaid == null){
                    val v = findViewById<View>(android.R.id.content)
                    AppUtils.getCustomSnackBar(v, "Please select a promoting plan..", this).show()

                }
            }
        }

    }

    private fun checkForFirstTime(){
        val sharedPrefs = getSharedPreferences("promo_pref", Context.MODE_PRIVATE)
        if (sharedPrefs.getString("startup", "") ==  "started"){
            onFirstStartLayout.visibility = GONE
        }

        closeFirstStartBtn.setOnClickListener {
            val editor = sharedPrefs.edit()
            editor.putString("startup", "started")
            val saved = editor.commit()
            if (saved)onFirstStartLayout.visibility = GONE
        }

        doneFirstStartBtn.setOnClickListener {
            val editor = sharedPrefs.edit()
            editor.putString("startup", "started")
            val saved = editor.commit()
            if (saved)onFirstStartLayout.visibility = GONE
        }
    }

    private fun payPromotionMoney(accountNum: String, expMonth: String, expYear: String, cvv: String, amount: Int){
        closeKeyboard()
        promoteEventText.visibility = GONE
        promoteEventContainer.background = ContextCompat.getDrawable(this, R.drawable.buttonbg_outline)
        promoteProgressBar.visibility = VISIBLE

        val card = Card.Builder(accountNum, expMonth.toInt(), expYear.toInt(), cvv).build()
        if (card.isValid){

            progressText.text = ("Validating Card..")
            val charge = Charge()
            charge.card = card
            charge.email = AuthRepo.getCurrentUser()?.email
            charge.amount = amount * 100

            progressText.text = ("Charging Card..")
            PaystackSdk.chargeCard(this, charge, object : Paystack.TransactionCallback {
                override fun onSuccess(transaction: Transaction?) {

                    val intent = Intent()
                    intent.putExtra("paymentStatus", "success")
                    intent.putExtra("promotedEvent", amount.toLong())
                    setResult(Activity.RESULT_OK, intent)

                    finish()
                }

                override fun beforeValidate(transaction: Transaction?) {

                }

                override fun onError(error: Throwable?, transaction: Transaction?) {
                    val v = findViewById<View>(android.R.id.content)
                    AppUtils.getCustomSnackBar(v, error!!.localizedMessage!!, this@PromoteEventActivity).show()

                    progressText.text = ("")
                    promoteEventText.visibility = VISIBLE
                    promoteEventContainer.background = ContextCompat.getDrawable(this@PromoteEventActivity, R.drawable.buttonbg)
                    promoteProgressBar.visibility = GONE
                }
            })

        }else{
            if (!card.validCVC()){

                cvvLayout.isErrorEnabled = true
                cvvLayout.error = "Please input a valid cvv"

                promoteEventText.visibility = VISIBLE
                promoteEventContainer.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
                promoteProgressBar.visibility = GONE

                progressText.text = ("")
                val v = findViewById<View>(android.R.id.content)
                AppUtils.getCustomSnackBar(v, "The account number is not valid..", this@PromoteEventActivity).show()

            }else if (!card.validExpiryDate()){

                expMonthLayout.isErrorEnabled = true
                expYearLayout.isErrorEnabled = true

                promoteEventText.visibility = VISIBLE
                promoteEventContainer.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
                promoteProgressBar.visibility = GONE

                val v = findViewById<View>(android.R.id.content)
                AppUtils.getCustomSnackBar(v, "The expiry date is not valid..", this@PromoteEventActivity).show()

            }else if (!card.validNumber()){

                accountNumberLayout.isErrorEnabled = true
                accountNumberLayout.error = "Please enter a valid account number"

                promoteEventText.visibility = VISIBLE
                promoteEventContainer.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
                promoteProgressBar.visibility = GONE

                val v = findViewById<View>(android.R.id.content)
                AppUtils.getCustomSnackBar(v, "This account number is not valid", this@PromoteEventActivity).show()

            }
        }

    }

    private fun closeKeyboard(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null){
            view = View(this)
        }

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onBackPressed() {

        val intent = Intent()
        intent.putExtra("promotedEvent", 0L)
        intent.putExtra("paymentStatus", "failed")
        setResult(Activity.RESULT_OK, intent)

        finish()
    }
}

package io.neolution.eventify.View.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TimePicker
import android.widget.Toast
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import io.neolution.eventify.Data.Adapters.AddTagsAdapter
import io.neolution.eventify.Data.ModelClasses.ChipModel
import io.neolution.eventify.Listeners.OnChipSelected
import io.neolution.eventify.R
import io.neolution.eventify.Utils.AppUtils
import io.neolution.eventify.Utils.DateUtils
import io.neolution.eventify.Utils.IntentUtils
import kotlinx.android.synthetic.main.activity_add_event_prem.*
import kotlinx.android.synthetic.main.add_tag_bottomsheet.*
import java.util.*

class AddEventPremActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, OnChipSelected {

    override fun onChipSelected(chipText: String) {
        eventTag = chipText
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        add_event_prem_tag_container.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
        add_event_prem_select_tag_text.text = chipText
        add_event_prem_select_tag_text.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

        for (chip in chipList){
            if (chip.chipText == chipText){
                add_event_prem_tag_image.visibility = VISIBLE
                add_event_prem_tag_image.setImageDrawable(ContextCompat.getDrawable(this, chip.imageResource))
            }
        }
    }

    override fun onChipDeselected(chipText: String) {

    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        eventDate = DateUtils.resolveDate(day, month, year)
        add_event_prem_date_edit.setText(eventDate)

        val calender = Calendar.getInstance()
        calender.set(Calendar.DAY_OF_MONTH, day)
        calender.set(Calendar.MONTH, month)
        calender.set(Calendar.YEAR, year)

        eventFinalDateLong = calender.timeInMillis

        val dialog = TimePickerDialog(
            this@AddEventPremActivity, R.style.MyTimePickerDialogTheme,
            this@AddEventPremActivity, initHour, initMin, true
        )
        dialog.show()
    }

    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
        eventTime = DateUtils.resovleTime(hour = hour, min = minute)
        eventFinalDate = "$eventDate, $eventTime"
        add_event_prem_date_edit.setText(eventFinalDate)
    }

    private var eventTag: String? = null
    private var eventPicUri: Uri? = null

    private var eventTime: String? = null
    private var eventDate: String? = null
    private var eventFinalDate: String? = null
    private var eventFinalDateLong: Long? = null
    private lateinit var chipList: MutableList<ChipModel>

    //Date-Time variables
    private var initDay: Int = 0
    private var initMonth: Int = 0
    private var initYear: Int = 0
    private var initHour: Int = 0
    private var initMin: Int = 0

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var tagBottomSheet: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event_prem)

         chipList = AppUtils.createChipList()

        tagBottomSheet = findViewById(R.id.add_event_prem__tag_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(tagBottomSheet)

        val manager = StaggeredGridLayoutManager(
            7,
            StaggeredGridLayoutManager.HORIZONTAL
        )
        manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        val adapter = AddTagsAdapter(chipList, this, this)

        add_tag_bsheet_recycler.setHasFixedSize(true)
        add_tag_bsheet_recycler.layoutManager = manager
        add_tag_bsheet_recycler.adapter = adapter

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                add_event_prem_bottom_sheet_bg.visibility = VISIBLE
                add_event_prem_bottom_sheet_bg.alpha = slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    add_event_prem_bottom_sheet_bg.visibility = GONE
                }
            }
        })

        add_tag__bsheet_close.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        add_event_prem_tag_container.setOnClickListener {
            closeKeyboard()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        }

        add_event_prem_bottom_sheet_bg.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        add_event_prem_back_btn.setOnClickListener {

            val eventTitle = add_event_prem_title_edit.text.toString().trim()
            val eventDesc = add_event_prem_desc_edit.text.toString().trim()
            val eventDate = add_event_prem_date_edit.text.toString().trim()
            val eventLocation = add_event_prem_location_edit.text.toString().trim()

            if (eventTitle.isNotEmpty() || eventDate.isNotEmpty() || eventDesc.isNotEmpty() ||
                eventLocation.isNotEmpty() || eventTag != null || eventPicUri != null || eventFinalDateLong != null){
                val dialog = AlertDialog.Builder(this, R.style.MyTimePickerDialogTheme)
                dialog.setMessage("Do you want to discard your changes?")
                dialog.setPositiveButton("DISCARD") { _, _ ->
                    finish()
                }

                dialog.setNegativeButton("CANCEL") { dialogInterface, _ ->

                    dialogInterface.dismiss()
                }

                val alert = dialog.create()
                alert.show()
            }else{
                finish()
            }
        }

        add_event_prem_almost_done_btn.setOnClickListener {

            val eventTitle = add_event_prem_title_edit.text.toString().trim()
            val eventDesc = add_event_prem_desc_edit.text.toString().trim()
            val eventDate = add_event_prem_date_edit.text.toString().trim()
            val eventLocation = add_event_prem_location_edit.toString().trim()

            if (eventTitle.isNotEmpty() && eventDate.isNotEmpty() && eventDesc.isNotEmpty() &&
                eventLocation.isNotEmpty() && eventTag != null && eventPicUri != null && eventFinalDateLong != null){

                val intent = Intent(this, AddEventFinalActivity::class.java)
                intent.putExtra("eventTitle", "--beta--$eventTitle--beta--")
                intent.putExtra("eventDesc", eventDesc)
                intent.putExtra("eventDate", eventDate)
                intent.putExtra("eventLocation", eventLocation)
                intent.putExtra("eventTag", eventTag!!)
                intent.putExtra("eventPicUri", eventPicUri!!.toString())
                intent.putExtra("eventMillis", eventFinalDateLong!!)

                startActivity(intent)

            }else {
                if (eventTitle.isEmpty()){
                    add_event_prem_title_layout.isErrorEnabled = true
                    add_event_prem_title_layout.error = "Please input a suitable event title"
                }else if (eventDesc.isEmpty()){
                    add_event_prem_desc_layout.isErrorEnabled = true
                    add_event_prem_desc_layout.error = "Please input a description for your event"
                } else if (eventDate.isEmpty()){
                    add_event_prem_date_layout.isErrorEnabled = true
                    add_event_prem_date_layout.error = "Please add the date for your event"
                }else if (eventLocation.isEmpty()){
                    add_event_prem_location_layout.isErrorEnabled = true
                    add_event_prem_location_layout.error = "Please input a description for your event"
                }else if (eventTag == null){
                    val v = findViewById<View>(android.R.id.content)
                    AppUtils.getCustomSnackBar(v, "Select an event tag", this).show()
                }else if (eventPicUri == null){
                    val v = findViewById<View>(android.R.id.content)
                    AppUtils.getCustomSnackBar(v, "Add a picture for you event", this).show()
                }else if (eventFinalDateLong == null){
                    val v = findViewById<View>(android.R.id.content)
                    AppUtils.getCustomSnackBar(v, "Press the calendar button to select a date properly", this).show()
                }
            }

        }

        add_event_prem_date_btn.setOnClickListener {

            initDateVariables()
            val dialog = DatePickerDialog(
                this@AddEventPremActivity, R.style.MyTimePickerDialogTheme, this@AddEventPremActivity,
                initYear, initMonth, initDay
            )
            dialog.show()
        }

        add_event_prem_location_btn.setOnClickListener {
            try{
                val placeIntent = IntentUtils.startPlacePicker(this)
                startActivityForResult(placeIntent, IntentUtils.PLACE_PICKER_REQUEST_CODE)

            }catch (e: Exception){
                if (e is GooglePlayServicesNotAvailableException){

                    val v = findViewById<View>(android.R.id.content)
                    AppUtils.getCustomSnackBar(v, "Google Play Services is not available on this device", this).show()
                }else if(e is GooglePlayServicesRepairableException){
                    val v = findViewById<View>(android.R.id.content)
                    AppUtils.getCustomSnackBar(v, "Please try updating your Google Play Services", this).show()
                }

            }
        }

        add_event_prem_poster_container.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(
                        this@AddEventPremActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), IntentUtils.photoPermissionRequestCode)
                } else {
                    startActivityForResult(IntentUtils.selectPhotoIntent(), IntentUtils.photoIntentUniqueID)
                }

            } else {
                startActivityForResult(IntentUtils.selectPhotoIntent(), IntentUtils.photoIntentUniqueID)
            }
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            when (requestCode ) {

                IntentUtils.photoIntentUniqueID -> {
                    if (resultCode == Activity.RESULT_OK) {
                        val firstUri = data!!.data
                        CropImage.activity(firstUri)
                            .setAspectRatio(1, 1)
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .start(this)
                    }
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)

                    if (resultCode == Activity.RESULT_OK) {

                        eventPicUri = result.uri
                        add_event_prem_poster_image.setImageURI(eventPicUri)
                        add_event_prem_poster_image.visibility = VISIBLE
                        event_prem_add_poster_text.run {
                            text = "Image Selected!"
                            setTextColor(ContextCompat.getColor(this@AddEventPremActivity, R.color.colorPrimary))
                        }
                        add_event_prem_poster_container.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
                    }
                }

                IntentUtils.PLACE_PICKER_REQUEST_CODE -> {

                    if (data != null) {

                        val place = PlacePicker.getPlace(this, data)
                        add_event_prem_location_edit.setText("${place.name}, ${place.address}")

                    }
                }

            }
        }

    }

    private fun initDateVariables(){
        val c = Calendar.getInstance()
        initDay = c.get(Calendar.DAY_OF_WEEK)
        initMonth = c.get(Calendar.MONTH)
        initYear = c.get(Calendar.YEAR)
        initHour = c.get(Calendar.HOUR)
        initMin = c.get(Calendar.MINUTE)
    }

    override fun onBackPressed() {
        val eventTitle = add_event_prem_title_edit.text.toString().trim()
        val eventDesc = add_event_prem_desc_edit.text.toString().trim()
        val eventDate = add_event_prem_date_edit.text.toString().trim()
        val eventLocation = add_event_prem_location_edit.text.toString().trim()

        if (eventTitle.isNotEmpty() || eventDate.isNotEmpty() || eventDesc.isNotEmpty() ||
            eventLocation.isNotEmpty() || eventTag != null || eventPicUri != null || eventFinalDateLong != null){
            val dialog = AlertDialog.Builder(this, R.style.MyTimePickerDialogTheme)
            dialog.setMessage("Do you want to discard your changes?")
            dialog.setPositiveButton("DISCARD") { _, _ ->
                finish()
            }

            dialog.setNegativeButton("CANCEL") { dialogInterface, _ ->

                dialogInterface.dismiss()
            }

            val alert = dialog.create()
            alert.show()
        }else{
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == IntentUtils.photoPermissionRequestCode) {
            val galleryIntent = IntentUtils.selectPhotoIntent()
            startActivityForResult(galleryIntent, IntentUtils.photoIntentUniqueID)

        } else {
            Toast.makeText(this@AddEventPremActivity, "Permission Denied", Toast.LENGTH_LONG)
                .show()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), IntentUtils.photoPermissionRequestCode)
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

}

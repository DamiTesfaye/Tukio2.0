package io.neolution.eventify.View.Activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.view.View.GONE
import android.widget.*
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import io.neolution.eventify.Data.Adapters.ExploreTagsAdapter
import io.neolution.eventify.Data.ModelClasses.EventsModel
import io.neolution.eventify.Data.ModelClasses.indicate
import io.neolution.eventify.Listeners.OnEventTagsSelected
import io.neolution.eventify.Listeners.OnGuestAddedListener
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.Utils.AppUtils
import io.neolution.eventify.Utils.DateUtils.Companion.resolveDate
import io.neolution.eventify.Utils.DateUtils.Companion.resovleTime
import io.neolution.eventify.Utils.IntentUtils
import io.neolution.eventify.View.CustomViews.GuestDialogFragment
import io.neolution.eventify.View.CustomViews.TagsDialogFragment
import java.util.*
import kotlin.collections.HashMap


class AddEventActivity : AppCompatActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, OnGuestAddedListener, OnEventTagsSelected {

    override fun onTagsSelected(chipTexts: MutableList<String>) {

        eventTags = chipTexts

        val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        val adapter = ExploreTagsAdapter(chipTexts, this, this)
        addEventTags.run {
            setHasFixedSize(true)
            layoutManager = manager
        }

        addEventTags.adapter = adapter
        if (tagsDialog.isVisible) tagsDialog.dismiss()
    }

    override fun onGuestAdded(name: String, bio: String) {

        val view = layoutInflater.inflate(R.layout.speaker_layout, addSpeakersLayout, false)
        val guestNameTv = view.findViewById<TextView>(R.id.speaker_layout_name_tv)
        val guestBioTv = view.findViewById<TextView>(R.id.speaker_layout_bio_tv)

        guestNameTv.text = name
        guestBioTv.text = bio


        listOfGuests[name] = bio
        addSpeakersLayout.addView(view)

        if (guestDialog.isVisible) guestDialog.dismiss()

    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

        finalHour = hourOfDay
        finalMin = minute

        val time = resovleTime(finalHour, finalMin)
        val date = resolveDate(finalDay, finalMonth, finalYear)

        eventDate = "$date, $time"
        eventDateTV.text = eventDate
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        finalDay = dayOfMonth
        finalMonth = month + 1
        finalYear = year


        val dialog = TimePickerDialog(
            this@AddEventActivity, R.style.MyTimePickerDialogTheme,
            this@AddEventActivity, initHour, initMin, true
        )
        dialog.show()

        eventDate = resolveDate(finalDay, finalMonth, finalYear)
        eventDateTV.text = eventDate

    }

    private var dialog: ProgressDialog? = null

    override fun onClick(v: View?) {
        when (v!!.id){

            R.id.close_add_event -> {

                eventTitle = eventTitleET.text.toString()
                eventDesc = eventDescET.text.toString()

                if (!eventTitle.isNullOrEmpty() || !eventDesc.isNullOrEmpty() || eventTags.isNotEmpty()){

                    val dialog = AlertDialog.Builder(this, R.style.MyTimePickerDialogTheme)
                    dialog.setMessage("Do you want to discard your changes?")
                    dialog.setPositiveButton("YES") { _, _ ->
                        onBackPressed()
                    }

                    dialog.setNegativeButton("NO") { dialogInterface, _ ->

                        dialogInterface.dismiss()
                    }

                    val alert = dialog.create()
                    alert.show()

                }else{

                    //Close activity
                    onBackPressed()

                }

            }

            R.id.add_event_event_date_tv -> {

                initDateVariables()

                val dialog = DatePickerDialog(
                    this@AddEventActivity, R.style.MyTimePickerDialogTheme, this@AddEventActivity,
                    initYear, initMonth, initDay
                )
                dialog.show()

            }

            R.id.add_event_event_poster_layout -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(
                            this@AddEventActivity,
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

            R.id.add_event, R.id.add_event_event_add_event_aux_button -> {

                eventTitle = eventTitleET.text.toString()
                eventDesc = eventDescET.text.toString()
                eventTicketLink = eventTicketLinkET.text.toString()
                eventRegLink = eventRegLinkET.text.toString()
                eventDressCode = eventDressCodeET.text.toString()
                userUID = AuthRepo.getUserUid()
                val eventPostTime = System.currentTimeMillis().toString()


                if (!eventTitle.isNullOrEmpty() && !eventDate.isNullOrEmpty() && !eventDesc.isNullOrEmpty() && eventTags.isNotEmpty() && !imageUri.toString().isNullOrEmpty()){

                    if(eventLocationFromMap != null){
                         dialog = AppUtils.instantiateProgressDialog("Sending your event", this)
                        dialog?.show()


                        val finalEventModel = EventsModel ("normal", eventTitle!!, eventDesc!!, eventLocationFromMap!!, eventDate!!,
                            eventTags, eventRegLink
                            , userUID, eventTicketLink, eventDressCode, imageUri.toString() , listOfGuests, null, eventPostTime, null)

                        FireStoreRepo().postEvent(this, finalEventModel, {
                            if(dialog != null){
                                if (dialog!!.isShowing){
                                    dialog!!.dismiss()
                                }
                            }

                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()

                        }, {

                        if (dialog != null){
                            if (dialog!!.isShowing){
                                dialog!!.dismiss()
                            }
                        }
                            indicate(it)
                        })
                    }else{
                        val dialog = AppUtils.instantiateProgressDialog("Sending your event", this)
                        dialog.show()


                        val finalEventModel = EventsModel ("normal",eventTitle!!, eventDesc!!, eventLocationET.text.toString(), eventDate!!,
                            eventTags, eventRegLink
                            , userUID, eventTicketLink, eventDressCode, imageUri.toString() , listOfGuests, null, eventPostTime, null)

                        FireStoreRepo().postEvent(this, finalEventModel, {
                            dialog.dismiss()
                            startActivity(Intent(this, HomeActivity::class.java))
                        }, {
                            dialog.dismiss()
                            indicate(it)
                        })
                    }

                }else{
                    indicate("Please fill in all compulsory fields")
                }
            }

            R.id.add_event_event_tags_fab -> {
                tagsDialog = TagsDialogFragment()
                tagsDialog.show(supportFragmentManager, "")

            }

            R.id.add_event_event_guests_fab -> {

                guestDialog = GuestDialogFragment()
                guestDialog.show(supportFragmentManager, "")

            }

            R.id.add_event_event_date_fab -> {

                initDateVariables()
                val dialog = DatePickerDialog(
                    this@AddEventActivity, this@AddEventActivity,
                    initYear, initMonth, initDay
                )
                dialog.show()
            }

            R.id.add_event_event_location_fab -> {

                try{
                    val placeIntent = IntentUtils.startPlacePicker(this)
                    startActivityForResult(placeIntent, IntentUtils.PLACE_PICKER_REQUEST_CODE)

                }catch (e: Exception){
                    if (e is GooglePlayServicesNotAvailableException){
                        indicate("Sorry you do not have Google Play Services Installed. Please type in your location or Download Google Play Services")
                    }else if(e is GooglePlayServicesRepairableException){
                        indicate("Please try updating your Google Play Services")
                    }

                }

            }

            R.id.add_event_event_poster_fab -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(
                            this@AddEventActivity,
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
    }

    //Event Variables
    private var eventTitle: String? = null
    private var eventDesc: String? = null
    private var eventLocationFromMap: String? = null
    private var imageUri: Uri? = null
    private var eventDate: String? = null
    private lateinit var userUID: String
    private var eventTags: MutableList<String> = mutableListOf()
    private var eventRegLink: String? = null
    private var eventTicketLink: String? = null
    private var eventDressCode: String? = null


    //Lists
    private var listOfGuests: HashMap<String, Any> = HashMap()

    //Layouts for adding views
    private lateinit var addSpeakersLayout: LinearLayout
    private lateinit var addPhotosLayout: LinearLayout
    private lateinit var addEventTags: RecyclerView

    //Buttons
    private lateinit var closeActivity: ImageButton
    private lateinit var addEvent: ImageButton
    private lateinit var eventLocationFab: ImageButton
    private lateinit var eventPosterFab: ImageButton
    private lateinit var eventDateFab: ImageButton

    //EditTexts
    private lateinit var eventTitleET: EditText
    private lateinit var eventDressCodeET: EditText
    private lateinit var eventRegLinkET: EditText
    private lateinit var eventTicketLinkET: EditText
    private lateinit var eventDescET: EditText
    private lateinit var eventLocationET: EditText

    //FABs
    private lateinit var eventGuestsFab: FloatingActionButton
    private lateinit var eventTagsFab: FloatingActionButton

    //Result and Hint TextViews
    private lateinit var eventDateTV: TextView
    private lateinit var eventPosterImageTV: TextView

    //Date-Time variables
    private var initDay: Int = 0
    private var initMonth: Int = 0
    private var initYear: Int = 0
    private var initHour: Int = 0
    private var initMin: Int = 0

    private var finalDay: Int = 0
    private var finalMonth: Int = 0
    private var finalYear: Int = 0
    private var finalHour: Int = 0
    private var finalMin: Int = 0

    //Init-ers
    private lateinit var guestDialog: GuestDialogFragment
    private lateinit var tagsDialog: TagsDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        closeActivity = findViewById(R.id.close_add_event)
        addEvent = findViewById(R.id.add_event)

        //Initing Layouts
        addEventTags = findViewById(R.id.adding_tags_layout)
        addSpeakersLayout = findViewById(R.id.adding_speakers_layout)
        addPhotosLayout = findViewById(R.id.add_event_event_poster_layout)

        //Initing ETs
        eventTitleET = findViewById(R.id.add_event_event_title_et)
        eventDressCodeET = findViewById(R.id.add_event_event_dresscode_et)
        eventRegLinkET = findViewById(R.id.add_event_event_reg_link_et)
        eventTicketLinkET = findViewById(R.id.add_event_event_ticket_link_et)
        eventDescET = findViewById(R.id.add_event_event_desc_et)
        eventLocationET = findViewById(R.id.add_event_event_location_et)

        //Initing TVs
        eventDateTV = findViewById(R.id.add_event_event_date_tv)
        eventPosterImageTV = findViewById(R.id.add_event_event_poster_hint_tv)

        //Initing FABs
        eventLocationFab = findViewById(R.id.add_event_event_location_fab)
        eventPosterFab = findViewById(R.id.add_event_event_poster_fab)
        eventDateFab = findViewById(R.id.add_event_event_date_fab)
        eventGuestsFab = findViewById(R.id.add_event_event_guests_fab)
        eventTagsFab = findViewById(R.id.add_event_event_tags_fab)


        //Initing onClickListeners
        closeActivity.setOnClickListener(this)
        addEvent.setOnClickListener(this)
        addPhotosLayout.setOnClickListener(this)
        eventDateTV.setOnClickListener(this)
        eventGuestsFab.setOnClickListener(this)
        eventPosterFab.setOnClickListener(this)
        eventDateFab.setOnClickListener(this)
        eventLocationFab.setOnClickListener(this)
        eventTagsFab.setOnClickListener(this)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == IntentUtils.photoPermissionRequestCode) {
            val galleryIntent = IntentUtils.selectPhotoIntent()
            startActivityForResult(galleryIntent, IntentUtils.photoIntentUniqueID)

        } else {
            Toast.makeText(this@AddEventActivity, "Permission Denied", Toast.LENGTH_LONG)
                .show()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), IntentUtils.photoPermissionRequestCode)
            }
        }
    }

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

                        imageUri = result.uri
                        val view = layoutInflater.inflate(R.layout.imageposter_view, addPhotosLayout, false)

                        (view as CircleImageView).setImageURI(imageUri)

                        eventPosterImageTV.visibility = GONE

                        addPhotosLayout.apply {

                            removeAllViews()
                            addView(view)

                        }
                    }
                }

                IntentUtils.PLACE_PICKER_REQUEST_CODE -> {

                    if (data != null) {

                        val place = PlacePicker.getPlace(this, data)
                        eventLocationET.setText(place.name)
                        eventLocationFromMap = "${place.name}, ${place.address}"

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

    override fun onDestroy() {
        super.onDestroy()


        if (dialog != null) {
            if (dialog!!.isShowing){
                dialog!!.dismiss()
            }
        }
    }

    override fun onPause() {
        super.onPause()

        if (dialog != null) {
            if (dialog!!.isShowing){
                dialog!!.dismiss()
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()

        eventTitle = eventTitleET.text.toString()
        eventDesc = eventDescET.text.toString()

        if (!eventTitle.isNullOrEmpty() || !eventDesc.isNullOrEmpty() ){

            val dialog = AlertDialog.Builder(this, R.style.MyTimePickerDialogTheme)
            dialog.setMessage("Do you want to discard your changes?")
            dialog.setPositiveButton("YES") { _, _ ->
                onBackPressed()
            }

            dialog.setNegativeButton("NO") { dialogInterface, _ ->

                dialogInterface.dismiss()
            }

            val alert = dialog.create()
            alert.show()

        }
    }

}

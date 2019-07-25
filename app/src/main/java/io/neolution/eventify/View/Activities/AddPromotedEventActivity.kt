package io.neolution.eventify.View.Activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.Transaction
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import io.neolution.eventify.Data.Adapters.ExploreTagsAdapter
import io.neolution.eventify.Data.ModelClasses.EventsModel
import io.neolution.eventify.Data.ModelClasses.indicate
import io.neolution.eventify.Listeners.OnAudienceRangeSelected
import io.neolution.eventify.Listeners.OnCardDetailsInputted
import io.neolution.eventify.Listeners.OnEventTagsSelected
import io.neolution.eventify.Listeners.OnGuestAddedListener
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Repos.CardDetailsRepo
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.Utils.AppUtils
import io.neolution.eventify.Utils.DateUtils
import io.neolution.eventify.Utils.IntentUtils
import io.neolution.eventify.View.CustomViews.*
import kotlinx.android.synthetic.main.activity_add_event.*
import kotlinx.android.synthetic.main.activity_add_promoted_event.*
import java.util.*
import kotlin.collections.HashMap

class AddPromotedEventActivity : AppCompatActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, OnGuestAddedListener, OnEventTagsSelected,OnAudienceRangeSelected,
    OnCardDetailsInputted{

    override fun onCardInputted(card: Card) {
        if (mAmountPaid != null && card.isValid){

                cardDetailsDialog.dismiss()

                val charge = Charge()
                charge.card = card
                charge.email = AuthRepo.getCurrentUser()!!.email
                charge.amount = mAmountPaid!!

                val dialog = AppUtils.instantiateProgressDialog("Charging card..", this)
                dialog.show()

                PaystackSdk.chargeCard(this, charge, object : Paystack.TransactionCallback {

                    override fun onSuccess(transaction: Transaction?) {
                        dialog.setMessage("Charge Done! Now sending your event..")
                        sendEvent({
                            dialog.dismiss()

                            indicate("Promoted Event Sent Successfully")

                            startActivity(Intent(this@AddPromotedEventActivity, HomeActivity::class.java))
                            finish()

                        }, {exception : String ->
                            dialog.dismiss()
                            indicate(exception)

                            startActivity(Intent(this@AddPromotedEventActivity, HomeActivity::class.java))
                            finish()

                        })

                    }

                    override fun beforeValidate(transaction: Transaction?) {

                    }

                    override fun onError(error: Throwable?, transaction: Transaction?) {
                        dialog.dismiss()
                        indicate("${error?.localizedMessage}")
                        Log.e(AddPromotedEventActivity::class.java.simpleName, error!!.localizedMessage)
                    }

                })



        }else{
            indicate("Sorry your card is not valid")
        }

    }

    private fun sendEvent(onSuccessful : () -> Unit, onFailure: (exception: String) -> Unit) {
        val eventModel = getLoadFinalModel()
        eventModel.amountPaid = (mAmountPaid!! / 100).toLong()
        FireStoreRepo().postPromotedEvent(context = this, eventsModel = eventModel, ifCompleted = onSuccessful, ifNotCompeted = onFailure)
    }

    override fun onAudienceRangeSelected(amountPaid: Int) {
        mAmountPaid = amountPaid
        audienceDialog.dismiss()

        cardDetailsDialog = CardDetailsDialog()

        val bundle = Bundle()
        bundle.putInt("amountPaid", (amountPaid / 100))

        cardDetailsDialog.arguments = bundle
        cardDetailsDialog.show(supportFragmentManager, "")

    }

    override fun onGuestAdded(name: String, bio: String) {

        val view = layoutInflater.inflate(R.layout.speaker_layout, promoted_adding_speakers_layout, false)
        val guestNameTv = view.findViewById<TextView>(R.id.speaker_layout_name_tv)
        val guestBioTv = view.findViewById<TextView>(R.id.speaker_layout_bio_tv)

        guestNameTv.text = name
        guestBioTv.text = bio

        listOfGuests[name] = bio
        adding_speakers_layout.addView(view)

        if (guestDialog.isVisible) guestDialog.dismiss()

    }

    override fun onTagsSelected(chipTexts: MutableList<String>) {
        eventTags = chipTexts
        val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        val adapter = ExploreTagsAdapter(chipTexts, this, this)
        promoted_adding_tags_layout.run{
            setHasFixedSize(true)
            layoutManager = manager
        }

        promoted_adding_tags_layout.adapter = adapter
        if (tagsDialog.isVisible) tagsDialog.dismiss()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        finalDay = dayOfMonth
        finalMonth = month + 1
        finalYear = year


        val dialog = TimePickerDialog(
            this@AddPromotedEventActivity, R.style.MyTimePickerDialogTheme,
            this@AddPromotedEventActivity, initHour, initMin, true
        )
        dialog.show()

        eventDate = DateUtils.resolveDate(finalDay, finalMonth, finalYear)
        add_promoted_event_event_date_tv.text = eventDate

    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

        finalHour = hourOfDay
        finalMin = minute

        val time = DateUtils.resovleTime(finalHour, finalMin)
        val date = DateUtils.resolveDate(finalDay, finalMonth, finalYear)

        eventDate = "$date, $time"
        add_promoted_event_event_date_tv.text = eventDate

    }

    lateinit var eventDate: String
    private var imageUri: Uri? = null
    private var mAmountPaid: Int? = null
    private var eventLocationFromMap: String? = null

    private var eventTags: MutableList<String> = mutableListOf()
    private var listOfGuests: HashMap<String, Any> = HashMap()

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

    private lateinit var guestDialog: GuestDialogFragment
    private lateinit var tagsDialog: TagsDialogFragment
    private lateinit var audienceDialog: AudienceRangeDialogFragment
    private lateinit var cardDetailsDialog: CardDetailsDialog

    override fun onClick(v: View?) {
        when (v!!.id){

            R.id.add_promoted_event_close_activity -> {
                if (!add_promoted_event_event_desc_et.text.toString().isEmpty() ||
                    !add_promoted_event_event_location_et.text.toString().isEmpty() ||
                    !add_promoted_event_event_title_et.text.toString().isEmpty()) {

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

            R.id.add_promoted_event_event_date_tv -> {

                initDateVariables()

                val dialog = DatePickerDialog(
                    this@AddPromotedEventActivity,  R.style.MyTimePickerDialogTheme, this@AddPromotedEventActivity,
                    initYear, initMonth, initDay
                )
                dialog.show()

            }

            R.id.add_promoted_event_event_poster_layout -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(
                            this@AddPromotedEventActivity,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), IntentUtils.photoPermissionRequestCode)
                    } else {

                        CropImage.activity(null)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setMinCropResultSize(512, 512)
                            .setAspectRatio(1, 1)
                            .start(this)

//                        startActivityForResult(IntentUtils.selectPhotoIntent(), IntentUtils.photoIntentUniqueID)
                    }

                } else {
//                    startActivityForResult(IntentUtils.selectPhotoIntent(), IntentUtils.photoIntentUniqueID)
                }
            }

            R.id.add_promoted_event_btn -> {

                if (!add_promoted_event_event_desc_et.text.toString().isEmpty() &&
                    !add_promoted_event_event_location_et.text.toString().isEmpty() && !eventDate.isEmpty() &&
                    !add_promoted_event_event_title_et.text.toString().isEmpty() && eventTags.isNotEmpty()) {

                    if (!imageUri.toString().isNullOrEmpty()){
                        getLoadFinalModel()
                        audienceDialog = AudienceRangeDialogFragment()
                        audienceDialog.show(supportFragmentManager, "")

                    }

                }else{
                    indicate("Please fill in all fields")
                }

            }

            R.id.add_promoted_event_event_tags_fab -> {
                tagsDialog = TagsDialogFragment()
                tagsDialog.show(supportFragmentManager, "")
            }

            R.id.add_promoted_event_event_guests_fab -> {
                guestDialog = GuestDialogFragment()
                guestDialog.show(supportFragmentManager, "")
            }

            R.id.add_promoted_event_event_date_fab -> {
                initDateVariables()
                val dialog = DatePickerDialog(
                    this@AddPromotedEventActivity, this@AddPromotedEventActivity,
                    initYear, initMonth, initDay
                )
                dialog.show()
            }

            R.id.add_promoted_event_event_location_fab -> {

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

            R.id.add_promoted_event_event_poster_fab -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(
                            this@AddPromotedEventActivity,
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_promoted_event)

        val isFirstTime = CardDetailsRepo(this).getFirstTimePromoting()
        if (isFirstTime){
            val promotedDialog = PromotedDialog()
            promotedDialog.show(supportFragmentManager, "")
        }

        add_promoted_event_close_activity.setOnClickListener(this)
        add_promoted_event_event_date_tv.setOnClickListener(this)
        add_promoted_event_event_poster_layout.setOnClickListener(this)
        add_promoted_event_btn.setOnClickListener(this)
        add_promoted_event_event_tags_fab.setOnClickListener(this)
        add_promoted_event_event_guests_fab.setOnClickListener(this)
        add_promoted_event_event_date_fab.setOnClickListener(this)
        add_promoted_event_event_location_fab.setOnClickListener(this)
        add_promoted_event_event_poster_fab.setOnClickListener(this)

//        add_pro_event_event_poster_fab.setOnClickListener(this)
    }

    fun initDateVariables(){
        val c = Calendar.getInstance()
        initDay = c.get(Calendar.DAY_OF_WEEK)
        initMonth = c.get(Calendar.MONTH)
        initYear = c.get(Calendar.YEAR)
        initHour = c.get(Calendar.HOUR)
        initMin = c.get(Calendar.MINUTE)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == IntentUtils.photoPermissionRequestCode) {
            val galleryIntent = IntentUtils.selectPhotoIntent()
            startActivityForResult(galleryIntent, IntentUtils.photoIntentUniqueID)

        } else {
            Toast.makeText(this@AddPromotedEventActivity, "Permission Denied", Toast.LENGTH_LONG)
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
                        val view = layoutInflater.inflate(R.layout.imageposter_view, add_promoted_event_event_poster_layout, false)

                        (view as CircleImageView).setImageURI(imageUri)

                        add_promoted_event_event_poster_hint_tv.visibility = View.GONE

                        add_promoted_event_event_poster_layout.apply {

                            removeAllViews()
                            addView(view)

                        }
                    }
                }

                IntentUtils.PLACE_PICKER_REQUEST_CODE -> {

                    if (data != null) {

                        val place = PlacePicker.getPlace(this, data)
                        add_promoted_event_event_location_et.setText(place.name)
                        eventLocationFromMap = "${place.name}, ${place.address}"

                    }


                }

            }
        }

    }

    private fun getLoadFinalModel(): EventsModel{

        val eventDesc = add_promoted_event_event_desc_et.text.toString()
        val eventLocation = add_promoted_event_event_location_et.text.toString()
        val eventTitle = add_promoted_event_event_title_et.text.toString()
        val eventTicketLink = add_promoted_event_event_ticket_link_et.text.toString()
        val eventRegLink = add_promoted_event_event_reg_link_et.text.toString()
        val eventDressCode = add_promoted_event_event_dresscode_et.text.toString()
        val eventPostTime = System.currentTimeMillis().toString()

        return if (eventLocationFromMap != null){
//            val dialog = AppUtils.instantiateProgressDialog("Sending your event", this)
//            dialog.show()

            EventsModel(eventType = "promoted", eventDesc = eventDesc, eventDate = eventDate, eventTitle = eventTitle, eventLocation =  eventLocationFromMap!!, eventTicketLink = eventTicketLink,
                eventRegLink = eventRegLink, eventDressCode = eventDressCode, eventPostTime = eventPostTime, amountPaid = null,  userUID = AuthRepo.getUserUid(),
                eventTags = eventTags, eventImageLinkThumb = null, eventImageLink = imageUri.toString(), eventGuests = listOfGuests
            )

        }else {

            EventsModel(eventType = "promoted", eventDesc = eventDesc, eventDate = eventDate, eventTitle = eventTitle, eventLocation =  eventLocation, eventTicketLink = eventTicketLink,
                eventRegLink = eventRegLink, eventDressCode = eventDressCode, eventPostTime = eventPostTime, amountPaid = null, userUID = AuthRepo.getUserUid(),
                eventTags = eventTags, eventImageLinkThumb = null, eventImageLink = imageUri.toString(), eventGuests = listOfGuests
            )
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (!add_promoted_event_event_desc_et.text.toString().isEmpty() ||
            !add_promoted_event_event_location_et.text.toString().isEmpty() ||
            !add_promoted_event_event_title_et.text.toString().isEmpty()) {

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

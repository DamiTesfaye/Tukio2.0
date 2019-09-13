package com.exceptos.tukio.View.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.exceptos.tukio.Data.ModelClasses.UserModel
import com.exceptos.tukio.Data.ModelClasses.breakDownToUserModel
import com.exceptos.tukio.R
import com.exceptos.tukio.Repos.AuthRepo
import com.exceptos.tukio.Repos.FireStoreRepo
import com.exceptos.tukio.Utils.AppUtils
import com.exceptos.tukio.Utils.IntentUtils
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {

    private var userModel: UserModel? = null
    private var userImageUri: String? = null
    private var changesMade = false
    private lateinit var backBtn: ImageButton

    private lateinit var imageContainer: FrameLayout
    private lateinit var userImage: ImageView

    private lateinit var nameLayout: TextInputLayout
    private lateinit var nameEdit: TextInputEditText
    private lateinit var bioLayout: TextInputLayout
    private lateinit var bioEdit: TextInputEditText

    private lateinit var saveContainer: RelativeLayout
    private lateinit var saveText: TextView
    private lateinit var saveProgress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        imageContainer = findViewById(R.id.edit_profile_container)
        userImage = findViewById(R.id.edit_profile_image_view)
        backBtn = findViewById(R.id.edit_profile_back_btn)

        nameLayout = findViewById(R.id.edit_profile_name_layout)
        nameEdit = findViewById(R.id.edit_profile_name_edit)
        bioLayout = findViewById(R.id.edit_profile_bio_layout)
        bioEdit = findViewById(R.id.edit_profile_bio_edit)

        saveContainer = findViewById(R.id.edit_profile_save_container)
        saveProgress = findViewById(R.id.edit_profile_save_progress)
        saveText = findViewById(R.id.edit_profile_save_text)

        backBtn.setOnClickListener {
            if (changesMade){
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

        imageContainer.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(
                        this@EditProfileActivity,
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

        val userDetails = FireStoreRepo().getUserAccountDetailsFromUid(AuthRepo.getUserUid())
        userDetails.get().addOnSuccessListener {snapshot ->
            if(snapshot != null && snapshot.exists()){
                userModel = snapshot.breakDownToUserModel()
                nameEdit.setText(userModel!!.userName)
                bioEdit.setText( userModel!!.userBio)

                val requestOptions = RequestOptions()
                requestOptions.placeholder(ContextCompat.getDrawable(this, R.drawable.ic_male_placeholder))
                val thumbNailRequest = Glide.with(this.applicationContext).load(userModel!!.userThumbLink)

                Glide.with(this.applicationContext)
                    .setDefaultRequestOptions(requestOptions)
                    .asDrawable()
                    .load(userModel!!.userImage)
                    .thumbnail(thumbNailRequest)
                    .into(userImage)
            }
        }.addOnFailureListener {

        }

        saveText.setOnClickListener {
            if (changesMade){
                val nameText = nameEdit.text.toString().trim()
                val bioText = bioEdit.text.toString().trim()

                if (nameText.isNotEmpty()){

                    saveContainer.background = ContextCompat.getDrawable(this, R.drawable.buttonbg_outline)
                    saveText.visibility = GONE
                    saveProgress.visibility = VISIBLE

                    FireStoreRepo().updateUserAccount(userName = nameText, userBio = bioText, userPicLink = userImageUri, ifCompleted = {

                        Toast.makeText(this, "Profile Updated!", Toast.LENGTH_LONG)
                            .show()
                        finish()

                    }, ifNotCompeted = {

                        saveContainer.background = ContextCompat.getDrawable(this, R.drawable.buttonbg)
                        saveText.visibility = VISIBLE
                        saveProgress.visibility = GONE

                        val v = findViewById<View>(android.R.id.content)
                        AppUtils.getCustomSnackBar(v, "Check your internet connection and try again", this).show()
                    })
                }else{
                    val v = findViewById<View>(android.R.id.content)
                    AppUtils.getCustomSnackBar(v, "Name cannot be empty", this).show()
                }
            }
        }

        nameEdit.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && userModel != null){
                    if (p0 != userModel!!.userName){
                        changesMade = true
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        bioEdit.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && userModel != null){
                    if (p0 != userModel!!.userBio){
                        changesMade = true
                    }
                }
            }
        })

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == IntentUtils.photoPermissionRequestCode) {
            val galleryIntent = IntentUtils.selectPhotoIntent()
            startActivityForResult(galleryIntent, IntentUtils.photoIntentUniqueID)

        } else {
            Toast.makeText(this@EditProfileActivity, "Permission Denied", Toast.LENGTH_LONG)
                .show()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), IntentUtils.photoPermissionRequestCode)
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
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .start(this)
                    }
                    edit_profile_trans_image.visibility = GONE
                }


                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    if (resultCode == Activity.RESULT_OK) {
                        userImage.setImageURI(result.uri)
                        userImageUri = result.uri.toString()
                    }
                }

            }
        }

    }
}

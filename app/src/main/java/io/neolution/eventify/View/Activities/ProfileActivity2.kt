package io.neolution.eventify.View.Activities

import android.Manifest
import android.app.Activity
import android.app.ActivityOptions
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.ConnectionResult
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import io.neolution.eventify.Data.ModelClasses.breakDownToUserModel
import io.neolution.eventify.Data.ModelClasses.indicate
import io.neolution.eventify.Data.ViewModels.ProfileViewModel
import io.neolution.eventify.Listeners.OnNameBioInputted
import io.neolution.eventify.R
import io.neolution.eventify.Repos.AuthRepo
import io.neolution.eventify.Repos.FireStoreRepo
import io.neolution.eventify.Utils.AppUtils
import io.neolution.eventify.Utils.IntentUtils
import io.neolution.eventify.View.CustomViews.NameBioDialog
import io.neolution.eventify.View.Fragments.AuthFragment.SignupFragments
import kotlinx.android.synthetic.main.activity_profile2.*

class ProfileActivity2 : AppCompatActivity(), OnNameBioInputted {

    override fun onNameBioDone(name: String, bio: String) {

        profileii_username.text = name
        profileii_userbio.text = bio

    }

    private var imageUri2: Uri? = null
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var userImage: CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R .layout.activity_profile2)


        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        userImage = findViewById(R.id.profileii_userimage)

        profileii_edit_username_bio.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("userName", profileii_username.text.toString())
            bundle.putString("userBio", profileii_userbio.text.toString())

            val dialog = NameBioDialog()
            dialog.arguments = bundle

            dialog.show(supportFragmentManager, "tag")
        }

        profileii_close.setOnClickListener {
            onBackPressed()
        }

        profileii_save_user_details.setOnClickListener {
            val userName = profileii_username.text.toString()
            val userBio = profileii_userbio.text.toString()

            if (userName.isNotEmpty() && userBio.isNotEmpty()){

                val dialog = AppUtils.instantiateProgressDialog("Making Your Changes", this)
                dialog.show()

                if (imageUri2 != null ){


                    val userPicLink = imageUri2.toString()
                    profileViewModel.updateUserAccount(userName = userName, userBio = userBio, userPicLink = userPicLink, ifNotSuccessful = {

                        dialog.dismiss()
                        AppUtils.getCustomSnackBar(v = it, context = this, m = "Sorry an error occurred. Please try again").show()

                    }, if_Successful = {

                        dialog.dismiss()
                        AppUtils.getCustomSnackBar(v = it, context = this, m = "Your details have been saved!").show()

                    })
                }else{

                    profileViewModel.updateUserAccount(userName = userName, userBio = userBio, userPicLink = null, ifNotSuccessful = {

                        dialog.dismiss()
                        AppUtils.getCustomSnackBar(v = it, context = this, m = "Sorry an error occurred. Please try again").show()

                    }, if_Successful = {

                        dialog.dismiss()
                        AppUtils.getCustomSnackBar(v = it, context = this, m = "Your details have been saved!").show()

                    })

                }
            }
        }

        profileii_edit_interests.setOnClickListener {
            val intent = Intent(this, TagsActivity::class.java)
            intent.putExtra("startedFrom", ProfileActivity2::class.java.simpleName)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent,  ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            }else{
                startActivity(intent)
            }
        }

        profileii_view_posted_events.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(Intent(this, PostedEventsActivity::class.java),
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            }else{
                startActivity(Intent(this, PostedEventsActivity::class.java))
            }
        }

        profileii_view_promoted_events.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(Intent(this, PromotedEventsActivity::class.java),
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            }else{
                startActivity(Intent(this, PromotedEventsActivity::class.java))
            }
        }

        profileii_signout.setOnClickListener {
            val dialog = android.support.v7.app.AlertDialog.Builder(this, R.style.MyTimePickerDialogTheme)
            dialog.setMessage("Are you sure you want to sign out??")
            dialog.setCancelable(false)
            dialog.setPositiveButton("Yes"){
                    _, _ ->

                AuthRepo.signOut()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(Intent(this, AuthActivity::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                    finish()
                }else{
                    startActivity(Intent(this, AuthActivity::class.java))
                    finish()
                }
                indicate("Signed out!")

            }
            dialog.setNegativeButton("No"){
                    dialogInterface, _ ->

                dialogInterface.dismiss()
            }

            dialog.show()
        }

        profileii_select_image.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(
                        this@ProfileActivity2,
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
                }

            } else {

                CropImage.activity(null)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMinCropResultSize(512, 512)
                    .setAspectRatio(1, 1)
                    .start(this)
            }
        }

        loadUserDetails()
    }

    private fun loadUserDetails() {

        FireStoreRepo().getCurrentUserDocumentPath().addSnapshotListener(this){
            snapshot, _ ->
            if (snapshot != null && snapshot.exists()){
                val userModel = snapshot.breakDownToUserModel()

                profileii_username.text = userModel.userName
                profileii_userbio.text = userModel.userBio

                val requestOptions = RequestOptions()
                requestOptions.placeholder(ContextCompat.getDrawable(this, R.drawable.eventify_placeholder))

                Glide.with(this.applicationContext)
                    .setDefaultRequestOptions(requestOptions)
                    .asDrawable()
                    .load(userModel.userImage)
                    .into(userImage)
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == IntentUtils.photoPermissionRequestCode) {
            val galleryIntent = IntentUtils.selectPhotoIntent()
            startActivityForResult(galleryIntent, IntentUtils.photoIntentUniqueID)

        } else {
            indicate("Permission denied!")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), IntentUtils.photoPermissionRequestCode)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

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
                        imageUri2 = result.uri

                        Glide.with(this.applicationContext)
                            .asDrawable()
                            .load(imageUri2)
                            .into(userImage)



                    }
                }
            }

        }

    }
}

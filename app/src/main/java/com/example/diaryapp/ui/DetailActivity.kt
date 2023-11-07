package com.example.diaryapp.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.example.diaryapp.model.Diary
import com.example.diaryapp.FirebaseHandler
import com.example.diaryapp.Helper
import com.example.diaryapp.R

class DetailActivity : AppCompatActivity() {

    private lateinit var firebaseHandler: FirebaseHandler

    private lateinit var tvDate : TextView
    private lateinit var edtName : EditText
    private lateinit var edtContent : EditText
    private lateinit var ivDiary : ImageView

    private lateinit var btnAddImage : Button
    private lateinit var btnSave : Button

    private lateinit var progressBar: ProgressBar

    private lateinit var imageUri : String

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == RESULT_OK){
            val selectedImg : Uri = it.data?.data as Uri

            ivDiary.apply {
                setImageURI(selectedImg)
                visibility = View.VISIBLE
            }
            imageUri = selectedImg.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        firebaseHandler = FirebaseHandler()

        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tvDate = findViewById(R.id.tv_diary_date)
        edtName = findViewById(R.id.edt_diary_title)
        edtContent = findViewById(R.id.edt_diary_content)
        ivDiary = findViewById(R.id.iv_diary)

        btnAddImage = findViewById(R.id.btn_add_image)
        btnSave = findViewById(R.id.btn_save)

        progressBar = findViewById(R.id.progressBar)

        val diaryID = intent.getStringExtra(MainActivity.DIARY_KEY)

        tvDate.text = Helper.getCurrentDate()
        imageUri = ""

        if(diaryID!=null){
            showLoading(true)

            firebaseHandler.getDiary(diaryID){ success, diary ->
                if(success){
                    tvDate.text = diary?.date
                    edtName.setText(diary?.title)
                    edtContent.setText(diary?.content)

                    if(diary?.imageUri!=""){
                        btnAddImage.text = resources.getString(R.string.btn_update_iamge)
                        firebaseHandler.getDiaryImage(diaryID){ successImage, bitmap ->
                            if(successImage){
                                showLoading(false)
                                ivDiary.setImageBitmap(bitmap)
                                ivDiary.visibility = View.VISIBLE
                            }
                            else{
                                Toast.makeText(
                                    this@DetailActivity,
                                    resources.getString(R.string.failed_image),
                                    Toast.LENGTH_SHORT
                                ).show()
                                showLoading(false)
                            }
                        }
                    }
                    else showLoading(false)
                }
                else{
                    showLoading(false)
                    Toast.makeText(
                        this@DetailActivity,
                        resources.getString(R.string.failed_get),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


        btnAddImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            val chooserIntent = Intent.createChooser(intent, resources.getString(R.string.choose_picture))
            launcherIntentGallery.launch(chooserIntent)
        }

        btnSave.setOnClickListener {
            val date = tvDate.text.toString()
            val nama = edtName.text.toString()
            val content = edtContent.text.toString()
            when{
                nama.isEmpty() -> edtName.error = resources.getString(R.string.email_empty)
                content.isEmpty() -> edtContent.error = resources.getString(R.string.pass_empty)
                edtName.error==null && edtContent.error==null -> {
                    showLoading(true)

                    val id = diaryID ?: Helper.getDiaryKey(nama, date, Helper.getCurrentMinute())

                    firebaseHandler.insertDiary(
                        Diary(
                            id = id,
                            title = nama,
                            content = content,
                            date = date,
                            imageUri = imageUri
                        )
                    ){success ->
                        when {
                            success && imageUri != "" ->{
                                showLoading(true)
                                firebaseHandler.uploadDiaryImage(imageUri.toUri(), id){success ->
                                    if(!success){
                                        showLoading(false)
                                        Toast.makeText(
                                            this@DetailActivity,
                                            resources.getString(R.string.failed_upload),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    else {
                                        showLoading(false)
                                        startActivity(Intent(this@DetailActivity, MainActivity::class.java))
                                    }
                                }
                            }
                            !success -> {
                                showLoading(false)
                                Toast.makeText(
                                    this@DetailActivity,
                                    resources.getString(R.string.failed_save),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {
                                showLoading(false)
                                startActivity(Intent(this@DetailActivity, MainActivity::class.java))
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun showLoading(isLoading : Boolean){
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
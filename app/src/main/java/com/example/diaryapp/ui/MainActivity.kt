package com.example.diaryapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.model.Diary
import com.example.diaryapp.DiaryAdapter
import com.example.diaryapp.FirebaseHandler
import com.example.diaryapp.R


class MainActivity : AppCompatActivity() {

    private lateinit var rvDiary : RecyclerView
    private lateinit var tvUsername : TextView
    private lateinit var tvEmail : TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var firebaseHandler: FirebaseHandler
    private lateinit var diaryAdapter: DiaryAdapter
    private lateinit var diaries: List<Diary>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseHandler = FirebaseHandler()

        rvDiary = findViewById(R.id.rv_diary)
        tvUsername = findViewById(R.id.tv_username)
        tvEmail = findViewById(R.id.tv_email)
        progressBar = findViewById(R.id.progressBar)

        showLoading(true)
        firebaseHandler.getUserData { success, user ->
            if(success){
                tvUsername.text = user?.name
                tvEmail.text = user?.email
            }
            else{
                Toast.makeText(
                    this@MainActivity,
                    resources.getString(R.string.failed_get),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        firebaseHandler.getDiaries { success, diaryList ->
            if(success){
                showLoading(false)
                diaries = diaryList as List<Diary>
                diaryAdapter = DiaryAdapter(dataList = diaries, onClickedListener = { diary ->
                    val intent = Intent(this@MainActivity, DetailActivity::class.java)
                    intent.putExtra(DIARY_KEY, diary.id)
                    startActivity(intent)
                })
                rvDiary.layoutManager = LinearLayoutManager(this)
                rvDiary.adapter = diaryAdapter
            }
            else {
                showLoading(false)
                Toast.makeText(
                    this@MainActivity,
                    resources.getString(R.string.failed_get),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_add_diary ->{
                val intent = Intent(this, DetailActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_logout ->{
                firebaseHandler.signOut()
                startActivity(Intent(this, SignInActivity::class.java))
                Toast.makeText(
                    this@MainActivity,
                    resources.getString(R.string.logout_success),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return true
    }

    private fun showLoading(isLoading : Boolean){
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object{
        const val DIARY_KEY = "DIARY_KEY"
    }
}
package com.example.diego.twitterdemo

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlin.math.PI

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        ivPicture.setOnClickListener(View.OnClickListener {
            //TODO: select image from the phone
                checkPermission()
        })
    }

    val READIMAGE = 123
    fun checkPermission()
    {
        if(Build.VERSION.SDK_INT > 23)
        {
            if(ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),READIMAGE)
                return
            }
        }

        loadImage()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode)
        {
            READIMAGE->{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    loadImage()
                }
                else
                {
                    Toast.makeText(this, "Cannot acces your images", Toast.LENGTH_SHORT).show()
                }
            }
            else->super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        }


    }

    val PICKIMAGECODE = 123
    fun loadImage()
    {
        var intent=Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICKIMAGECODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==PICKIMAGECODE && data != null && resultCode== Activity.RESULT_OK)
        {
            val selectedImage=data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage, filePathColumn,null,null,null)
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()
            ivPicture.setImageBitmap(BitmapFactory.decodeFile(picturePath))
        }
    }



    fun bLogin(view: View)
    {
        var intent=Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}

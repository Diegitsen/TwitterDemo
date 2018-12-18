package com.example.diego.twitterdemo

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_login.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.PI

class LoginActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth?=null

    private var database=FirebaseDatabase.getInstance()
    private var myRef = database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()


        ivPicture.setOnClickListener { checkPermission() }
    }

    fun loginWithFirebase(email:String, password:String)
    {
        mAuth!!.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){ //notifica si la operacion fue un exito o una fail
                    task ->
                if(task.isSuccessful)
                {
                    Toast.makeText(this, "Succedfull login", Toast.LENGTH_SHORT).show()
                    saveImageInFirebase()


                }
                else
                {
                    Toast.makeText(this, "Fail login", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun loadTweets()
    {
        var currentUser = mAuth!!.currentUser

        if(currentUser!=null)
        {
            var intent = Intent(this, MainActivity::class.java)

            intent.putExtra("email", currentUser.email)
            intent.putExtra("uid", currentUser.uid)

            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        loadTweets()
    }


    fun saveImageInFirebase()
    {
        var currentUser = mAuth!!.currentUser
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://twitterdemo-8db3f.appspot.com")
        val df = SimpleDateFormat("ddMMyyHHmmss")
        val dataobj = Date()
        val email:String = currentUser!!.email.toString()
        val imagePath = splitString(email) + "." + df.format(dataobj) + ".jpg"
        //esto guarda en la subcarpeta images, la ruta , por asi decrilo, de la imagen subida
        val imageRef = storageRef.child("images/" + imagePath)
        ivPicture.isDrawingCacheEnabled=true
        ivPicture.buildDrawingCache()

        //proceso para convertir imagen a bitmap
        val drawable = ivPicture.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask=imageRef.putBytes(data)
        uploadTask.addOnFailureListener()//////////////
        {
            Toast.makeText(applicationContext, "fallo al subir", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {

                taskSnapshot ->var downloadURL = taskSnapshot.metadata!!.reference!!.downloadUrl!!.toString()

                Toast.makeText(this, "yeees", Toast.LENGTH_SHORT).show()
                myRef.child("Users").child(currentUser.uid).child("email").setValue(currentUser.email)
                myRef.child("Users").child(currentUser.uid).child("Profile Image").setValue(downloadURL)
                loadTweets()
        }








/*
        uploadTask.addOnFailureListener()//////////////
        {
            Toast.makeText(applicationContext, "fallo al subir", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
               taskSnapshot-> var DownloadURL = taskSnapshot

        }
*/

    }

    fun splitString(str:String):String
    {
        var split = str.split("@")
        return split[0]
    }

    val READIMAGE = 123
    fun checkPermission()
    {
        if(Build.VERSION.SDK_INT >= 23)
        {
            //activityCompat -> ayudante que te facilita acceder a las funciones del activity
            //(context, permission)
            //packageManager->recupera los tipos de informacion de los paquetes instalados
            if(ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),READIMAGE)
                return
            }

            loadImage()

        }


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

    //contentResolver -> permite manipular los datos de la memoria
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
        loginWithFirebase(etEmail.text.toString(), etPassword.text.toString())
    }
}

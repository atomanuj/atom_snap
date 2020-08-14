package com.abc.atom_snap

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_choose_users.*
import java.io.ByteArrayOutputStream
import java.util.*



class CreateSnap : AppCompatActivity() {

    var createSnapIv : ImageView? = null
    var messageEt : EditText? = null
    val imageName = UUID.randomUUID().toString()+".jpg"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)
        createSnapIv = findViewById(R.id.createSnapIv)
        messageEt= findViewById(R.id.messageEt)
    }

    fun getPhoto(){
        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,1)
    }
    fun chooseclicked(view: View) {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else {
            getPhoto()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage= data!!.data
        if(requestCode==1&&resultCode==Activity.RESULT_OK && data!=null){
            try {
                val bitmap= MediaStore.Images.Media.getBitmap(this.contentResolver,selectedImage)
                createSnapIv?.setImageBitmap(bitmap)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==1){
            if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getPhoto()
            }
        }
    }

    fun nextClicked(view: View){

        // Get the data from an ImageView as bytes
        createSnapIv?.isDrawingCacheEnabled = true
        createSnapIv?.buildDrawingCache()
        val bitmap = (createSnapIv?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()



        var uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(this,"Upload Failed",Toast.LENGTH_LONG).show()
        }.addOnSuccessListener (OnSuccessListener<UploadTask.TaskSnapshot>{ taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            val downloadURL = taskSnapshot.storage.downloadUrl.toString();

            val intent= Intent(this, ChooseUsersActivity::class.java)
            intent.putExtra("imageUrl",downloadURL.toString())
            intent.putExtra("imageName",imageName)
            intent.putExtra("message",messageEt?.text.toString())
            startActivity(intent)
        })
    }
}

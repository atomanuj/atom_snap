package com.abc.atom_snap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    var emaileT: EditText?= null
    var passwordeT: EditText?=null
    var mAuth= FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emaileT = findViewById(R.id.emaileT)
        passwordeT = findViewById(R.id.passswordeT)

        if(mAuth.currentUser!=null)
        {
            logIn()
        }
    }
    fun goclicked(view: View){
        //check if we can sign in the user
        mAuth.signInWithEmailAndPassword(emaileT?.text.toString(), passwordeT?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    logIn()
                } else {
                    //sign in the user
                    mAuth.createUserWithEmailAndPassword(emaileT?.text.toString(), passwordeT?.text.toString()).addOnCompleteListener(this){task ->
                        if(task.isSuccessful){
                            task.result?.user?.uid?.let {
                                FirebaseDatabase.getInstance().getReference().child("users").child(
                                    task.result!!.user!!.uid).child("email").setValue(emaileT?.text.toString())
                            }
                            logIn()
                        }
                        else{
                            Toast.makeText(this,"Login failed, try again.",Toast.LENGTH_LONG).show()
                        }
                    }
                }


            }


    }
    fun logIn()
    {
        //move to next activity
        val Intent= Intent(this,SnapsActivity::class.java)
        startActivity(Intent)
    }
}

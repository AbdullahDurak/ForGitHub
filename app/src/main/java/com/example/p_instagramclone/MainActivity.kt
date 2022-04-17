package com.example.p_instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.p_instagramclone.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        auth= FirebaseAuth.getInstance()
    }


    fun signup(view: View){


        val email=binding.emailEditText.text.toString()
        val password=binding.passwordEditText.text.toString()

        if(email.equals("")|| password.equals("")){
            Toast.makeText(this,"Your Email or Password empty. Please Fill Up",Toast.LENGTH_LONG).show()

        }
        else{

            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                val intent=Intent(this,FeedActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener {
                Toast.makeText(this,"Your Email or Password uncorrect",Toast.LENGTH_LONG).show()
            }

        }





    }

    fun signin(view:View){

        val email=binding.emailEditText.text.toString()
        val password=binding.passwordEditText.text.toString()

        if(email.equals("")|| password.equals("")){
            Toast.makeText(this,"Your Email or Password empty. Please Fill Up",Toast.LENGTH_LONG).show()

        }
        else{

            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                val intent=Intent(this,FeedActivity::class.java)
                startActivity(intent)
                println("Başarıyla Giriş Yapıldı!")
            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }

        }



    }
}
package com.example.p_instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.p_instagramclone.adapter.FeedRecyclerAdapter
import com.example.p_instagramclone.databinding.ActivityFeedBinding
import com.example.p_instagramclone.databinding.ActivityMainBinding
import com.example.p_instagramclone.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var postArrayList:ArrayList<Post>
    private lateinit var feedAdapter:FeedRecyclerAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFeedBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        db= Firebase.firestore

        postArrayList=ArrayList<Post>()

        getData()


        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        feedAdapter= FeedRecyclerAdapter(postArrayList)
        binding.recyclerView.adapter=feedAdapter

    }

    private fun getData(){
        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener{ value, error ->

            //value burada değerleri veriyor,
            //error hataları veriyor

            if(error!=null){
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if(value!=null){
                    if(!value.isEmpty){

                        //if içindeki parantezin anlamı
                        //İçindeki boş değilse
                       val documents= value.documents

                        postArrayList.clear()

                        for(document in documents){
                            document
                            //yukarıda document dediğin şey tek bir document
                            val comment=document.get("comment") as String
                            val userMail=document.get("userEmail") as String
                            val dowloadUrl=document.get("dowloadUrl") as String

                            println(comment)

                            val post=Post(userMail,comment,dowloadUrl)
                            postArrayList.add(post)
                        }

                        feedAdapter.notifyDataSetChanged()
                        //Yeni gelen verileri göster demek

                    }
                }
            }

        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater=menuInflater
        inflater.inflate(R.menu.option_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.sign_out){
            auth.signOut()
        }else{
            val intent=Intent(this,UploadActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}
package com.example.p_instagramclone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.p_instagramclone.databinding.ActivityMainBinding
import com.example.p_instagramclone.databinding.ActivityUploadBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.sql.Timestamp
import java.util.*

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding

    private lateinit var activityResultLauncher:ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    //izinleri string şekilde yazdığımız için buna string dedik
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage:FirebaseStorage
    private lateinit var auth: FirebaseAuth


    var selectedPicture: Uri?=null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUploadBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        registerLauncher()

        auth=Firebase.auth
        firestore=Firebase.firestore
        storage=Firebase.storage




    }



    fun save(view:View){


        val uuid=UUID.randomUUID()
        val imageName="${uuid}.jpg"

        //şuan database e ulaştık.
        //val refrence=storage.reference bu şekilde bırakırsan boş bir database sayfasına referans verecek
        val refrence=storage.reference
        //        refrence.putFile(selectedPicture) yazarsan eğer direkt resmi oraya koyacak

        val imageRefrence=refrence.child("images").child(imageName)
        //bunu yazdığında da images diye klasör aç içine de image.jpg diye dosya koy

        if(selectedPicture!=null){
            imageRefrence.putFile(selectedPicture!!).addOnSuccessListener {

               // Direkt firestore a kaydedilecek

                val uploadPictureReference=storage.reference.child("images").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    //kaydemiş oldugumu url yi aldık
                    val downloadUrl=it.toString()


                    if(auth.currentUser!=null){

                        val postMap= hashMapOf<String, Any>()
                        postMap.put("dowloadUrl", downloadUrl)
                        postMap.put("userEmail",auth.currentUser!!.email!!)
                        postMap.put("comment",binding.commentText.text.toString())
                        postMap.put("date",com.google.firebase.Timestamp.now())

                        firestore.collection("Posts").add(postMap).addOnSuccessListener {

                            Toast.makeText(this,"Uploaded Succesfully",Toast.LENGTH_LONG).show()
                            finish()

                        }.addOnFailureListener {

                            Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()


                        }

                    }

                }

            }.addOnFailureListener{
                    Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }


    fun selectImage(view:View){

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            //

                //kullanıcıya rational ı gösterip göstermeyeceğimi

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                Snackbar.make(view,"Permisson needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {


                    //izin isteyeceğiz

                    val intentToGallery =Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                    activityResultLauncher.launch(intentToGallery)


                }).show()


            }else{


                val intentToGallery =Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                //izin isteyeceğiz

                //******bu izin olaylarını activityLauncher ile hallediyoruz aklında olsun
                activityResultLauncher.launch(intentToGallery)

            }



        }else{

            val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)


            activityResultLauncher.launch(intentToGallery)
            //galeriye gideceğiz

        }




    }


    private fun registerLauncher(){
        //oncreate altına kayıt etmeyi sakın unutma

        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->


            //bize bir sonuç döndürüyor
            //mesela galeriye gittin onun sonucu

            if(result.resultCode== RESULT_OK){

               val intentForResult= result.data
                if(intentForResult!=null){

                    //intentForResult.data
                    //bunu dediğinde bize uri nullable veriyor

                   selectedPicture=intentForResult.data
                    selectedPicture?.let {
                        binding.imageView.setImageURI(it)
                        //böylelikle imageView içerisnde seçtiği görseli gösterebilecek
                    }
                }

            }

        }


        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
            if(result){
                //permission granted
                //intentToGallery kodunu getireceğiz buraya
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)


                activityResultLauncher.launch(intentToGallery)

            }else{
                //permission denied
                Toast.makeText(this,"Permission Needed!",Toast.LENGTH_LONG).show()
            }
        }
    }

}
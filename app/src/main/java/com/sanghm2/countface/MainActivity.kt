package com.sanghm2.countface

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var original_iv : ImageView ;
    private lateinit var detectFaceBtn : Button ;
    private lateinit var croppedIv : ImageView;


    private lateinit var detector : FaceDetector ;

    private companion object{
        private const val  SCALING_FATOR = 10 ;
        private const val TAG  = "FACE_DETECT_TAG" ;
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        original_iv = findViewById(R.id.original_iv) ;
        detectFaceBtn = findViewById(R.id.detectFacebtn);
        croppedIv = findViewById(R.id.cropped_iv);


        val realTimeFdo = FaceDetectorOptions.Builder()
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .build()

        detector = FaceDetection.getClient(realTimeFdo) ;
        // image from drawable
        val bitmap1 = BitmapFactory.decodeResource(resources , R.drawable.newtwoperson);
//        // image from ImageView
//        val bitmapDrawable  = original_iv.drawable as BitmapDrawable
//        val bitmap2 = bitmapDrawable.bitmap
//        //image from URI
//        val imageUri : Uri?  = null
//        try {
//            val bitmap3 = MediaStore.Images.Media.getBitmap(contentResolver,imageUri)
//        }catch (e : Exception){
//            Log.e(TAG, "onCreate: " ,e)
//        }

        detectFaceBtn.setOnClickListener {
                anlyzePhoto(bitmap1) ;
        }
    }
    private fun anlyzePhoto(bitmap : Bitmap){
        Log.d(TAG, "analyzePhoto: ")
        val smallerBitmap = Bitmap.createScaledBitmap(
            bitmap,
            bitmap.width / SCALING_FATOR,
            bitmap.height / SCALING_FATOR,
            false
        )
        val inputImage = InputImage.fromBitmap(smallerBitmap,0)
        detector.process(inputImage).addOnSuccessListener {faces ->
            Log.d(TAG, "anlyzePhoto: Successfully detected face ... ")
            Toast.makeText(this, "Face Detected ...", Toast.LENGTH_SHORT).show()

            for (face in faces){
                val rect  = face.boundingBox
                rect.set(rect.left * SCALING_FATOR,
                rect.top * (SCALING_FATOR -1) ,
                rect.right * (SCALING_FATOR),
                rect.bottom * SCALING_FATOR + 90)
            }
            Log.d(TAG, "anlyzePhoto: Number of faces   ${faces.size}")
            cropDetectedFace(bitmap,faces)
        }.addOnFailureListener { e->
            Log.e(TAG, "anlyzePhoto: ", e)
            Toast.makeText(this, "Failed due to  ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cropDetectedFace(bitmap: Bitmap, faces : List<Face>){
        Log.d(TAG, "cropDetectedFace: ")
        val rect = faces[0].boundingBox
        val x = Math.max(rect.left,0)
        val y = Math.max(rect.top , 0)
        val width = rect.width()
        val heigth = rect.height()

        val croppedBitmap = Bitmap.createBitmap(
            bitmap,
            x,
            y,
            if(x + width > bitmap.width) bitmap.width - x else width ,
            if(y + heigth > bitmap.height) bitmap.height - y else heigth
        )
        croppedIv.setImageBitmap(croppedBitmap)
    }
}
package org.codesolo.manservapp

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.activity_place.*
import kotlinx.android.synthetic.main.content_place.*
import org.codesolo.manservapp.adapter.PlacePageAdapter
import org.codesolo.manservapp.model.InfoPlaceCard
import java.io.File
import android.graphics.Bitmap
import org.codesolo.manservapp.R.id.imageView
import android.graphics.drawable.BitmapDrawable
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.adapter_place.view.*
import kotlinx.android.synthetic.main.adapter_place_pic.view.*
import kotlinx.android.synthetic.main.content_report.*
import kotlinx.android.synthetic.main.dialog_place.view.*
import kotlinx.android.synthetic.main.dialog_place_pic.view.*
import org.codesolo.manservapp.adapter.PlaceAdapter
import org.codesolo.manservapp.adapter.PlaceCardAdapter
import org.codesolo.manservapp.model.InfoPlace


class PlaceActivity : AppCompatActivity() {

    class PlacePicViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val iv_img = view.adp_place_img_main
        val txv_title = view.adp_place_txv_title
        val txv_desc = view.adp_place_txv_desc
    }

    private val tag = this::class.java.name
    private var namePlace:String = ""
    private var reportCode:String = ""
    private var placeCode:String = ""
    var im_bitmap:Bitmap? = null
    var localPath:String = ""
    private var uri:Uri? = null

    private var imgUrl: Uri? = null

    private val TAKE_PHOTO_REQUEST = 101
    private var mCurrentPhotoPath: String = ""

    val listPlacePic = arrayListOf<InfoPlaceCard>()

    private var adapter: FirestoreRecyclerAdapter<InfoPlaceCard, PlaceActivity.PlacePicViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)
        setSupportActionBar(toolbar)


        namePlace = intent.getStringExtra("namePlace")
        reportCode = intent.getStringExtra("reportCode")
        placeCode = intent.getStringExtra("placeCode")

        supportActionBar!!.title = namePlace


        place_rv_pics.layoutManager = GridLayoutManager(this,2)
        place_rv_pics.adapter = PlaceCardAdapter(listPlacePic,this)

        getPlacePicList()

        fab.setOnClickListener {
            launchCamera()
        }
        /*
        TODO if u want use tabs
        val fragmentAdapter = PlacePageAdapter(supportFragmentManager)

        viewpager_main.adapter = fragmentAdapter
        tabs_main.setupWithViewPager(viewpager_main)
        */
    }




    private fun getPlacePicList(){

        val query = firemanager.firestore!!
                .collection("reports")
                .document(reportCode)
                .collection("places")
                .document(placeCode)
                .collection("pics")


        val options = FirestoreRecyclerOptions.Builder<InfoPlaceCard>()
                .setQuery(query, InfoPlaceCard::class.java)
                .build()

        adapter = object : FirestoreRecyclerAdapter<InfoPlaceCard, PlaceActivity.PlacePicViewHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceActivity.PlacePicViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.adapter_place_pic, parent, false)
                return PlaceActivity.PlacePicViewHolder(view)

            }

            override fun onBindViewHolder(holder: PlaceActivity.PlacePicViewHolder , position: Int, model: InfoPlaceCard) {

                val docId = snapshots.getSnapshot(position).id
                holder.txv_title.text = model.title
                holder.txv_desc.text = model.description
                Glide.with(this@PlaceActivity)
                        .load(model.imgUrl)
                        .apply(RequestOptions().placeholder(R.drawable.im_placeholder))
                        .into(holder.iv_img)


                holder.itemView.setOnClickListener {
                    val builder = AlertDialog.Builder(this@PlaceActivity)

                    val inflater = LayoutInflater.from(this@PlaceActivity)
                    val dialogView = inflater.inflate(R.layout.dialog_place_pic, null)

                    builder.setView(dialogView)
                    builder.setTitle("Edit Information")

                    dialogView.dg_place_pic_etx_title.setText(model.title)
                    dialogView.dg_place_pic_etx_desc.setText(model.description)

                    Glide.with(this@PlaceActivity)
                            .load(model.imgUrl)
                            .apply(RequestOptions().placeholder(R.drawable.im_placeholder))
                            .into(dialogView.dg_place_pic_iv_pic)


                    builder.setPositiveButton("Save"){dialog, which ->

                        val title = dialogView.dg_place_pic_etx_title.text.toString()
                        val description = dialogView.dg_place_pic_etx_desc.text.toString()

                        val pic = InfoPlaceCard()
                        pic.code = docId
                        pic.title = title
                        pic.imgUrl = model.imgUrl
                        pic.description = description
                        val dbFS = firemanager.firestore

                        dbFS!!.collection("reports").document(reportCode).collection("places").document(placeCode).collection("pics").document(docId).update(pic.toMap())
                    }

                    builder.setNeutralButton("Eliminate"){dialog, which ->
                        val dbFS = firemanager.firestore
                        dbFS!!.collection("reports").document(reportCode).collection("places").document(placeCode).collection("pics").document(docId).delete()
                    }

                    builder.setNegativeButton("Cancel"){dialog, which ->
                        dialog.dismiss()
                    }

                    builder.show()

                }

            }

        }


        adapter!!.notifyDataSetChanged()
        place_rv_pics.adapter = adapter

    }

    override fun onBackPressed() {
        setResult(0)
        finish()
    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()

    }

    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO_REQUEST) {
            processCapturedPhoto()

            val builder = AlertDialog.Builder(this)

            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_place_pic, null)

            builder.setView(dialogView)
            builder.setTitle("Add Information")

            val image = dialogView.dg_place_pic_iv_pic
            image.setImageBitmap(im_bitmap)
            //builder.setMessage("Add the name of the new place")

            builder.setPositiveButton("Ok"){dialog, which ->

                val title = dialogView.dg_place_pic_etx_title.text.toString()
                val description = dialogView.dg_place_pic_etx_desc.text.toString()

                val placePic = InfoPlaceCard("","", title, description)

                val storageRef = firemanager.firestorage.reference
                val imageRef   = storageRef.child(localPath)

                val baos = ByteArrayOutputStream()
                im_bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                val uploadTask =  imageRef.putBytes(data)
                uploadTask.addOnSuccessListener {
                    Log.i(tag,"Exito")
                    imgUrl =  it.downloadUrl
                    placePic.imgUrl = imgUrl!!.toString()

                    val dbFS = firemanager.firestore!!.collection("reports").document(reportCode).collection("places").document(placeCode).collection("pics")

                    dbFS.add(placePic.toMap()).addOnSuccessListener {
                        Log.i(tag, "place pic added")
                        //listPlacePic.add(placePic)
                        //place_rv_pics.adapter.notifyDataSetChanged()
                    }
                }

            }
            
            builder.setNegativeButton("Cancel"){dialog, which ->  
                dialog.dismiss()
            }
                
            
            builder.show()

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun launchCamera() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        val fileUri = contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(packageManager) != null) {
            mCurrentPhotoPath = fileUri.toString()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, TAKE_PHOTO_REQUEST)
        }
    }




    private fun uploadPlacePic(bitmap: Bitmap, localPath: String) {

        val storageRef = firemanager.firestorage.reference
        val imageRef   = storageRef.child(localPath)

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask =  imageRef.putBytes(data);
        uploadTask.addOnSuccessListener {
            Log.i(tag,"Exito")
            imgUrl =  it.downloadUrl
        }
    }

    private fun processCapturedPhoto() {
        val cursor = contentResolver.query(Uri.parse(mCurrentPhotoPath),
                Array(1) {android.provider.MediaStore.Images.ImageColumns.DATA},
                null, null, null)
        cursor.moveToFirst()
        val photoPath = cursor.getString(0)
        cursor.close()
        val file = File(photoPath)
        this.uri = Uri.fromFile(file)



        localPath = reportCode+"/"+placeCode+"/"+this.uri!!.lastPathSegment

        val bitmap = BitmapFactory.decodeFile(photoPath)
        im_bitmap = bitmap

        val height = bitmap.height
        val width = bitmap.width

        var h = 720
        //resize to hd
        im_bitmap = if (width<height){
            //image in portrait mode
            Log.i(tag, "PORTRAIT")
            getResizedBitmap(bitmap,h*width/height, h)
        }else{
            Log.i(tag, "LANDSCAPE")
            getResizedBitmap(bitmap, h,h*height/width)
            //im_bitmap = RotateBitmap(bitmap, 90F)
        }


        //im_bitmap = getResizedBitmap(bitmap,360,720)
        //uploadPlacePic(im_bitmap!!, localPath)
    }

    fun RotateBitmap(bm: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        val width = bm.width
        val height = bm.height
        val rotateBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true)
        bm.recycle()
        return rotateBitmap
    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false)
        bm.recycle()
        return resizedBitmap
    }

}

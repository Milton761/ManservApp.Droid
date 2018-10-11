package org.codesolo.manservapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.*

import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.adapter_agency.view.*
import kotlinx.android.synthetic.main.adapter_place.view.*
import kotlinx.android.synthetic.main.content_place.*
import kotlinx.android.synthetic.main.content_report.*
import kotlinx.android.synthetic.main.dialog_list_agency.*
import kotlinx.android.synthetic.main.dialog_list_agency.view.*
import kotlinx.android.synthetic.main.dialog_place.*
import kotlinx.android.synthetic.main.dialog_place.view.*
import org.codesolo.manservapp.adapter.PlaceAdapter
import org.codesolo.manservapp.adapter.PlaceCardAdapter
import org.codesolo.manservapp.model.InfoAgency
import org.codesolo.manservapp.model.InfoPlace

import org.codesolo.manservapp.model.Report





class ReportActivity : AppCompatActivity() {

    class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txv_name = view.adt_place_txv_name!!
    }
    class AgencyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txv_name = view.adt_agency_txv_name!!
    }



    private var adapter: FirestoreRecyclerAdapter<InfoPlace, PlaceViewHolder>? = null
    private var adapterDialog: FirestoreRecyclerAdapter<InfoAgency, AgencyViewHolder>? = null

    private var reportCode:String = ""
    private var placeCode:String = ""
    private var firestoreListener: ListenerRegistration? = null


    private var listPlace = mutableListOf<InfoPlace>()
    private var listAgency = mutableListOf<InfoAgency>()

    private var tag = this::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        setSupportActionBar(report_toolbar)
        reportCode = intent.getStringExtra("reportCode")

        report_rv_places.layoutManager = LinearLayoutManager(this)
        report_rv_places.itemAnimator = DefaultItemAnimator()



        getPlaceList()

//        report_rv_places.adapter = PlaceAdapter(listPlace,this,reportCode)
//
        Log.i(tag,"On Create")
    }

    override fun onBackPressed() {
        return
    }

    public override fun onStart() {
        super.onStart()
        Log.i(tag, "On Start")
        adapter!!.startListening()


    }

    override fun onResume() {
        super.onResume()
        Log.i(tag, "On Resume")
    }

    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()

    }

//    private fun getAgencyList() {
//        val query = firemanager.firestore!!
//                .collection("template")
//                .orderBy("name")
//
//        val options = FirestoreRecyclerOptions.Builder<InfoAgency>()
//                .setQuery(query, InfoAgency::class.java)
//                .build()
//
//        adapterDialog = object : FirestoreRecyclerAdapter<InfoAgency, AgencyViewHolder>(options) {
//            override fun onBindViewHolder(holder: AgencyViewHolder, position: Int, model: InfoAgency) {
//                holder.txv_name.text = model.name;
//                val docId = snapshots.getSnapshot(position).id
//            }
//
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgencyViewHolder {
//                val view = LayoutInflater.from(parent.context)
//                        .inflate(R.layout.adapter_agency, parent, false)
//                return AgencyViewHolder(view)
//            }
//
//        }
//        adapterDialog!!.notifyDataSetChanged()
//
//
//    }

    private fun getPlaceList(){
        val query = firemanager.firestore!!
                .collection("reports")
                .document(reportCode)
                .collection("places")
                .orderBy("name")



        val options = FirestoreRecyclerOptions.Builder<InfoPlace>()
                .setQuery(query, InfoPlace::class.java)
                .build()

        adapter = object : FirestoreRecyclerAdapter<InfoPlace,PlaceViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.adapter_place, parent, false)
                return PlaceViewHolder(view)

            }

            override fun onBindViewHolder(holder: PlaceViewHolder, position: Int, model: InfoPlace) {
                holder.txv_name.text = model.name
                Log.i("ADAPTER", model.name)

                val docId = snapshots.getSnapshot(position).id

                holder.itemView.setOnClickListener{v ->

                    val intent = Intent(v.context, PlaceActivity::class.java)
                    intent.putExtra("reportCode",reportCode)
                    intent.putExtra("placeCode",docId)
                    intent.putExtra("namePlace",model.name)
                    intent.putExtra("from",true)
                    Log.i(tag,"onclick")
                    v.context.startActivity(intent)

                }

                holder.itemView.setOnLongClickListener {
                    val builder = AlertDialog.Builder(this@ReportActivity)

                    val inflater = LayoutInflater.from(this@ReportActivity)
                    val dialogView = inflater.inflate(R.layout.dialog_place, null)

                    builder.setView(dialogView)
                    builder.setTitle("Edit Place")
                    builder.setMessage("Edit the information below")

                    dialogView.report_dg_name.setText(model.name)
                    dialogView.report_dg_desc.setText(model.description)


                    builder.setPositiveButton("Save"){dialog, which ->

                        val name = dialogView.report_dg_name.text.toString()
                        val description = dialogView.report_dg_desc.text.toString()

                        val place1 = InfoPlace(model.code, name, description)
                        place1.name = name
                        place1.description = description
                        val dbFS = firemanager.firestore



                        dbFS!!.collection("reports").document(reportCode).collection("places").document(docId).update(place1.toMap())
                    }

                    builder.setNeutralButton("Eliminate"){dialog, which ->
                        val dbFS = firemanager.firestore
                        dbFS!!.collection("reports").document(reportCode).collection("places").document(docId).delete()
                    }

                    builder.setNegativeButton("Cancel"){dialog, which ->
                        dialog.dismiss()
                    }

                    builder.show()

                    true
                }



            }

        }

        adapter!!.notifyDataSetChanged()
        report_rv_places.adapter = adapter


    }

    fun addPlace( place:InfoPlace){
        val dbFS = firemanager.firestore!!.collection("reports").document(reportCode).collection("places")
        dbFS.add(place.toMap()).addOnSuccessListener {
            Log.i(tag,"COMPLETE PLACE")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.report, menu)
        return super.onCreateOptionsMenu(menu)


    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item!!.itemId) {
        R.id.report_menu_addplace -> {
            // User chose the "Settings" item, show the app settings UI...

            Log.i(tag, reportCode)

            val builder = AlertDialog.Builder(this)

            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_place, null)

            builder.setView(dialogView)
            builder.setTitle("New Place")
            builder.setMessage("Add the name of the new place")

            builder.setPositiveButton("Ok"){dialog, which ->


                val name = dialogView.report_dg_name.text.toString()
                val description = dialogView.report_dg_desc.text.toString()

                val place =InfoPlace(placeCode,name,description)



                val dbFS = firemanager.firestore!!.collection("reports").document(reportCode).collection("places")

                dbFS.add(place.toMap()).addOnSuccessListener {

                    placeCode = it.id

                    val intent = Intent(this, PlaceActivity::class.java)
                    intent.putExtra("namePlace",name)
                    intent.putExtra("reportCode",reportCode)
                    intent.putExtra("placeCode",placeCode)

                    //listPlace.add(InfoPlace(placeCode,name,description))
                    startActivityForResult(intent,0)

                }

            }

            builder.setNegativeButton("Cancel"){dialog, which ->
                dialog.dismiss()
            }

            builder.show()
            true
        }

        R.id.report_menu_finish -> {
            startActivity(Intent(this, StartReportActivity::class.java))
            true
        }

        R.id.report_menu_eliminate ->{
            val dbFS = firemanager.firestore
            dbFS!!.collection("reports").document(reportCode).delete().addOnSuccessListener {
                startActivity(Intent(this, StartReportActivity::class.java))
            }
            true
        }

        R.id.report_menu_load -> {
            val builder = AlertDialog.Builder(this)

            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_list_agency, null)
            // var recView = inflater.inflate(R.layout.dialog_list_agency, null);


            builder.setView(dialogView)
            builder.setTitle("Load Places")
            builder.setMessage("Write the agency code to load all places")
            //builder.setMessage("All agency codes can be found in https://manservapp.firebaseapp.com/template ")

            builder.setPositiveButton("OK"){dialog, which ->
                var code = dialogView.agency_dg_code.text.toString();
                if(code.isNotEmpty()){
                    val dbFS = firemanager.firestore!!.collection("template").document(code).collection("places")

                    //class Place(val name: String val: description:String)
                    dbFS!!.get().addOnCompleteListener{
                        if(it.isSuccessful){
                            for(doc in it.result.documents){
                                var p = doc.toObject(InfoPlace::class.java)!!
                                Log.i(tag, p.name)
                                addPlace(p)
                            }

                        }
                    }
                }else{
                    Log.i(tag, "EMPTY CODE")
                }
            }

            builder.setNegativeButton("Cancel"){dialog, which ->
                dialog.dismiss()
            }

            builder.show()



            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode==0)
        {
            Log.i(tag,"Places")
            //getPlaceList()
            //adapter!!.startListening()
        }

    }

}

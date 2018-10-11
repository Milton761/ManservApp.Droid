package org.codesolo.manservapp.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.codesolo.manservapp.R
import org.codesolo.manservapp.model.InfoPlace
import android.widget.TextView
import kotlinx.android.synthetic.main.adapter_place.view.*
import kotlinx.android.synthetic.main.dialog_place.view.*
import org.codesolo.manservapp.PlaceActivity
import org.codesolo.manservapp.StartReportActivity
import org.codesolo.manservapp.firemanager



class PlaceAdapter(val items : ArrayList<InfoPlace>, val context: Context, val reportCode: String) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

//    // Gets the number of animals in the list
//    public interface ItemClickListener{
//        void onClick(view:View, position:Int)
//    }
//
//    private clickListener:ItemClickListener

    private val tag = PlaceAdapter::class.java.name

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        //val tvAnimalType = view.tv_animal_type

        val txv_name = view.adt_place_txv_name!!


    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_place,parent,false))
    }

    // Inflates the item views
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val place = items[position]
        holder.txv_name.text = place.name

        holder.itemView.setOnClickListener{v ->

            val intent = Intent(v.context, PlaceActivity::class.java)
            intent.putExtra("reportCode",reportCode)
            intent.putExtra("placeCode",place.code)
            intent.putExtra("namePlace",place.name)
            intent.putExtra("from",true)
            Log.i(tag,"onclick")
            v.context.startActivity(intent)

        }

        holder.itemView.setOnLongClickListener {
            val builder = AlertDialog.Builder(context)

            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.dialog_place, null)

            builder.setView(dialogView)
            builder.setTitle("Edit Place")
            builder.setMessage("Edit the information below")

            dialogView.report_dg_name.setText(place.name)
            dialogView.report_dg_desc.setText(place.description)

            builder.setPositiveButton("Save"){dialog, which ->

                val name = dialogView.report_dg_name.text.toString()
                val description = dialogView.report_dg_desc.text.toString()

                val place1 = InfoPlace(place.code, name, description)
                place1.name = name
                place1.description = description
                val dbFS = firemanager.firestore
                dbFS!!.collection("reports").document(reportCode).collection("places").document(place.code).update(place1.toMap())
            }

            builder.setNegativeButton("Eliminate"){dialog, which ->
                val dbFS = firemanager.firestore
                dbFS!!.collection("reports").document(reportCode).collection("places").document(place.code).delete()
            }

            builder.setNeutralButton("Cancel"){dialog, which ->
                dialog.dismiss()
            }

            builder.show()

            true
        }
    }
}


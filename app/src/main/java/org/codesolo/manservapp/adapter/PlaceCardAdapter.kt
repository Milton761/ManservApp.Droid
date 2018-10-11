package org.codesolo.manservapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.codesolo.manservapp.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.adapter_place_pic.view.*
import org.codesolo.manservapp.model.InfoPlaceCard


class PlaceCardAdapter(val items : ArrayList<InfoPlaceCard>, val context: Context) : RecyclerView.Adapter<PlaceCardAdapter.ViewHolder>() {

//    // Gets the number of animals in the list
//    public interface ItemClickListener{
//        void onClick(view:View, position:Int)
//    }
//
//    private clickListener:ItemClickListener

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        //val tvAnimalType = view.tv_animal_type

        val iv_img = view.adp_place_img_main
        val txv_title = view.adp_place_txv_title
        val txv_desc = view.adp_place_txv_desc

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_place_pic,parent,false))
    }

    // Inflates the item views
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val place = items[position]
        holder.txv_title.text = place.title
        holder.txv_desc.text = place.description
        Glide.with(context)
                .load(place.imgUrl)
                .apply(RequestOptions().placeholder(R.drawable.im_placeholder))
                .into(holder.iv_img)
    }
}


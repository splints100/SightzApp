package com.example.sightzapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.play.core.integrity.v

class ViewPagerAdapter(private val dataList: List<Custom>) : RecyclerView.Adapter<ViewPagerAdapter.Page2ViewHolder>() {


    inner class Page2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.ivimage)
        private val titleTextView: TextView = itemView.findViewById(R.id.tvHeading)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tvDesc)

        init {
            imageView.setOnClickListener { v: View ->
                val position = adapterPosition
                Toast.makeText(itemView.context, "You Clicked on Item#${position + 1}", Toast.LENGTH_SHORT).show()

            }
        }

        fun bind(data: Custom) {
            imageView.setImageResource(data.image)
            titleTextView.text = data.title
            descriptionTextView.text = data.description
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerAdapter.Page2ViewHolder {
        return Page2ViewHolder (LayoutInflater.from(parent.context).inflate(R.layout.viewpager_item, parent, false))

    }


    override fun onBindViewHolder(holder: ViewPagerAdapter.Page2ViewHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data)
    }



    override fun getItemCount(): Int {
        return dataList.size
    }


}


/*inner class Page2ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){

        val itemTitle: TextView = itemView.findViewById(R.id.tvHeading)
        val itemDetails: TextView = itemView.findViewById(R.id.tvDesc)
        val itemImage: ImageView = itemView.findViewById(R.id.ivimage)

        init {
            itemImage.setOnClickListener { v: View ->
                val position = adapterPosition
                Toast.makeText(itemView.context, "You Clicked on Item#${position + 1}",Toast.LENGTH_SHORT).show()

            }
        }

    }*/


/*override fun onBindViewHolder(holder: ViewPagerAdapter.Page2ViewHolder, position: Int) {
      holder.itemTitle.text = title[position]
      holder.itemDetails.text = details[position]
      holder.itemImage.setImageResource(images[position])
    }*/
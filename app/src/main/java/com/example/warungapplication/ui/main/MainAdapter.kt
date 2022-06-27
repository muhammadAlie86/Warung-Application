package com.example.warungapplication.ui.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.warungapplication.data.model.Warung
import com.example.warungapplication.databinding.ListWarungBinding
import com.example.warungapplication.ui.edit.EditActivity
import com.example.warungapplication.ui.edit.EditActivity.Companion.EXTRA_DATA

class MainAdapter(private val context : Context) :
    RecyclerView.Adapter<MainAdapter.MyViewHolder>(){


    private var listWarung : ArrayList<Warung> = ArrayList()

    fun setData(warung : List<Warung>){
        listWarung.clear()
        listWarung.addAll(warung)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val listWarungBinding = ListWarungBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(listWarungBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val warung = listWarung[position]
        holder.bind(warung)

    }

    override fun getItemCount(): Int = listWarung.size

    inner class MyViewHolder (private val binding: ListWarungBinding): RecyclerView.ViewHolder(binding.root) {
        lateinit var getWarung : Warung
        fun bind(warung: Warung){
            with(binding){
                getWarung = warung
                tvNameWarung.text = warung.name
                tvAddress.text = warung.address
                tvLocation.text = warung.location
                tvEdit.setOnClickListener {
                    val intent = Intent(context, EditActivity::class.java)
                    intent.putExtra(EXTRA_DATA,warung)
                    context.startActivity(intent)
                }
                Glide.with(context)
                    .load(warung.imgWarung)
                    .into(binding.imageView)

            }
        }
    }
}
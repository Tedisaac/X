package com.isaac.x.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.isaac.x.models.ContactEntity

class ContactAdapter(private val contacts: List<ContactEntity>) : RecyclerView.Adapter<ContactAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }


    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ContactEntity>() {
            override fun areItemsTheSame(oldContactEntity: ContactEntity, newContactEntity: ContactEntity): Boolean {
                return oldContactEntity === newContactEntity
            }

            override fun areContentsTheSame(oldContactEntity: ContactEntity, newContactEntity: ContactEntity): Boolean {
                return oldContactEntity.contact == newContactEntity.contact
            }
        }
    }



}
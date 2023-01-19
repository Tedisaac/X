package com.isaac.x.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.isaac.x.R
import com.isaac.x.databinding.ContactLayoutBinding
import com.isaac.x.models.ContactEntity

class ContactAdapter(private val contacts: List<ContactEntity>) : RecyclerView.Adapter<ContactAdapter.ItemViewHolder>() {

    /*override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ContactLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }*/

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_layout, parent, false)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var currentContact = contacts[position]

        holder.contact.text = currentContact.contact
        //holder.bind(currentContact)

    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    /*class ItemViewHolder(private var contactBinding: ContactLayoutBinding) : RecyclerView.ViewHolder(contactBinding.root) {

        fun bind(contact: ContactEntity){
            contactBinding.apply {
                txtContact.text = contact.contact
            }
        }
    }*/

    class ItemViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val contact: TextView = itemView.findViewById(R.id.txtContact)

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
package com.example.sdcresourcemonitor.view.adapter

import android.accounts.AccountAuthenticatorActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sdcresourcemonitor.R
import com.example.sdcresourcemonitor.databinding.EntityFilterItemBinding
import com.example.sdcresourcemonitor.view.listener.RadioButtonClickListener
import com.example.sdcresourcemonitor.view.listener.ViewOnClickListener
import kotlinx.android.synthetic.main.entity_filter_item.view.*
import java.util.zip.Inflater

class EntityFilterAdapter(private var _entities: ArrayList<String>, var radioButtonClickListener: RadioButtonClickListener) :
    RecyclerView.Adapter<EntityFilterAdapter.MyViewHolder>() {

    fun update(newEntities: List<String>) {
        _entities.clear()
        _entities.add("All")
        _entities.addAll(newEntities)
        notifyDataSetChanged()
    }

    fun updatePosition(newPosition : Int) {
        selectedPosition = newPosition
        notifyDataSetChanged()
    }

    var selectedPosition = 0

    class MyViewHolder(var view: EntityFilterItemBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = DataBindingUtil.inflate<EntityFilterItemBinding>(
            LayoutInflater.from(parent.context)
            , R.layout.entity_filter_item, parent, false
        )
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.entity_filter_item,parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount() = _entities.size

    override fun onBindViewHolder(_holder: MyViewHolder, _position: Int) {
        _holder.itemView.entityCheckBox.text = _entities[_position]
        _holder.view.listener = radioButtonClickListener
        _holder.view.entity = _entities[_position]
        _holder.view.position = _position
        _holder.view.entityCheckBox.isChecked = _position == selectedPosition
    }
}
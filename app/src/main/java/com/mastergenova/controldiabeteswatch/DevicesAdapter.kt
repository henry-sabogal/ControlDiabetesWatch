package com.mastergenova.controldiabeteswatch

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class DevicesAdapter (private val devices: Set<BluetoothDevice>, private val itemClickListener:OnItemClickListener): RecyclerView.Adapter<DevicesAdapter.DevicesViewHolder>(){

    class DevicesViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val name = view.findViewById(R.id.txtName) as TextView
        val address = view.findViewById(R.id.txtAddress) as TextView

        fun bind(device: BluetoothDevice, clickListener: OnItemClickListener){
            view.setOnClickListener{
                clickListener.onItemClick(device)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesAdapter.DevicesViewHolder{
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_devices, parent, false) as View



        return DevicesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DevicesViewHolder, position: Int) {
        holder.name.text = "Name: " + devices.elementAt(position).name
        holder.address.text = "Address: " + devices.elementAt(position).address
        holder.bind(devices.elementAt(position), itemClickListener)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

}
package com.example.matt.gamefinal

import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.matt.gamefinal.R.id.select_device_list
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.io.IOException
import java.util.*



class MainActivity : AppCompatActivity() {


    private lateinit var mPairedDevice: Set<BluetoothDevice>
    private var mBTAdapter: BluetoothAdapter? = null
    private val REQUEST_ENABLE_BLUETOOTH = 1
    var isPlayer1 = 0
    var isPlayer2 = 0

    companion object {
        val EXTRA_ADDRESS: String = "Device_address"
        var myUUID: UUID = UUID.fromString("1c463fe7-09d1-4d5c-bdd0-0722931b4f6a")
        var mBluetoothSocket: BluetoothSocket? = null
        lateinit var mProgress: ProgressDialog
        var isConnected = false
        lateinit var deviceAddress:String

//        lateinit var address:String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBTAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBTAdapter == null) {
            toast("This device doesn't support bluetooth")
            return
        }
        if (!mBTAdapter!!.isEnabled) {
            val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BLUETOOTH)
        }
        findPlayer_btn.setOnClickListener { pairedDeviceList() }

//        player1_toggle.toggle()
        //FIXME - desired: when P1 is selected, P2 is deselected and vice versa
        player1_toggle?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                isPlayer1 = 1
                isPlayer2 = 0
                play_btn.visibility = View.VISIBLE
            }else {
                play_btn.visibility = View.INVISIBLE
            }
        }
        player2_toggle?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                isPlayer2 = 1
                isPlayer1 = 0
                play_btn.visibility = View.VISIBLE
            }else {
                play_btn.visibility = View.INVISIBLE
            }
        }
//        if (!player1_toggle.isChecked)
//            player2_toggle.toggle()
//        if (!player2_toggle.isChecked)
//            player1_toggle.toggle()


        play_btn.setOnClickListener {
            if (isPlayer1 == 1) {
                val play1Intent = Intent(this, Player1_Guesser::class.java)
                this.startActivity(play1Intent)
            } else if (isPlayer2 == 1) {
                val play2Intent = Intent(this, Player2_Hinter::class.java)
                this.startActivity(play2Intent)
            } else {
                toast("Choose a Player!")
            }
        }
    }




    private fun pairedDeviceList() {

        mPairedDevice = mBTAdapter!!.bondedDevices
        val list: ArrayList<BluetoothDevice> = ArrayList()

        if (!mPairedDevice.isEmpty()) {
            for (device: BluetoothDevice in mPairedDevice) {
                deviceAddress = device.address
                list.add(device)
                Log.i("devices", " " + device)
            }
        } else {
            toast("no paired devices found")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        select_device_list.adapter = adapter
        select_device_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address:String = device.address


            ConnectToDevice(this,address).execute()
            if(mBluetoothSocket != null){
                try {
//                    mBluetoothSocket!!.outputStream.write(isPlayer1)
                }catch (e: ExceptionInInitializerError){
                    e.printStackTrace()
                }
            }
//
        }
    }
    private inner class ConnectToDevice(c: Context,val address:String) : AsyncTask<Void, Void, String>() {
        private var connectSuccess = true
        private val context:Context


        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            mProgress = ProgressDialog.show(context, "Connecting...", "Please Wait")
        }
        override fun doInBackground(vararg params: Void?): String? {
            try {
                if(mBluetoothSocket == null || !isConnected){
                    Log.i("HEEEEEEERRRRRRRREEEEEEEEE", "987654321")
                    mBTAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device = mBTAdapter!!.getRemoteDevice(address)
                    mBluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    mBluetoothSocket!!.connect()
                }

            }catch (e: IOException){
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if(mBluetoothSocket!!.isConnected) {
                    toast("Connected")
                    Log.i("", "")

            }else {
                toast("Not Connected!")
            }
            mProgress.dismiss()
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (mBTAdapter!!.isEnabled) {
                    toast("Bluetooth has been enabled")
                } else {
                    toast("Bluetooth has been disabled")
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                toast("Bluetooth enabling has been canceled")
            }
        }
    }
}

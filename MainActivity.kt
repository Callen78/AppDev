package com.example.matt.gamefinal

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.io.IOException
import java.util.*



class MainActivity : AppCompatActivity() {


    private lateinit var mPairedDevice: Set<BluetoothDevice>
    private var mBTAdapter: BluetoothAdapter? = null
    private val REQUEST_ENABLE_BLUETOOTH = 1

    companion object {
        val EXTRA_ADDRESS: String = "Device_address"
        private const val ACCESS_FINE_LOCATION = 1
        var myUUID: UUID = UUID.fromString("1c463fe7-09d1-4d5c-bdd0-0722931b4f6a")
        var mBluetoothSocket: BluetoothSocket? = null
        lateinit var mProgress: ProgressDialog
        var isConnected = false
        lateinit var deviceAddress: String
        private const val SERVICE_NAME = "Talker"

//        lateinit var address:String
    }
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
//            Log.i(LOG_TAG, "BroadcastReceiver onReceive()")
            handleBTServer(intent)
        }
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
//        findPlayer_btn.setOnClickListener { handleBTServer() }

        play_btn.isEnabled = false
        player1_toggle?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
//                play_btn.visibility = View.VISIBLE
                play_btn.isEnabled = true
                player2_toggle.isEnabled = false
            } else {
//                play_btn.visibility = View.INVISIBLE
                play_btn.isEnabled = false
                player2_toggle.isEnabled = true
            }
        }
        player2_toggle?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
//                play_btn.visibility = View.VISIBLE
                player1_toggle.isEnabled = false
                play_btn.isEnabled = true

            } else {
                play_btn.isEnabled = false
//                play_btn.visibility = View.INVISIBLE
                player1_toggle.isEnabled = true

            }
        }


        play_btn.setOnClickListener {
            if (player1_toggle.isChecked) {
                handleBTServer(intent)
                val play1Intent = Intent(this, Player1_Guesser::class.java)
                this.startActivity(play1Intent)
            } else if (player2_toggle.isChecked) {
                acceptBT()
                val play2Intent = Intent(this, Player2_Hinter::class.java)
                this.startActivity(play2Intent)
            } else {
                toast("Choose a Player!")
            }
        }
    }

    private fun setupDiscovery() {
//        Log.i(TCLIENT,"Activating Discovery")
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)
        mBTAdapter!!.startDiscovery()
    }


    private fun handleBTServer(intent: Intent) {

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
            var address: String = device.address

            address = "34:8A:7B:4D:1C:37"
            ConnectToDevice(this, address)
            if (mBluetoothSocket != null) {
                try {
//                    mBluetoothSocket!!.outputStream.write(isPlayer1)
                } catch (e: ExceptionInInitializerError) {
                    e.printStackTrace()
                }
            }
//
        }
    }

    private inner class ConnectToDevice(c: Context, val address: String) : Thread() {
        private val context: Context
        init {
            this.context = c
        }
        override fun run() {

            var player = ""
            if (player1_toggle.isChecked) {
                player = "Player 2"
            } else if (player2_toggle.isChecked) {
                player = "Player 1"
            }

            mProgress = ProgressDialog.show(context, "Connecting to ${player}", "Please Wait")

            try {
                if (mBluetoothSocket == null || !isConnected) {
                    if (address == "34:8A:7B:4D:1C:37") {
                        mBTAdapter = BluetoothAdapter.getDefaultAdapter()
                        val device = mBTAdapter!!.getRemoteDevice(address)
                        mBluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID)
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                        mBluetoothSocket!!.connect()
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (mBluetoothSocket!!.isConnected) {
                toast("Connected")
                Log.i("", "")

            } else {
                toast("Not Connected!")
            }
            mProgress.dismiss()

        }
}
    private inner class acceptBT():Thread(){


        override fun run() {
            try {
                var serverSocket: BluetoothServerSocket? = null
                var socket:BluetoothSocket? = null

                mBTAdapter!!.listenUsingRfcommWithServiceRecord(SERVICE_NAME, myUUID)
//                serverSocket = tmp
                    socket = serverSocket!!.accept()

            } catch (e: IOException) {
                e.printStackTrace()
            }

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

    private fun setUpBroadcastReceiver() {
        // Create a BroadcastReceiver for ACTION_FOUND
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)    {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    ACCESS_FINE_LOCATION)
//            Log.i(TCLIENT,"Getting Permission")
            return
            //Discovery will be setup in onRequestPermissionResult() if permission is granted
        }
        setupDiscovery()
    }

    //after game over - disconnect from BT by serverSocket!!.close()
    // this can be accomplished correctly by calling the MainActivity intent then having an
    //   "extra" passed that will call gameOver() that can hold some toast msgs, etc with serverSocket
    //       being closed here.
}

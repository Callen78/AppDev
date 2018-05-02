package com.example.matt.gamefinal

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    var mBTAdapter: BluetoothAdapter? = null
    lateinit var mPairedDevice: Set<BluetoothDevice>
    val REQUEST_ENABLE_BLUETOOTH =1

    companion object {
        val EXTRA_ADDRESS: String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBTAdapter = BluetoothAdapter.getDefaultAdapter()
        if(mBTAdapter ==null){
            toast("This device doesn't support bluetooth")
            return
        }
        if(!mBTAdapter!!.isEnabled){
            val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BLUETOOTH)
        }


//        select_device_refresh.setOnClickListener{ pariedDeviceList()}
    }

    private fun pariedDeviceList(){

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_ENABLE_BLUETOOTH){
            if(resultCode == Activity.RESULT_OK){
                if(mBTAdapter!!.isEnabled){
                    toast("Bluetooth has been enabled")
                }else {
                    toast("Bluetooth has been disabled")
                }
            } else if(resultCode ==Activity.RESULT_CANCELED){
                toast("Bluetooth enabling has been canceled")
            }
        }
    }
}

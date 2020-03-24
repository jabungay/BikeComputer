package com.example.bikecomputer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_galileo.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class Galileo : AppCompatActivity() {

    val UUID:String="fc7ca420-c971-4b1d-9b09-6f61abe22b05"
    lateinit var BlueDevice: BluetoothDevice
    companion object{
        val selectedaddress:String="hello world"
        var isconnected:Boolean=false
        var isfound:Boolean=false
        lateinit var Bsocket: BluetoothSocket
        lateinit var Output: OutputStream
        lateinit var Input: InputStream
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle: Bundle? = intent.extras

        var macAddr: String? = null

        if (bundle != null)
        {
            macAddr = bundle.getString("MAC_Address")
        }


        setContentView(R.layout.activity_galileo)

        reconnectButton.setOnClickListener { val connectIntent = Intent(this, MainActivity::class.java )
            startActivity(connectIntent)}

        BluetoothInitialization()
    }
    fun BluetoothInitialization(){
        var blueadapt: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(blueadapt==null)
        {
            println("This device does not support bluetooth")
        }
        if(!blueadapt.isEnabled) {
            val blueadaptIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivity(blueadaptIntent)
            try{
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        var setofdevices:Set<BluetoothDevice> = blueadapt.bondedDevices
        if(setofdevices.isEmpty())
        {
            println("Must pair devices first")
        }
        else
        {
            for(BluetoothDevice in setofdevices)
            {
                if(BluetoothDevice.address==selectedaddress)
                {
                    BlueDevice=BluetoothDevice
                    isfound=true
                    break

                }
            }
        }
    }
    fun BlueConnect(){
        if(isfound)
        {
            try {
                Bsocket=BlueDevice.createInsecureRfcommSocketToServiceRecord(java.util.UUID.randomUUID())
                Bsocket.connect()
            }
            catch(e: IOException) {
                e.printStackTrace()
                isconnected=false
            }
            if(isconnected) {
                try {
                    Output= Bsocket.outputStream
                } catch(e: IOException) {
                    e.printStackTrace()
                }
                try {
                    Input= Bsocket.inputStream
                }catch (e: IOException)
                {
                    e.printStackTrace()
                }
            }
        }
    }
    //Allows the user to view the speedometer and the odometer
    //Allows the user to set the wheel diameter
}

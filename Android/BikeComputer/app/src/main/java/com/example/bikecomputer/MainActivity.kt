package com.example.bikecomputer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//
//        // Get the bluetooth adapter
//        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
//
//        // Turn on bluetooth if it's off
//        if (bluetoothAdapter?.isEnabled == false) {
//            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            startActivityForResult(enableBtIntent, 1)
//        }
//
//        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
//        pairedDevices?.forEach { device ->
//
//            val button: RadioButton = RadioButton(this)
//
//            val btnText = device.name + "\n" + device.address
//
//            button.text = btnText
//
//            btList.addView(button)
//
//            var dataString = ""
//            dataString += device.name
//            dataString += device.address
//        }
    }

    fun connectToDevice(view: View)
    {
//        val buttonID = btList.checkedRadioButtonId
//        val selectedButton: RadioButton = findViewById<RadioButton>(buttonID)
//        val btnString = selectedButton.text.toString()
//
//        var macAddr = btnString.split("\n")[1]
//
//        val toastLength = Toast.LENGTH_SHORT
//        Toast.makeText(this, "Will Connect to: \n" + selectedButton.text, toastLength).show()

        val macIntent: Intent = Intent(this, Galileo::class.java)
        macIntent.putExtra("MAC_Address", "98:D3:32:11:50:96")
        startActivity(macIntent)
    }
}

package com.example.bikecomputer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Layout
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_galileo.*
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.util.*


const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2

var speedGlobal: Float? = null
var odoGlobal: Float? = null



class Galileo : AppCompatActivity() {

    val uuid: UUID = UUID.fromString("5acc375a-6e15-11ea-bc55-0242ac130003")
    val bta: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    var macAddr: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("BTINIT", "setting view")
        setContentView(R.layout.activity_galileo)
        reconnectButton.setOnClickListener { val connectIntent = Intent(this, MainActivity::class.java )
            startActivity(connectIntent)}

        val bundle: Bundle? = intent.extras

        if (bundle != null)
        {
            macAddr = bundle.getString("MAC_Address").toString()
        }

        val device: BluetoothDevice = bta.getRemoteDevice(macAddr)

        val han: Handler = object : Handler(Looper.getMainLooper()) {

            override fun handleMessage(msg: Message) {
                val jsonData = msg.obj as JSONObject


                when (msg.what)
                {
                    1 -> {
                        var speed: Float = jsonData.getInt("speed") * 3.6f / 1000.0f
                        var odometer: Float = jsonData.getInt("odometer") / 1000000.0f

                        println("speed: " + speed.toString())
                        println("odo: " + odometer.toString())

                        val speedTV: TextView = findViewById(R.id.speedText)

                        speedTV.text = "hi"
                        speedTV.postInvalidate()
                          setContentView(R.layout.activity_galileo)


                    }

                    else -> super.handleMessage(msg)

                }

            }

        }


        startButton.setOnClickListener { startBT(device, han) }

    }

    private fun startBT(device: BluetoothDevice, handler: Handler)
    {
        ConnectBT(device, handler).run()
    }

    private inner class ConnectBT(device: BluetoothDevice, private val handler: Handler) : Thread()
    {
        val clazz = bta.getRemoteDevice(macAddr).javaClass
        val paramTypes = arrayOf<Class<*>>(Integer.TYPE)
        val m = clazz.getMethod("createRfcommSocket", *paramTypes)
        val params = arrayOf<Any>(Integer.valueOf(1))


        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            m.invoke(bta.getRemoteDevice(macAddr), 1) as BluetoothSocket
        }

        override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bta?.cancelDiscovery()

            mmSocket?.use { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()
                Log.d("BTINIT", "CONNECTED!")

                MyBluetoothService(handler).startThread(mmSocket)

            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e("BTINIT", "Could not close the client socket", e)
            }
        }
    }


}

class MyBluetoothService(private val handler: Handler) {



    fun startThread(mmSocket: BluetoothSocket?)
    {
        if (mmSocket != null)
        {
            ConnectedThread(mmSocket).run()
        }
    }

    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        override fun run() {
            var numBytes: Int // bytes returned from read()

            var dataStr: String = ""


            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    if(mmInStream.available() > 0)
                    {
                        var byte: Int = mmInStream.read()
                        do
                        {
                            dataStr += byte.toChar()
                            byte = mmInStream.read()
                        } while (byte.toChar() != 'n')

                        if (dataStr.isNotEmpty())
                        {
                            var jsonData: JSONObject = JSONObject(dataStr)
                            var message:Message = Message()

                            message.obj = jsonData
                            message.what = 1

                            handler.handleMessage(message)



//                            var speed: Float = jsonData.getInt("speed") * 3.6f / 1000.0f
//                            var odometer: Float = jsonData.getInt("odometer") / 1000000.0f

                            dataStr = ""
                        }

                    }
                } catch (e: IOException) {
                    Log.d("BTINIT", "Input stream was disconnected", e)
                    break
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e("BTINIT", "Error occurred when sending data", e)

                // Send a failure message back to the activity.
                val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
                val bundle = Bundle().apply {
                    putString("toast", "Couldn't send data to the other device")
                }
                writeErrorMsg.data = bundle
                handler.sendMessage(writeErrorMsg)
                return
            }

            // Share the sent message with the UI activity.
            val writtenMsg = handler.obtainMessage(
                MESSAGE_WRITE, -1, -1, mmBuffer)

            writtenMsg.sendToTarget()
        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e("BTINIT", "Could not close the connect socket", e)
            }
        }
    }
}


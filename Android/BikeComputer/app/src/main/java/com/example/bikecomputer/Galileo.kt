package com.example.bikecomputer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_galileo.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*


const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2

class Galileo : AppCompatActivity() {

    val uuid: UUID = UUID.fromString("5acc375a-6e15-11ea-bc55-0242ac130003")
    val bta: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("BTINIT", "setting view")
        setContentView(R.layout.activity_galileo)
        reconnectButton.setOnClickListener { val connectIntent = Intent(this, MainActivity::class.java )
            startActivity(connectIntent)}

        val bundle: Bundle? = intent.extras

        var macAddr: String? = null

        if (bundle != null)
        {
            macAddr = bundle.getString("MAC_Address").toString()
        }

        val device: BluetoothDevice = bta.getRemoteDevice(macAddr)

        startButton.setOnClickListener { startBT(device) }

    }

    private fun startBT(device: BluetoothDevice)
    {
        ConnectBT(device).run()
    }

    private inner class ConnectBT(device: BluetoothDevice) : Thread()
    {
        val clazz = bta.getRemoteDevice("98:D3:32:11:50:96").javaClass
        val paramTypes = arrayOf<Class<*>>(Integer.TYPE)
        val m = clazz.getMethod("createRfcommSocket", *paramTypes)
        val params = arrayOf<Any>(Integer.valueOf(1))


        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            m.invoke(bta.getRemoteDevice("98:D3:32:11:50:96"), 1) as BluetoothSocket
        }

        public override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bta?.cancelDiscovery()

            mmSocket?.use { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()
                Log.d("BTINIT", "CONNECTED!")
                MyBluetoothService(Handler()).startThread(mmSocket)

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

class MyBluetoothService(
    // handler that gets info from Bluetooth service
    private val handler: Handler) {

    fun startThread(mmSocket: BluetoothSocket?)
    {
        if (mmSocket != null)
        {
            ConnectedThread(mmSocket).run()
            val bts = "Connected".toByteArray(Charsets.UTF_8)
            ConnectedThread(mmSocket).write(bts)
        }
    }

    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        override fun run() {
            var numBytes: Int // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                // Read from the InputStream.
                numBytes = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.d("BTINIT", "Input stream was disconnected", e)
                    break
                }

                for (b in mmBuffer)
                {
                    print(b.toChar())

                }
                println("")

                // Send the obtained bytes to the UI activity.
                val readMsg = handler.obtainMessage(
                    MESSAGE_READ, numBytes, -1,
                    mmBuffer)
                val bundle = readMsg.data
                readMsg.sendToTarget()
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


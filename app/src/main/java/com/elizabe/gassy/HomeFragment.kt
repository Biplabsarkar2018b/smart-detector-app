package com.elizabe.gassy

import android.os.Bundle
import android.util.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.socket.client.Socket
import io.socket.emitter.*
import kotlinx.coroutines.Dispatchers.IO
import org.json.*
import java.net.*

class HomeFragment : Fragment() {

    private lateinit var mq2value:TextView
    private lateinit var mq135value:TextView
    private lateinit var mSocket: Socket
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        mq2value = view.findViewById(R.id.mq2value)
        mq135value = view.findViewById(R.id.mq2value)

        mSocket = SocketHandler.getSocket()
        mSocket.on("storedValue",onNewValue)
        Log.d("Holy Moly",mSocket.toString())

        return view
    }
    private val onNewValue = Emitter.Listener { args ->
        activity?.runOnUiThread {
            val data = args[0]
            if (data is JSONObject) {
                Log.d("cfdvhfdv",data.toString())
                try {
                    val value = data.getString("value")
                    // Update TextView with the received value
                    mq2value.text = value
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else if (data is Double) {
                // Handle the case when data is a Double
                // Update TextView with the received value
                mq2value.text = data.toString()
            }else{
                mq2value.text = data.toString()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Remove listener when fragment is destroyed
        mSocket.off("storedValue", onNewValue)
    }
}
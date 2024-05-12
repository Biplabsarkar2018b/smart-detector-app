package com.elizabe.gassy

import android.app.*
import android.content.*
import android.content.pm.*
import android.graphics.*
import android.os.*
import android.util.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.*
import androidx.core.content.ContextCompat.getSystemService
import com.android.volley.*
import com.android.volley.toolbox.*
import io.socket.client.Socket
import io.socket.emitter.*
import kotlinx.coroutines.Dispatchers.IO
import org.json.*
import org.xml.sax.ContentHandler
import java.net.*

class HomeFragment : Fragment() {

    private lateinit var mq2value:TextView
    private lateinit var mq135value:TextView
    private lateinit var mSocket: Socket
    private var THRESHOLD = 300.0 // Threshold value for mq2 and mq135



    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        mq2value = view.findViewById(R.id.mq2value)
        mq135value = view.findViewById(R.id.mq135value)
        notificationManager =requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        THRESHOLD = requireContext().getSharedPreferences("sp",Context.MODE_PRIVATE).getInt("thValue",400).toDouble()

        mSocket = SocketHandler.getSocket()
        mSocket.on("storedValues",onNewValue)

        return view
    }
    private val onNewValue = Emitter.Listener { args ->
        activity?.runOnUiThread {
            val data = args[0] as JSONObject
            try {
                // Extract mq2 and mq135 values from the received JSON object
                val mq2 = data.getDouble("mq2")
                val mq135 = data.getDouble("mq135")

                // Update TextViews with the received values
                mq2value.text = mq2.toString()
                mq135value.text = mq135.toString()
                // Check if mq2 or mq135 values exceed the threshold
                if (mq2 > THRESHOLD || mq135 > THRESHOLD) {
                    showNotification()
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
    private fun showNotification() {
        val context = requireContext()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(requireContext(), channelId)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("Alert")
                .setContentText("The Threshold value has been reached!")
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
        } else {

            builder = Notification.Builder(requireContext())
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("Alert")
                .setContentText("The Threshold value has been reached!")
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
        }
        notificationManager.notify(1234, builder.build())
    }


    override fun onDestroy() {
        super.onDestroy()
        // Remove listener when fragment is destroyed
        Log.d("whatsds","destroying")
        mSocket.off("storedValues", onNewValue)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSocket.off("storedValues", onNewValue)
    }

    override fun onResume() {
        super.onResume()
        mSocket.on("storedValues", onNewValue)

        val ip = requireActivity().getSharedPreferences("sp",Context.MODE_PRIVATE).getString("ip","")
        val queue = Volley.newRequestQueue(requireContext())
        val url = "http://${ip}:3000/api/values"
        val res = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                mq2value.text = response["mq2"].toString()
                mq135value.text = response["mq135"].toString()
            },
            { error ->
                Toast.makeText(requireContext(),"Error Fetching The Values",Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(res)


    }


}
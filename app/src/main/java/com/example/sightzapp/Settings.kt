package com.example.sightzapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private lateinit var btnShowOnMap: Button

/**
 * A simple [Fragment] subclass.
 * Use the [Settings.newInstance] factory method to
 * create an instance of this fragment.
 */
class Settings : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)


        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View? = null
        view = inflater.inflate(R.layout.fragment_settings, container, false)

        val btnShowOnMap: Button = view.findViewById(R.id.btnShowOnMap)
        btnShowOnMap.setOnClickListener {
            val txtShowOnMap: TextView = view.findViewById(R.id.txtShowOnMap)
            val location: String = txtShowOnMap.text.toString()

            val intent: Intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse("geo:0,0?q=" + location)
            }
            startActivity(intent)
        }
        val btnCallNumber: Button = view.findViewById(R.id.btnCallNumber)
        btnCallNumber.setOnClickListener {
            val txtCallNumber: TextView = view.findViewById(R.id.txtCallNumber)
            val number: String = txtCallNumber.text.toString()

            val intent: Intent = Intent().apply {
                action = Intent.ACTION_DIAL
                data = Uri.parse("tel:" + number)
            }
            startActivity(intent)
        }

        val btnOpenWebsite: Button = view.findViewById(R.id.btnOpenWebsite)
        btnOpenWebsite.setOnClickListener {
            val txtOpenWebsite: TextView = view.findViewById(R.id.txtOpenWebsite)
            val url: String = txtOpenWebsite.text.toString()

            val intent: Intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(url)
            }
            startActivity(intent)
        }

        val btnShareText: Button = view.findViewById(R.id.btnShareText)
        btnShareText.setOnClickListener {
            val txtShareText: TextView = view.findViewById(R.id.txtShareText)
            val url: String = txtShareText.text.toString()

            val intent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
            }

            startActivity(intent)
        }


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Settings.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Settings().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
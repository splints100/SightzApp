package com.example.sightzapp

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Maps.newInstance] factory method to
 * create an instance of this fragment.
 */
class Maps : Fragment(), OnMapReadyCallback, LocationListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mapView: MapView
    private lateinit var mMap: GoogleMap
    private lateinit var searchView: SearchView
    private lateinit var autocompleteSessionToken: AutocompleteSessionToken

    //Places.initialize(getApplicationContext(), apiKey);


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Maps.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Maps().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        searchView = view.findViewById(R.id.idSearchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // location name from search view.
                val location = searchView.query.toString()

                // below line is to create a list of address
                // where we will store the list of all address.
                var addressList: List<Address>? = null

                if (location != null || location == "") {
                    val geocoder = Geocoder(requireContext())
                    try {

                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val address = addressList!![0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    mMap.addMarker(MarkerOptions().position(latLng).title(location))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                }

                // Perform search functionality here
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                // Update search results as the user types
                return true
            }
        })

        //val placesClient = Places.

        autocompleteSessionToken = AutocompleteSessionToken.newInstance()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Places SDK
        Places.initialize(requireContext().applicationContext, "AIzaSyDMxgfizFsN3hv6QXvH3o-7241Mg_gmhpM")

        // Optional: Set additional configuration options
        val placesClient = Places.createClient(requireContext())

        // You can now use the Places SDK APIs
        // For example, you can use the Autocomplete API:
        val autocompleteFragment =
            AutocompleteSupportFragment.newInstance()

        // Configure the AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )
    }





    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

   override  fun onMapReady(googleMap: GoogleMap) {

       mMap = googleMap

        // Customize the map settings as needed
        mMap.uiSettings.isZoomControlsEnabled = true

        // Move the camera to a default location
        val defaultLocation = LatLng(53.0091, -2.1761) // Staffordshire University
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))

        // Add a marker to the default location
        mMap.addMarker(
            MarkerOptions()
                .position(defaultLocation)
                .title("Staffordshire University")
        )
    }

    override fun onLocationChanged(location: Location) {

    }

}


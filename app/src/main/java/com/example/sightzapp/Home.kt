package com.example.sightzapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewPager: ViewPager2
    private lateinit var indicator: DotsIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)


        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view: View? = null
        view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize ViewPager2 and indicator
        viewPager = view.findViewById(R.id.viewpager)
        indicator = view.findViewById(R.id.dots_indicator)

        // Set up the adapter
        val adapter = ViewPagerAdapter(getDataList())
        viewPager.adapter = adapter

        // Attach the indicator to the ViewPager2
        indicator.attachTo(viewPager)




    return view
    }

    private fun getDataList(): List<Custom> {
        // Create a list of data items (images, titles, descriptions)
        val dataList = ArrayList<Custom>()
        dataList.add(Custom(getString(R.string.a_desc), getString(R.string.bead_1),R.drawable.a ))
        dataList.add(Custom(getString(R.string.b_desc), getString(R.string.bead_2),R.drawable.b ))
        dataList.add(Custom(getString(R.string.c_desc), getString(R.string.bead_3),R.drawable.c ))
        dataList.add(Custom(getString(R.string.d_desc), getString(R.string.bead_4),R.drawable.d ))
        dataList.add(Custom(getString(R.string.e_desc), getString(R.string.bead_5),R.drawable.e ))
        return dataList
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}


/*val adapter = ViewPagerAdapter(titleList, descList, imagesList)
viewPager.adapter = adapter

postToList()
*/

/*private fun addToList(title: String, description: String, image: Int){
    titleList.add(title)
    descList.add(description)
    imagesList.add(image)
}*/




/*private  fun postToList(){
    for (i in 1..6) {

        addToList(

            title = listOf(
                getString(R.string.bead_1),
                getString(R.string.bead_1),
                getString(R.string.bead_3),
                getString(R.string.bead_4),
                getString(R.string.bead_5)

            )

                    description = listOf(
                    getString(R.string.a_desc),
            getString(R.string.b_desc),
            getString(R.string.c_desc),
            getString(R.string.d_desc),
            getString(R.string.e_desc)
        )

        image = listOf(
            R.drawable.a,
            R.drawable.b,
            R.drawable.c,
            R.drawable.d,
            R.drawable.e)
        )
    }
}*/
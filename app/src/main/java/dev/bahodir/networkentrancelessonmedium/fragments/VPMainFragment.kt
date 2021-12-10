package dev.bahodir.networkentrancelessonmedium.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.bahodir.networkentrancelessonmedium.R
import dev.bahodir.networkentrancelessonmedium.adapters.RV
import dev.bahodir.networkentrancelessonmedium.databinding.BottomSheetDialogBinding
import dev.bahodir.networkentrancelessonmedium.databinding.FragmentVPMainBinding
import dev.bahodir.networkentrancelessonmedium.network.NetworkHelper
import dev.bahodir.networkentrancelessonmedium.share.Shared
import dev.bahodir.networkentrancelessonmedium.user.User
import io.reactivex.Observable

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VPMainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VPMainFragment : Fragment() {
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

    private var _binding: FragmentVPMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var rv: RV
    private lateinit var requestQueue: RequestQueue
    private lateinit var networkHelper: NetworkHelper
    private lateinit var list: MutableList<User>

    private var isLike = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVPMainBinding.inflate(inflater, container, false)



        requestQueue = Volley.newRequestQueue(requireContext())
        networkHelper = NetworkHelper(requireContext())

        if (networkHelper.isNetworkConnected()) {
            rv = RV(object : RV.OnTouchListener {
                override fun itemClick(user: User, position: Int, view: View) {
                    val bind: BottomSheetDialogBinding =
                        BottomSheetDialogBinding.inflate(layoutInflater)
                    /*val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)
                    dialog.setContentView(bind.root)*/

                    val builder = AlertDialog.Builder(requireContext(), R.style.SheetDialog)
                    builder.setView(bind.root)

                    val dialog = builder.create()

                    bind.countryOne.text = user.Ccy

                    bind.fabCircle.setOnClickListener {
                        bind.courseTwo.text = ""

                        val cOne = bind.countryOne.text.toString()
                        val cTwo = bind.countryTwo.text.toString()

                        var bufferTwo = cOne
                        var bufferOne = cTwo

                        bind.countryOne.text = bufferOne
                        bind.countryTwo.text = bufferTwo
                    }

                    onCreateObservable(bind)
                        .subscribe {
                            if (it == "") {

                            }
                            else {
                                if (bind.countryOne.text.toString() == "UZS") {
                                    bind.courseTwo.text = "${it.toDouble() / user.Rate.toDouble()}"
                                } else {
                                    bind.courseTwo.text = "${user.Rate.toDouble() * it.toDouble()}"
                                }
                            }
                        }

                    dialog.show()
                }

                override fun likeClick(user: User, position: Int, view: View) {
                    Toast.makeText(requireContext(), "Like clicked", Toast.LENGTH_SHORT).show()
                }

            })
            binding.rv.adapter = rv
            val jsonArrayRequest = JsonArrayRequest(
                Request.Method.GET,
                "https://cbu.uz/uz/arkhiv-kursov-valyut/json/",
                null,
                { response ->
                    list = Gson().fromJson(
                        response.toString(),
                        object : TypeToken<MutableList<User>>() {}.type
                    )

                    for (i in list.indices) {
                        list[i].image =
                            "https://flagcdn.com/w160/" + list[i].Ccy[0].lowercase() + list[i].Ccy[1].lowercase() + ".png"
                    }
                    rv.submitList(list)
                }
            ) { error ->
                Log.d("TAG", "onCreateView: ${error?.message}")
            }
            requestQueue.add(jsonArrayRequest)

        } else {
            Toast.makeText(requireContext(), "Not Connected", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun onCreateObservable(bind: BottomSheetDialogBinding): Observable<String> {
        return Observable.create { emitter ->
            bind.courseOne.addTextChangedListener {
                emitter.onNext(it.toString())
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VPMainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VPMainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
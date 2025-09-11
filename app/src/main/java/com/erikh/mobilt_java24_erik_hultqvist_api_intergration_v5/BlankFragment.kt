package com.erikh.mobilt_java24_erik_hultqvist_api_intergration_v5

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.erikh.mobilt_java24_erik_hultqvist_api_intergration_v5.MainActivity.Companion.navController
import com.google.firebase.auth.FirebaseAuth

private val TAG = "ERIK"

lateinit var loginName : EditText
lateinit var loginPassword : EditText

lateinit var loginButton : Button

class BlankFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var backgroundImage: ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //FragmentDone(view)

        loginName = view.findViewById(R.id.loginName)
        loginPassword = view.findViewById(R.id.loginPassword)
        loginButton = view.findViewById(R.id.loginButton)

        val prefs = requireContext().getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        Log.i(TAG, "prefs - Location : " + prefs.getString(PreferenceKeys.KEY_LOCATION_NAME, Constants.DEFAULT_LOCATION_NAME) + " lat: " + prefs.getFloat(PreferenceKeys.KEY_LATITUDE, Constants.DEFAULT_LAT) + " long: " + prefs.getFloat(PreferenceKeys.KEY_LONGITUDE, Constants.DEFAULT_LON) + " ")

        auth = FirebaseAuth.getInstance()

        val backgrounds = listOf(
            R.drawable.bg_fragment_1,
            R.drawable.bg_fragment_2,
            R.drawable.bg_fragment_3_1,
            R.drawable.bg_fragment_3_2,
            R.drawable.bg_fragment_3_3
        )

        backgroundImage = view.findViewById<ImageView>(R.id.backgroundImage)
        backgroundImage.setImageResource(backgrounds.random())

        val userName = "testar@testar.com"
        val userPassword = "testar"

        loginButton.setOnClickListener {
            /*val userName = loginName.text.toString()
            val userPassword = loginPassword.text.toString()*/

            if (userName.isNotEmpty() && userPassword.isNotEmpty()) {

                auth.signInWithEmailAndPassword(userName, userPassword)

                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Login success
                            Log.i(TAG, "onViewCreated: user: " + userName + "passw: " + userPassword + "")
                            Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()

                            navController.navigate(R.id.action_blankFragment_to_blankFragment2)
                        } else {
                            // Login failed
                            Toast.makeText(requireContext(), "Login Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show()

            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view : View = inflater.inflate(R.layout.fragment_blank, container, false)

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment().apply {
            }
    }

    fun FragmentDone(view: View) {
        view.findViewById<TextView>(R.id.loginFrag).setText("LOGIN")
    }

}
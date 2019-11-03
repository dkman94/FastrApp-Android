package app.android.fastrapp

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import android.widget.Toast



class EmergencyContactFragment : Fragment() {
    lateinit var helper:UserPrefsHelper;

    fun initialize(helper: UserPrefsHelper){
        this.helper = helper
    }
    fun callPhoneNumber(phoneNumber: String){
        val intent = Intent(Intent.ACTION_CALL)

        intent.data = Uri.parse("tel:" + phoneNumber)
        context?.startActivity(intent)
    }

    fun sendSMS(phoneNo: String, msg: String, context:Context) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNo, null, msg, null, null)
            Toast.makeText(
                context, "Message Sent",
                Toast.LENGTH_LONG
            ).show()
        } catch (ex: Exception) {
            Toast.makeText(
                context, ex.message.toString(),
                Toast.LENGTH_LONG
            ).show()
            ex.printStackTrace()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.emergency_page_fragment, container, false)
        val results = helper.currentTestSession
        var failedFAST:Boolean = false
        val allClearBtn = view.findViewById<TextView>(R.id.all_clear)
        val armsRes = view.findViewById<TextView>(R.id.arm_results_val)
        val faceRes = view.findViewById<TextView>(R.id.face_results_val)
        val speechRes = view.findViewById<TextView>(R.id.arm_results_val)

        if(results.hasArmsWeakness == true){
            armsRes.setText("Fail")
            armsRes.setTextColor(Color.RED)
            failedFAST = true
        }
        else if(results.hasCheckedArms == true){
            armsRes.setText("Pass")
            armsRes.setTextColor(Color.GREEN)
        }
        if(results.hasFaceWeakness == true){
            faceRes.setText("Fail")
            faceRes.setTextColor(Color.RED)
            failedFAST = true

        }
        else if(results.hasCheckedFace == true){
            faceRes.setText("Pass")
            faceRes.setTextColor(Color.GREEN)
        }
        if(results.hasSpeechSlur == true){
            speechRes.setText("Fail")
            speechRes.setTextColor(Color.RED)
            failedFAST = true
        }
        else if(results.hasCheckedSpeech == true){
            speechRes.setText("Pass")
            speechRes.setTextColor(Color.GREEN)
        }
        if (failedFAST == true){
            val summaryTextView = view.findViewById<TextView>(R.id.summary_message)
            summaryTextView.setText("Your tests indicate that you have had a stroke. Please call either 911 or your emergency contact")
            summaryTextView.setTextColor(Color.RED)
            val EMSBtn = view.findViewById<TextView>(R.id.call_911)
            val ECBtn = view.findViewById<TextView>(R.id.call_ec)
            allClearBtn.visibility = View.INVISIBLE

            EMSBtn.setOnClickListener(){
                callPhoneNumber("+15132183679")
            }
            ECBtn.setOnClickListener(){
                callPhoneNumber("+15132183679")
            }
            EMSBtn.visibility = View.VISIBLE
            ECBtn.visibility = View.VISIBLE
        }
        else{
            allClearBtn.setOnClickListener(){
                sendSMS("+15132183679", "Hi, I just wanted to let you know that I used FASTr to reassure us that I do not have the common symptoms of stroke. Love you!", context!!)
            }
        }

        return view;
    }
}

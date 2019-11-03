package app.android.fastrapp

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri



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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.emergency_page_fragment, container, false)
        val results = helper.currentTestSession
        var failedFAST:Boolean = false
        if(results.arms == true){
            val armsRes = view.findViewById<TextView>(R.id.arm_results_val)
            armsRes.setText("Fail")
            armsRes.setTextColor(Color.RED)
            failedFAST = true
        }
        if(results.face == true){
            val armsRes = view.findViewById<TextView>(R.id.face_results_val);
            armsRes.setText("Fail")
            armsRes.setTextColor(Color.RED)
            failedFAST = true

        }
        if(results.speech == true){
            val armsRes = view.findViewById<TextView>(R.id.arm_results_val);
            armsRes.setText("Fail")
            armsRes.setTextColor(Color.RED)
            failedFAST = true
        }
        if (failedFAST == true){
            val summaryTextView = view.findViewById<TextView>(R.id.summary_message)
            summaryTextView.setText("Your tests indicate that you have had a stroke. Please call either 911 or your emergency contact")
            summaryTextView.setTextColor(Color.RED)
            val EMSBtn = view.findViewById<TextView>(R.id.call_911)
            val ECBtn = view.findViewById<TextView>(R.id.call_ec)
            EMSBtn.setOnClickListener(){
                callPhoneNumber("+15132183679")
            }
            ECBtn.setOnClickListener(){
                callPhoneNumber("+15132183679")
            }
            EMSBtn.visibility = View.VISIBLE
            ECBtn.visibility = View.VISIBLE
        }
        return view;
    }
}

package app.android.fastrapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment

class CallEmergencyServicesAlert : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.emergency_services_alert, container, false)
        val callEmergencyButton = view.findViewById<Button>(R.id.call_emergency_button)
        callEmergencyButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:" + "+15132367002")
            context?.startActivity(intent)
        }
        return view
    }
}

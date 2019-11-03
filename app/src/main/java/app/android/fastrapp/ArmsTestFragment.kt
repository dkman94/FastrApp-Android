package app.android.fastrapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class ArmsTestFragment : Fragment() {
    lateinit var helper: UserPrefsHelper
    lateinit var rightChevron: ImageView
    lateinit var yesBtn: TextView
    lateinit var noBtn: TextView
    lateinit var armsResult: TextView

    fun initialize(helper: UserPrefsHelper) {
        this.helper = helper
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.arms_test_page_fragment, container, false)
        armsResult = view.findViewById<TextView>(R.id.arms_test_result)
        rightChevron = view.findViewById<ImageView>(R.id.right_chevron)
        rightChevron.visibility = View.INVISIBLE

        yesBtn = view.findViewById<TextView>(R.id.yes_weakness)
        yesBtn.setOnClickListener {
            helper.UpdateActivity(UserPrefsHelper.TestType.ARM, false)
            armsResult.setText(R.string.no_arm_weakness)
            armsResult.setBackgroundColor(context!!.resources.getColor(R.color.healthyTestAlert))
            rightChevron.visibility = View.VISIBLE
        }

        noBtn = view.findViewById<TextView>(R.id.no_weakness)
        noBtn.setOnClickListener {
            helper.UpdateActivity(UserPrefsHelper.TestType.ARM, true)
            armsResult.setText(R.string.arms_weakness)
            armsResult.setBackgroundColor(context!!.resources.getColor(R.color.failedTestAlert))
            val emergencyServicesAlert = CallEmergencyServicesAlert()
            emergencyServicesAlert.show(fragmentManager!!, "emergencyAlert")
            rightChevron.visibility = View.INVISIBLE
        }

        return view
    }
}

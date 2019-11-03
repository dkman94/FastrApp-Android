package app.android.fastrapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class ArmsTestFragment : Fragment() {
    lateinit var helper: UserPrefsHelper;

    fun initialize(helper: UserPrefsHelper) {
        this.helper = helper
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.arms_test_page_fragment, container, false)
        val yesBtn = view.findViewById<TextView>(R.id.yes_weakness);
        val armsResult = view.findViewById<TextView>(R.id.arms_test_result)
        yesBtn.setOnClickListener {
            helper.UpdateActivity(UserPrefsHelper.TestType.ARM, false)
            armsResult.setText(R.string.no_arm_weakness)
            armsResult.setBackgroundColor(context!!.resources.getColor(R.color.healthyTestAlert))
        }
        val noBtn = view.findViewById<TextView>(R.id.no_weakness);
        noBtn.setOnClickListener {
            helper.UpdateActivity(UserPrefsHelper.TestType.ARM, true)
            armsResult.setText(R.string.arms_weakness)
            armsResult.setBackgroundColor(context!!.resources.getColor(R.color.failedTestAlert))
        }
        return view;
    }
}

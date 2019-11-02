package app.android.fastrapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.arms_test_page_fragment.view.*

class ArmsTestFragment : Fragment() {
    lateinit var helper:UserPrefsHelper;

    fun initialize(helper: UserPrefsHelper){
        this.helper = helper
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view= inflater.inflate(R.layout.arms_test_page_fragment, container, false)
        val yesBtn = view.findViewById<TextView>(R.id.yes_weakness);
        yesBtn.setOnClickListener(){
            helper.UpdateActivity(UserPrefsHelper.TestType.ARM, true)
            //helper.WriteActivity() example of how to call WriteActivity
        }
        val noBtn = view.findViewById<TextView>(R.id.no_weakness);
        noBtn.setOnClickListener(){
            helper.UpdateActivity(UserPrefsHelper.TestType.ARM, false)
        }
        return view;
    }
}

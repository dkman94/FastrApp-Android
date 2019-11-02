package app.android.fastrapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class SmileTestFragment : Fragment() {
    lateinit var helper:UserPrefsHelper;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.smile_tests_page_fragment, container, false)

    fun initialize(helper: UserPrefsHelper){
        this.helper = helper
    }
}

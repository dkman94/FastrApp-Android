package app.android.fastrapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class EmergencyContactFragment : Fragment() {
    lateinit var helper:UserPrefsHelper;

    fun initialize(helper: UserPrefsHelper){
        this.helper = helper
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.emergency_page_fragment, container, false)
}

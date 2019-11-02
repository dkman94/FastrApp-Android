package app.android.fastrapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager

class MainActivity : FragmentActivity() {

    private lateinit var mPager: ViewPager
    private lateinit var userPrefsHelper:UserPrefsHelper;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPager = findViewById(R.id.pager)
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.adapter = pagerAdapter
        userPrefsHelper = UserPrefsHelper(this)
        userPrefsHelper.CreateNewEntry()
    }

    override fun onBackPressed() {
        if (mPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            mPager.currentItem = mPager.currentItem - 1
        }
    }


    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = 5

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> SmileTestFragment().apply { initialize(userPrefsHelper) }
            1 -> ArmsTestFragment().apply { initialize(userPrefsHelper) }
            2 -> SpeakTestFragment().apply { initialize(userPrefsHelper) }
            3 -> EmergencyContactFragment().apply { initialize(userPrefsHelper) }
            4 -> ResultsFragment().apply { initialize(userPrefsHelper) }

            else -> ResultsFragment()
        }
    }
}

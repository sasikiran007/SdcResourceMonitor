package com.example.sdcresourcemonitor.view.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sdcresourcemonitor.R
import kotlinx.android.synthetic.main.fragment_selected_alerts.*


private const val ARG_OBJECT = "object"

class AlertListTabbedAdapter(fragment : Fragment, val itemsCount: Int) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return itemsCount
    }

    override fun createFragment(position: Int): Fragment {
        val fragment  = SelectedAlertsFragment()
        fragment.arguments = Bundle().apply {
            putInt(ARG_OBJECT,position + 1)
        }
        return fragment
    }
}

class SelectedAlertsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_selected_alerts,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            numberTextView.text = getInt(ARG_OBJECT).toString()
        }
    }

}

package com.example.sdcresourcemonitor.view.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sdcresourcemonitor.R
import com.example.sdcresourcemonitor.view.listener.RadioButtonClickListener
import com.example.sdcresourcemonitor.viewModel.AlertViewModel
import kotlinx.android.synthetic.main.fragment_alert_list.*


private const val ARG_OBJECT1 = "alertSection"
private const val ARG_OBJECT2 = "alertLevel"
private const val ARG_OBJECT3 = "entity"

class AlertListTabbedAdapter(
    fragment: Fragment,
    val itemsCount: Int,
    val alertSection: String,
    val entity: String
) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return itemsCount
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = SelectedAlertsFragment()
        fragment.arguments = Bundle().apply {
            putString(ARG_OBJECT1, alertSection)
            putInt(ARG_OBJECT2, position)
            putString(ARG_OBJECT3, entity)
        }
        return fragment
    }
}

class SelectedAlertsFragment : Fragment(), RadioButtonClickListener{

    lateinit var alertListAdapter: AlertListAdapter
    lateinit var entityFilterAdapter: EntityFilterAdapter
    lateinit var viewModel: AlertViewModel
    private var _alertSection: String = "%%"
    private var _alertLevel = "%%"
    private var _entity = "%%"
    private val TAG = "SelectedAlertsFragment"

    private var _showFilterGrid = false

    private var ENTITY_FILTER_SELECTED_POSITION = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_alert_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(this).get(AlertViewModel::class.java)

        alertListAdapter = AlertListAdapter(ArrayList())

        entityFilterAdapter = EntityFilterAdapter(ArrayList(),this)

        arguments?.takeIf { it.containsKey(ARG_OBJECT1) }?.apply {
            _alertSection = getString(ARG_OBJECT1).toString()
        }

        arguments?.takeIf { it.containsKey(ARG_OBJECT2) }?.apply {
            _alertLevel = when (getInt(ARG_OBJECT2)) {
                0 -> "critical"
                1 -> "major"
                2 -> "minor"
                else -> "%%"
            }

        }

        arguments?.takeIf { it.containsKey(ARG_OBJECT3) }?.apply {
            _entity = getString(ARG_OBJECT3).toString()
        }

        Log.i(TAG, "$_alertLevel , $_alertSection, $_entity")

        viewModel.isLoading.value = true

        _showFilterGrid = false
        viewModel.refresh(_alertSection, _alertLevel, _entity)

        swipeRefreshList.setOnRefreshListener {
            _showFilterGrid = false
            swipeRefreshList.isRefreshing = false
            viewModel.isLoading.value = true
            viewModel.refreshByPassLocal(_alertSection, _alertLevel, _entity)
        }

        observeViewModel()


        alertListRecyclerView.apply {
            alertListRecyclerView.layoutManager = LinearLayoutManager(context)
            alertListRecyclerView.adapter = alertListAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }

        filterGridRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
//            layoutManager = LinearLayoutManager(context)
            adapter = entityFilterAdapter
        }

    }


    private fun observeViewModel() {
        viewModel.entities.observe(viewLifecycleOwner, Observer { entities ->
            entities?.let {
                Log.i(TAG,"show entities filter : $_showFilterGrid")
                entityFilterAdapter.update(entities)
                when {
                    entities.isEmpty() -> setVisibility(hasAlert = true,hasEntity =  _showFilterGrid)
                    entities.size == 1 -> {
                        setVisibility(hasAlertList = true, hasEntity = _showFilterGrid)
                    }
                    else -> {
                        setVisibility(hasAlertList = true, hasEntity = true)
                    }
                }

            }
        })
        viewModel.alerts.observe(viewLifecycleOwner, Observer { alerts ->
            alerts?.let {

                Log.i(TAG,"show alerts list : $_showFilterGrid")
                if (alerts.isEmpty()) {
                    setVisibility(hasAlert = true,hasEntity = _showFilterGrid)
                } else {
                    setVisibility(hasAlertList = true,hasEntity = true)
                }
                alertListAdapter.update(alerts)
            }
        })
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            Log.i(TAG,"show grid : $_showFilterGrid")
            setVisibility(hasEntity = _showFilterGrid,hasProgress = true)
//            if(!_showFilterGrid) setVisibility(hasProgress = isLoading)
//            else setVisibility(hasAlert = true,hasProgress = isLoading)

        })
        viewModel.hasError.observe(viewLifecycleOwner, Observer { hasError ->
            Log.i(TAG,"show error : $_showFilterGrid")
            setVisibility(hasError = hasError)
        })
    }

    private fun setVisibility(
        hasAlert: Boolean = false,
        hasError: Boolean = false,
        hasProgress: Boolean = false,
        hasAlertList: Boolean = false,
        hasEntity: Boolean = false
    ) {
        if (hasAlert) noAlerts.visibility = View.VISIBLE else noAlerts.visibility = View.GONE
        if (hasAlertList) alertListRecyclerView.visibility = View.VISIBLE else alertListRecyclerView.visibility = View.GONE
        if (hasError) errorTextView.visibility = View.VISIBLE else errorTextView.visibility = View.GONE
        if (hasProgress) progressBarList.visibility = View.VISIBLE else progressBarList.visibility = View.GONE
        if (hasEntity) filterGridRecyclerView.visibility = View.VISIBLE else filterGridRecyclerView.visibility = View.GONE

        Log.i(
            TAG,
            "noAlerts:${noAlerts.visibility}, alerts:${alertListRecyclerView.visibility}, hasError:${errorTextView.visibility}," +
                    "progress:${progressBarList.visibility}, filter:${filterGridRecyclerView.visibility}"
        )

    }

    override fun radioButtonClickListener(position: Int, name : String) {
        if(ENTITY_FILTER_SELECTED_POSITION != position) {
            Log.i(TAG,"Radio button clicked")
            entityFilterAdapter.updatePosition(position)
            ENTITY_FILTER_SELECTED_POSITION = position
            _entity = if(name == "All") "%%" else name
            _showFilterGrid = true
            viewModel.refresh(_alertSection, _alertLevel, _entity)
        }
    }

}

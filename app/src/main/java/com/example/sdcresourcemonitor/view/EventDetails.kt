package com.example.sdcresourcemonitor.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.sdcresourcemonitor.R
import com.example.sdcresourcemonitor.model.Event
import kotlinx.android.synthetic.main.event_history.view.*
import kotlinx.android.synthetic.main.event_history.view.hostname
import kotlinx.android.synthetic.main.fragment_event_details.view.*


private const val KEY_HOSTNAME = "KEY_HOSTNAME"
private const val KEY_APPNAME = "KEY_APPNAME"
private const val KEY_STARTTIME = "KEY_STARTTIME"
private const val KEY_ENDTIME = "KEY_ENDTIME"
private const val KEY_MESSAGE = "KEY_MESSAGE"

/**
 * A simple [Fragment] subclass.
 * Use the [EventDetails.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventDetails : DialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
        setupClickListeners(view)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupView(view: View) {
        view.hostnameValue.text = arguments?.getString(KEY_HOSTNAME)
        view.appNameValue.text = arguments?.getString(KEY_APPNAME)
        view.messageValue.text = arguments?.getString(KEY_MESSAGE)
        view.startTimeValue.text = arguments?.getString(KEY_STARTTIME)
        view.endTimeValue.text = arguments?.getString(KEY_ENDTIME)
    }

    private fun setupClickListeners(view: View) {
//        view.btnPositive.setOnClickListener {
//            // TODO: Do some task here
//            dismiss()
//        }
//        view.btnNegative.setOnClickListener {
//            // TODO: Do some task here
//            dismiss()
//        }
    }

    companion object {

        @JvmStatic
        fun newInstance(event : Event) =
            EventDetails().apply {
                arguments = Bundle().apply {
                    putString(KEY_HOSTNAME, event.hostname)
                    putString(KEY_APPNAME, event.appName)
                    putString(KEY_MESSAGE, event.infoMessage)
                    putString(KEY_STARTTIME, event.startTime)
                    putString(KEY_ENDTIME, event.endTime)
                }
            }
    }
}
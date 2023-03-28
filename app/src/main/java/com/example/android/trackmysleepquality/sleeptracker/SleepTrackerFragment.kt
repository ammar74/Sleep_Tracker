

package com.example.android.trackmysleepquality.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar

class SleepTrackerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        // هنا بربط ال viewModel ب Database ب ViewModelFactory
        val application= requireNotNull(this.activity).application
        val dataSource=SleepDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory=SleepTrackerViewModelFactory(dataSource,application)

        val sleepTrackerViewModel=ViewModelProviders.of(this,viewModelFactory).
        get(SleepTrackerViewModel::class.java)

        val adapter=SleepNightAdapter(SleepNightListener {
            nightId -> sleepTrackerViewModel.onSleepNightClicked(nightId)
        })
        binding.sleepListRV.adapter=adapter

        sleepTrackerViewModel.nights.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        sleepTrackerViewModel.navigateToSleepDataQuality.observe(viewLifecycleOwner, Observer { night->
            night?.let {
                this.findNavController().navigate(
                    SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepDetailFragment(night))
                sleepTrackerViewModel.onSleepNightNavigated()
            }
        })

        val manager =GridLayoutManager(activity,3)
        binding.sleepListRV.layoutManager = manager


        //وهنا بربط ال ViewModel بال User Interface وبخليه شغال كـ observer
        binding.sleepTrackerViewModel=sleepTrackerViewModel
        binding.setLifecycleOwner(this)

        sleepTrackerViewModel.navigateToSleepQuality.observe(viewLifecycleOwner, Observer {
                night ->
            night ?.let {
                this.findNavController().navigate(SleepTrackerFragmentDirections.
                actionSleepTrackerFragmentToSleepQualityFragment(night.nightId))
                sleepTrackerViewModel.doneNavigating()
            }
        })

        sleepTrackerViewModel.showSnackBarEvent.observe(viewLifecycleOwner, Observer {
            if (it == true){
                Snackbar.make(
                    activity!!.findViewById(android.R.id.content),getString(R.string.cleared_message),
                Snackbar.LENGTH_SHORT).show()
                sleepTrackerViewModel.doneShowingSnackBar()
            }
        })

        return binding.root
    }
}

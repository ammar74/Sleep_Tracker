/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.provider.SyncStateContract.Helpers.insert
import android.provider.SyncStateContract.Helpers.update
import android.text.method.TextKeyListener.clear
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

        private var viewModelJob = Job()

        override fun onCleared() {
                super.onCleared()
                viewModelJob.cancel()
        }
        private val uiScope= CoroutineScope(Dispatchers.Main+ viewModelJob)
        private val tonight =MutableLiveData<SleepNight?>()
         val nights= database.getAllNights()

        val nightString=Transformations.map(nights){nights->
                formatNights(nights,application.resources)
        }
        // start , stop and clear Buttons Visibility conditions
        //whenever Tonight changes the variables update
        // هنا بنمشي بالعكس يعني كاننا بنقول لما it = null  خلي ال start BTN هو اللي Visible

        //tonight is null at beginning so we want to make start Btn visible
        val startButtonVisible= Transformations.map(tonight){
                null ==it
        }
        //if tonight at beginning has a value so we will make stop Btn visible
        val stopButtonVisible=Transformations.map(tonight){
                null != it
        }
//هنا بقوله خلي ال clear Btn  يكون visible  طول ما ال nights مش فاضية
        val clearButtonVisible=Transformations.map(nights){
                it?.isNotEmpty()
        }

        private var _showSnackBarEvent=MutableLiveData<Boolean>()
        val showSnackBarEvent :LiveData<Boolean>
        get() = _showSnackBarEvent

        fun doneShowingSnackBar(){
                _showSnackBarEvent.value= false
        }

        private val _navigateToSleepQuality= MutableLiveData<SleepNight>()
        val navigateToSleepQuality : LiveData<SleepNight>
        get() = _navigateToSleepQuality

        fun doneNavigating(){
                _navigateToSleepQuality.value= null
        }

        init {
            initializeTonight()
        }

        private fun initializeTonight(){
                uiScope.launch { tonight.value=getTonightFromDatabase() }
        }

        private suspend fun getTonightFromDatabase(): SleepNight? {
                return withContext(Dispatchers.IO){
                        var night=database.getTonight()
                        if (night?.endTimeMilli != night?.startTimeMilli){
                                night=null
                        }
                        night
                }
        }

        fun onStartTracking(){
                uiScope.launch {
                        val newNight= SleepNight()
                        insert(newNight)
                        tonight.value=getTonightFromDatabase()
                }
        }

        private suspend fun insert(night: SleepNight){
                withContext(Dispatchers.IO){
                        database.insert(night)
                }
        }

        fun onStopTracking(){
                uiScope.launch {
                        val oldNight= tonight.value ?: return@launch  //
                        oldNight.endTimeMilli=System.currentTimeMillis()
                        update(oldNight)
                        _navigateToSleepQuality.value=oldNight
                }
        }
        private suspend fun update(night: SleepNight){
                withContext(Dispatchers.IO){
                        database.update(night)
                }
        }
        fun onClear(){
                uiScope.launch {
                      clear()
                      tonight.value= null
                }
        }
        private suspend fun clear(){
                withContext(Dispatchers.IO){
                        database.clear()
                }
        }

        private val _navigateToSleepDataQuality= MutableLiveData<Long>()
        val navigateToSleepDataQuality :LiveData<Long>
        get() = _navigateToSleepDataQuality


        fun onSleepNightClicked(id: Long) {
                _navigateToSleepDataQuality.value=id
        }

        fun onSleepNightNavigated() {
                _navigateToSleepDataQuality.value=null
        }

}


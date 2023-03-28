
package com.example.android.trackmysleepquality.sleepquality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import kotlinx.coroutines.*

class SleepQualityViewModel (
    private val sleepNightKey :Long =0L,
    val database: SleepDatabaseDao) : ViewModel(){

        private val viewModelJob= Job()
        private val uiScope= CoroutineScope(Dispatchers.Main+viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val _navigateToSleepTracker =MutableLiveData<Boolean?>()
    val navigateToSleepTracker : LiveData<Boolean?>
    get() = _navigateToSleepTracker


    fun doneNavigation(){
        _navigateToSleepTracker.value=null
    }
//  هنا بستخدم ال coroutines ببعتله ال context من خلال ال ui scope جوة ال dispatcher
//  وبعدين بحفظ الquality جوة ال tonight وبعدها بعمل update لل database

    fun onSetSleepQuality(quality :Int){
        uiScope.launch {
            withContext(Dispatchers.IO){
                val tonight = database.get(sleepNightKey) ?: return@withContext
                tonight.sleepQuality=quality
                database.update(tonight)
            }
            _navigateToSleepTracker.value=true
        }
    }
}


package com.dicoding.asclepius.view.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.local.AsclepiusDatabase
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import kotlinx.coroutines.launch

class HistoryViewModel(app: Application): AndroidViewModel(app) {
    private val db = AsclepiusDatabase.getInstance(app.applicationContext)
    private val dao = db.historyDao()

    val data = dao.getAll().asLiveData()

    fun deleteItem(historyEntity: HistoryEntity) {
        viewModelScope.launch {
            dao.delete(historyEntity)
        }
    }

}
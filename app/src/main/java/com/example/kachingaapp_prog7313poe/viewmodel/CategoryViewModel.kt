package com.example.kachingaapp_prog7313poe.viewmodel


import com.example.kachingaapp_prog7313poe.data.KachingaDatabase
import com.example.kachingaapp_prog7313poe.data.SessionManager
import com.example.kachingaapp_prog7313poe.data.entity.Category
import com.example.kachingaapp_prog7313poe.data.repository.CategoryRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = CategoryRepository(
        KachingaDatabase.getDatabase(application).categoryDao()
    )
    private val session = SessionManager(application)

    private val userId: StateFlow<Int> = session.userId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), -1)

    //Default categories and the user's own categories are shown
    val allCategories: StateFlow<List<Category>> = userId
        .flatMapLatest { id ->
            if (id >= 0) repo.getAll(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenseCategories: StateFlow<List<Category>> = userId
        .flatMapLatest { id ->
            if (id >= 0) repo.getExpenseCategories(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incomeCategories: StateFlow<List<Category>> = userId
        .flatMapLatest { id ->
            if (id >= 0) repo.getIncomeCategories(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())





}
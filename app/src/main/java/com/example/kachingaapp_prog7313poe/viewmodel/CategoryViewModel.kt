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

    //StateFlow type is now String, defaulting to "" instead of -1
    private val userId: StateFlow<String> = session.userId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    //Default categories and the user's own categories are shown
    //Checks if the alphanumeric Firebase UID string is not blank
    val allCategories: StateFlow<List<Category>> = userId
        .flatMapLatest { id ->
            if (id.isNotBlank()) repo.getAll(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenseCategories: StateFlow<List<Category>> = userId
        .flatMapLatest { id ->
            if (id.isNotBlank()) repo.getExpenseCategories(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incomeCategories: StateFlow<List<Category>> = userId
        .flatMapLatest { id ->
            if (id.isNotBlank()) repo.getIncomeCategories(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    fun addCategory(name: String, icon: String, isExpense: Boolean) {
        viewModelScope.launch {
            val currentUserId = session.userId.first()
            // he Category object now receives a String currentUserId (Firebase UID)
            repo.insert(
                Category(
                    userId = currentUserId,
                    name = name,
                    icon = icon,
                    isExpense = isExpense
                )
            ).fold(
                onSuccess = {
                    _uiState.update { it.copy(successMessage = "Category added!") }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(error = e.message ?: "Failed to add category")
                    }
                }
            )
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch { repo.delete(category) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}

data class CategoryUiState(
    val error: String? = null,
    val successMessage: String? = null
)
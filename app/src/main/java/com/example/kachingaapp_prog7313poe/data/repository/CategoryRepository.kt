package com.example.kachingaapp_prog7313poe.data.repository

import com.example.kachingaapp_prog7313poe.data.dao.CategoryDao
import com.example.kachingaapp_prog7313poe.data.entity.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val dao: CategoryDao) {

    //Functions for Categories
    // UserId now uses string for firebase integration
    fun getAll(userId: String): Flow<List<Category>> = dao.getAllCategories(userId)

    fun getExpenseCategories(userId: String): Flow<List<Category>> =
        dao.getCategoriesByType(userId, true)

    fun getIncomeCategories(userId: String): Flow<List<Category>> =
        dao.getCategoriesByType(userId, false)

    //Insert Function
    suspend fun insert(category: Category): Result<Unit> {
        return try {
            if (category.name.isBlank())
                return Result.failure(Exception("Category name cannot be empty"))
            dao.insertCategory(category)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    //Delete Function
    suspend fun delete(category: Category) = dao.deleteCategory(category)

    //Update Function
    suspend fun update(category: Category) = dao.updateCategory(category)

}
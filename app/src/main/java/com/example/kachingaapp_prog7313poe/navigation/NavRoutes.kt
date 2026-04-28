package com.example.prog7313_poe_kachinga.navigation

object NavRoutes {
    const val SPLASH = "splash"
    const val LAUNCH = "launch"
    const val LOGIN = "login"
    const val CREATE_ACCOUNT = "create_account"
    const val HOME = "home"
    const val TRANSACTIONS = "transactions"
    const val ADD_TRANSACTION = "add_transaction"
    const val CATEGORIES = "categories"
    const val ADD_CATEGORY = "add_category"
    const val SAVINGS = "savings"
    const val SAVINGS_DETAIL = "savings_detail/{goalId}"
    const val ADD_SAVINGS_GOAL = "add_savings_goal"
    const val CALENDAR = "calendar"
    const val ACHIEVEMENTS = "achievements"
    const val PROFILE = "profile"
    const val CATEGORY_REPORT = "category_report"

    fun savingsDetail(goalId: Int) = "savings_detail/$goalId"
}
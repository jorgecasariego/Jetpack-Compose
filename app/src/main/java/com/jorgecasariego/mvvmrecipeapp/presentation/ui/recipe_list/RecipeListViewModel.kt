package com.jorgecasariego.mvvmrecipeapp.presentation.ui.recipe_list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel

class RecipeListViewModel
@ViewModelInject
constructor(
    private val algunSringPorAhi: String
): ViewModel() {

    init {
        println("ViewModel: $algunSringPorAhi")
    }

}
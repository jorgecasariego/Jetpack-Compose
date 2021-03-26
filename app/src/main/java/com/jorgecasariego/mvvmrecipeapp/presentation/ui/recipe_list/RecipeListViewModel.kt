package com.jorgecasariego.mvvmrecipeapp.presentation.ui.recipe_list

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jorgecasariego.mvvmrecipeapp.domain.model.Recipe
import com.jorgecasariego.mvvmrecipeapp.presentation.ui.recipe_list.RecipeListEvent.*
import com.jorgecasariego.mvvmrecipeapp.repository.RecipeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Named

const val PAGE_SIZE = 30

class RecipeListViewModel
@ViewModelInject
constructor(
    private val repository: RecipeRepository,
    private @Named("auth_token") val token: String
) : ViewModel() {

    val recipes: MutableState<List<Recipe>> = mutableStateOf(ArrayList())
    val query = mutableStateOf("")
    val selectedCategory: MutableState<FoodCategory?> = mutableStateOf(null)
    var categoryScrollPosition: Float = 0F
    val loading = mutableStateOf(false)
    val page = mutableStateOf(1)
    var recipeListScrollPosition = 0

    init {
        onTriggerEvent(NewSearchEvent)
    }

    fun onTriggerEvent(event: RecipeListEvent) {
        viewModelScope.launch {
            try {
                when (event) {
                    is NewSearchEvent -> {
                        newSearch()
                    }

                    is NextPageEvent -> {
                        nextPage()
                    }
                }

            } catch (e: Exception) {
                Log.d("TEST", "onTriggerEvent: Exception: $e, ${e.cause}")
            }
        }
    }

    // Use case #1
    private suspend fun newSearch() {
        loading.value = true

        resetSearchState()

        delay(1000)

        val result = repository.search(
            token = token,
            page = 1,
            query = query.value
        )

        recipes.value = result

        loading.value = false

    }

    // Use case #2
    private suspend fun nextPage() {
        // prevent duplicate events due to recompose happening to quickly
        if ((recipeListScrollPosition + 1) >= (page.value * PAGE_SIZE)) {
            loading.value = true
            incrementPage()
            //Log.d("TEST", "nextPage: triggered: ${page.value}")

            //jus to show pagination, api is fast
            delay(1000)

            if (page.value > 1) {
                val result = repository.search(
                    token = token,
                    page = page.value,
                    query = query.value
                )
                //Log.d("TEST", "nextPage: $result")
                appendRecipes(result)
            }

            loading.value = false
        }

    }

    /**
     * Append new recipes to the current list of recipes
     */
    private fun appendRecipes(recipe: List<Recipe>) {
        val current = ArrayList(this.recipes.value)
        current.addAll(recipe)
        this.recipes.value = current
    }

    private fun incrementPage() {
        page.value = page.value + 1
    }

    fun onChangeScrollPostion(position: Int) {
        recipeListScrollPosition = position
    }

    fun onQueryChanged(query: String) {
        this.query.value = query
    }

    fun onSelectedCategoryChanged(category: String) {
        val newCategory = getFoodCategory(category)
        selectedCategory.value = newCategory
        onQueryChanged(category)
    }

    fun onChangeCategoryScrollPosition(position: Float) {
        categoryScrollPosition = position
    }

    private fun clearSelectedCategory() {
        selectedCategory.value = null
    }

    fun onChangeRecipeScrollPosition(position: Int) {
        Log.d("TEST", "onChangeRecipeScrollPosition: $position")
        recipeListScrollPosition = position
    }

    private fun resetSearchState() {
        recipes.value = listOf()
        page.value = 1
        onChangeRecipeScrollPosition(0)
        if (selectedCategory.value?.value != query.value) {
            clearSelectedCategory()
        }
    }

}
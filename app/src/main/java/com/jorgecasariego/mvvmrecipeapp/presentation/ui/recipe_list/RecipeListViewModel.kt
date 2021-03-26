package com.jorgecasariego.mvvmrecipeapp.presentation.ui.recipe_list

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.jorgecasariego.mvvmrecipeapp.domain.model.Recipe
import com.jorgecasariego.mvvmrecipeapp.presentation.ui.recipe_list.RecipeListEvent.*
import com.jorgecasariego.mvvmrecipeapp.repository.RecipeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Named

const val PAGE_SIZE = 30

const val STATE_KEY_PAGE = "recipe.state.page.key"
const val STATE_KEY_QUERY = "recipe.state.query.key"
const val STATE_KEY_LIST_POSITION = "recipe.state.query.list_position"
const val STATE_KEY_SELECTED_CATEGORY = "recipe.state.query.selected_category"

class RecipeListViewModel
@ViewModelInject
constructor(
    private val repository: RecipeRepository,
    private @Named("auth_token") val token: String,
    @Assisted private val saveStateHandle: SavedStateHandle
) : ViewModel() {

    val recipes: MutableState<List<Recipe>> = mutableStateOf(ArrayList())
    val query = mutableStateOf("")
    val selectedCategory: MutableState<FoodCategory?> = mutableStateOf(null)
    var categoryScrollPosition: Float = 0F
    val loading = mutableStateOf(false)
    val page = mutableStateOf(1)
    var recipeListScrollPosition = 0

    init {
        saveStateHandle.get<Int>(STATE_KEY_PAGE)?.let { p ->
            setPage(page = p)
        }

        saveStateHandle.get<String>(STATE_KEY_QUERY)?.let { q ->
            setQuery(query = q)
        }

        saveStateHandle.get<Int>(STATE_KEY_LIST_POSITION)?.let { p ->
            setListScrollPosition(position = p)
        }

        saveStateHandle.get<FoodCategory>(STATE_KEY_SELECTED_CATEGORY)?.let { c ->
            setSelectedCategory(category = c)
        }

        if (recipeListScrollPosition != 0) {
            onTriggerEvent(RestoreStateEvent)
        } else {
            onTriggerEvent(NewSearchEvent)
        }

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

                    is RestoreStateEvent -> {
                        restoreState()
                    }
                }

            } catch (e: Exception) {
                Log.d("TEST", "onTriggerEvent: Exception: $e, ${e.cause}")
            }
        }
    }

    private suspend fun restoreState() {
        loading.value = true
        val results: MutableList<Recipe> = mutableListOf()

        // this is not for production. We can iterate in production.
        // Caching is the solution
        for (p in 1..page.value) {
            val result = repository.search(
                token = token,
                page = p,
                query = query.value
            )

            results.addAll(result)

            if (p == page.value) {
                recipes.value = results
                loading.value = false
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
        setPage(page = page.value + 1)
    }

    fun onQueryChanged(query: String) {
        setQuery(query = query)
    }

    fun onSelectedCategoryChanged(category: String) {
        val newCategory = getFoodCategory (category)
        setSelectedCategory(category = newCategory)
        onQueryChanged(category)
    }

    fun onChangeCategoryScrollPosition(position: Float) {
        categoryScrollPosition = position
    }

    private fun clearSelectedCategory() {
        setSelectedCategory(category = null)
    }

    fun onChangeRecipeScrollPosition(position: Int) {
        setListScrollPosition(position = position)
    }

    private fun resetSearchState() {
        recipes.value = listOf()
        page.value = 1
        onChangeRecipeScrollPosition(0)
        if (selectedCategory.value?.value != query.value) {
            clearSelectedCategory()
        }
    }
    
    private fun setListScrollPosition(position: Int) {
        recipeListScrollPosition = position
        saveStateHandle.set(STATE_KEY_LIST_POSITION, position)
    }

    private fun setPage(page: Int) {
        this.page.value = page
        saveStateHandle.set(STATE_KEY_PAGE, page)
    }

    private fun setSelectedCategory(category: FoodCategory?) {
        selectedCategory.value = category
        saveStateHandle.set(STATE_KEY_SELECTED_CATEGORY, category)
    }

    private fun setQuery(query: String) {
        this.query.value = query
        saveStateHandle.set(STATE_KEY_QUERY, query)
    }
}
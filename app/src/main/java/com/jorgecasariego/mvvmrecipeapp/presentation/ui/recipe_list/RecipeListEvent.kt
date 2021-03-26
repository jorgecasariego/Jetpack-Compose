package com.jorgecasariego.mvvmrecipeapp.presentation.ui.recipe_list

/**
 * This is like a Enum with extra properties.
 */
sealed class RecipeListEvent {
    /**
     * We're not passing any parameters to the event. That's the reason we're doing as objects
     */
    object NewSearchEvent: RecipeListEvent()

    object NextPageEvent: RecipeListEvent()
}

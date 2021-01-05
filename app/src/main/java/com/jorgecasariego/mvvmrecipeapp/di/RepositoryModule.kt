package com.jorgecasariego.mvvmrecipeapp.di

import com.jorgecasariego.mvvmrecipeapp.network.RecipeService
import com.jorgecasariego.mvvmrecipeapp.network.model.RecipeDtoMapper
import com.jorgecasariego.mvvmrecipeapp.repository.RecipeRepository
import com.jorgecasariego.mvvmrecipeapp.repository.RecipeRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRecipeRepository(
        recipeService: RecipeService,
        recipeDtoMapper: RecipeDtoMapper
    ): RecipeRepository {
        return RecipeRepositoryImpl(recipeService, recipeDtoMapper)
    }
}
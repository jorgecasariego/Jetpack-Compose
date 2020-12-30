package com.jorgecasariego.mvvmrecipeapp.network.responses

import com.google.gson.annotations.SerializedName
import com.jorgecasariego.mvvmrecipeapp.network.model.RecipeDto

data class RecipeSearchResponse (
    @SerializedName("count")
    val count: Int,

    @SerializedName("results")
    val recipes: List<RecipeDto>
)
package com.jorgecasariego.mvvmrecipeapp.presentation.ui.recipe_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.jorgecasariego.mvvmrecipeapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeListFragment : Fragment() {

    val viewModel: RecipeListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {

                val recipes = viewModel.recipes.value
                for(recipe in recipes) {
                    Log.d("TEST", "onCreateView: ${recipe.title}")
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Recipe List",
                        style = TextStyle(
                            fontSize = TextUnit.Companion.Sp(21)
                        )
                    )
                    Spacer(modifier = Modifier.padding(10.dp))
                    Button(onClick = {
                        findNavController().navigate(R.id.viewRecipe)
                    }) {
                        Text(text = "To Recipe Fragment")
                    }
                }
            }
        }
    }
}
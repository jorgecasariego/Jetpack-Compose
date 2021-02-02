package com.jorgecasariego.mvvmrecipeapp.presentation.ui.recipe_list

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.jorgecasariego.mvvmrecipeapp.R
import com.jorgecasariego.mvvmrecipeapp.presentation.BaseApplication
import com.jorgecasariego.mvvmrecipeapp.presentation.components.*
import com.jorgecasariego.mvvmrecipeapp.presentation.components.HeartAnimationDefinition.HeartButtonState.*
import com.jorgecasariego.mvvmrecipeapp.presentation.components.util.SnackbarController
import com.jorgecasariego.mvvmrecipeapp.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RecipeListFragment : Fragment() {

    @Inject
    lateinit var application: BaseApplication

    @ExperimentalMaterialApi
    private val snackbarController = SnackbarController(lifecycleScope )

    val viewModel: RecipeListViewModel by viewModels()

    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {

                AppTheme(darkTheme = application.isDark.value) {
                    val recipes = viewModel.recipes.value

                    // Remember
                    val query = viewModel.query.value

                    val selectedCategory = viewModel.selectedCategory.value

                    val loading = viewModel.loading.value

                    val scaffoldState = rememberScaffoldState()
                    Scaffold(
                        topBar = {
                            SearchAppBar(
                                query = query,
                                onQueryChanged = viewModel::onQueryChanged,
                                onExecuteSearch = {
                                    if (viewModel.selectedCategory.value?.value == "Milk") {
                                        snackbarController.getScope().launch {
                                            snackbarController.showSnackbar(
                                                scaffoldState = scaffoldState,
                                                message = "Invalid category: Milk!",
                                                actionLabel = "Hide"
                                            )
                                        }
                                    } else {
                                        viewModel.newSearch()
                                    }
                                },
                                scrollPosition = viewModel.categoryScrollPosition,
                                selectedCategory = selectedCategory,
                                onSelectedCategoryChanged = viewModel::onSelectedCategoryChanged,
                                onChangeCategoryScrollPosition = viewModel::onChangeCategoryScrollPosition,
                                onToggleTheme = {
                                    application.toggleLightTheme()
                                }
                            )
                        },
                        /*bottomBar = {
                            MyBottomBar()
                        },
                        drawerContent = {
                            MyDrawer()
                        },*/
                        scaffoldState = scaffoldState,
                        snackbarHost = {
                            scaffoldState.snackbarHostState
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = MaterialTheme.colors.surface)
                        ) {
                            if (loading) {
                                LoadingRecipeListShimmer(imageHeight = 250.dp)
                            } else {
                                LazyColumn {
                                    itemsIndexed(
                                        items = recipes
                                    ) { index, recipe ->
                                        RecipeCard(recipe = recipe, onClick = {})
                                    }
                                }
                            }
                            CircularIndeterminateProgressBar(
                                isDisplayed = loading,
                                verticalBias = 0.3f
                            )

                            DefaultSnackbar(
                                snackbarHostState = scaffoldState.snackbarHostState,
                                onDismiss = { scaffoldState.snackbarHostState.currentSnackbarData?.dismiss() },
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }

                    /*Column {

                        //PulsingDemo()

                        /*Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(top = 20.dp),
                            horizontalArrangement = Arrangement.Center) {

                            // It is better to do this state in the ViewModel
                            val state = remember { mutableStateOf(IDLE)}
                            AnimationHeartButton(
                                modifier = Modifier,
                                buttonState = state,
                                onToggle = {
                                    state.value = if (state.value == IDLE) ACTIVE else IDLE
                                })
                        }*/
                    }*/
                }
            }
        }
    }
}

@Composable
fun GradientDemo() {
    val colors = listOf(
        Color.LightGray,
        Color.White,
        Color.LightGray,
    )

    val brush = linearGradient(
        colors,
        start = Offset(200f, 200f),
        end = Offset(400f, 400f)
    )
    Surface(shape = MaterialTheme.shapes.small) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = brush)
        )
    }

}

@Composable
fun MyBottomBar() {
    BottomNavigation(
        elevation = 12.dp
    ) {
        BottomNavigationItem(
            icon = { Icon(Icons.Default.BrokenImage) },
            selected = true,
            onClick = { })

        BottomNavigationItem(
            icon = { Icon(Icons.Default.AccountBalanceWallet) },
            selected = false,
            onClick = { })

        BottomNavigationItem(
            icon = { Icon(Icons.Default.Search) },
            selected = false,
            onClick = { })
    }
}

@Composable
fun MyDrawer() {
    Column() {
        Text(text = "Item 1")
        Text(text = "Item 2")
        Text(text = "Item 3")
        Text(text = "Item 4")
        Text(text = "Item 5")

    }
}

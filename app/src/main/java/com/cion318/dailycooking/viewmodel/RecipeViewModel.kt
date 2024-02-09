package com.cion318.dailycooking.viewmodel

import androidx.lifecycle.ViewModel
import com.cion318.dailycooking.data.Recipe
import com.cion318.dailycooking.data.RecipeDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeViewModel(private val recipeDao: RecipeDao) : ViewModel() {

    suspend fun insertRecipe(recipe: Recipe) {
        withContext(Dispatchers.IO) {
            recipeDao.insert(recipe)
        }
    }

    suspend fun updateRecipe(recipe: Recipe) {
        withContext(Dispatchers.IO) {
            recipeDao.update(recipe)
        }
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        withContext(Dispatchers.IO) {
            recipeDao.delete(recipe)
        }
    }

    suspend fun getRecipesByNameAsc(): List<Recipe> {
        return withContext(Dispatchers.IO) {
            recipeDao.getRecipesByNameAsc()
        }
    }

    suspend fun getRecipesByNameDesc(): List<Recipe> {
        return withContext(Dispatchers.IO) {
            recipeDao.getRecipesByNameDesc()
        }
    }

    suspend fun getRecipesByCategoryAsc(category: String): List<Recipe> {
        return withContext(Dispatchers.IO) {
            recipeDao.getRecipesByNameAsc().filter { category in it.categories }
        }
    }

    suspend fun getRecipesByCategoryDesc(category: String): List<Recipe> {
        return withContext(Dispatchers.IO) {
            recipeDao.getRecipesByNameDesc().filter { category in it.categories }
        }
    }

    suspend fun getAllUniqueCategories(): List<String> {
        return withContext(Dispatchers.IO) {
            val allRecipes = recipeDao.getAllRecipes()

            // Use a HashSet to store unique categories
            val uniqueCategoriesSet = HashSet<String>()

            // Iterate through each recipe and add its tags to the set
            for (recipe in allRecipes) {
                uniqueCategoriesSet.addAll(recipe.categories)
            }

            // Convert the set back to a list
            val uniqueCategoriesList = uniqueCategoriesSet.toList().sortedBy{it.lowercase()}

            uniqueCategoriesList
        }
    }
}
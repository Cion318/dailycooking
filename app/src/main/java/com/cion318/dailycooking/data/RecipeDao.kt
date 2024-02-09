package com.cion318.dailycooking.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecipeDao {
    @Insert
    suspend fun insert(recipe: Recipe)

    @Update
    suspend fun update(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)

    @Query("SELECT * FROM recipe")
    suspend fun getAllRecipes(): List<Recipe>

    @Query("SELECT * FROM recipe WHERE id = :id")
    suspend fun getRecipeById(id: Long): Recipe

    @Query("SELECT * FROM recipe ORDER BY name COLLATE NOCASE ASC")
    suspend fun getRecipesByNameAsc(): List<Recipe>

    @Query("SELECT * FROM recipe ORDER BY name COLLATE NOCASE DESC")
    suspend fun getRecipesByNameDesc(): List<Recipe>
}
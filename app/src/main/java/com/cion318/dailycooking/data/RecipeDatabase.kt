package com.cion318.dailycooking.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

@Database(entities = [Recipe::class], version = 1, exportSchema = false)
@TypeConverters(RecipeTypeConverters::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    // Pre-population of the database using a json file
    companion object {
        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        fun getDatabase(context: Context): RecipeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecipeDatabase::class.java,
                    "recipe_database"
                )
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(private val context: Context) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Do operations when the database is created
            // For example, insert initial data if the database is empty
            CoroutineScope(Dispatchers.IO).launch {
                populateDatabase(INSTANCE!!.recipeDao(), context)
            }
        }

        private suspend fun populateDatabase(recipeDao: RecipeDao, context: Context) {
            if (recipeDao.getAllRecipes().isEmpty()) {
                // Read JSON file from assets
                val jsonString = context.assets.open("initial_recipes.json").bufferedReader().use {
                    it.readText()
                }

                // Parse JSON array
                val jsonArray = JSONArray(jsonString)

                // Insert recipes into the database
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val name = jsonObject.getString("name")
                    val categories = jsonObject.getJSONArray("categories")
                        .let { 0.until(it.length()).map { index -> it.getString(index) } }
                    val source = jsonObject.getString("source")
                    val cookTime = jsonObject.getString("cookTime")
                    val servings = jsonObject.getString("servings")
                    val calories = jsonObject.getString("calories")
                    val points = jsonObject.getString("points")
                    val ingredients = jsonObject.getJSONArray("ingredients")
                        .let { 0.until(it.length()).map { index -> it.getString(index) } }
                    val instructions = jsonObject.getJSONArray("instructions")
                        .let { 0.until(it.length()).map { index -> it.getString(index) } }
                    val image = jsonObject.getString("image")


                    val recipe = Recipe(
                        name = name,
                        categories = categories,
                        source = source,
                        cookTime = cookTime,
                        servings = servings,
                        calories = calories,
                        points = points,
                        ingredients = ingredients,
                        instructions = instructions,
                        image = image
                    )
                    recipeDao.insert(recipe)
                }
            }
        }
    }
}
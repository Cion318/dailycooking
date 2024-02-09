package com.cion318.dailycooking

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cion318.dailycooking.data.RecipeDatabase
import com.cion318.dailycooking.ui.HomeFragment
import com.cion318.dailycooking.ui.NewRecipeFragment
import com.cion318.dailycooking.ui.CookbookFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigationrail.NavigationRailView

class MainActivity : AppCompatActivity() {

    lateinit var database: RecipeDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = RecipeDatabase.getDatabase(this)

        // Set up the navigation
        setupNavigation()

        // Load the default fragment
        loadFragment(HomeFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun setupNavigation() {
        when (resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
            Configuration.SCREENLAYOUT_SIZE_SMALL -> {
                val navigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
                navigation.visibility = BottomNavigationView.VISIBLE

                navigation.setOnItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.navigation_home -> loadFragment(HomeFragment())
                        R.id.navigation_recipes -> loadFragment(CookbookFragment())
                        R.id.navigation_new_recipe -> loadFragment(NewRecipeFragment())
                    }
                    true
                }
            }

            Configuration.SCREENLAYOUT_SIZE_NORMAL -> {
                val navigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
                navigation.visibility = BottomNavigationView.VISIBLE
                navigation.setOnItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.navigation_home -> loadFragment(HomeFragment())
                        R.id.navigation_recipes -> loadFragment(CookbookFragment())
                        R.id.navigation_new_recipe -> loadFragment(NewRecipeFragment())
                    }
                    true
                }
            }

            Configuration.SCREENLAYOUT_SIZE_LARGE -> {
                val navigation = findViewById<NavigationRailView>(R.id.rail_navigation)
                navigation.visibility = BottomNavigationView.VISIBLE
                navigation.setOnItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.navigation_home -> loadFragment(HomeFragment())
                        R.id.navigation_recipes -> loadFragment(CookbookFragment())
                        R.id.navigation_new_recipe -> loadFragment(NewRecipeFragment())
                    }
                    true
                }
            }

            Configuration.SCREENLAYOUT_SIZE_XLARGE -> {
                val navigation = findViewById<NavigationRailView>(R.id.rail_navigation)
                navigation.visibility = BottomNavigationView.VISIBLE
                navigation.setOnItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.navigation_home -> loadFragment(HomeFragment())
                        R.id.navigation_recipes -> loadFragment(CookbookFragment())
                        R.id.navigation_new_recipe -> loadFragment(NewRecipeFragment())
                    }
                    true
                }
            }
        }
    }

}


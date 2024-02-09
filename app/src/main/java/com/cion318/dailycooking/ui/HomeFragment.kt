package com.cion318.dailycooking.ui

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.cion318.dailycooking.MainActivity
import com.cion318.dailycooking.R
import com.cion318.dailycooking.data.Recipe
import com.cion318.dailycooking.viewmodel.RecipeAdapter
import com.cion318.dailycooking.viewmodel.RecipeViewModel
import com.cion318.dailycooking.viewmodel.RecipeViewModelFactory
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var categoriesContainer: LinearLayout


    // Database ViewModel
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var recipeAdapter1: RecipeAdapter
    private lateinit var recipeAdapter2: RecipeAdapter
    private lateinit var recyclerViewGenerated: RecyclerView
    private lateinit var recyclerViewUserSelected: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        categoriesContainer = view.findViewById(R.id.categoriesContainer)


        // Initialize the RecyclerView and RecipeAdapter
        recyclerViewGenerated = view.findViewById(R.id.recyclerViewGenerated)
        recyclerViewUserSelected = view.findViewById(R.id.recyclerViewUserSelected)
        recipeAdapter1 = RecipeAdapter()
        recipeAdapter2 = RecipeAdapter()

        val layoutManager1 = FlexboxLayoutManager(context)
        layoutManager1.flexDirection = FlexDirection.ROW
        layoutManager1.justifyContent = JustifyContent.CENTER
        layoutManager1.alignItems = AlignItems.CENTER
        recyclerViewGenerated.layoutManager = layoutManager1
        recyclerViewGenerated.adapter = recipeAdapter1

        val layoutManager2 = FlexboxLayoutManager(context)
        layoutManager2.flexDirection = FlexDirection.ROW
        layoutManager2.justifyContent = JustifyContent.CENTER
        layoutManager2.alignItems = AlignItems.CENTER
        recyclerViewUserSelected.layoutManager = layoutManager2
        recyclerViewUserSelected.adapter = recipeAdapter2

        // Initialize the RecipeViewModel
        recipeViewModel = ViewModelProvider(
            this,
            RecipeViewModelFactory((requireActivity() as MainActivity).database.recipeDao())
        )[RecipeViewModel::class.java]


        handleGeneratedSuggestion()
        populateCategoriesContainer()

        return view
    }



    private fun openRecipeFragment(recipe: Recipe) {
        val recipeFragment = RecipeFragment()

        // Pass the recipe data to RecipeFragment
        val args = Bundle()
        args.putParcelable("recipe", recipe)
        recipeFragment.arguments = args

        // Show the RecipeFragment
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, recipeFragment)
            .addToBackStack(null)
            .commit()
    }



    private fun populateCategoriesContainer() {
        lifecycleScope.launch {
            val uniqueCategories = recipeViewModel.getAllUniqueCategories()

            for (category in uniqueCategories) {
                val newCategoryButton = createChip(category)
                categoriesContainer.addView(newCategoryButton)

                newCategoryButton.setOnClickListener {
                    handleCategoryButtonClick(category)
                }
            }
        }
    }



    private fun handleGeneratedSuggestion() {
        lifecycleScope.launch {
            // Perform a database query to find recipes with the selected category
            val recipesWitchClickedCategory = recipeViewModel.getRecipesByNameAsc()

            if (recipesWitchClickedCategory.isNotEmpty()) {
                // Select a random recipe from the list
                val randomIndex = (recipesWitchClickedCategory.indices).random()
                val selectedRecipe = recipesWitchClickedCategory[randomIndex]

                recipeAdapter1.submitList(listOf(selectedRecipe))
            }

        }
        recipeAdapter1.setOnItemClickListener { recipe ->
            openRecipeFragment(recipe)
        }
    }



    private fun handleCategoryButtonClick(category: String) {
        lifecycleScope.launch {
            // Perform a database query to find recipes with the selected category
            val recipesWitchClickedCategory = recipeViewModel.getRecipesByCategoryAsc(category)

            // Select a random recipe from the list
            val randomIndex = (recipesWitchClickedCategory.indices).random()
            val selectedRecipe = recipesWitchClickedCategory[randomIndex]

            recipeAdapter2.submitList(listOf(selectedRecipe))
        }
        recipeAdapter2.setOnItemClickListener { recipe ->
            openRecipeFragment(recipe)
        }
    }



    @Suppress("NAME_SHADOWING")
    private fun createChip(chipText: String): Chip {
        val chipView = Chip(requireContext())
        val chipDrawable = ChipDrawable.createFromAttributes(
            requireContext(),
            null,
            0,
            com.google.android.material.R.style.Widget_MaterialComponents_Chip_Filter
        )
        chipView.setChipDrawable(chipDrawable)

        chipView.text = chipText
        chipView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        chipView.setChipBackgroundColorResource(R.color.orange)
        chipView.setChipStrokeColorResource(R.color.orange)
        chipView.setChipStrokeWidthResource(R.dimen.chip_stroke_width)
        chipView.isCheckable = false

        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, dpToPx(6), 0)
            chipView.layoutParams = this
        }


        chipView.setOnClickListener {
            chipView.isChecked = chipView.isChecked
        }

        return chipView
    }



    private fun dpToPx(dp: Int): Int {
        val resources = requireContext().resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

}
package com.cion318.dailycooking.ui

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
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
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch


class CookbookFragment : Fragment() {

    private lateinit var switchButton: SwitchCompat
    private lateinit var ascendingFlavorText: TextView
    private lateinit var descendingFlavorText: TextView
    private lateinit var categoriesContainer: LinearLayout
    private lateinit var categoriesChipGroup: ChipGroup

    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cookbook, container, false)

        // Initialize views
        switchButton = view.findViewById(R.id.switchButton)
        categoriesContainer = view.findViewById(R.id.categoriesContainer)
        categoriesChipGroup = view.findViewById(R.id.categoriesChipGroup)
        ascendingFlavorText = view.findViewById(R.id.ascendingFlavorText)
        descendingFlavorText = view.findViewById(R.id.descendingFlavorText)

        // Initialize the RecyclerView and RecipeAdapter
        recyclerView = view.findViewById(R.id.recyclerView)
        recipeAdapter = RecipeAdapter()
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.CENTER
        layoutManager.alignItems = AlignItems.CENTER
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recipeAdapter

        // Initialize the RecipeViewModel
        recipeViewModel = ViewModelProvider(
            this,
            RecipeViewModelFactory((requireActivity() as MainActivity).database.recipeDao())
        )[RecipeViewModel::class.java]


        // Setup the SwitchButton and CategoriesChipGroup filter system
        setupFilterSystem()


        // Get all recipes and submit them to the RecipeAdapter
        lifecycleScope.launch {
            val recipes = recipeViewModel.getRecipesByNameAsc()
            recipeAdapter.submitList(recipes)
        }
        recipeAdapter.setOnItemClickListener { recipe ->
            openRecipeFragment(recipe)
        }


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




    private fun setupFilterSystem() {
        // Get all categories for the ChipGroup filter
        lifecycleScope.launch {
            val uniqueCategories = recipeViewModel.getAllUniqueCategories()

            for (category in uniqueCategories) {
                val categoryChip = createChip(category, categoriesContainer)
                categoriesChipGroup.addView(categoryChip)
            }

        }

        switchButton.setOnClickListener {
            if (switchButton.isChecked) {
                // Set color of active and inactive state
                ascendingFlavorText.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_gray_text))
                descendingFlavorText.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))

                filterDatabase()
            } else {
                // Set color of active and inactive state
                ascendingFlavorText.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                descendingFlavorText.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_gray_text))

                filterDatabase()
            }

        }
    }




    private fun filterDatabase() {
        disableFilterViews()

        val selectedChipId = categoriesChipGroup.checkedChipId
        val selectedChip = if (selectedChipId != View.NO_ID) {
            categoriesChipGroup.findViewById<Chip>(selectedChipId)
        } else {
            null
        }
        val selectedCategory = selectedChip?.text?.toString()

        // Category chip selected and SwitchButton selected on descending
        if (selectedCategory != null && switchButton.isChecked) {
            lifecycleScope.launch {
                val recipes = recipeViewModel.getRecipesByCategoryDesc(selectedCategory)
                recipeAdapter.submitList(recipes)

                enableFilterViews()
            }
        }
        // Category chip selected and SwitchButton selected on ascending
        else if (selectedCategory != null && !switchButton.isChecked) {
            lifecycleScope.launch {
                val recipes = recipeViewModel.getRecipesByCategoryAsc(selectedCategory)
                recipeAdapter.submitList(recipes)

                enableFilterViews()
            }
        }
        // Category chip NOT selected and SwitchButton selected on descending
        else if (selectedCategory == null && switchButton.isChecked) {
            lifecycleScope.launch {
                val recipes = recipeViewModel.getRecipesByNameDesc()
                recipeAdapter.submitList(recipes)

                enableFilterViews()
            }
        }
        // Category chip NOT selected and SwitchButton selected on ascending
        else if (selectedCategory == null && !switchButton.isChecked) {
            lifecycleScope.launch {
                val recipes = recipeViewModel.getRecipesByNameAsc()
                recipeAdapter.submitList(recipes)

                enableFilterViews()
            }
        }

        recipeAdapter.setOnItemClickListener { recipe ->
            openRecipeFragment(recipe)
        }
    }




    private fun disableFilterViews() {
        switchButton.isEnabled = false

        for (i in 0 until categoriesChipGroup.childCount) {
            val chip = categoriesChipGroup.getChildAt(i) as Chip
            chip.isEnabled = false
        }
    }




    private fun enableFilterViews() {
        switchButton.isEnabled = true

        for (i in 0 until categoriesChipGroup.childCount) {
            val chip = categoriesChipGroup.getChildAt(i) as Chip
            chip.isEnabled = true
        }
    }




    @Suppress("NAME_SHADOWING")
    private fun createChip(
        chipText: String,
        container: LinearLayout,
    ): Chip {
        val chipView = Chip(requireContext())
        val chipDrawable = ChipDrawable.createFromAttributes(
            requireContext(),
            null,
            0,
            com.google.android.material.R.style.Widget_MaterialComponents_Chip_Filter
        )
        chipView.setChipDrawable(chipDrawable)

        chipView.text = chipText
        chipView.setChipBackgroundColorResource(R.color.dark_gray)
        chipView.setChipStrokeColorResource(R.color.orange)
        chipView.setChipStrokeWidthResource(R.dimen.chip_stroke_width)
        chipView.isCheckedIconVisible = false

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

        chipView.setOnCheckedChangeListener { chipView, isChecked ->
            if (isChecked) {
                chipDrawable.setChipBackgroundColorResource(R.color.orange)
                chipView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

                // Filter Recipe data by category
                if (switchButton.isChecked) {
                    lifecycleScope.launch {
                        val recipes = recipeViewModel.getRecipesByCategoryDesc(chipView.text.toString())
                        recipeAdapter.submitList(recipes)
                    }
                } else {
                    lifecycleScope.launch {
                        val recipes = recipeViewModel.getRecipesByCategoryAsc(chipView.text.toString())
                        recipeAdapter.submitList(recipes)
                    }
                }

                recipeAdapter.setOnItemClickListener { recipe ->
                    openRecipeFragment(recipe)
                }

            } else {
                chipDrawable.setChipBackgroundColorResource(R.color.dark_gray)
                chipView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                // Filter Recipe data by category
                if (switchButton.isChecked) {
                    lifecycleScope.launch {
                        val recipes = recipeViewModel.getRecipesByNameDesc()
                        recipeAdapter.submitList(recipes)
                    }
                } else {
                    lifecycleScope.launch {
                        val recipes = recipeViewModel.getRecipesByNameAsc()
                        recipeAdapter.submitList(recipes)
                    }
                }
            }
        }

        return chipView
    }

    // *********************************************************************************************
    private fun dpToPx(dp: Int): Int {
        val resources = requireContext().resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
}
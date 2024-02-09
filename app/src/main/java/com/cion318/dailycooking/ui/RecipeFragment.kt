package com.cion318.dailycooking.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.compose.ui.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.cion318.dailycooking.R
import com.cion318.dailycooking.data.Recipe
import com.google.android.material.imageview.ShapeableImageView

class RecipeFragment : Fragment() {

    private lateinit var backButton: Button
    private lateinit var editButton: Button
    private lateinit var recipeImageImageView: ShapeableImageView
    private lateinit var titleTextView: TextView
    private lateinit var cookTimeTextView: TextView
    private lateinit var caloriesTextView: TextView
    private lateinit var pointsTextView: TextView
    private lateinit var switchButton: SwitchCompat
    private lateinit var ingredientsFlavorText: TextView
    private lateinit var instructionsFlavorText: TextView
    private lateinit var ingredientsTextView: TextView
    private lateinit var instructionsTextView: TextView

    private lateinit var helperView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recipe, container, false)

        // Initialize views
        backButton = view.findViewById(R.id.backButton)
        editButton = view.findViewById(R.id.editButton)
        recipeImageImageView = view.findViewById(R.id.recipeImageImageView)
        titleTextView = view.findViewById(R.id.titleTextView)
        cookTimeTextView = view.findViewById(R.id.cookTimeTextView)
        caloriesTextView = view.findViewById(R.id.caloriesTextView)
        pointsTextView = view.findViewById(R.id.pointsTextView)
        switchButton = view.findViewById(R.id.switchButton)
        ingredientsFlavorText = view.findViewById(R.id.ingredientsFlavorText)
        instructionsFlavorText = view.findViewById(R.id.instructionsFlavorText)
        ingredientsTextView = view.findViewById(R.id.ingredientsTextView)
        instructionsTextView = view.findViewById(R.id.instructionsTextView)

        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.constraintLayoutInner)
        helperView = view.findViewById(R.id.helperView)

        val recipeData = arguments?.getParcelable<Recipe>("recipe")

        // Set up click listeners
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        editButton.setOnClickListener {
            if (recipeData != null) {
                openEditRecipeFragment(recipeData)
            }
        }

        switchButton.setOnClickListener {
            if (switchButton.isChecked) {
                ingredientsFlavorText.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_gray_text))
                instructionsFlavorText.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                ingredientsTextView.visibility = View.INVISIBLE
                instructionsTextView.visibility = View.VISIBLE

                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintLayout)
                constraintSet.connect(R.id.helperView, ConstraintSet.TOP, R.id.instructionsTextView, ConstraintSet.BOTTOM)
                constraintSet.applyTo(constraintLayout)
            } else {
                ingredientsFlavorText.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                instructionsFlavorText.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_gray_text))
                ingredientsTextView.visibility = View.VISIBLE
                instructionsTextView.visibility = View.INVISIBLE

                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintLayout)
                constraintSet.connect(R.id.helperView, ConstraintSet.TOP, R.id.ingredientsTextView, ConstraintSet.BOTTOM)
                constraintSet.applyTo(constraintLayout)
            }
        }


        populateFields(recipeData)

        return view
    }

    private fun openEditRecipeFragment(recipe: Recipe) {
        val editRecipeFragment = EditRecipeFragment()

        // Pass the recipe data to RecipeFragment
        val args = Bundle()
        args.putParcelable("recipe", recipe)
        editRecipeFragment.arguments = args

        // Show the RecipeFragment
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, editRecipeFragment)
            .addToBackStack(null)
            .commit()

    }

    private fun populateFields(recipeData: Recipe?) {
        if (recipeData != null) {
            recipeImageImageView.setImageURI(recipeData.image.toUri())
            titleTextView.text = recipeData.name
            cookTimeTextView.text = recipeData.cookTime
            caloriesTextView.text = recipeData.calories
            pointsTextView.text = recipeData.points
            ingredientsTextView.text = recipeData.ingredients.joinToString("\n\u2022 ", "\u2022 ")
            instructionsTextView.text = recipeData.instructions.joinToString("\n\u2022 ", "\u2022 ")
        }
    }
}
@file:Suppress("DEPRECATION")

package com.cion318.dailycooking.ui

import android.Manifest
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.cion318.dailycooking.MainActivity
import com.cion318.dailycooking.R
import com.cion318.dailycooking.data.Recipe
import com.cion318.dailycooking.viewmodel.RecipeViewModel
import com.cion318.dailycooking.viewmodel.RecipeViewModelFactory
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


private const val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 87123

class EditRecipeFragment : DialogFragment() {

    private lateinit var backButton: Button
    private lateinit var deleteButton: Button
    private lateinit var nameEditTextLayout: TextInputLayout
    private lateinit var nameEditText: EditText
    private lateinit var categoryEditTextLayout: TextInputLayout
    private lateinit var categoryEditText: EditText
    private lateinit var addCategoryButton: Button
    private lateinit var categoriesContainer: FlexboxLayout
    private lateinit var sourceEditText: EditText
    private lateinit var cookTimeEditText: EditText
    private lateinit var servingsEditText: EditText
    private lateinit var caloriesEditText: EditText
    private lateinit var pointsEditText: EditText
    private lateinit var ingredientEditText: EditText
    private lateinit var addIngredientButton: Button
    private lateinit var ingredientsContainer: FlexboxLayout
    private lateinit var instructionsEditText: EditText
    private lateinit var addInstructionButton: Button
    private lateinit var instructionsContainer: FlexboxLayout
    private lateinit var recipeImage: ShapeableImageView
    private lateinit var selectImageButton: Button
    private lateinit var updateRecipeButton: Button

    private lateinit var imageUri: Uri

    // Arrays to store categories, ingredients, and instructions
    private val categories = ArrayList<String>()
    private val ingredients = ArrayList<String>()
    private val instructions = ArrayList<String>()

    // Database ViewModel
    private lateinit var recipeViewModel: RecipeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_recipe, container, false)

        // Get recipe data from arguments
        val recipe = arguments?.getParcelable<Recipe>("recipe")

        // Initialize views
        backButton = view.findViewById(R.id.backButton)
        deleteButton = view.findViewById(R.id.deleteButton)
        nameEditTextLayout = view.findViewById(R.id.nameEditTextLayout)
        nameEditText = view.findViewById(R.id.nameEditText)
        categoryEditTextLayout = view.findViewById(R.id.categoryEditTextLayout)
        categoryEditText = view.findViewById(R.id.categoryEditText)
        addCategoryButton = view.findViewById(R.id.addCategoryButton)
        categoriesContainer = view.findViewById(R.id.categoriesContainer)
        sourceEditText = view.findViewById(R.id.sourceEditText)
        cookTimeEditText = view.findViewById(R.id.cookTimeEditText)
        servingsEditText = view.findViewById(R.id.servingsEditText)
        caloriesEditText = view.findViewById(R.id.caloriesEditText)
        pointsEditText = view.findViewById(R.id.pointsEditText)
        ingredientEditText = view.findViewById(R.id.ingredientEditText)
        addIngredientButton = view.findViewById(R.id.addIngredientButton)
        ingredientsContainer = view.findViewById(R.id.ingredientsContainer)
        instructionsEditText = view.findViewById(R.id.instructionEditText)
        addInstructionButton = view.findViewById(R.id.addInstructionButton)
        instructionsContainer = view.findViewById(R.id.instructionsContainer)
        recipeImage = view.findViewById(R.id.recipeImage)
        selectImageButton = view.findViewById(R.id.selectImageButton)
        updateRecipeButton = view.findViewById(R.id.updateRecipeButton)


        // Setup image selection interface
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    val copiedUri = copyImageToAppStorage(uri)
                    if (copiedUri != null) {
                        recipeImage.setImageURI(copiedUri)
                        imageUri = copiedUri
                    }
                } else {
                    imageUri = Uri.parse(
                        ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                                resources.getResourcePackageName(R.drawable.default_image) + "/" +
                                resources.getResourceTypeName(R.drawable.default_image) + "/" +
                                resources.getResourceEntryName(R.drawable.default_image)
                    )
                    recipeImage.setImageURI(imageUri)
                }
            }

        // Initialize the RecipeViewModel
        recipeViewModel = ViewModelProvider(
            this,
            RecipeViewModelFactory((requireActivity() as MainActivity).database.recipeDao())
        )[RecipeViewModel::class.java]


        // Populate all fields with recipe data
        if (recipe != null) {
            populateFields(recipe)
        }


        // Set click listeners for buttons
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        deleteButton.setOnClickListener {
            if (recipe != null) {
                handleDeleteButtonClick(recipe)
            }
        }

        addCategoryButton.setOnClickListener {
            addItem(categoryEditText, categoriesContainer, categories)
        }
        addIngredientButton.setOnClickListener {
            addItem(ingredientEditText, ingredientsContainer, ingredients)
        }
        addInstructionButton.setOnClickListener {
            addItem(instructionsEditText, instructionsContainer, instructions)
        }


        selectImageButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_PERMISSION_CODE
                )
            }

            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }


        updateRecipeButton.setOnClickListener {
            if (recipe != null) {
                updateRecipe(recipe)
            }
        }

        return view
    }


    private fun copyImageToAppStorage(sourceUri: Uri): Uri? {
        val contentResolver = requireContext().contentResolver
        val inputStream = contentResolver.openInputStream(sourceUri)

        try {
            val outputDir = File(requireContext().filesDir, "images")
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }

            val fileName = "copied_image_${System.currentTimeMillis()}.jpg"
            val outputFile = File(outputDir, fileName)
            val outputStream = FileOutputStream(outputFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            return Uri.fromFile(outputFile)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }

        return null
    }


    // *********************************************************************************************
    private fun populateFields(recipe: Recipe) {
        nameEditText.setText(recipe.name)
        sourceEditText.setText(recipe.source)
        cookTimeEditText.setText(recipe.cookTime)
        servingsEditText.setText(recipe.servings)
        caloriesEditText.setText(recipe.calories)
        pointsEditText.setText(recipe.points)

        for (category in recipe.categories) {
            categories.add(category)
            val categoryChip = createChip(category, categoriesContainer, categories)
            categoriesContainer.addView(categoryChip)
        }

        for (ingredient in recipe.ingredients) {
            ingredients.add(ingredient)
            val ingredientChip = createChip(ingredient, ingredientsContainer, ingredients)
            ingredientsContainer.addView(ingredientChip)
        }

        for (instruction in recipe.instructions) {
            instructions.add(instruction)
            val instructionChip = createChip(instruction, instructionsContainer, instructions)
            instructionsContainer.addView(instructionChip)
        }

        imageUri = Uri.parse(recipe.image)
        recipeImage.setImageURI(recipe.image.toUri())
    }

    // *********************************************************************************************
    private fun handleDeleteButtonClick(recipe: Recipe) {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)

        builder.setTitle("Löschvorgang")
        builder.setMessage("Möchtest du das Rezept wirklich löschen?")

        builder.setPositiveButton("Ja") { _: DialogInterface, _: Int ->
            // Delete the recipe from the database
            lifecycleScope.launch {
                recipeViewModel.deleteRecipe(recipe)

            }

            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CookbookFragment())
                .addToBackStack(null)
                .commit()

            // Show success Toast
            StyleableToast.makeText(
                requireContext(),
                getString(R.string.updateToastDelete),
                R.style.customToast
            ).show()
        }
        builder.setNegativeButton("Nein") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

    }

    // *********************************************************************************************
    private fun updateRecipe(recipe: Recipe) {
        // Get all updated values from UI
        nameEditTextLayout.error = null
        categoryEditTextLayout.error = null

        val updatedName = nameEditText.text.toString()
        val updatedCategories = categories.toList()

        if (updatedName.isNotEmpty() && updatedCategories.isNotEmpty()) {

            var updatedSource = sourceEditText.text.toString()
            if (updatedSource.isEmpty()) {
                updatedSource = "-"
            }

            var updatedCookTime = cookTimeEditText.text.toString()
            if (updatedCookTime.isEmpty()) {
                updatedCookTime = "-"
            }

            var updatedServings = servingsEditText.text.toString()
            if (updatedServings.isEmpty()) {
                updatedServings = "-"
            }

            var updatedCalories = caloriesEditText.text.toString()
            if (updatedCalories.isEmpty()) {
                updatedCalories = "-"
            }

            var updatedPoints = pointsEditText.text.toString()
            if (updatedPoints.isEmpty()) {
                updatedPoints = "-"
            }
            val updatedIngredients = ingredients.toList().toMutableList()
            if (updatedIngredients.isEmpty()) {
                updatedIngredients += "keine Angaben"
            }

            val updatedInstructions = instructions.toList().toMutableList()
            if (updatedInstructions.isEmpty()) {
                updatedInstructions += "keine Angaben"
            }


            // Create an updated recipe object
            val updatedRecipe = Recipe(
                id = recipe.id,
                name = updatedName,
                categories = updatedCategories,
                source = updatedSource,
                cookTime = updatedCookTime,
                servings = updatedServings,
                calories = updatedCalories,
                points = updatedPoints,
                ingredients = updatedIngredients,
                instructions = updatedInstructions,
                image = imageUri.toString()
            )

            // Add recipe to database
            lifecycleScope.launch {
                recipeViewModel.updateRecipe(updatedRecipe)
            }

            // Show success Toast
            StyleableToast.makeText(
                requireContext(),
                getString(R.string.updateToastEdit),
                R.style.customToast
            ).show()

            // Return to cookbook fragment
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CookbookFragment())
                .addToBackStack(null)
                .commit()
        } else {
            if (updatedName.isEmpty()) {
                nameEditTextLayout.error = getString(R.string.updateNameError)
            }
            if (categories.isEmpty()) {
                categoryEditTextLayout.error = getString(R.string.updateCategoryError)
            }
        }
    }

    // *********************************************************************************************
    private fun addItem(editText: EditText, container: FlexboxLayout, itemList: ArrayList<String>) {
        val item = editText.text.toString()

        if (item.isNotEmpty()) {
            itemList.add(item)

            val newItem = createChip(item, container, itemList)
            container.addView(newItem)
            editText.text.clear()
        }

    }

    // *********************************************************************************************
    @Suppress("NAME_SHADOWING")
    private fun createChip(
        chipText: String,
        container: FlexboxLayout,
        itemList: ArrayList<String>
    ): Chip {
        val chipView = Chip(requireContext())
        val chipDrawable = ChipDrawable.createFromAttributes(
            requireContext(),
            null,
            0,
            com.google.android.material.R.style.Widget_MaterialComponents_Chip_Entry
        )
        chipView.setChipDrawable(chipDrawable)

        chipView.text = chipText
        chipView.setChipBackgroundColorResource(R.color.dark_gray)
        chipView.setChipStrokeColorResource(R.color.orange)
        chipView.setChipStrokeWidthResource(R.dimen.chip_stroke_width)
        chipView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, dpToPx(6), 0)
            chipView.layoutParams = this
        }

        chipView.setOnClickListener {
            container.removeView(chipView)
            itemList.remove(chipText)
        }

        chipView.setOnCloseIconClickListener {
            container.removeView(chipView)
            itemList.remove(chipText)
        }

        chipView.setOnCheckedChangeListener { chipView, _ ->
            container.removeView(chipView)
            itemList.remove(chipText)
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
package com.cion318.dailycooking.viewmodel

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cion318.dailycooking.R
import com.cion318.dailycooking.data.Recipe
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.imageview.ShapeableImageView

class RecipeAdapter : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(DiffCallback()) {

    private var onItemClickListener: ((Recipe) -> Unit)? = null

    fun setOnItemClickListener(listener: (Recipe) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = getItem(position)
        holder.bind(recipe)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(recipe)
        }
    }

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeImage: ShapeableImageView = itemView.findViewById(R.id.recipeImage)
        private val recipeNameTextView: TextView = itemView.findViewById(R.id.recipeName)
        private val recipeCookTimeTextView: TextView = itemView.findViewById(R.id.recipeCookTime)
        private val recipeSourceTextView: TextView = itemView.findViewById(R.id.recipeSource)
        private val categories: TextView = itemView.findViewById(R.id.categoriesContainer)


        fun bind(recipe: Recipe) {
            Glide.with(itemView.context).load(recipe.image).into(recipeImage)
            recipeNameTextView.text = recipe.name
            val cookTimeText = recipe.cookTime + "\nMin"
            recipeCookTimeTextView.text = cookTimeText
            recipeSourceTextView.text = recipe.source

            categories.text = recipe.categories.joinToString(separator = " - ")
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }
}
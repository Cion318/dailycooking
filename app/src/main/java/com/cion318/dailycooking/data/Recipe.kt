package com.cion318.dailycooking.data

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "categories") val categories: List<String>,
    @ColumnInfo(name = "source") val source: String,
    @ColumnInfo(name = "cookTime") val cookTime: String,
    @ColumnInfo(name = "servings") val servings: String,
    @ColumnInfo(name = "calories") val calories: String,
    @ColumnInfo(name = "points") val points: String,
    @ColumnInfo(name = "ingredients") val ingredients: List<String>,
    @ColumnInfo(name = "instructions") val instructions: List<String>,
    @ColumnInfo(name = "image") val image: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeStringList(categories)
        parcel.writeString(source)
        parcel.writeString(cookTime)
        parcel.writeString(servings)
        parcel.writeString(calories)
        parcel.writeString(points)
        parcel.writeStringList(ingredients)
        parcel.writeStringList(instructions)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }
}

package com.example.assignmentlast.ui.details

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.assignmentlast.R
import com.example.assignmentlast.databinding.ActivityDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor()  // ⬅️ Set the top system bar color to blue

        binding.backButton.setOnClickListener { finish() }

        loadEntityDetails()
    }

    private fun setStatusBarColor() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = 0
        }
    }

    private fun loadEntityDetails() {
        @Suppress("UNCHECKED_CAST")
        val entity = intent.getSerializableExtra("entity") as? HashMap<String, Any> ?: return

        val entityType = determineEntityType(entity)
        binding.toolbarTitle.text = "$entityType Details"

        binding.container.removeAllViews()

        val descriptionField = findDescriptionField(entity)

        val primaryFields = findPrimaryFields(entity)
        val statusFields = findStatusFields(entity)
        val remainingFields = entity.keys
            .filterNot { it in primaryFields || it in statusFields || it == descriptionField }
            .sorted()

        primaryFields.forEach { field ->
            if (entity.containsKey(field)) {
                val value = entity[field].toString()
                val isTitle = primaryFields.indexOf(field) == 0
                val isItalic = field.contains("scientific", ignoreCase = true)

                addLabelAndValue(
                    label = formatFieldName(field),
                    value = value,
                    isTitle = isTitle,
                    isItalic = isItalic,
                    topMargin = if (primaryFields.indexOf(field) > 0) dpToPx(16) else 0
                )
            }
        }

        if (statusFields.isNotEmpty()) {
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dpToPx(24)
                }
            }

            statusFields.forEach { field ->
                if (entity.containsKey(field)) {
                    val statusValue = entity[field].toString()
                    val statusLayout = createStatusLayout(field, statusValue)

                    val params = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    statusLayout.layoutParams = params

                    rowLayout.addView(statusLayout)
                }
            }

            if (rowLayout.childCount > 0) {
                binding.container.addView(rowLayout)
            }
        }

        remainingFields.forEach { field ->
            addLabelAndValue(
                label = formatFieldName(field),
                value = entity[field].toString(),
                topMargin = dpToPx(16)
            )
        }

        if (descriptionField != null && entity.containsKey(descriptionField)) {
            addLabelAndValue(
                label = formatFieldName(descriptionField),
                value = entity[descriptionField].toString(),
                topMargin = dpToPx(24)
            )
        }
    }

    private fun determineEntityType(entity: Map<String, Any>): String {
        return when {
            entity.containsKey("commonName") && entity.containsKey("scientificName") -> "Plant"
            entity.containsKey("species") && entity.containsKey("habitat") -> "Animal"
            entity.containsKey("dishName") || entity.containsKey("mealType") -> "Food"
            entity.containsKey("title") && entity.containsKey("author") -> "Book"
            else -> "Entity"
        }
    }

    private fun findPrimaryFields(entity: Map<String, Any>): List<String> {
        val result = mutableListOf<String>()
        val titleFieldOptions = listOf("dishName", "commonName", "species", "name", "title")
        for (field in titleFieldOptions) {
            if (entity.containsKey(field)) {
                result.add(field)
                break
            }
        }
        val secondaryFieldOptions = listOf("origin", "scientificName", "author", "subtitle")
        for (field in secondaryFieldOptions) {
            if (entity.containsKey(field)) {
                result.add(field)
                break
            }
        }
        return result
    }

    private fun findStatusFields(entity: Map<String, Any>): List<String> {
        val result = mutableListOf<String>()
        val statusFieldOptions = listOf(
            "mainIngredient", "mealType", "careLevel", "conservationStatus",
            "difficulty", "status", "lightRequirement", "habitat", "diet"
        )
        for (field in statusFieldOptions) {
            if (entity.containsKey(field)) {
                result.add(field)
            }
        }
        entity.keys.forEach { field ->
            if ((field.contains("level", ignoreCase = true)
                        || field.contains("status", ignoreCase = true)
                        || field.contains("requirement", ignoreCase = true)
                        || field.contains("type", ignoreCase = true)
                        || field.contains("ingredient", ignoreCase = true))
                && field !in result
            ) {
                result.add(field)
            }
        }
        return result.take(2)
    }

    private fun findDescriptionField(entity: Map<String, Any>): String? {
        return entity.keys.firstOrNull {
            it.contains("description", ignoreCase = true)
                    || it.contains("summary", ignoreCase = true)
                    || it.contains("about", ignoreCase = true)
        }
    }

    private fun formatFieldName(field: String): String {
        return field.replaceFirstChar { it.uppercase() }
            .replace(Regex("([a-z])([A-Z])"), "$1 $2")
            .replace("_", " ")
    }

    private fun createStatusLayout(field: String, value: String): LinearLayout {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val label = TextView(this).apply {
            text = formatFieldName(field)
            textSize = 16f
            setTextColor(Color.parseColor("#4CAF50"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        layout.addView(label)

        val shouldBeBadge = field.contains("level", ignoreCase = true)
                || field.contains("status", ignoreCase = true)
                || field.contains("difficulty", ignoreCase = true)

        if (shouldBeBadge) {
            val (bgColor, textColor) = getStatusColors(field, value)

            val badge = TextView(this).apply {
                text = value
                textSize = 14f
                setTextColor(Color.parseColor(textColor))
                setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8))

                val drawable = ContextCompat.getDrawable(
                    this@DetailsActivity,
                    R.drawable.badge_background
                )?.mutate()
                if (drawable is GradientDrawable) {
                    drawable.setColor(Color.parseColor(bgColor))
                    background = drawable
                }

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dpToPx(8)
                }
            }
            layout.addView(badge)
        } else {
            val valueView = TextView(this).apply {
                text = value
                textSize = 14f
                setTextColor(Color.parseColor("#212121"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dpToPx(8)
                }
            }
            layout.addView(valueView)
        }

        return layout
    }

    private fun getStatusColors(field: String, value: String): Pair<String, String> {
        var bgColor = "#E3F2FD"
        var textColor = "#1565C0"
        when (value.lowercase()) {
            "easy", "low", "good", "stable", "least concern" -> {
                bgColor = "#E8F5E9"
                textColor = "#2E7D32"
            }
            "moderate", "medium", "fair", "near threatened" -> {
                bgColor = "#FFF8E1"
                textColor = "#F57F17"
            }
            "hard", "difficult", "high", "poor", "endangered", "vulnerable", "critical" -> {
                bgColor = "#FFEBEE"
                textColor = "#C62828"
            }
        }
        return Pair(bgColor, textColor)
    }

    private fun addLabelAndValue(
        label: String,
        value: String,
        isTitle: Boolean = false,
        isItalic: Boolean = false,
        topMargin: Int = 0
    ) {
        val labelView = TextView(this).apply {
            text = label
            textSize = 16f
            setTextColor(Color.parseColor("#4CAF50"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                this.topMargin = topMargin
            }
        }
        binding.container.addView(labelView)

        val valueView = TextView(this).apply {
            text = value
            textSize = if (isTitle) 24f else 16f
            setTextColor(Color.parseColor("#212121"))
            if (isTitle) setTypeface(null, Typeface.BOLD)
            if (isItalic) setTypeface(null, Typeface.ITALIC)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        binding.container.addView(valueView)
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}


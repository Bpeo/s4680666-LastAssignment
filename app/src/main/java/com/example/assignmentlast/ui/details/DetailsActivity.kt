package com.example.assignmentlast.ui.details

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.assignmentlast.R
import com.example.assignmentlast.databinding.ActivityDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

// Enable Hilt dependency injection
@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup back button
        binding.backButton.setOnClickListener { finish() }

        // Load entity details
        loadEntityDetails()
    }

    // Main method to load and display entity details
    private fun loadEntityDetails() {
        // Get entity from intent extras
        @Suppress("UNCHECKED_CAST")
        val entity = intent.getSerializableExtra("entity") as? HashMap<String, Any> ?: return

        // Set toolbar title based on entity type
        val entityType = determineEntityType(entity)
        binding.toolbarTitle.text = "$entityType Details"

        // Clear the container
        binding.container.removeAllViews()

        // Find the description field
        val descriptionField = findDescriptionField(entity)

        // Organize fields into categories
        val primaryFields = findPrimaryFields(entity)
        val statusFields = findStatusFields(entity)
        val remainingFields = entity.keys
            .filterNot { it in primaryFields || it in statusFields || it == descriptionField }
            .sorted()

        // Add primary fields (name, species, etc.)
        primaryFields.forEach { field ->
            if (entity.containsKey(field)) {
                val value = entity[field].toString()
                val isTitle = primaryFields.indexOf(field) == 0 // First primary field is title
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

        // Add status fields in a row if there are any
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

                    // Add with equal weight
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

        // Add remaining fields (except description)
        remainingFields.forEach { field ->
            addLabelAndValue(
                label = formatFieldName(field),
                value = entity[field].toString(),
                topMargin = dpToPx(16)
            )
        }

        // Add description at the end
        if (descriptionField != null && entity.containsKey(descriptionField)) {
            addLabelAndValue(
                label = formatFieldName(descriptionField),
                value = entity[descriptionField].toString(),
                topMargin = dpToPx(24)
            )
        }
    }

    private fun determineEntityType(entity: Map<String, Any>): String {
        // Try to determine entity type from the fields
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

        // First primary field (title field)
        val titleFieldOptions = listOf("dishName", "commonName", "species", "name", "title")
        for (field in titleFieldOptions) {
            if (entity.containsKey(field)) {
                result.add(field)
                break
            }
        }

        // Second primary field (usually scientific name or origin)
        val secondaryFieldOptions = listOf("origin", "scientificName", "author", "subtitle")
        for (field in secondaryFieldOptions) {
            if (entity.containsKey(field)) {
                result.add(field)
                break
            }
        }

        return result
    }

    // Find status fields for the entity
    private fun findStatusFields(entity: Map<String, Any>): List<String> {
        val result = mutableListOf<String>()

        // Status fields
        val statusFieldOptions = listOf(
            "mainIngredient", "mealType", "careLevel", "conservationStatus",
            "difficulty", "status", "lightRequirement", "habitat", "diet"
        )

        for (field in statusFieldOptions) {
            if (entity.containsKey(field)) {
                result.add(field)
            }
        }

        // Add any other fields containing "level", "status", "requirement"
        entity.keys.forEach { field ->
            if ((field.contains("level", ignoreCase = true) ||
                        field.contains("status", ignoreCase = true) ||
                        field.contains("requirement", ignoreCase = true) ||
                        field.contains("type", ignoreCase = true) ||
                        field.contains("ingredient", ignoreCase = true)) &&
                field !in result) {
                result.add(field)
            }
        }

        // Limit to 2 status fields for the row
        return result.take(2)
    }

    // Find description field for the entity
    private fun findDescriptionField(entity: Map<String, Any>): String? {
        // Look for description field
        return entity.keys.firstOrNull {
            it.contains("description", ignoreCase = true) ||
                    it.contains("summary", ignoreCase = true) ||
                    it.contains("about", ignoreCase = true)
        }
    }

    // Format field name for display
    private fun formatFieldName(field: String): String {
        return field.replaceFirstChar { it.uppercase() }
            .replace(Regex("([a-z])([A-Z])"), "$1 $2")
            .replace("_", " ")
    }

    // Create a layout for status fields
    private fun createStatusLayout(field: String, value: String): LinearLayout {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        // Add label
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

        // Determine if this should be a badge
        val shouldBeBadge = field.contains("level", ignoreCase = true) ||
                field.contains("status", ignoreCase = true) ||
                field.contains("difficulty", ignoreCase = true)

        if (shouldBeBadge) {
            // Create badge for status-like fields
            val (bgColor, textColor) = getStatusColors(field, value)

            val badge = TextView(this).apply {
                text = value
                textSize = 14f
                setTextColor(Color.parseColor(textColor))
                setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8))

                // Load badge_background and change its color while keeping radius
                val drawable = ContextCompat.getDrawable(this@DetailsActivity, R.drawable.badge_background)?.mutate()
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
            // Regular text for non-status fields
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

    // Get appropriate colors for status values
    private fun getStatusColors(field: String, value: String): Pair<String, String> {
        // Default colors
        var bgColor = "#E3F2FD" // Light blue
        var textColor = "#1565C0" // Dark blue

        // Determine colors based on status value
        when (value.lowercase()) {
            "easy", "low", "good", "stable", "least concern" -> {
                bgColor = "#E8F5E9" // Light green
                textColor = "#2E7D32" // Dark green
            }
            "moderate", "medium", "fair", "near threatened" -> {
                bgColor = "#FFF8E1" // Light amber
                textColor = "#F57F17" // Dark amber
            }
            "hard", "difficult", "high", "poor", "endangered", "vulnerable", "critical" -> {
                bgColor = "#FFEBEE" // Light red
                textColor = "#C62828" // Dark red
            }
        }

        return Pair(bgColor, textColor)
    }

    // Helper method to add a label and value to the container
    private fun addLabelAndValue(
        label: String,
        value: String,
        isTitle: Boolean = false,
        isItalic: Boolean = false,
        topMargin: Int = 0
    ) {
        // Label
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

        // Value
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

    // Convert dp to pixels
    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}

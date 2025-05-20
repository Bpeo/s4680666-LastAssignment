// app/src/main/java/com/example/assignmentlast/data/models/DashboardResponse.kt
package com.example.assignmentlast.data.models

data class DashboardResponse(
    // List of entities returned by the API
    val entities: List<Map<String, Any>>,  // Dynamic entity structure using Map
    val entityTotal: Int   // Total number of entities
)

// app/src/main/java/com/example/assignmentlast/MyApplication.kt
package com.example.assignmentlast

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp  // Enable Hilt dependency injection for the application
class MyApplication : Application()
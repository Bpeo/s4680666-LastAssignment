# API-Assignment for NIT3213

## Overview
This Android application was developed for the NIT3213 final assignment. It demonstrates API integration, user interface design, and Android development best practices through a three-screen app that authenticates users and displays data from the 'vu-nit3213-api'.

## Features
- **Login Screen**: Authenticates users against the course API with location selection
- **Dashboard**: Displays entities retrieved from the API in a RecyclerView with dynamic field detection
- **Details View**: Shows comprehensive information about selected entities with intelligent field organization

## Technical Implementation

### Architecture
- **MVVM Architecture Pattern**: Clear separation of UI, business logic, and data
- **Repository Pattern**: Centralized data access layer
- **Dependency Injection**: Using Hilt for managing dependencies
- **LiveData & Coroutines**: For reactive UI updates and asynchronous operations

### API Integration
The application integrates with the 'vu-nit3213-api' at https://nit3213api.onrender.com/

**Endpoints used:**
- `/[location]/auth` - For user authentication (POST)
- `/dashboard/{keypass}` - For retrieving entity data (GET)

### Key Components

### Dependencies Used

- Retrofit2 (networking)
- Gson (JSON parsing)
- Hilt (DI)
- Coroutines (async tasks)
- ViewModel + LiveData (architecture)
- RecyclerView & CardView (UI components)
- JUnit + Mockito (for testing)

#### Data Layer
- **ApiService**: Interface defining API endpoints using Retrofit
- **AppRepository**: Repository implementation handling data operations
- **Data Models**: Structured models for requests and responses

#### UI Layer
- **LoginActivity & ViewModel**: Handles user authentication
- **DashboardActivity & ViewModel**: Displays entity list with dynamic field detection
- **DetailsActivity**: Shows comprehensive entity details with intelligent field organization
- **EntityAdapter**: RecyclerView adapter with dynamic field handling

### Testing
- **Unit Tests**: Testing repository and ViewModel functionality
- **Mock Testing**: Using Mockito for dependency mocking

## Project Structure
```
com.example.assignmentlast/
├── data/
│   ├── api/
│   │   └── ApiService.kt
│   ├── models/
│   │   ├── DashboardResponse.kt
│   │   ├── Entity.kt
│   │   ├── LoginRequest.kt
│   │   └── LoginResponse.kt
│   └── repository/
│       ├── AppRepository.kt
│       └── AppRepositoryImpl.kt
├── di/
│   └── AppModule.kt
├── ui/
│   ├── dashboard/
│   │   ├── DashboardActivity.kt
│   │   ├── DashboardViewModel.kt
│   │   └── EntityAdapter.kt
│   ├── details/
│   │   └── DetailsActivity.kt
│   └── login/
│       ├── LoginActivity.kt
│       └── LoginViewModel.kt
├── MainActivity.kt
└── MyApplication.kt
```

## Technical Highlights

### Dynamic Entity Handling
The application intelligently handles various entity types without hardcoding field names:

- **Smart Field Detection**: Identifies primary, secondary, and status fields based on field names
- **Adaptive UI**: Adjusts the UI based on the entity structure
- **Status Visualization**: Color-codes status fields based on their values

### Error Handling
- **Result Class**: Uses Kotlin's Result class for comprehensive error handling
- **User Feedback**: Provides clear error messages to users
- **Graceful Degradation**: Handles API failures gracefully

### UI/UX Considerations
- **Loading States**: Visual indicators during network operations
- **Field Organization**: Intelligent grouping of related fields
- **Visual Hierarchy**: Clear visual distinction between different types of information

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or newer
- Android SDK 21 or higher
- Gradle 7.0+

### Installation
1. Clone the repository:
2. Open the project in Android Studio
3. Sync Gradle files
4. Run the application on an emulator or physical device

## Usage
1. Launch the application
2. Enter your credentials and select a location (Sydney, Footscray, or ORT)
3. Browse the entities on the dashboard
4. Tap on an entity to view its details


# s4680666-LastAssignment – NIT3213 Final Android App

## 📱 About This App

This Android app was made for the NIT3213 final assignment. It allows students to log in and view data based on their student ID. The app shows architecture-related information and updates the display depending on which student is logged in.

## ✅ Main Features

- **Login Page** – Users log in using their **first name** and **student ID**, and select their **location** (Sydney, Footscray, ORT).
- **Dashboard Page** – Shows a list of architecture entities pulled from the API.
- **Details Page** – Displays full information for any selected architecture item, with smart layout and labels.

## 🔧 Tools & Technologies

- **Kotlin**
- **MVVM Architecture**
- **Hilt (Dependency Injection)**
- **Retrofit2 + Gson (API calls and JSON parsing)**
- **LiveData + Coroutines**
- **Material Design Components**
- **ViewBinding**

## 🌐 API Used

- **Base URL:** `https://nit3213api.onrender.com/`
- **POST** `/login/{location}` → Used to log in
- **GET** `/dashboard/{keypass}` → Used to get architecture data after login

## 🗂️ Project Structure

com.example.assignmentlast/
├── data/ # API calls, models, repository
├── di/ # Hilt dependency setup
├── ui/ # login, dashboard, and details screens
├── MainActivity.kt
└── MyApplication.kt

## 📸 What the App Does

- After login, it shows data based on your name and student ID
- Dashboard shows a list of items (e.g., buildings)
- Clicking on an item opens a detailed view
- Colors and layout adjust depending on the data

## 🛠 How to Run the App

1. **Clone this project**
   git clone https://github.com/Bpeo/s4680666-LastAssignment.git
2. **Open it in Android Studio**
- Let Gradle sync and finish setup

3. **Run the app**
- Use an emulator or real Android device

4. **Login**
- Username = your first name
- Password = your student ID
- Pick a location and click login

## 🧑‍💻 Made By

**Bimal Karki**  
Student ID: `s4680666`  
Victoria University  
Subject: NIT3213


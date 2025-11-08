
# 🚀 Taller 3 — Firebase Mobile Computing  
**Android App with Jetpack Compose, Firebase Authentication, Firestore, and Real-Time Location Tracking**

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-blueviolet?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-UI_Toolkit-4285F4?logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-Backend-orange?logo=firebase&logoColor=white)](https://firebase.google.com/)
[![Google Maps](https://img.shields.io/badge/Google_Maps-API-34A853?logo=google-maps&logoColor=white)](https://developers.google.com/maps)
[![Cloudinary](https://img.shields.io/badge/Cloudinary-Storage-blue?logo=cloudinary&logoColor=white)](https://cloudinary.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

---

## 📱 Overview

**Taller 3 — Firebase** is an Android application built with **Jetpack Compose** that implements **user authentication**, **profile management**, and **real-time geolocation tracking** using **Firebase** and **Google Maps**.

Each authenticated user can:
- Register or log in using **Email/Password**, **Google**, or **Facebook**.  
- Update personal information and upload a profile photo.  
- Toggle a **Connected Mode** to broadcast and track their **real-time location**.  
- View other connected users on the map with **custom profile markers**.  
- See their **travel history** rendered as a **polyline route**.

---

## ⚙️ Tech Stack

| Layer | Technology |
|-------|-------------|
| **Frontend** | Kotlin · Jetpack Compose · Material3 |
| **Authentication** | Firebase Auth (Email/Password, Google, Facebook) |
| **Database** | Firestore · Firebase Realtime Database |
| **Storage** | Cloudinary (profile images) |
| **Maps & Location** | Google Maps API · FusedLocationProvider |
| **Architecture** | MVVM (ViewModel · Repository · Composable UI) |

---

## ✨ Core Features

- **User Authentication:** Secure registration and login via Firebase.  
- **Profile Editing:** Update personal information and password.  
- **Cloudinary Storage:** Upload and serve profile photos securely.  
- **Live Map Tracking:** Visualize users’ current positions in real time.  
- **Switch Connectivity:** Toggle live updates (Connected / Disconnected).  
- **Route Drawing:** Trace users’ real-world paths dynamically.  
- **Auto Cleanup:** Automatically removes disconnected users.  

---

## 🗺️ App Structure

```

tallericm3/
├── data/
│   ├── model/ → User data model
│   ├── repository/ → Firebase & Cloudinary repositories
├── ui/
│   ├── screens/ → Compose screens (Auth, Home, Profile, Splash)
│   ├── components/ → Reusable UI components
├── viewModel/ → MVVM state management
├── navigation/ → Route management
└── utils/ → Utilities (login setup, permissions, etc.)

````

---

## 🔐 Firebase Integration

- **Auth:** Handles user registration, login, and logout.  
- **Firestore:** Stores static user information (name, ID, email, phone).  
- **Realtime Database:** Tracks active users and location changes in real time.  
- **Cloudinary:** Uploads and serves secure profile image URLs.

---

## 🧭 Real-Time Map Behavior

- When a user connects, their **location and connection state** are stored in Firebase.  
- The map dynamically displays:
  - **Your position:** Blue marker with your profile photo.  
  - **Other users:** Custom photo markers.  
  - **Routes:** Polylines showing movement paths.  
- When disconnected, the user’s marker and path are **automatically removed**.

---

## 🧑‍💻 How to Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/KevinGGaitan/TallerICM3.git
   cd TallerICM3
2. **Open the project** in **Android Studio Giraffe (or newer)**.
3. **Connect to Firebase** (Auth, Firestore, Realtime Database).
4. **Add your Google Maps API key** to `AndroidManifest.xml`.
5. **Configure Cloudinary credentials** in
   `StorageRepository.kt`:

   ```kotlin
   val config = mapOf(
       "cloud_name" to "your_cloud_name",
       "api_key" to "your_api_key",
       "api_secret" to "your_api_secret"
   )
   ```
6. **Run the app** on a real device or emulator with **Google Play Services**.

---

## 🎨 UI Highlights

* 100% **Jetpack Compose** interface.
* Built with **Material Design 3** and **responsive layouts**.
* Smooth transitions, modern cards, and rounded corners.
* Includes a **custom animated bottom navigation bar**.
* Profile photo and route visualization integrated seamlessly.

---

## 📸 Screenshots

| Login                           | Map                         | Profile                             |
| ------------------------------- | --------------------------- | ----------------------------------- |
| ![Login](screenshots/login.png) | ![Map](screenshots/map.png) | ![Profile](screenshots/profile.png) |

*(Add your screenshots in the `/screenshots` directory.)*

---

## 🔮 Future Improvements

* Add push notifications for friend connections.
* Include offline map caching.
* Dark mode and adaptive color themes.
* Multi-language support (EN/ES).
* Live chat between connected users.

---

## 👥 Credits

Developed by
**Kevin G. Gaitán**
*Mobile Computing — Taller 3*
*Faculty of Engineering, 2025*

---

## 📝 License

This project is licensed under the **MIT License**.
You are free to use, modify, and distribute it with proper credit.

---

> *“Real-time collaboration and mobility through Firebase and Jetpack Compose — bringing location to life.”*

```

---

Would you like me to make a **Spanish version** of this README too (for submission or bilingual portfolio presentation)?
```

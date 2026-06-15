# ParkMate

ParkMate is a native Android travel companion app for National Park visitors. It combines a focused park guide with a photo-sharing community so users can plan a visit, browse attractions, upload photos, and exchange tips with other travelers.

## High-Level Design

### Goal

ParkMate helps users move through the National Park travel journey:

```text
Plan trip -> Browse park -> View attractions -> Take/upload photo -> Share with community
```

The app is not a full map app and not a general social media app. The product stays focused on National Park planning, attraction discovery, photo sharing, and visitor tips.

### Tech Stack

```text
Platform: Android
IDE: Android Studio
Language: Kotlin
UI: Jetpack Compose
Architecture: MVVM + Repository Pattern
Backend: Firebase
Database: Cloud Firestore
Authentication: Firebase Authentication
File Storage: Firebase Storage
Device Features: Camera, Photo Picker, Location
```

### System Architecture

```text
ParkMate Android App
|
|-- UI Layer
|   |-- LoginScreen
|   |-- HomeScreen
|   |-- ParkDetailScreen
|   |-- AttractionDetailScreen
|   |-- UploadScreen
|   |-- CommunityScreen
|   `-- ProfileScreen
|
|-- ViewModel Layer
|   |-- AuthViewModel
|   |-- ParkViewModel
|   |-- PostViewModel
|   `-- ProfileViewModel
|
|-- Repository Layer
|   |-- AuthRepository
|   |-- ParkRepository
|   |-- PostRepository
|   `-- StorageRepository
|
`-- Data Sources
    |-- Firebase Authentication
    |-- Cloud Firestore
    |-- Firebase Storage
    |-- Local Park Seed Data
    `-- Android Device APIs
```

### Core Screen Flow

```text
Launch App
   |
   v
Login / Sign Up
   |
   v
Home / Park List
   |
   v
Park Detail
   |
   v
Attraction Detail
   |
   +----> Upload Photo
   |          |
   |          v
   |      Community Feed
   |
   +----> Back to Park Detail

Bottom Navigation:
Home | Community | Profile
```

## UI Skeleton and Rubric Coverage

This repository includes the first skeleton for all intended proposal screens. The screens are implemented as Jetpack Compose composables with baseline layout structure, dummy data, and previews.

### Intended Screens Implemented

| Proposal screen | Compose file | Current skeleton contents |
| --- | --- | --- |
| Login / Sign Up | `app/src/main/java/com/example/parkmate/ui/screens/LoginScreen.kt` | Email/password fields, login button, create account button |
| Home / Park List | `app/src/main/java/com/example/parkmate/ui/screens/HomeScreen.kt` | App title, search field, park cards, category chips |
| Park Detail | `app/src/main/java/com/example/parkmate/ui/screens/ParkDetailScreen.kt` | Hero image, park overview, location, attraction list |
| Attraction Detail | `app/src/main/java/com/example/parkmate/ui/screens/AttractionDetailScreen.kt` | Attraction image, route info, photo tips, nearby food, safety tips, upload entry point |
| Camera / Upload | `app/src/main/java/com/example/parkmate/ui/screens/UploadScreen.kt` | Caption field, upload placeholder, post button |
| Community | `app/src/main/java/com/example/parkmate/ui/screens/CommunityScreen.kt` | Dummy community post feed with user, park, caption, likes, and comments |
| Profile | `app/src/main/java/com/example/parkmate/ui/screens/ProfileScreen.kt` | User profile card and logout button placeholder |

### Scaffold and Layout Structure

- `app/src/main/java/com/example/parkmate/ui/ParkMateApp.kt` defines the app-level `Scaffold`, navigation graph, and bottom navigation.
- Top-level tabs: Home, Community, Profile.
- Detail flow: Home -> Park Detail -> Attraction Detail -> Upload.
- Login starts the app and routes into the main app skeleton.

### Reusable UI Components

Reusable components are placed under `app/src/main/java/com/example/parkmate/ui/components`:

- `ParkMateSectionCard`: shared section card for detail information blocks.
- `ParkMateEmptyState`: reusable empty-state card for missing selections or placeholder states.

Shared preview data is placed under `app/src/main/java/com/example/parkmate/ui/preview`:

- `ParkMatePreviewData`: dummy parks, attractions, and home UI state used by previews.

### Compose Previews with Dummy Data

Each intended screen includes an Android Studio `@Preview` using dummy data:

- `LoginScreenPreview`
- `HomeScreenPreview`
- `ParkDetailScreenPreview`
- `AttractionDetailScreenPreview`
- `UploadScreenPreview`
- `CommunityScreenPreview`
- `ProfileScreenPreview`

### Data Strategy

Park and attraction content is static for the final demo, so it starts as local seed data. User-generated data is dynamic and belongs in Firebase.

Static local data:

- Parks
- Attractions
- Trail notes
- Photo tips
- Nearby food
- Safety tips

Firebase dynamic data:

- Users
- Posts
- Comments
- Likes
- Saved parks

### Firestore Schema

```text
users/{userId}
  displayName: string
  email: string
  photoUrl: string?
  createdAt: timestamp

posts/{postId}
  userId: string
  userName: string
  parkId: string
  attractionId: string?
  imageUrl: string
  caption: string
  likeCount: number
  commentCount: number
  createdAt: timestamp

posts/{postId}/comments/{commentId}
  userId: string
  userName: string
  text: string
  createdAt: timestamp

posts/{postId}/likes/{userId}
  userId: string
  createdAt: timestamp

users/{userId}/savedParks/{parkId}
  parkId: string
  savedAt: timestamp
```

### Firebase Storage Paths

```text
post_images/{userId}/{postId}.jpg
profile_images/{userId}.jpg
```

### MVP Scope

Required for the final demo:

1. User registration, login, and logout.
2. Home screen with a National Park list.
3. Park detail screen with attraction list.
4. Attraction detail screen with trail info, photo tips, food, and safety tips.
5. Photo picker or camera upload.
6. Firebase Storage upload and Firestore post creation.
7. Community feed from Firestore posts.
8. Basic like and comment interaction.
9. Profile screen with user info and posted photos.

Stretch goals:

- GPS nearby parks.
- Compass.
- Map view.
- Advanced itinerary planning.
- Friend/follow system.
- Push notifications.

### Suggested Implementation Order

1. Android project setup and Firebase dependencies.
2. Navigation and empty screens.
3. Local park seed data.
4. Home, park detail, and attraction detail.
5. Firebase Auth.
6. Photo upload.
7. Community feed.
8. Like and comment.
9. Profile.
10. UI polish and final testing.

## Current Implementation Status

Done:

- Android Studio Kotlin project scaffold.
- Jetpack Compose app shell.
- Login/sign up, home, park detail, attraction detail, upload placeholder, community placeholder, and profile placeholder screens.
- Screen previews with dummy data for the intended screens.
- Common reusable UI components under `ui/components`.
- Local seed data for Yosemite, Yellowstone, and Grand Canyon.
- `ParkRepository` and `ParkViewModel` unit tests.
- Firebase dependencies added to Gradle.

Next required tasks:

1. Create the Firebase project and add `app/google-services.json`.
2. Enable the Google Services Gradle plugin.
3. Implement Firebase Auth registration, login, logout, and user profile creation.
4. Replace upload placeholder with Android Photo Picker and Firebase Storage upload.
5. Replace community placeholder posts with Firestore posts.
6. Add like and comment repository methods.

## Firebase Setup Note

The Gradle Firebase dependencies are present, but the Google Services plugin is commented out until `app/google-services.json` is added from the Firebase Console. After adding the file, enable this line in `app/build.gradle.kts`:

```kotlin
alias(libs.plugins.google.services)
```

# ParkMate

**Version:** 0.6.1

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

### Firebase Setup

Firebase dependencies are already included. The app uses Firebase Authentication for login/sign-up, Firestore for user profiles and community posts, and Firebase Storage for community post photos.

To connect a real Firebase project:

1. Create a Firebase project in the Firebase Console.
2. Add an Android app with package name `com.example.parkmate`.
3. Download `google-services.json`.
4. Place it at `app/google-services.json`.
5. Enable Email/Password sign-in in Firebase Authentication.
6. Create a Firestore database.
7. Review `firestore.rules` and `storage.rules`, then deploy them to the Firebase project.
8. Create a Firebase Storage bucket before testing post or profile photo uploads.

The Gradle build automatically applies the Google Services plugin when `app/google-services.json` is present. The file is gitignored so local Firebase credentials are not committed. The rule files in this repository are not deployed automatically.

### Local Verification

Run the unit tests and assemble a debug APK before pushing changes:

```bash
./gradlew testDebugUnitTest assembleDebug
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
|   `-- SavedParkRepository
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

## Current UI

The current version connects the main Firebase-backed community flow. Users can authenticate, create community posts tagged with the park/attraction they were viewing, browse the Firestore feed, like/unlike and comment on posts, save parks, and manage a profile (own posts, saved parks, and a profile photo). Photo upload (post and profile photos) is implemented in code but requires a Firebase Storage bucket, which may require the Blaze plan for new Firebase projects.

<p>
  <img src="docs/screenshots/login.png" width="190" alt="ParkMate login screen" />
  <img src="docs/screenshots/home.png" width="190" alt="ParkMate home screen" />
  <img src="docs/screenshots/park-detail.png" width="190" alt="ParkMate park detail screen" />
  <img src="docs/screenshots/attraction-detail.png" width="190" alt="ParkMate attraction detail screen" />
</p>

The app currently includes the planned proposal screens:

- Login / Sign Up
- Home / Park List
- Park Detail
- Attraction Detail
- Camera / Upload
- Community
- Profile

The UI code is organized under `app/src/main/java/com/example/parkmate/ui`:

- `ParkMateApp.kt` contains the app scaffold, navigation graph, and bottom navigation.
- `navigation/Destination.kt` contains the route names used by the app navigation.
- `screens/` contains the screen-level composables.
- `components/` contains reusable UI pieces such as shared section cards and empty states.
- `preview/` contains dummy data used by Android Studio previews.

Each screen has a Compose `@Preview` so the layouts can be checked quickly in Android Studio without running the full app.

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
  createdAtMillis: number

posts/{postId}
  userId: string
  userName: string
  parkId: string
  attractionId: string?
  imageUrl: string
  caption: string
  likeCount: number
  commentCount: number
  createdAtMillis: number

posts/{postId}/comments/{commentId}
  userId: string
  userName: string
  text: string
  createdAtMillis: number

posts/{postId}/likes/{userId}
  userId: string
  createdAtMillis: number

users/{userId}/savedParks/{parkId}
  parkId: string
  savedAtMillis: number
```

### Firebase Storage Paths

```text
post-images/{userId}/{postId}
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
6. Photo picker, Firebase Storage upload, and Firestore post creation.
7. Community feed.
8. Like and comment.
9. Profile.
10. UI polish and final testing.

## Current Implementation Status

Done:

- Android Studio Kotlin project scaffold.
- Jetpack Compose app shell.
- Login/sign up, home, park detail, attraction detail, upload, community, and profile screens.
- Screen previews with dummy data for the intended screens.
- Common reusable UI components under `ui/components`.
- Local seed data for Yosemite, Yellowstone, and Grand Canyon.
- Auth and post form validation unit tests.
- Firebase dependencies added to Gradle.
- Firebase Auth repository and AuthViewModel added.
- Login and sign-up UI connected to authentication state.
- Profile screen reads the authenticated user and supports logout.
- Firestore post repository and PostViewModel added.
- Upload screen creates photo community posts using Android Photo Picker, Firebase Storage, and Firestore.
- Community screen reads and displays Firestore posts with optional images.
- Community posts support a per-user Like/Unlike toggle. Each tap writes or removes a
  `posts/{postId}/likes/{userId}` document inside a Firestore transaction that also adjusts the
  atomic `likeCount`. On load, the feed restores the signed-in user's liked state by reading each
  visible post's `likes/{userId}` document directly (point reads, so no Firestore index is needed).
- Community posts support comments. Each card expands an inline thread that reads the
  `posts/{postId}/comments` subcollection in real time, and posting a comment writes the document and
  increments the atomic `commentCount` in a single Firestore transaction.
- Profile screen lists posts authored by the current signed-in user, shows their saved parks, and
  supports uploading a profile photo to `profile_images/{userId}.jpg`.
- Posts are tagged with the park (and attraction, when uploaded from an attraction) the user was
  viewing, instead of always defaulting to Yosemite.
- Save/unsave parks. The park detail screen toggles a `users/{userId}/savedParks/{parkId}` document,
  and `ProfileViewModel` restores the saved set in real time.
- Sign-in ensures a `users/{userId}` document exists (merged), so accounts created before that step
  still get a profile document.
- Firestore and Storage security rules drafted in `firestore.rules` and `storage.rules`.

Next required tasks:

1. Deploy the drafted security rules (`firebase deploy --only firestore:rules,storage`) and verify
   them against the app before release.
2. Final UI polish and emulator walkthrough testing.
3. Keep Firebase Storage optional unless the project is upgraded to Blaze.

Stretch goals (GPS nearby parks, compass, map view, itinerary planning, friend/follow, push
notifications) remain out of scope for the current milestone.

## Firebase Setup Note

The app uses disabled Firebase repositories when `google-services.json` is missing. This keeps the skeleton runnable before Firebase credentials are added and shows clear setup errors on the login and community screens instead of crashing.

Restoring a user's liked posts reads each visible post's `likes/{userId}` document directly (a point
read on a known path). This deliberately avoids a `collectionGroup("likes").whereEqualTo("userId", ...)`
query, which would require a single-field collection-group index to be created in the Firebase console
before it works.

Security rules are drafted in `firestore.rules` and `storage.rules` at the project root. They allow
signed-in users to read the feed, write only their own posts/likes/comments/saved-parks, and upload
only their own post and profile photos. Deploy them with
`firebase deploy --only firestore:rules,storage` (test-mode rules are fine for local development).

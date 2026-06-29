# Campus Found

Campus Found is an Android lost and found app for Royal University of Phnom Penh
(RUPP). Students can browse lost or found items, post reports, open item details,
and manage their own profile.

This is a student demo release. It does not include an admin dashboard or staff
moderation tools.

## Features

| Area | What students can do |
| --- | --- |
| Account | Register, login with email or phone, edit profile, logout |
| Home | Browse items, search, filter by status or category, pull to refresh |
| Report | Post a lost or found item with optional photo, location, and contact info |
| Detail | View item photo, status, location, reporter, and contact action |
| Profile | View stats, manage own posts, delete own posts |
| App | Light/dark mode, Room cache fallback when network fails |

## Demo Flow

1. Register a new account.
2. Login with email or phone number.
3. Browse Home, search, and use filter chips.
4. Open an item detail page.
5. Report a new lost or found item.
6. Return to Home and pull to refresh.
7. Open Profile, edit profile, and logout.

## Tech Stack

- Kotlin
- Android XML layouts
- Material Design 3 components
- ViewBinding
- Navigation component
- Hilt dependency injection
- Retrofit
- Room
- Glide
- MockAPI backend
- Optional Firebase Storage support for photos

## Project Info

- Application ID: `com.lostfound`
- Android namespace: `com.example.lostfound`
- Version: `1.1`
- Version code: `2`
- Compile SDK: `35`
- Minimum SDK: `28`
- Target SDK: `35`
- MockAPI base URL: `https://6a1460d76c7db8aac05469d9.mockapi.io/`

## Setup

1. Install Android Studio with the Android SDK.
2. Copy `local.properties.example` to `local.properties`.
3. Update `sdk.dir` in `local.properties` for your machine.
4. Open the project in Android Studio and run the `app` configuration.

Command line build:

```powershell
.\gradlew.bat :app:assembleDebug
```

Command line install, with an emulator or USB device connected:

```powershell
.\gradlew.bat :app:installDebug
```

## Firebase

The app can run without Firebase Storage by using the built-in photo fallback.
If Firebase Storage is enabled later, copy `app/google-services.json.example` to
`app/google-services.json`, add the real Firebase config, and rebuild.

`app/google-services.json` is intentionally ignored by Git.

## Author

Vitkayo - Campus Found, RUPP Lost and Found project.

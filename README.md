# Campus Found

Lost & found mobile app for **Royal University of Phnom Penh (RUPP)** — Android (Kotlin), Material Design, MockAPI backend, optional Firebase Storage for photos.

**This release is a student user demo only.** There is no admin panel or staff dashboard. Features like mark-as-claimed, edit posts, and notifications are planned for a future semester.

---

## User features (this semester)

| Area | What students can do |
|------|----------------------|
| **Account** | Register (username, email, phone, password) · Login with **email or phone** · Edit profile · Logout |
| **Home** | Browse campus items · Search · Filter (Lost / Found / category) · Pull to refresh · Open item detail |
| **Report** | Post lost/found item (title & category required; photo, location, contact optional) · RUPP location picker |
| **Profile** | View stats · My posted items · Delete own posts |
| **App** | Light / dark mode · Offline cache (Room) when network fails |

---

## User demo script (~3 minutes)

Use this order when presenting to your teacher:

1. **Register** — Create account with username, email, phone, password.
2. **Login** — Show login works with email *or* phone number.
3. **Home** — Scroll the list, use a filter chip (e.g. Electronics), search (e.g. “wallet”).
4. **Detail** — Tap an item; show photo, location, contact link.
5. **Report** — Post a new lost/found item with RUPP location.
6. **Home again** — Pull to refresh; new post appears near the top.
7. **Profile** — Show your name, my posts, edit profile, logout.

---

## Planned for next semester (not in this build)

- Mark items as **claimed** / resolved  
- **Edit** my posts  
- **Recently viewed** items  
- Admin / staff tools (if required by course)  
- Push notifications · Khmer localization · CI pipeline  

---

## Setup

1. Open in Android Studio (or build with Gradle).
2. Optional: add `app/google-services.json` from Firebase Console for cloud photo uploads. Without it, photos use a local fallback. See `app/google-services.json.example` for the expected package name (`com.lostfound`).
3. Run on emulator or device (API 28+).

```powershell
java -jar gradle\wrapper\gradle-wrapper.jar -p . :app:assembleDebug
```

Release build:

```powershell
java -jar gradle\wrapper\gradle-wrapper.jar -p . :app:assembleRelease
```

## Stack

- Kotlin, ViewBinding, Navigation, Room, Retrofit, Glide
- MockAPI: `https://6a1460d76c7db8aac05469d9.mockapi.io/`
- Package: `com.lostfound`
- Version: **1.1** (`versionCode` 2)

## Author

**Vitkayo** — Campus Found, RUPP Lost & Found project

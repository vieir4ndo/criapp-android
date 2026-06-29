# CRIApp — Android

Native Android client for the CRIApp platform, written in **Kotlin**. It communicates with the CRIApp REST API to support parental guidance through questionnaires, gamification, forums, and personalised recommendations.

## Tech Stack

| Concern | Technology |
|---|---|
| Language | Kotlin |
| Min SDK | 28 (Android 9) / Target SDK 35 |
| UI | XML layouts, ViewBinding, Material Design 3 |
| Architecture | MVVM (ViewModel + LiveData) |
| Networking | Retrofit 2 + OkHttp 4 + Gson |
| Async | Kotlin Coroutines |
| Images | Glide |
| Animations | Lottie, Facebook Shimmer |
| Background work | WorkManager |
| Local storage | SharedPreferences (`PreferencesManager`) |

## Project Structure

```
app/src/main/java/com/example/criapp/
├── Activities              # One Activity per screen (root level)
├── abstractions/           # Shared interfaces and base contracts
├── adapters/               # RecyclerView adapters
├── base/                   # Base Activity / ViewModel classes
├── data/
│   ├── local/              # PreferencesManager (JWT + app state)
│   └── remote/             # Retrofit service, interceptors, ApiResponse
├── domain/
│   └── repository/         # Repository interfaces
├── helpers/                # View extensions, ViewFactory, DialogBuilder
├── models/                 # Request / Response data classes
├── presentation/           # Feature-scoped ViewModels + factories
│   ├── auth/
│   ├── award/
│   ├── child/
│   ├── childbook/
│   ├── faq/
│   ├── forum/
│   ├── notification/
│   ├── profile/
│   ├── question/
│   ├── recommendation/
│   └── resource/
└── services/               # NotificationWorker, FeatureFlagsManager, helpers
```

## Prerequisites

- Android Studio Hedgehog or newer
- JDK 8+
- An emulator or physical device running Android 9+

## Running the App

1. Clone the repository and open the `android/` folder in Android Studio.
2. Sync Gradle.
3. Run on an emulator or connected device (`Run > Run 'app'`).

The app targets the production API by default (both debug and release build types point to the Azure-hosted endpoint defined in `build.gradle.kts`). To point at a local instance, change `BASE_URL` in `app/build.gradle.kts`:

```kotlin
debug {
    buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:4500/\"")
}
```

> `10.0.2.2` is the emulator alias for `localhost` on the host machine. The backend listens on port `4500`.

## Authentication Flow

- JWT access token + refresh token stored in `SharedPreferences` via `PreferencesManager`.
- `AuthInterceptor` (OkHttp) attaches the `Authorization: Bearer <token>` header and the device `Accept-Language` to every request.
- On a `401` response the interceptor transparently calls `auth/refresh-token`, persists the new tokens, and retries the original request. If the refresh also fails the user is redirected to `SignInActivity`.

## Key Screens

| Activity | Description |
|---|---|
| `SignInActivity` | Login with email/username and password |
| `SignUpActivity` | User registration |
| `ForgotPasswordActivity` / `ResetPasswordActivity` | Password recovery via email/SMS |
| `MenuActivity` | Main navigation hub |
| `ChildActivity` / `CreateChildActivity` / `UpdateChildActivity` | Child profile management |
| `ChildBookListActivity` / `ChildBookActivity` | Gamification dashboard and child book detail |
| `QuestionsActivity` / `AnswerActivity` | HEFAS questionnaire flow |
| `RecommendationActivity` | Personalised parenting recommendations |
| `AwardActivity` | Earned awards and badges |
| `ForumsActivity` / `ForumActivity` / `CreateForumActivity` / `UpdateForumActivity` | Community forum |
| `NotificationActivity` | In-app notification history |
| `AudioVisualResourcesActivity` | Educational audiovisual resources |
| `FaqActivity` | Frequently asked questions |
| `ProfileActivity` | User profile settings |
| `DevToolsActivity` | Internal developer tools (debug only) |

## Notifications

Local notifications are scheduled via `WorkManager` using `NotificationWorker`. The `NotificationScheduler` helper registers periodic work tasks. Push notification permission is requested at runtime (`POST_NOTIFICATIONS`).

## Feature Flags

`FeatureFlagsManager` fetches the server-side feature flags on startup (e.g., `PhoneNumberUsernameEnabled`) and makes them available throughout the app to gate UI features without a new release.

## Running Tests

```bash
./gradlew test                  # Unit tests
./gradlew connectedAndroidTest  # Instrumented tests (requires a device/emulator)
```

## Building a Release APK

```bash
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

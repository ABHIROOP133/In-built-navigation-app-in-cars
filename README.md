# AutoNav — Standalone Automotive Infotainment Navigation System

AutoNav is a standalone, native Android navigation system engineered for embedded and aftermarket automotive infotainment head units. Developed as a third-year Computer Science engineering project, AutoNav operates directly on Android-powered in-vehicle hardware without relying on smartphone projection protocols or tethered mobile devices.

---

## 1. Problem Statement

Many modern automotive infotainment systems either depend on smartphone projection (Apple CarPlay or Android Auto) or ship with dated, proprietary navigation software that lacks responsive touch ergonomics and modern routing capabilities. When a driver's smartphone is unavailable, out of battery, or incompatible with the vehicle's projection unit, dependent head units lose core navigation functionality.

AutoNav addresses this gap by providing a self-contained Android navigation application designed specifically for landscape automotive touchscreens, featuring a low-glare dark cabin interface, high-contrast typography, large touch targets, and a modular MVVM architecture capable of integrating directly with vehicle GPS sensors and mapping engines.

---

## 2. Project Objectives

1. **Standalone Operation**: Execute natively on Android-based automotive infotainment devices (`minSdk = 28`, `targetSdk = 35`) with zero dependency on Apple CarPlay, Android Auto, or a connected smartphone.
2. **Automotive-Grade Ergonomics**: Provide a landscape-first (`sensorLandscape`), low-glare dark charcoal interface (`#0D1014` / `#141920`) with minimum `48dp`–`56dp` touch targets and glanceable typography.
3. **Truthful System Telemetry**: Report accurate hardware and subsystem states (`GPS status`, `Navigation status`, `Application status`) without fabricating coordinates, vehicle speed, or route telemetry when hardware modules are disconnected or pending integration.
4. **Clean, Modular MVVM Architecture**: Maintain strict separation between Jetpack Compose UI components (`ui`), state holders (`viewmodel`), domain/data repositories (`data`), and upcoming hardware/navigation engines (`location`, `maps`, `navigation`, `voice`).

---

## 3. Technology Stack

| Layer / Tool | Technology & Version | Purpose |
| :--- | :--- | :--- |
| **Language** | Kotlin `2.0.21` | Primary programming language & Coroutines/StateFlow concurrency |
| **UI Framework** | Jetpack Compose (BOM `2024.12.01`) | Declarative, responsive automotive user interface |
| **Design System** | Material 3 (Custom Dark Automotive Theme) | High-contrast dark charcoal theme (`AutoNavTheme`) |
| **Architecture** | AndroidX Lifecycle & ViewModel `2.8.7` | Unidirectional data flow (`StateFlow<HomeUiState>`) |
| **Build System** | Gradle `8.9` / Android Gradle Plugin `8.7.3` | Version catalog (`gradle/libs.versions.toml`) & Kotlin DSL |
| **Android SDK** | `compileSdk = 35`, `minSdk = 28`, `targetSdk = 35` | Android 9.0 (Pie) through Android 15 compatibility |
| **Testing** | JUnit `4.13.2`, Coroutines Test `1.9.0`, Compose UI Test | Unit testing for ViewModels/Repositories & UI instrumentation tests |
| **Version Control** | Git | Source control with strict secret/key exclusion |

---

## 4. Planned Features

AutoNav is structured across progressive engineering milestones:

- **Milestone 1 (Day 1 — Completed)**: Project foundation, MVVM package structure, responsive automotive home screen, destination search input bar, technical map viewport placeholder, and truthful system status bar.
- **Milestone 2**: Hardware GPS location tracking (`com.autonav.location`), vehicle speed calculation, and Google Maps SDK viewport integration (`com.autonav.maps`).
- **Milestone 3**: Destination search and geocoding/places resolution (`com.autonav.data.repository`).
- **Milestone 4**: Route calculation, polyline rendering, turn-by-turn maneuver guidance, and automatic off-route rerouting (`com.autonav.navigation`).
- **Milestone 5**: Hands-free voice navigation announcements using Android Text-to-Speech (`com.autonav.voice`).
- **Milestone 6**: Local persistence for saved destinations and recent searches (`com.autonav.data.local`).

---

## 5. Architecture Overview

AutoNav implements the **Model–View–ViewModel (MVVM)** pattern with Unidirectional Data Flow (UDF):

```
com.autonav
├── MainActivity.kt                  # Edge-to-edge landscape activity entry point
├── ui
│   ├── screens
│   │   └── HomeScreen.kt            # Responsive automotive home screen & layout posture logic
│   ├── components
│   │   ├── TopHeaderBar.kt          # Branding, live clock/date, and Settings trigger
│   │   ├── DestinationSearchBar.kt  # Touch-sized destination search field & milestone notice
│   │   ├── MapPlaceholderView.kt    # Orthographic technical grid & pending map SDK notice
│   │   └── BottomStatusBar.kt       # Truthful GPS, Navigation, and Application status strip
│   └── theme
│       ├── Color.kt                 # Low-glare dark charcoal & steel-blue palette
│       ├── Type.kt                  # High-legibility automotive typography scale
│       └── Theme.kt                 # AutoNavTheme definition
├── viewmodel
│   ├── HomeUiState.kt               # Immutable UI state snapshot
│   └── HomeViewModel.kt             # User intent handler & StateFlow holder
├── data
│   ├── model
│   │   └── SystemStatus.kt          # GpsConnectionStatus, NavigationStatus, ApplicationStatus
│   ├── repository
│   │   └── SystemStatusRepository.kt# System telemetry contract & Day 1 baseline implementation
│   └── local                        # Reserved for Milestone 6 (Saved destinations)
├── location                         # Reserved for Milestone 2 (GPS location & speed provider)
├── maps                             # Reserved for Milestone 2 (Google Maps SDK controller)
├── navigation                       # Reserved for Milestone 4 (Routing & turn-by-turn engine)
├── voice                            # Reserved for Milestone 5 (TTS voice guidance)
└── utils
    └── AutomotiveClock.kt           # Deterministic header date & 24-hour clock formatter
```

Detailed architectural diagrams and data-flow documentation are available in [`docs/architecture.md`](docs/architecture.md) and [`docs/development-roadmap.md`](docs/development-roadmap.md).

---

## 6. Installation and Build Instructions

### Prerequisites
- **JDK**: Java 17 or newer.
- **Android SDK**: Android SDK Platform 35 (`compileSdk = 35`) and Build Tools.
- **IDE**: Android Studio Ladybug (2024.2.1+) or IntelliJ IDEA with the Android plugin.

### Setup Steps

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd "in built navigation app in cars"
   ```

2. **Configure local SDK path**:
   Copy [`local.properties.example`](local.properties.example) to `local.properties` and set your Android SDK path:
   ```properties
   sdk.dir=C\:\\Users\\<username>\\AppData\\Local\\Android\\Sdk
   ```
   *(Never commit `local.properties` or API keys to version control.)*

3. **Build the debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run unit tests**:
   ```bash
   ./gradlew testDebugUnitTest
   ```

5. **Deploy to an Automotive Emulator or Landscape Tablet AVD**:
   Configure an Android Virtual Device with a landscape profile (e.g., `1280x720` or `1024x600` dp) and run the `app` configuration from Android Studio.

---

## 7. Current Development Status (Day 1 Milestone)

| Subsystem / Component | Status | Notes |
| :--- | :--- | :--- |
| **Gradle & Version Catalog** | Completed | Kotlin `2.0.21`, AGP `8.7.3`, Compose BOM `2024.12.01` |
| **MVVM Foundation (`data`, `viewmodel`, `ui`)** | Completed | `SystemStatusRepository`, `HomeViewModel`, `HomeUiState` |
| **Automotive Home Screen (`HomeScreen.kt`)** | Completed | Landscape-first responsive UI with multi-viewport previews |
| **System Telemetry Bar (`BottomStatusBar.kt`)** | Completed | Reports `GPS: Not connected`, `Navigation: Inactive`, `App: Ready` |
| **Unit Test Suite (`app/src/test`)** | Completed | 9/9 unit tests passing for Repository, ViewModel, and Clock utilities |
| **Google Maps SDK & GPS Tracking** | Pending | Scheduled for Milestone 2 |
| **Destination Search & Geocoding** | Pending | UI input complete; backend geocoding scheduled for Milestone 3 |
| **Turn-by-Turn & Voice Navigation** | Pending | Scheduled for Milestones 4 & 5 |

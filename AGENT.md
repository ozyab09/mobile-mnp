# MNP Detector Pro - Project Information

## Project Overview
MNP Detector Pro is an Android application that detects and displays the carrier for incoming calls with ported (transferred) numbers in real-time. The app works completely offline using a local database that can be updated manually or automatically from a specified URL.

## Technical Architecture
- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room with SQLite for local storage
- **Dependency Injection**: Hilt
- **Background Tasks**: WorkManager for scheduled updates
- **Networking**: Retrofit + OkHttp for secure downloads
- **Asynchronous Operations**: Kotlin Coroutines

## Key Components

### 1. Database Layer
- Room database with two main entities:
  - `MnpNumber`: Stores phone numbers and their current carrier
  - `Metadata`: Stores database metadata (last update, source, record count)
- Optimized for fast lookups (<50ms for 20M+ records)
- Indexes on number field for performance

### 2. Business Logic Layer
- Repository pattern for data access
- MNP file parser for processing ZIP files containing number data
- Phone number normalization utility
- ZIP extraction and parsing functionality

### 3. Service Layer
- `CallDetectionService`: Monitors incoming calls using TelephonyManager
- `OverlayService`: Shows system overlay during calls for ported numbers
- `DatabaseUpdateWorker`: Background worker for database updates

### 4. UI Layer
- MainActivity: Manual number lookup interface
- SettingsActivity: Configuration for updates and appearance
- ViewModels: MainViewModel and SettingsViewModel for state management

### 5. Background Processing
- WorkManager for scheduled database updates
- Configurable update schedule (weekly, bi-weekly, monthly)
- Network constraints (Wi-Fi only, charging only)
- Atomic database updates to ensure data integrity

## Features Implemented

### Call Detection
- Real-time detection of incoming calls
- Checks numbers against local MNP database
- Shows overlay notification only for ported numbers
- No notifications for non-ported numbers

### Manual Lookup
- Input field for checking any phone number
- Displays current carrier if number is ported
- Shows database status (last update, record count)

### Settings
- Customizable update URL
- Auto-update schedule configuration
- Network condition settings (Wi-Fi only, charging only)
- Overlay timeout configuration

### Automatic Updates
- Scheduled database updates via WorkManager
- ZIP file download and parsing
- Atomic database replacement
- Metadata tracking

## File Structure
```
app/src/main/
├── java/com/example/mnpdetector/
│   ├── data/
│   │   ├── model/          # Data models (MnpNumber, Metadata)
│   │   ├── dao/            # Room Data Access Objects
│   │   └── database/       # Room database
│   ├── repository/         # Data repository
│   ├── service/            # Background services (call detection, overlay)
│   ├── ui/                 # Activities and fragments
│   │   └── viewmodel/      # ViewModels
│   ├── worker/             # WorkManager workers
│   ├── workmanager/        # WorkManager helper
│   ├── receiver/           # Broadcast receivers
│   ├── util/               # Utility classes
│   └── di/                 # Dependency injection modules
├── res/                    # Resources (layouts, drawables, etc.)
└── AndroidManifest.xml
```

## Permissions Required
- `READ_PHONE_STATE` - To detect incoming calls
- `SYSTEM_ALERT_WINDOW` - To show overlay during calls
- `INTERNET` - To download database updates
- `ACCESS_NETWORK_STATE` - To check network conditions for updates
- `RECEIVE_BOOT_COMPLETED` - To restart service after boot
- `FOREGROUND_SERVICE` - To run call detection service

## Data Format
The application expects a ZIP file containing a text file with the format:
```
79000000000;ПАО 'Мобильные ТелеСистемы'
79000000001;ООО 'Теле2'
```
Where each line contains a phone number and carrier separated by a semicolon.

## Build Configuration
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- Dependencies managed via Gradle
- GitHub Actions for CI/CD

## Security Considerations
- Uses SharedPreferences in MODE_PRIVATE for settings
- SSL certificate validation for HTTP downloads
- Secure local database storage
- No sensitive data transmission

## Performance Optimizations
- Indexed database queries for fast lookups
- Efficient memory usage during file parsing
- Background processing for updates
- Battery optimization through WorkManager constraints

## GitHub Actions Integration
- Automatic APK building on push, pull requests, and manual triggers
- Debug and release builds
- Artifact storage for download
- Workflow files in `.github/workflows/`
- Build verification for pull requests (MR) with APK artifacts available for testing
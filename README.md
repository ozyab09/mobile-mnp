# MNP Detector Pro

An Android application that detects and displays the carrier for incoming calls with ported (transferred) numbers in real-time. Works completely offline using a local database that can be updated manually or automatically from a specified URL.

## Features

- Real-time detection of ported numbers during incoming calls
- Manual number lookup functionality
- Automatic database updates with configurable schedule
- Overlay notification showing the carrier during calls
- Support for custom update URLs
- Configurable update conditions (Wi-Fi only, charging only)

## Technical Architecture

- **Language**: Kotlin
- **Architecture**: MVVM
- **Database**: Room with SQLite
- **Dependency Injection**: Hilt
- **Background Tasks**: WorkManager
- **Networking**: Retrofit + OkHttp
- **Coroutines**: For asynchronous operations

## GitHub Actions Build

This project includes GitHub Actions workflows for automatic APK building:

- `build-apk.yml` - Builds debug APK on every push, pull request, and manual trigger
- `build-and-release.yml` - Builds both debug and release APKs on every push, pull request, and manual trigger

The APKs are automatically generated and available as artifacts in the Actions tab after each successful build.

When creating a pull request (MR), the build will automatically run to verify that changes don't break the build process. The APK artifacts will be available for testing after the build completes.

## Project Structure

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

## Build Instructions

### Local Build
1. Ensure you have Java 17 installed
2. Clone the repository
3. Open in Android Studio
4. Sync the project with Gradle files
5. Build the project (Build > Make Project)

To generate an APK:
1. In Android Studio: Build > Generate Signed Bundle/APK
2. Select APK
3. Use the debug keystore for testing or create a release keystore

### GitHub Actions Build
The project is configured to automatically build APKs when pushed to GitHub:
1. Push your changes to the repository
2. Go to the "Actions" tab
3. Find the workflow run for your commit
4. Download the APK artifacts from the "Artifacts" section

## Key Components

### Database
- Room database with two tables: `mnp_numbers` and `metadata`
- Indexes on number field for fast lookups
- Local storage of ported number data

### Call Detection
- Uses `TelephonyManager` and `PhoneStateListener`
- Detects incoming calls and checks against local database
- Shows overlay notification for ported numbers only

### Overlay Service
- System overlay window showing carrier information
- Configurable timeout and appearance
- Automatically dismissed after call ends

### Update System
- Automatic updates via WorkManager
- Configurable schedule (weekly, bi-weekly, monthly)
- Support for Wi-Fi only and charging only conditions
- ZIP file parsing for MNP data

## Permissions

- `READ_PHONE_STATE` - To detect incoming calls
- `SYSTEM_ALERT_WINDOW` - To show overlay during calls
- `INTERNET` - To download database updates
- `ACCESS_NETWORK_STATE` - To check network conditions for updates
- `RECEIVE_BOOT_COMPLETED` - To restart service after boot
- `FOREGROUND_SERVICE` - To run call detection service

## Configuration

The app can be configured through the settings screen:
- Update source URL (default: https://files.bdpn.online/mobile_number_portability.zip)
- Auto-update schedule and conditions
- Overlay timeout duration
- Network conditions for updates

## Data Format

The application expects a ZIP file containing a text file with the format:
```
79000000000;ПАО 'Мобильные ТелеСистемы'
79000000001;ООО 'Теле2'
```

Where each line contains a phone number and carrier separated by a semicolon.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
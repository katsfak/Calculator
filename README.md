# 🧮 Samsung-Style Calculator

A modern, feature-rich Android calculator application inspired by Samsung's calculator design. Built with Java and Material Design 3, featuring both basic arithmetic operations and advanced scientific functions with automatic light/dark mode support.

![Platform](https://img.shields.io/badge/Platform-Android-green.svg)
![Language](https://img.shields.io/badge/Language-Java-orange.svg)
![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)
![Material Design 3](https://img.shields.io/badge/Material%20Design-3-blue.svg)

## ✨ Features

### 🔢 Basic Operations

- **Arithmetic Operations**: Addition (+), Subtraction (−), Multiplication (×), Division (÷)
- **Decimal Support**: Full floating-point number support with decimal points
- **Percentage Calculations**: Quick percentage conversions
- **Clear Function (AC)**: Reset all calculations instantly
- **Chain Operations**: Perform multiple operations in sequence
- **Error Handling**: Smart division by zero and invalid operation detection

### 🔬 Scientific Functions

- **Trigonometric Functions**:
  - `sin(x)` - Sine (input in degrees)
  - `cos(x)` - Cosine (input in degrees)
  - `tan(x)` - Tangent (input in degrees)
- **Logarithmic Functions**:
  - `log(x)` - Logarithm base 10
  - `ln(x)` - Natural logarithm (base e)
- **Advanced Operations**:
  - `√x` - Square root
  - `x^y` - Power/Exponentiation
  - `x!` - Factorial (supports 0 to 20)
- **Mathematical Constants**:
  - `π` (Pi) ≈ 3.14159...
  - `e` (Euler's number) ≈ 2.71828...

### 🎨 User Interface

- **Dual Display System**:
  - Main display showing current input/result
  - Secondary display showing previous operation
- **Toggle Modes**: Switch between Basic and Scientific calculator modes
- **Material Design 3**: Modern, clean interface following Google's design guidelines
- **Automatic Theme Detection**:
  - Light mode with bright, clean colors
  - Dark mode with comfortable dark theme
  - Automatically switches based on system settings
- **Color-Coded Buttons**:
  - Numbers: Gray buttons for easy identification
  - Operators: Orange buttons for quick access
  - Functions: Medium gray for special operations
- **Responsive Layout**: Optimized for different screen sizes

## 📱 Screenshots

### Light Mode

_Light theme with clean, modern design_

### Dark Mode

_Dark theme for comfortable nighttime use_

### Scientific Mode

_Extended scientific functions panel_

## 🛠️ Technologies Used

- **Language**: Java
- **Minimum SDK**: API 21 (Android 5.0 Lollipop)
- **Target SDK**: API 36 (Android 14+)
- **Build System**: Gradle with Kotlin DSL
- **UI Framework**: Android XML Layouts
- **Design System**: Material Design 3 (Material You)
- **Architecture**: Single Activity with MVC pattern

## 📋 Requirements

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or higher
- Android SDK API 21 or higher
- Gradle 8.13+

## 🚀 Installation & Setup

### Option 1: Clone the Repository

```bash
# Clone the repository
git clone https://github.com/yourusername/calculator.git

# Navigate to the project directory
cd Calculator

# Open in Android Studio
# File -> Open -> Select the Calculator folder
```

### Option 2: Download ZIP

1. Download the project as ZIP file
2. Extract to your desired location
3. Open Android Studio
4. Select `File > Open` and choose the extracted folder

## 🏃 Running the Application

### Using Android Studio (Recommended)

1. **Open the project** in Android Studio
2. **Wait for Gradle sync** to complete
3. **Connect a device** or **start an emulator**:
   - Physical Device: Enable USB debugging and connect via USB
   - Emulator: Tools → Device Manager → Create/Start emulator
4. **Click Run** button (▶️) or press `Shift + F10`
5. **Select target device** and wait for installation

### Using Command Line

```bash
# Set Android SDK path (if not already set)
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools

# Build the project
./gradlew build

# Install on connected device/emulator
./gradlew installDebug

# Or use the provided script
./run_on_emulator.sh
```

## 📖 Usage Guide

### Basic Operations

1. **Enter Numbers**: Tap number buttons (0-9)
2. **Decimal Numbers**: Use the `.` button for decimals
3. **Perform Calculations**:
   - Enter first number
   - Tap an operator (+, −, ×, ÷)
   - Enter second number
   - Tap `=` to see result
4. **Chain Operations**: Continue calculating without clearing
5. **Clear**: Tap `AC` to reset everything
6. **Percentage**: Enter a number and tap `%` to convert to percentage

### Scientific Functions

1. **Enable Scientific Mode**: Tap the `SCI` button
2. **Trigonometric Functions**:
   - Enter angle in degrees
   - Tap `sin`, `cos`, or `tan`
3. **Logarithms**:
   - Enter a positive number
   - Tap `log` (base 10) or `ln` (natural log)
4. **Square Root**:
   - Enter a non-negative number
   - Tap `√`
5. **Power**:
   - Enter base number
   - Tap `x^y`
   - Enter exponent
   - Tap `=`
6. **Factorial**:
   - Enter an integer (0-20)
   - Tap `x!`
7. **Constants**:
   - Tap `π` to insert Pi
   - Tap `e` to insert Euler's number

### Switching Themes

The app automatically adapts to your system theme:

1. **Android Settings → Display → Dark theme**
2. Toggle on/off
3. Return to Calculator app
4. Theme updates automatically

## 📁 Project Structure

```
Calculator/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/calculator/
│   │       │   └── MainActivity.java          # Main calculator logic
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   │   └── activity_main.xml      # Main UI layout
│   │       │   ├── values/
│   │       │   │   ├── colors.xml             # Color definitions
│   │       │   │   ├── colors_theme.xml       # Light mode colors
│   │       │   │   ├── strings.xml            # String resources
│   │       │   │   └── themes.xml             # Light theme
│   │       │   ├── values-night/
│   │       │   │   ├── colors_theme.xml       # Dark mode colors
│   │       │   │   └── themes.xml             # Dark theme
│   │       │   └── ... (other resources)
│   │       └── AndroidManifest.xml
│   ├── build.gradle.kts                       # App-level build config
│   └── proguard-rules.pro
├── gradle/
│   └── libs.versions.toml                     # Dependency versions
├── build.gradle.kts                           # Project-level build config
├── settings.gradle.kts
├── run_on_emulator.sh                         # Quick run script
└── README.md
```

## 🔑 Key Code Components

### MainActivity.java

The main activity containing all calculator logic:

- **Number Input Handling**: Manages user input and display updates
- **Operation Processing**: Performs arithmetic and scientific calculations
- **State Management**: Tracks calculator state across operations
- **UI Updates**: Handles display formatting and theme adaptation

### Layout System

- **activity_main.xml**: Main calculator interface with button grid
- **Responsive Design**: Adapts to different screen sizes and orientations

### Theme System

- **Automatic Detection**: Uses `Theme.Material3.DayNight` for theme switching
- **Color Resources**: Separate color definitions for light and dark modes
- **Status Bar Styling**: Matches status/navigation bars to current theme

## 🎯 Key Features Explained

### Automatic Theme Detection

The calculator uses Android's configuration change system to detect theme changes:

```xml
<!-- values/themes.xml (Light Mode) -->
<style name="Base.Theme.Calculator" parent="Theme.Material3.DayNight.NoActionBar">
    <item name="android:windowLightStatusBar">true</item>
</style>

<!-- values-night/themes.xml (Dark Mode) -->
<style name="Base.Theme.Calculator" parent="Theme.Material3.DayNight.NoActionBar">
    <item name="android:windowLightStatusBar">false</item>
</style>
```

### Operation Chaining

Users can chain multiple operations without pressing equals:

```
Example: 5 + 3 × 2 = (auto-calculates 5+3=8, then 8×2=16)
```

### Smart Number Formatting

- Whole numbers display without decimals: `5` instead of `5.0`
- Decimal numbers show up to 10 decimal places
- Very large/small numbers handled gracefully

## 🐛 Known Issues & Limitations

- Parenthesis functionality is for display only (not fully functional)
- Factorial limited to integers 0-20 to prevent overflow
- Trigonometric functions use degree input (not radians)
- Chain operations follow left-to-right precedence (no PEMDAS/BODMAS)

## 🚧 Future Enhancements

- [ ] Full parenthesis evaluation support
- [ ] History of calculations
- [ ] Copy/paste functionality
- [ ] Landscape mode with more functions
- [ ] Radians/Degrees toggle for trigonometric functions
- [ ] Memory functions (M+, M-, MR, MC)
- [ ] Haptic feedback on button presses
- [ ] Custom themes and color schemes
- [ ] Expression evaluation with proper operator precedence

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Commit your changes**: `git commit -m 'Add amazing feature'`
4. **Push to the branch**: `git push origin feature/amazing-feature`
5. **Open a Pull Request**

Please ensure your code:

- Follows Java coding conventions
- Includes appropriate comments
- Has been tested on multiple Android versions
- Doesn't break existing functionality

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

## 👨‍💻 Author

**Your Name**

- GitHub: [@katsfak](https://github.com/katsfak)

## 🙏 Acknowledgments

- Design inspiration from Samsung Calculator
- Material Design 3 guidelines by Google
- Android developer community for best practices

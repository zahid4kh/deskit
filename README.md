# Deskit

[![](https://jitpack.io/v/zahid4kh/deskit.svg)](https://jitpack.io/#zahid4kh/deskit) [![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20-blue.svg?logo=kotlin)](https://kotlinlang.org/docs/releases.html#release-details) [![Compose](https://img.shields.io/badge/Compose-1.7.3-blue.svg?logo=jetpackcompose)](https://github.com/JetBrains/compose-jb)

A library for desktop components designed for *Compose for Desktop* applications using Kotlin.

---

## Features

- 🗂️ **File System Dialogs**: Choose files, save files, and select folders
- ✅ **Confirmation Dialogs**: User-friendly confirmation prompts with customizable messages
- ℹ️ **Information Dialogs**: Clean information display with single-action acknowledgment
- 🧭 **Breadcrumb Navigation**: Intuitive path navigation with clickable segments

---

## Installation

### Option 1: If you manage repositories in `build.gradle.kts`

Your module level `build.gradle.kts` should look similar to this:

```kotlin
plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

repositories {
    maven { url = uri("https://jitpack.io") }
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenCentral()
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("com.github.zahid4kh:deskit:1.1.1")
}

kotlin {
    jvmToolchain(17)
}
```

### Option 2: If you manage repositories in `settings.gradle.kts`

1. Add the JitPack repository to your `settings.gradle.kts` file:

```kotlin
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        mavenCentral()
        google()
    }
}
```

2. Add the dependency to your `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("com.github.zahid4kh:deskit:1.1.1")
}
```

---

## Quick Start

### File Chooser Dialog

```kotlin
import dialogs.file.FileChooserDialog

@Composable
fun MyApp() {
    var showFileDialog by remember { mutableStateOf(false) }
    var selectedFile by remember { mutableStateOf<File?>(null) }

    Button(onClick = { showFileDialog = true }) {
        Text("Open File")
    }

    if (showFileDialog) {
        FileChooserDialog(
            title = "Select a File",
            allowedExtensions = listOf("txt", "pdf", "md"),
            onFileSelected = { file ->
                selectedFile = file
                showFileDialog = false
            },
            onCancel = { showFileDialog = false }
        )
    }
}
```

### File Saver Dialog

```kotlin
import dialogs.file.FileSaverDialog

@Composable
fun SaveExample() {
    var showSaveDialog by remember { mutableStateOf(false) }

    Button(onClick = { showSaveDialog = true }) {
        Text("Save File")
    }

    if (showSaveDialog) {
        FileSaverDialog(
            title = "Save Document",
            suggestedFileName = "my-document",
            extension = ".txt",
            onSave = { file ->
                file.writeText("Hello, World!")
                showSaveDialog = false
            },
            onCancel = { showSaveDialog = false }
        )
    }
}
```

### Confirmation Dialog

```kotlin
import dialogs.ConfirmationDialog

@Composable
fun ConfirmExample() {
    var showConfirmDialog by remember { mutableStateOf(false) }

    Button(onClick = { showConfirmDialog = true }) {
        Text("Delete Item")
    }

    if (showConfirmDialog) {
        ConfirmationDialog(
            title = "Confirm Deletion",
            message = "Are you sure you want to delete this item?",
            onConfirm = {
                // Perform deletion
                showConfirmDialog = false
            },
            onCancel = { showConfirmDialog = false }
        )
    }
}
```

### Info Dialog

```kotlin
import dialogs.InfoDialog

@Composable
fun InfoExample() {
    var showInfoDialog by remember { mutableStateOf(false) }

    if (showInfoDialog) {
        InfoDialog(
            title = "Success",
            message = "Your file has been saved successfully!",
            onClose = { showInfoDialog = false }
        )
    }
}
```

### Folder Chooser Dialog

```kotlin
import dialogs.file.FolderChooserDialog

@Composable
fun FolderExample() {
    var showFolderDialog by remember { mutableStateOf(false) }

    if (showFolderDialog) {
        FolderChooserDialog(
            title = "Select Export Folder",
            onFolderSelected = { folder ->
                println("Selected: ${folder.absolutePath}")
                showFolderDialog = false
            },
            onCancel = { showFolderDialog = false }
        )
    }
}
```

---

## API Documentation

For detailed documentation and examples, visit the [Deskit documentation](https://zahid4kh.github.io/deskit/).

---

## Requirements

- Kotlin 2.1.20+
- Compose Multiplatform 1.7.3+
- JVM 17+

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE.txt) file for details.
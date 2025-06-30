# Deskit

[![](https://jitpack.io/v/zahid4kh/deskit.svg)](https://jitpack.io/#zahid4kh/deskit) [![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20-blue.svg?logo=kotlin)](https://kotlinlang.org/docs/releases.html#release-details) [![Compose](https://img.shields.io/badge/Compose-1.7.3-blue.svg?logo=jetpackcompose)](https://github.com/JetBrains/compose-jb)

[Latest release 1.3.0](https://github.com/zahid4kh/deskit/wiki/1.3.0)

## Installation

Add Deskit to your Compose for Desktop project:

```kotlin
implementation("com.github.zahid4kh:deskit:1.3.0")
```

For detailed setup instructions, see the [Installation Guide](https://github.com/zahid4kh/deskit/wiki/Installation).

## Quick Start

Get started with a simple file chooser dialog:

```kotlin
@Composable
fun FileChooserExample() {
    var showFileDialog by remember { mutableStateOf(false) }
    var selectedFile by remember { mutableStateOf<File?>(null) }

    Button(onClick = { showFileDialog = true }) {
        Text("Choose File")
    }

    if (showFileDialog) {
        FileChooserDialog(
            title = "Select a Document",
            allowedExtensions = listOf("txt", "pdf", "md"),
            resizableFileInfoDialog = true, // New in 1.3.0
            onFileSelected = { file ->
                selectedFile = file
                showFileDialog = false
            },
            onCancel = { showFileDialog = false }
        )
    }
}
```

For complete examples of all dialog types, visit the [Quick Start Guide](https://github.com/zahid4kh/deskit/wiki/Quick-Start).

## Custom Content

Create rich, interactive dialogs with custom content:

```kotlin
InfoDialog(
    title = "Processing Files",
    content = {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Processing your files...")
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Text("75% Complete")
        }
    },
    onClose = { }
)
```

Learn more about creating custom dialog content in the [Custom Content Guide](https://github.com/zahid4kh/deskit/wiki/Custom-Content-in-Dialogs).

## API Documentation

For complete documentation, visit the [Documentation](https://zahid4kh.github.io/deskit/)

## Requirements

- Kotlin 2.1.20+
- Compose Multiplatform 1.7.3+
- JVM 17+

For detailed requirements and compatibility information, see [Requirements](https://github.com/zahid4kh/deskit/wiki/Installation#requirements).


## What's New in 1.3.0

- **📋 File Information System**: Comprehensive metadata display with clipboard integration
- **🖱️ Hover Info Buttons**: Smart info icons on file/folder hover
- **🎯 Enhanced User Experience**: Individual hover states and improved feedback
- **🔧 Technical Improvements**: Better performance and error handling

See the complete [v1.3.0 Release Notes](https://github.com/zahid4kh/deskit/wiki/1.3.0) for detailed changes.
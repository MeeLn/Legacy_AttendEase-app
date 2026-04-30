# AttendEase Legacy Android Guide

## Overview

**App name:** AttendEase  
**Framework:** Native Android project  
**Language mix:** Java with Gradle Kotlin DSL  
**Primary target:** Android only

This folder contains the original Android Studio implementation of AttendEase before the Flutter migration. It has been preserved for reference so the original screens, database flow, and native face-recognition logic can still be inspected while the Flutter app evolves separately.

## What Is Included

- Original Android activities for admin, teacher, and student flows
- SQLite-backed data management through `DatabaseHelper`
- Session handling with `SessionManager`
- Native face registration and recognition flow
- MobileFaceNet TensorFlow Lite model asset
- Original XML layouts, drawables, and manifest configuration

## Key Source Areas

```text
legacy_android/
  app/
    src/main/java/com/example/loginscreen/
    src/main/res/
    src/main/assets/mobilefacenet.tflite
  build.gradle.kts
  settings.gradle.kts
```

## Main Flows

### Authentication and roles

- `MainActivity` handles login
- `Register`, `StudentRegistration`, and `TeacherRegistrationActivity` handle sign-up
- `AdminDashboard`, `TeacherDashboard`, and `StudentDashboard` split behavior by role

### Admin operations

- Add departments
- Add courses
- Manage user activation
- Delete users

### Teacher operations

- Start attendance sessions
- View course attendance

### Student operations

- Register face image
- Verify face before attendance
- View attendance records

## Face Recognition Notes

The preserved native flow uses:

- CameraX for front-camera preview and frame analysis
- ML Kit face detection for locating a face
- A cropped 112x112 face image saved per student
- MobileFaceNet TensorFlow Lite embeddings
- Euclidean distance matching for recognition

## Notes

- If you want to revive the native Android app independently, the first step is to restore or remove the missing `:opencv` dependency.

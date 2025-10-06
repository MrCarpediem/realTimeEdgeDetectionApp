
# 🧪 Android + OpenCV-C++ + OpenGL + Web (RnD Intern Assessment)

## 📌 Overview
This repository contains a **time-bound technical assessment (3 Days)** focused on real-time image processing and rendering using **Android, OpenCV (C++), OpenGL ES, JNI, and TypeScript (Web)**.  
The objective was to build a minimal system to demonstrate:
- Camera feed integration on Android
- Native frame processing with OpenCV (C++)
- Rendering via OpenGL ES
- A small TypeScript-based web viewer for processed frames

---

## ⚠️ Important
- Proper Git usage has been maintained with meaningful commits.
- This repo demonstrates **practical integration skills**, not a polished production app.
- Evaluation is based on architecture, modularity, and correctness.

---

## 🔧 Tech Stack
- **Android SDK (Java/Kotlin)**
- **NDK (JNI bridge to C++)**
- **OpenCV (C++ for image processing)**
- **OpenGL ES 2.0+ (rendering)**
- **TypeScript (minimal web viewer)**
- *(Optional used)* GLSL shaders, Camera APIs

---

## 🚀 Challenge: Real-Time Edge Detection Viewer
The Android app:
- Captures camera frames (Camera2 API + TextureView)
- Sends frames to C++ layer (JNI)
- Applies **Canny Edge Detection** (OpenCV)
- Returns processed frame to OpenGL renderer
- Displays output in real-time (~15 FPS)

The Web component:
- Displays a static/dummy processed frame (sample saved from Android run)
- Shows FPS/resolution info
- Built with **TypeScript + HTML + DOM updates**

---

## 🧩 Key Features
1. 📸 **Camera Feed (Android)**
   - Live frame capture with Camera2 API
   - TextureView for rendering

2. 🔁 **OpenCV Frame Processing (C++ via JNI)**
   - Grayscale / Canny Edge Detection
   - Native C++ pipeline with JNI bridge

3. 🎨 **OpenGL Rendering**
   - Processed frame as texture
   - Smooth rendering on Android device

4. 🌐 **Web Viewer (TypeScript)**
   - Simple static image viewer
   - Overlay stats (FPS, resolution)

---

## ⚙️ Project Structure



---

## 🛠️ Setup Instructions

### Android
1. Install Android Studio + NDK
2. Sync Gradle
3. Build & Run on device

### OpenCV (JNI)
- Ensure OpenCV SDK is installed
- Link OpenCV `.so` in CMakeLists

### Web Viewer
```bash
cd web
npm install
npm run build   # compiles TypeScript to dist/




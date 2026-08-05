# 3dCNC#

3dCNC# is a modern Android application built with **Kotlin** and **Jetpack Compose**, designed to control next‑generation **CNC machines** and **3D printers** using **network‑based APIs**.
It targets modern firmware ecosystems such as **FluidNC (ESP32)** for CNC control and **Klipper + Moonraker** for 3D printing.

This project is the evolution of legacy serial‑based controllers, offering a clean, modular, API‑driven architecture with real‑time dashboards and WebSocket event streaming.

---

## 🚀 Features

### Modern Firmware Support
- **FluidNC (ESP32)** — CNC motion control, YAML configuration, WebSocket telemetry.
- **Klipper + Moonraker** — 3D printing control, temperature monitoring, job management, macro execution.

### Network‑Based Connectivity
- **Wi-Fi Local** (HTTP + WebSocket)
- **Optional Internet Remote Control** (secure WebSocket)
- No USB, Bluetooth, or serial dependencies.

### Unified API Layer
- Real‑time machine status
- Motion commands
- File upload and G‑code streaming
- Temperature and sensor monitoring
- Macro execution
- Job queue management

### Jetpack Compose UI
- CNC dashboard (jog, axes, feed, spindle, status)
- 3D printer dashboard (temperatures, extruder, job queue, macros)
- File manager and G‑code uploader
- Auto‑detection of FluidNC and Moonraker endpoints

---

## 🧱 Architecture

The project follows **Clean Architecture**, with clear separation of concerns and modular design.

3dCNC#/
├── core/
│    ├── api/
│    │     ├── fluidnc/
│    │     └── moonraker/
│    ├── network/
│    │     ├── http/
│    │     └── websocket/
│    ├── protocol/
│    │     ├── cnc/
│    │     └── printer/
│    └── detection/
│          ├── firmware/
│          └── capabilities/
├── domain/
│    ├── models/
│    └── usecases/
├── data/
│    ├── repositories/
│    └── datasources/
└── ui/
├── cnc/
├── printer/
└── common/

### Layer Responsibilities

- **core** — Networking, API clients, protocol handling, firmware detection.
- **domain** — Business logic, unified machine models, use cases.
- **data** — Repositories, data sources, API integration.
- **ui** — Jetpack Compose screens, ViewModels, state management.

---

## 🔍 Firmware Auto‑Detection

3dCNC# automatically identifies the connected firmware:

| Firmware | Method |
|---------|--------|
| FluidNC | HTTP `/status`, WebSocket handshake |
| Moonraker | HTTP `/printer/info`, WebSocket subscription |

---

## 📡 Real‑Time WebSocket Support

Both FluidNC and Moonraker provide real‑time event streams:

- CNC motion updates
- Printer temperature updates
- Job progress
- Error and alarm notifications
- Machine state changes

3dCNC# integrates these streams into reactive UI components using **StateFlow** and **Jetpack Compose**.

---

## 🎨 UI Overview

### CNC Dashboard
- Axis jog controls
- Position and feed rate
- Spindle control
- Real‑time status
- G‑code streaming

### 3D Printer Dashboard
- Temperature graphs
- Extruder control
- Job queue
- Macro execution
- File upload

---

## 📘 Goals

- Deliver a **modern, API‑driven** CNC/3D control app.
- Provide **real‑time dashboards** with WebSocket events.
- Support **FluidNC** and **Moonraker** natively.
- Maintain a **clean, scalable architecture** for long‑term evolution.

---

## 📌 Status

3dCNC# is under active development.
Initial modules include:
- Network layer
- FluidNC API
- Moonraker API
- CNC dashboard prototype

---

## 📄 License

To be defined.

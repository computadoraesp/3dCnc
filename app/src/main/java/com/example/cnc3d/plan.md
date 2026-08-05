# Industrial Mobile Application Architecture
## Complete Frontend & Backend Development Plan

> **Project Goal**
>
> Develop a modern industrial mobile application capable of controlling both **FluidNC-based CNC machines (ESP32)** and **Klipper/Moonraker-based 3D printers**, following a modular, scalable, secure, and real-time architecture.

---

# Table of Contents

- Overview
- System Architecture
- Frontend Architecture
- Backend Integration
- Communication Layer
- CNC Module
- 3D Printer Module
- Shared Modules
- Cloud Services
- Diagnostics & Analyzer
- Security
- Connectivity
- Data Models
- Development Roadmap
- Future Improvements

---

# 1. System Overview

The application is designed to support two industrial control environments:

- **CNC Machines**
  - FluidNC
  - ESP32
  - HTTP + WebSocket

- **3D Printers**
  - Klipper Firmware
  - Moonraker API
  - HTTP + WebSocket

The mobile application acts as an intelligent client, while FluidNC and Moonraker remain the machine backends.

```
                +----------------------+
                |   Android Mobile App |
                +----------+-----------+
                           |
         +-----------------+-----------------+
         |                                   |
   FluidNC Driver                    Moonraker Driver
         |                                   |
      HTTP / WS                         HTTP / WS
         |                                   |
      ESP32 CNC                     SBC + MCU Printer
```

---

# 2. Frontend Architecture

## Technology Stack

- Kotlin
- Jetpack Compose
- Material 3
- Kotlin Coroutines
- StateFlow
- ViewModel
- Navigation Compose
- Retrofit
- OkHttp
- WebSocket
- Dependency Injection (Hilt/Koin)

---

## Design Principles

- Industrial UI
- Siemens-inspired interface
- Mainsail-inspired interface
- SCADA-style layout
- Golden Ratio spacing
- Real-time updates
- Safety-first workflow
- Modular architecture
- Offline-ready communication

---

# 3. Frontend Modules

---

## 3.1 CNC Module (FluidNC)

### Machine Control

- Machine Status
- Machine State
- Homing
- Zero All Axes
- Individual Axis Zero
- Jog Controls
- Feed Hold
- Resume
- Reset Alarms
- Soft Reset

### Spindle Control

- ON/OFF
- RPM Control
- Direction
- Override

### Coolant Control

- Flood
- Mist
- Status

### Calibration

- Steps/mm Calibration
- Probe Wizard
- Tool Length Offset
- Work Coordinate System
- Base Leveling

### Machine Configuration

- Limits
- Speeds
- Acceleration
- Offsets
- Profiles

### Monitoring

- Real-time Position
- Feed Rate
- Spindle Speed
- Active G-Code
- Machine State
- Alarms
- Logs

---

## 3.2 3D Printer Module (Moonraker)

### Print Management

- Start Print
- Pause
- Resume
- Cancel
- Restart Firmware

### Motion Control

- Homing
- Move Axis
- Z Offset
- Baby Stepping

### Temperature Control

- Hotend
- Heated Bed
- Chamber
- Target Temperature

### Filament

- Load
- Unload
- Extrude
- Retract

### Fan Control

- Part Cooling Fan
- Auxiliary Fans

### Calibration

- Bed Mesh
- Probe Wizard
- Z Offset Wizard

### Monitoring

- Progress
- Remaining Time
- Layer
- Speed
- Temperatures
- Print Status
- Logs

---

## 3.3 Timelapse Module

- Camera Preview
- Capture Interval
- Storage Management
- Recording Status
- Gallery

---

## 3.4 Mesh Module

- Generate Mesh
- Visualize Mesh
- Clear Mesh
- Probe Test
- Mesh Statistics

---

## 3.5 Cloud Module

- Synchronization
- Upload Files
- Download Files
- Backup Profiles
- Cloud Status

---

## 3.6 Diagnostics Module

### Hardware

- CPU Usage
- RAM Usage
- Temperature
- Storage

### Network

- Ping
- Latency
- Packet Loss

### Machine

- Sensors
- Drivers
- Logs
- Export Report

---

## 3.7 Connectivity Module

Supported communication methods:

- Wi-Fi
- Ethernet (TCP/IP)
- Bluetooth (optional)
- USB OTG Serial

Features:

- Auto Discovery
- Connection Profiles
- Auto Reconnection
- Heartbeat
- Timeouts
- Device Selection

---

# 4. User Interface

## Industrial Layout

- Siemens-inspired top bar
- Mainsail-inspired controls
- Persistent status panel
- Emergency controls
- Industrial iconography
- Pulse animations
- Controlled visual density
- High-contrast color palette

### Navigation

- Back button on every screen
- Floating Emergency Stop
- Quick Actions
- Status Indicators

---

# 5. Backend Integration

The application does **not** implement machine firmware.

Instead, it integrates with existing industrial backends:

- FluidNC
- Moonraker

---

# 6. FluidNC Integration

## Protocols

- HTTP REST
- WebSocket

## Main Endpoints

```
/machine/status
/machine/state
/axis/jog
/axis/home
/axis/zero
/command
/spindle
/coolant
/config
/calibration
/probe
```

## WebSocket Events

- Machine State
- Axis Position
- Feed Override
- Spindle Status
- Coolant Status
- Alarms
- Real-time Coordinates

---

# 7. Moonraker Integration

## Protocols

- HTTP REST
- WebSocket

## Main Endpoints

```
/printer/info
/printer/objects/query
/printer/print/start
/printer/print/pause
/printer/print/resume
/printer/print/cancel
/printer/bed_mesh
/printer/temperature
/printer/homing
/printer/firmware_restart
/machine/system_info
```

## WebSocket Events

- Temperatures
- Progress
- Print State
- Mesh Updates
- Z Offset
- Logs
- Notifications

---

# 8. Integration Layer

## Drivers

```
FluidNCDriver
MoonrakerDriver
```

---

## Command Adapters

```
CNCCommandAdapter
PrinterCommandAdapter
```

---

## Communication Managers

```
HttpManager
WebSocketManager
ConnectionManager
HeartbeatManager
ReconnectManager
```

---

## Configuration

- Machine Profiles
- Connection Profiles
- Limits
- Overrides
- Calibration Data

---

# 9. Data Models

## CNC

```
CNCStatus
AxisState
MachineState
SpindleState
CoolantState
ProbeState
EmergencyState
```

---

## Printer

```
PrinterStatus
TemperatureState
BedMeshState
PrintJobState
FanState
FilamentState
```

---

## Shared

```
ConnectionState
LogEntry
Alarm
Notification
DeviceProfile
```

---

# 10. Security

Industrial safety mechanisms include:

- Command Validation
- Critical Action Confirmation
- Emergency Lockout
- Alarm Reset Protection
- Connection Authentication
- Timeout Detection
- Safe Reconnection
- Machine State Verification

---

# 11. Connectivity Workflow

```
App

 │

 ├── Detect Device

 │

 ├── Connect

 │

 ├── Authenticate

 │

 ├── HTTP Initialization

 │

 ├── WebSocket Subscription

 │

 ├── Real-Time Monitoring

 │

 └── Automatic Recovery
```

---

# 12. Development Roadmap

## Phase 1

- Project architecture
- Navigation
- Industrial UI
- Theme system

---

## Phase 2

FluidNC Integration

- HTTP
- WebSocket
- CNC controls
- Calibration
- Monitoring

---

## Phase 3

Moonraker Integration

- Print controls
- Temperatures
- Mesh
- Filament
- Monitoring

---

## Phase 4

Shared Features

- Cloud
- Diagnostics
- Timelapse
- Profiles

---

## Phase 5

Optimization

- Performance
- Offline mode
- Automated testing
- Security hardening

---

# 13. Future Improvements

- Multi-machine management
- User authentication
- OTA firmware updates
- MQTT support
- Industrial OPC-UA gateway
- AI-assisted diagnostics
- Predictive maintenance
- Remote monitoring
- Production statistics
- Multi-camera support
- Plugin architecture

---

# Conclusion

This document defines the complete architecture for an industrial mobile application capable of controlling **FluidNC CNC machines** and **Klipper/Moonraker 3D printers** from a single Android application.

The architecture emphasizes:

- Modular design
- Industrial-grade safety
- Real-time communication
- Scalable backend integration
- Modern Android development practices
- High-performance user experience
- Maintainability and future extensibility

The application is designed to serve as a unified industrial control platform for both CNC machining and additive manufacturing environments.

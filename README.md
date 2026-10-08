# Javions: Real-Time Aircraft Tracker

A Java/JavaFX application that decodes ADS-B messages broadcast by aircraft on 1090 MHz and shows the aircraft live on an interactive map, from the raw radio signal of a software-defined radio (AirSpy R2) all the way to the GUI.

Team project for the [CS-108 Practice of object-oriented programming](https://cs108.epfl.ch/archive/23/archive.html) course at EPFL.

## What we built

- **Signal demodulation:** turns raw radio samples into power values and detects ADS-B messages in the stream, without any external radio library.
- **Message decoding:** checks each message with a CRC-24, then parses identification, position and velocity messages. Positions are decoded with the CPR (Compact Position Reporting) algorithm.
- **Aircraft state tracking:** combines the messages from each aircraft into a live state (position, altitude, speed, heading, trajectory) and enriches it with registration and model data from an aircraft database of about 5 MB.
- **Interactive GUI (JavaFX):** an OpenStreetMap base map with pan and zoom and a local tile cache. Aircraft icons and trajectories are coloured by altitude, and a sortable table lists every aircraft in view. A status bar shows live aircraft and message counts.

## Tech stack

Java 17, JavaFX, OpenStreetMap tiles. Only the standard library and JavaFX: no third-party dependencies.

## How to run

You need a JDK 17+ and the [JavaFX SDK](https://openjfx.io/).

```bash
# Compile
javac --module-path $PATH_TO_FX --add-modules javafx.controls \
      -d out $(find src -name '*.java')

# Replay a recorded file of ADS-B messages
java --module-path $PATH_TO_FX --add-modules javafx.controls \
     -cp out:resources ch.epfl.javions.gui.Main <messages-file>
```

`$PATH_TO_FX` is the `lib` folder of the JavaFX SDK. `resources/` must be on the classpath, because it contains the aircraft database (`aircraft.zip`).

There are two input modes:
- **Recorded messages:** pass a binary message file as the first argument. It is replayed at the original timing. No recording is included in this repo.
- **Live radio:** run with no argument and pipe raw samples from an AirSpy receiver into standard input.

The interface and the code comments are in French, the language of the course.

## Repository structure

```
src/ch/epfl/javions/
├── demodulation/   # Raw samples → power → ADS-B messages
├── adsb/           # Message parsing, CPR position decoding, aircraft state accumulation
├── aircraft/       # Aircraft database lookup (registration, type, description)
├── gui/            # JavaFX map, aircraft layer, table, status bar, tile cache
└── *.java          # Shared utilities: bits, CRC-24, units, Web Mercator projection
resources/
└── aircraft.zip    # Aircraft database
```

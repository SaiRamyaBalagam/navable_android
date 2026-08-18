# NavAble Android

An Android client for indoor, accessibility-aware navigation — looks up a route from the [navable_backend](https://github.com/SaiRamyaBalagam/navable_backend) API and guides the user through it visually and by voice.

## Features

- **Route lookup & display** — pick a source and destination location within a building; `RouteActivity` renders the step-by-step path returned by the backend's Dijkstra routing.
- **Text-to-speech read-aloud** — route steps are read aloud via Android's `TextToSpeech`, so the app can be used without staring at the screen.
- **Accessible-route support** — surfaces the backend's accessibility-only routing option, so the computed path can avoid stairs and other non-accessible connections.

## Tech Stack

Java · Retrofit + OkHttp (networking) · Gson (JSON parsing) · RecyclerView · Android min SDK 26, target/compile SDK 36

## Status

Phases 1–2 (route lookup, display, and TTS read-aloud) are complete and verified end-to-end in the emulator against the backend's route API. Phase 3 (minimal/standard/precision turn-by-turn guidance modes — using per-step coordinates to compute and announce turn directions, not just a flat step list) is in progress.

## Getting Started

**Prerequisites:** Android Studio, a running instance of [navable_backend](https://github.com/SaiRamyaBalagam/navable_backend)

1. Open the project in Android Studio.
2. Update the base URL in `RetrofitClient.java` to point at your running backend instance.
3. Run on an emulator or device (min SDK 26).

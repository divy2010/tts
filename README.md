# TTS Toggle — Minecraft 1.21.11 Fabric

A Windows client-side text-to-speech mod that sends synthesized speech through Simple Voice Chat.

## Behaviour

- Type `/tts` to turn TTS mode ON or OFF.
- While ON, ordinary chat messages are intercepted locally, synthesized into speech, and are not sent as Minecraft chat messages.
- Speech is converted to 48 kHz, 16-bit, mono PCM and mixed into the Simple Voice Chat microphone stream.
- Turning TTS OFF clears queued audio and invalidates speech still being synthesized.

## Requirements

- Minecraft Java Edition 1.21.11
- Java 21
- Fabric Loader 0.18.1 or newer
- Fabric API for Minecraft 1.21.11
- Simple Voice Chat for Fabric installed on the client and server
- Windows PowerShell and a Windows speech voice available through `System.Speech`

## Build

This project uses Fabric Loom 1.14.x and should be built with Gradle 9.2.1 and JDK 21.

From the project root, run:

```bash
gradle --version
gradle clean build
```

The compiled mod is written to `build/libs/`. The GitHub Actions workflow in `.github/workflows/build.yml` installs Gradle 9.2.1 and Java 21, then uploads the built JAR as an Actions artifact.

## Important notes

- This project is Windows-specific because speech synthesis uses the Windows `System.Speech` engine. It uses a voice installed on your computer; it does not clone your personal voice or reproduce Discord's TTS bot voice.
- Keep Simple Voice Chat enabled and connected. The mod injects audio into the microphone stream; whether it is transmitted depends on the voice-chat client's microphone state and the server's voice-chat settings.
- Group membership, mute permissions, and voice-chat routing remain controlled by Simple Voice Chat and the server.
- The text is handled locally and is not sent as a Minecraft chat message. The generated audio is transmitted through the voice-chat server and can be heard or recorded by people who receive it. Voice-chat activity and related server-side metadata may still be visible to the server.
- This source has not been built or tested in this environment. Build it with the workflow or locally before using it.

## Troubleshooting

- If `/tts` is unknown, confirm that the mod and Fabric API are in the active Minecraft instance's `mods` folder.
- If speech synthesis fails, confirm that Windows PowerShell can load `System.Speech` and that a speech voice is installed.
- If nobody hears the speech, first test normal Simple Voice Chat microphone audio and check that you are connected to the intended group.

# TTS Toggle — Minecraft 1.21.11 Fabric

A client-side mod prototype for Windows + Fabric + Simple Voice Chat.

## Behaviour
- Type `/tts` once to turn TTS mode ON.
- Type a normal chat message. The original text is cancelled locally and is not sent as Minecraft chat.
- The message is synthesized using the Windows built-in `System.Speech` voice and queued as 48 kHz mono PCM.
- The voice-chat API merges the generated audio into the microphone stream.
- Type `/tts` again to turn TTS mode OFF.

## Requirements
- Minecraft Java 1.21.11
- Java 21
- Fabric Loader and Fabric API
- Simple Voice Chat for Fabric 1.21.11 installed on both client and server
- Windows PowerShell + a Windows speech voice installed

## Build
Run `gradle build` from this folder with Gradle 8.14+ and JDK 21.
The output jar will be in `build/libs/`.

## Important limitations
- This source project is Windows-specific for speech synthesis. It uses the Windows voice installed on your PC; it does not bundle or reproduce the Discord "TTS Bot" voice.
- This is a source project, not a prebuilt/tested jar. Build it first and test on a private server.
- The mod merges audio into Simple Voice Chat's microphone stream. Keep your Simple Voice Chat microphone mode enabled (voice activation or push-to-talk) while speaking. Server-side group membership, mute permissions, and voice-chat settings still control who can hear you.
- Text is processed locally for speech generation and is not submitted as a Minecraft chat message. However, your spoken audio is transmitted through the voice-chat server and can be heard/recorded by eligible listeners.
- If the voice-chat server rejects microphone audio or your mic is muted, TTS audio will not be heard.
- This does not hide the fact that voice chat is being used, nor bypass server voice-chat controls.

## Troubleshooting
- If `/tts` is unknown, check that this mod and Fabric API are in the active instance's `mods` folder.
- If speech synthesis fails, check that Windows has a speech voice installed and PowerShell can load `System.Speech`.
- If no one hears the speech, first test normal Simple Voice Chat microphone audio and confirm your group is joined.

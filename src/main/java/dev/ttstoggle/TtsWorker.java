package dev.ttstoggle;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;

final class TtsWorker {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "TTS-Toggle-Synthesis");
        t.setDaemon(true);
        return t;
    });

    private TtsWorker() {}

    static void submit(String text, long generation) {
        EXECUTOR.submit(() -> {
            if (!TtsState.isCurrentGeneration(generation)) return;
            Path wav = null;
            try {
                wav = Files.createTempFile("mc-tts-", ".wav");
                synthesizeWithWindowsSpeech(text, wav);
                try (AudioInputStream input = AudioSystem.getAudioInputStream(wav.toFile())) {
                    AudioFormat target = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED, 48000f, 16, 1, 2, 48000f, false);
                    try (AudioInputStream converted = AudioSystem.getAudioInputStream(target, input);
                         ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        byte[] buffer = new byte[8192];
                        int n;
                        while ((n = converted.read(buffer)) != -1) out.write(buffer, 0, n);
                        byte[] bytes = out.toByteArray();
                        short[] pcm = new short[bytes.length / 2];
                        for (int i = 0; i < pcm.length; i++) {
                            pcm[i] = (short) ((bytes[i * 2] & 0xff) | (bytes[i * 2 + 1] << 8));
                        }
                        TtsState.enqueuePcm(pcm, generation);
                    }
                }
            } catch (Exception e) {
                System.err.println("[TTS Toggle] Speech synthesis failed: " + e.getMessage());
            } finally {
                if (wav != null) try { Files.deleteIfExists(wav); } catch (IOException ignored) {}
            }
        });
    }

    private static void synthesizeWithWindowsSpeech(String text, Path output) throws Exception {
        // Windows built-in System.Speech voice. Text is passed through stdin rather than
        // embedded in PowerShell source, avoiding quoting/injection problems.
        String script = "$ErrorActionPreference='Stop'; " +
            "Add-Type -AssemblyName System.Speech; " +
            "$s=New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
            "$s.SetOutputToWaveFile($env:TTS_OUT); " +
            "$t=[Console]::In.ReadToEnd(); $s.Speak($t); " +
            "$s.Dispose();";
        ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive",
            "-ExecutionPolicy", "Bypass", "-Command", script);
        pb.environment().put("TTS_OUT", output.toAbsolutePath().toString());
        Process process = pb.start();
        try (OutputStream stdin = process.getOutputStream()) {
            stdin.write(text.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
        byte[] error = process.getErrorStream().readAllBytes();
        if (!process.waitFor(45, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new IOException("Windows speech timed out");
        }
        if (process.exitValue() != 0) {
            throw new IOException(new String(error, java.nio.charset.StandardCharsets.UTF_8));
        }
    }
}

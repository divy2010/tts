package dev.ttstoggle;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class TtsState {
    private static final AtomicBoolean ENABLED = new AtomicBoolean(false);
    // Simple Voice Chat normally requests 20 ms audio frames at 48 kHz.
    private static final int FRAME_SAMPLES = 960;
    private static final BlockingQueue<short[]> FRAMES = new LinkedBlockingQueue<>();

    private TtsState() {}

    public static boolean isEnabled() { return ENABLED.get(); }
    public static boolean toggle() { return ENABLED.updateAndGet(v -> !v); }

    public static void speak(String text) {
        // Keep TTS work off Minecraft's chat and voice threads.
        TtsWorker.submit(text);
    }

    static short[] nextFrame() {
        return FRAMES.poll();
    }

    static void enqueuePcm(short[] pcm) {
        if (pcm == null || pcm.length == 0) return;
        for (int offset = 0; offset < pcm.length; offset += FRAME_SAMPLES) {
            int length = Math.min(FRAME_SAMPLES, pcm.length - offset);
            short[] frame = new short[FRAME_SAMPLES];
            System.arraycopy(pcm, offset, frame, 0, length);
            FRAMES.offer(frame);
        }
    }

    static void clearAudio() {
        FRAMES.clear();
    }
}

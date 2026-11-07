package dev.ttstoggle;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public final class TtsState {
    private static final AtomicBoolean ENABLED = new AtomicBoolean(false);
    private static final AtomicLong GENERATION = new AtomicLong(0);
    private static final int FRAME_SAMPLES = 960;
    private static final int MAX_QUEUED_FRAMES = 1_000; // about 20 seconds
    private static final BlockingQueue<short[]> FRAMES = new ArrayBlockingQueue<>(MAX_QUEUED_FRAMES);

    private TtsState() {}

    public static boolean isEnabled() { return ENABLED.get(); }

    public static boolean toggle() {
        boolean enabled = ENABLED.updateAndGet(value -> !value);
        GENERATION.incrementAndGet();
        if (!enabled) clearAudio();
        return enabled;
    }

    public static void speak(String text) {
        if (!isEnabled() || text == null || text.isBlank()) return;
        TtsWorker.submit(text, GENERATION.get());
    }

    static boolean isCurrentGeneration(long generation) {
        return ENABLED.get() && GENERATION.get() == generation;
    }

    static short[] nextFrame() { return FRAMES.poll(); }

    static void enqueuePcm(short[] pcm, long generation) {
        if (!isCurrentGeneration(generation) || pcm == null || pcm.length == 0) return;
        for (int offset = 0; offset < pcm.length; offset += FRAME_SAMPLES) {
            if (!isCurrentGeneration(generation)) return;
            int length = Math.min(FRAME_SAMPLES, pcm.length - offset);
            short[] frame = new short[FRAME_SAMPLES];
            System.arraycopy(pcm, offset, frame, 0, length);
            if (!FRAMES.offer(frame)) {
                System.err.println("[TTS Toggle] Audio queue full; remaining speech was dropped.");
                return;
            }
        }
    }

    static void clearAudio() { FRAMES.clear(); }
}

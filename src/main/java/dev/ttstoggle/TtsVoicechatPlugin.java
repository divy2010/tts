package dev.ttstoggle;

import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MergeClientSoundEvent;

public final class TtsVoicechatPlugin implements VoicechatPlugin {
    @Override
    public String getPluginId() {
        return "ttstoggle";
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MergeClientSoundEvent.class, event -> {
            if (!TtsState.isEnabled()) return;
            short[] frame = TtsState.nextFrame();
            if (frame != null) {
                // Mix generated speech into the microphone stream.
                // Simple Voice Chat routes the resulting audio using its normal rules.
                event.mergeAudio(frame);
            }
        });
    }
}

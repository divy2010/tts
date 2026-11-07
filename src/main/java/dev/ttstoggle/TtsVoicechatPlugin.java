package dev.ttstoggle;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.MergeClientSoundEvent;

public final class TtsVoicechatPlugin implements VoicechatPlugin {
    @Override
    public String getPluginId() {
        return "ttstoggle";
    }

    @Override
    public void initialize(VoicechatApi api) {
        api.getEventRegistration().registerEvent(MergeClientSoundEvent.class, event -> {
            if (!TtsState.isEnabled()) return;
            short[] frame = TtsState.nextFrame();
            if (frame != null) {
                // Mix generated speech into the normal microphone stream.
                // Simple Voice Chat then applies its normal group/proximity routing.
                event.mergeAudio(frame);
            }
        });
    }
}

package dev.ttstoggle;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public final class TtsToggleClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(literal("tts").executes(context -> {
                boolean enabled = TtsState.toggle();
                if (context.getSource().getClient().player != null) {
                    context.getSource().getClient().player.sendMessage(
                        Text.literal("TTS mode " + (enabled ? "ON" : "OFF")), true);
                }
                return 1;
            }))
        );

        // This event runs before a normal player chat message is sent.
        // Returning false prevents the original text from being sent to the server.
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (!TtsState.isEnabled()) return true;
            String clean = message == null ? "" : message.trim();
            if (!clean.isEmpty()) {
                TtsState.speak(clean);
            }
            return false;
        });
    }
}

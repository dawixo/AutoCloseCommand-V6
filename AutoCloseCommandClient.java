package com.dawixo.autoclosecommand;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** Client-side automation for Minecraft 26.3 (Fabric). */
public final class AutoCloseCommandClient implements ClientModInitializer {
    private static final String MOD_ID = "autoclosecommand";

    // 54 server/container slots + 36 player inventory slots = 90 total.
    private static final int TARGET_MENU_SLOT_COUNT = 90;
    private static final int TICKS_PER_SECOND = 20;

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(MOD_ID, "main")
    );

    // F8: original AutoCloseCommand + /autoclicker automation.
    private static final KeyMapping TOGGLE_KEY = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.autoclosecommand.toggle",
                    InputConstants.Type.KEYBOARD,
                    InputConstants.KEY_F8,
                    CATEGORY
            )
    );

    // F7: independent AutoSell toggle. It does not change the F8 automation.
    private static final KeyMapping TOGGLE_AUTOSELL_KEY = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.autoclosecommand.toggle_sellall",
                    InputConstants.Type.KEYBOARD,
                    InputConstants.KEY_F7,
                    CATEGORY
            )
    );

    // Original behavior: enabled automatically when the mod is loaded.
    private static boolean enabled = true;
    private static Screen lastHandledScreen = null;
    private static int pendingCommandTicks = -1;

    // Counts only while a player is connected. AutoSell is disabled by default.
    private static long autoSellElapsedTicks = 0L;

    @Override
    public void onInitializeClient() {
        AutoCloseCommandConfig.load();
        ClientTickEvents.END_CLIENT_TICK.register(AutoCloseCommandClient::onClientTick);
    }

    private static void onClientTick(Minecraft client) {
        // F8 switches only the original GUI + /autoclicker automation.
        while (TOGGLE_KEY.consumeClick()) {
            enabled = !enabled;
            if (!enabled) {
                pendingCommandTicks = -1;
            }
            sendStatus(client, "AutoCloseCommand " + (enabled ? "WŁĄCZONY" : "WYŁĄCZONY"));
        }

        // F7 switches only AutoSell. The setting is saved between game launches.
        while (TOGGLE_AUTOSELL_KEY.consumeClick()) {
            boolean newState = !AutoCloseCommandConfig.isAutoSellEnabled();
            AutoCloseCommandConfig.setAutoSellEnabled(newState);
            autoSellElapsedTicks = 0L;
            sendStatus(client, "AutoSell /sellall " + (newState ? "WŁĄCZONY" : "WYŁĄCZONY")
                    + " (co " + AutoCloseCommandConfig.getAutoSellIntervalSeconds() + " s)");
        }

        if (client.player == null) {
            lastHandledScreen = null;
            pendingCommandTicks = -1;
            autoSellElapsedTicks = 0L;
            return;
        }

        // AutoSell is deliberately independent of the F8 automation and its
        // pending command. It waits a full configured interval before first send.
        if (AutoCloseCommandConfig.isAutoSellEnabled()) {
            autoSellElapsedTicks++;
            long intervalTicks = (long) AutoCloseCommandConfig.getAutoSellIntervalSeconds() * TICKS_PER_SECOND;
            if (autoSellElapsedTicks >= intervalTicks) {
                client.player.connection.sendCommand("sellall");
                autoSellElapsedTicks = 0L;
            }
        } else {
            autoSellElapsedTicks = 0L;
        }

        // Preserve original two-tick delay after closing a matching container.
        if (pendingCommandTicks >= 0) {
            if (!enabled) {
                pendingCommandTicks = -1;
                return;
            }
            pendingCommandTicks--;
            if (pendingCommandTicks < 0) {
                client.player.connection.sendCommand("autoclicker");
            }
            return;
        }

        if (!enabled) {
            return;
        }

        Screen screen = client.gui.screen();
        if (!(screen instanceof AbstractContainerScreen<?>)) {
            lastHandledScreen = null;
            return;
        }
        AbstractContainerScreen<?> containerScreen = (AbstractContainerScreen<?>) screen;

        // Target only a separate server container with exactly 90 total slots.
        boolean targetScreen = containerScreen.getMenu() != client.player.inventoryMenu
                && containerScreen.getMenu().slots.size() == TARGET_MENU_SLOT_COUNT;

        if (!targetScreen) {
            lastHandledScreen = null;
            return;
        }

        if (screen == lastHandledScreen) {
            return;
        }

        // Mark before closing, so one GUI opening can trigger only once.
        lastHandledScreen = screen;
        containerScreen.onClose();
        pendingCommandTicks = 2;
    }

    private static void sendStatus(Minecraft client, String message) {
        if (client.player != null) {
            client.player.sendSystemMessage(Component.literal("[AutoCloseCommand] " + message));
        }
    }
}

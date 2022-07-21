package dev.kejona.crossplatforms.spigot.common.handler;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.spigot.common.SpigotBase;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

public class SpigotPlayer implements FormPlayer {

    private final Player handle;

    public SpigotPlayer(@Nonnull Player handle) {
        this.handle = Objects.requireNonNull(handle);
    }

    @Override
    public UUID getUuid() {
        return handle.getUniqueId();
    }

    @Override
    public String getName() {
        return handle.getName();
    }

    @Override
    public boolean hasPermission(String permission) {
        if (!handle.getServer().isPrimaryThread()) {
            Logger.get().warn("Permission check off main thread, instead: " + Thread.currentThread());
            Thread.dumpStack();
        }
        return handle.hasPermission(permission);
    }

    @Override
    public void sendRaw(Component component) {
        handle.sendMessage(SpigotBase.LEGACY_SERIALIZER.serialize(component));
    }

    @Override
    public Object getHandle() {
        return handle;
    }
}

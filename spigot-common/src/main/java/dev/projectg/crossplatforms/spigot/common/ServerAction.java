package dev.projectg.crossplatforms.spigot.common;

import com.google.inject.Inject;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.action.SimpleAction;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class ServerAction extends SimpleAction<String> {

    public static JavaPlugin SENDER = null;

    public static final String IDENTIFIER = "server";

    @Inject
    private transient PlaceholderHandler placeholders;

    public ServerAction(String value) {
        super(IDENTIFIER, value);
    }

    @Inject
    private ServerAction() {
        this("");
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders) {
        String resolved = placeholders.setPlaceholders(player, value(), additionalPlaceholders);

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(stream)) {
            Logger.getLogger().debug("Attempting to send " + player.getName() + " to BungeeCord server " + value());
            out.writeUTF("Connect");
            out.writeUTF(resolved);
            ((Player) player.getHandle()).sendPluginMessage(SENDER, "BungeeCord", stream.toByteArray());
        } catch (IOException e) {
            Logger.getLogger().severe("Failed to send a plugin message to BungeeCord!");
            e.printStackTrace();
        }
    }
}

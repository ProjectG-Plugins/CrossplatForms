package dev.kejona.crossplatforms.proxy;

import dev.kejona.crossplatforms.action.Action;
import dev.kejona.crossplatforms.action.ActionSerializer;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.java.JavaMenu;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.providers.ProtocolizePlayerProvider;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;

@ConfigSerializable
public class CloseMenuAction implements Action<JavaMenu> {

    private static final String TYPE = "close";
    private static final ProtocolizePlayerProvider PLAYER_PROVIDER = Protocolize.playerProvider();

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Resolver resolver, @Nonnull JavaMenu menu) {
        PLAYER_PROVIDER.player(player.getUuid()).closeInventory();
    }

    @Override
    public String type() {
        return TYPE;
    }

    public static void register(ActionSerializer serializer) {
        serializer.register(TYPE, CloseMenuAction.class);
    }
}

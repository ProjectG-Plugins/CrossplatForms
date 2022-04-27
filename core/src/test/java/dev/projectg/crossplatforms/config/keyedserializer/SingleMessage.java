package dev.projectg.crossplatforms.config.keyedserializer;

import com.google.inject.Inject;
import dev.projectg.crossplatforms.serialize.SimpleType;
import org.jetbrains.annotations.NotNull;

public class SingleMessage extends SimpleType<String> implements Message {

    public static final String IDENTIFIER = "message";

    @Inject
    public SingleMessage(@NotNull String value) {
        super(IDENTIFIER, value);
    }

    @Override
    public void send() {
        System.out.println(value());
    }
}

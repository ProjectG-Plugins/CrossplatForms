package dev.kejona.crossplatforms;

import javax.annotation.Nullable;

public class IllegalValueException extends Exception {
    private static final long serialVersionUID = 0L;

    private final String value;
    private final String expectedType;
    private final String identifier;

    public IllegalValueException(@Nullable String value, String expectedType, String identifier) {
        super("Expected a " + expectedType + " for '" + identifier + "' which the following could not be converted to: " + value);
        this.value = value;
        this.expectedType = expectedType;
        this.identifier = identifier;
    }

    public String value() {
        return value;
    }

    public String expectedType() {
        return expectedType;
    }

    public String identifier() {
        return identifier;
    }
}

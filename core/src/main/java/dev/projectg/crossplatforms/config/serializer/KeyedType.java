package dev.projectg.crossplatforms.config.serializer;

/**
 * For use with {@link KeyedTypeSerializer}
 */
public interface KeyedType {

    /**
     * @return The identifier for the type of this implementation
     */
    String identifier();

    /**
     * @return An object serializable by Configurate that represents this implementation, which should be successfully
     * deserialized back to an equivalent instance of this implementation if necessary.
     */
    Object value();
}
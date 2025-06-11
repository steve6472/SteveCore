package steve6472.core.registry;

import com.mojang.serialization.Codec;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
public class NamespacedObjectRegistry<T extends Keyable> extends ObjectRegistry<T>
{
    private final String defaultNamespace;
    private final Codec<Key> keyCodec;

    public NamespacedObjectRegistry(String defaultNamespace, Key key, T defaultValue)
    {
        super(key, defaultValue);
        this.defaultNamespace = defaultNamespace;
        keyCodec = Key.withDefaultNamespace(defaultNamespace);
    }

    public NamespacedObjectRegistry(String defaultNamespace, Key key)
    {
        this(defaultNamespace, key, null);
    }

    @Override
    public Codec<Key> keyCodec()
    {
        return keyCodec;
    }

    public String getDefaultNamespace()
    {
        return defaultNamespace;
    }
}

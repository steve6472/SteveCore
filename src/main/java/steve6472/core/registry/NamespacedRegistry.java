package steve6472.core.registry;

import com.mojang.serialization.Codec;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
public class NamespacedRegistry<T extends Keyable & Serializable<?>> extends Registry<T>
{
    private final String defaultNamespace;
    private final Codec<Key> keyCodec;

    public NamespacedRegistry(String defaultNamespace, Key key)
    {
        super(key);
        this.defaultNamespace = defaultNamespace;
        keyCodec = Key.withDefaultNamespace(defaultNamespace);
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

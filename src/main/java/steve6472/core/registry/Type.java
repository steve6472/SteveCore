package steve6472.core.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
public abstract class Type<T> implements Keyable, Serializable<T>
{
    private final Key key;
    private final MapCodec<T> codec;

    public Type(Key key, MapCodec<T> codec)
    {
        this.key = key;
        this.codec = codec;
    }

    @Override
    public Key key()
    {
        return key;
    }

    @Override
    public Codec<T> codec()
    {
        return mapCodec().codec();
    }

    public MapCodec<T> mapCodec()
    {
        return codec;
    }
}

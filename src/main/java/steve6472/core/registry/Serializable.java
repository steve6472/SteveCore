package steve6472.core.registry;

import com.mojang.serialization.Codec;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
public interface Serializable<T>
{
    Codec<T> codec();
}

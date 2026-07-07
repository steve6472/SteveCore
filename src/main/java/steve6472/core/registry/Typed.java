package steve6472.core.registry;

import com.mojang.serialization.MapCodec;

/**
 * Created by steve6472
 * Date: 5/8/2026
 * Project: SteveCore <br>
 *
 */
public interface Typed<T>
{
    MapCodec<? extends T> codec();
}

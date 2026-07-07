package steve6472.core.module;

import steve6472.core.registry.Registry;
import steve6472.core.registry.ResourceKey;

/**
 * Created by steve6472
 * Date: 7/14/2025
 * Project: SteveCore <br>
 */
public record ModulePart<T>(ResourceKey<Registry<T>> registry, String name, String path)
{
}

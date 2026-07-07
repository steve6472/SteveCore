package steve6472.core.registry;

import com.google.common.collect.MapMaker;
import org.jetbrains.annotations.VisibleForTesting;
import steve6472.core.SteveCore;

import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve6472
 * Date: 5/5/2026
 * Project: SteveCore <br>
 *
 */
public class ResourceKey<T>
{
    private static final ConcurrentMap<Identifier, ResourceKey<?>> CACHE = new MapMaker().weakValues().makeMap();
    private final Key registry;
    private final Key resource;

    private ResourceKey(Key registry, Key resource)
    {
        this.registry = registry;
        this.resource = resource;
    }

    public static <T> ResourceKey<T> create(ResourceKey<? extends Registry<T>> registry, Key key)
    {
        return create(registry.resource(), key);
    }

    public static <T> ResourceKey<T> create(Key registry, Key resource)
    {
        //noinspection unchecked
        return (ResourceKey<T>) CACHE.computeIfAbsent(new Identifier(registry, resource), _ -> new ResourceKey<>(registry, resource));
    }

    public static <T> ResourceKey<Registry<T>> createRegistryKey(Key key)
    {
        return create(SteveCore.ROOT_MASTER_REGISTRY, key);
    }

    public Key registry()
    {
        return registry;
    }

    public Key resource()
    {
        return resource;
    }

    @VisibleForTesting
    public static void clearCache()
    {
        CACHE.clear();
    }

    private record Identifier(Key registry, Key resource) {}

    @Override
    public String toString()
    {
        return "ResourceKey{" + "registry=" + registry + ", resource=" + resource + '}';
    }
}

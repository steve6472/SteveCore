package steve6472.core.registry;

import com.mojang.datafixers.util.Either;
import steve6472.core.log.Log;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 5/16/2026
 * Project: SteveCore <br>
 *
 */
public class RegistryGroup
{
    private static final Logger LOGGER = Log.getLogger(RegistryGroup.class);

    private final Registry<Registry<?>> registries;
    private final Map<Key, Either<Runnable, Consumer<Object>>> loaders;
    private final boolean dynamic;

    public RegistryGroup(Key key, boolean dynamic)
    {
        this.registries = new SimpleRegistry<>(ResourceKey.createRegistryKey(key));
        this.loaders = new LinkedHashMap<>();
        this.dynamic = dynamic;
    }

    @SuppressWarnings("unchecked")
    public ResourceKey<Registry<?>> key()
    {
        return (ResourceKey<Registry<?>>) (Object) registries.key();
    }

    public Registry<Registry<?>> registry()
    {
        return registries;
    }

    public void bootstrap()
    {
        if (isRootFrozen() && !dynamic)
            throw new IllegalStateException("Tried to bootstrap non-dynamic registries multiple times");

        LOGGER.finer("Loading " + registries.key().resource() + ", dynamic " + dynamic);
        freezeRoot();
        load(dynamic, null);
        freeze();
    }

    public <T> void bootstrap(T extraParams)
    {
        if (isRootFrozen() && !dynamic)
            throw new IllegalStateException("Tried to bootstrap non-dynamic registries multiple times");

        LOGGER.finer("Loading " + registries.key().resource() + ", dynamic " + dynamic);
        freezeRoot();
        load(dynamic, extraParams);
        freeze();
    }

    private void freeze()
    {
        for (Registry<?> registry : registries)
        {
            registry.freeze();
        }
    }

    private void freezeRoot()
    {
        registries.freeze();
    }

    private boolean isRootFrozen()
    {
        return registries.isFrozen();
    }

    private void load(boolean dynamic, Object extraParams)
    {
        for (Registry<?> registry : registries)
        {
            if (dynamic)
            {
                if (registry instanceof ReloadableRegistry<?> reloadableRegistry)
                {
                    reloadableRegistry.reload();
                } else
                {
                    throw new IllegalStateException("uhh reload no reloadable or dynamic");
                }
            }

            try
            {
                if (extraParams == null)
                {
                    loaders.get(registry.key().resource()).left().orElseThrow().run();
                } else
                {
                    loaders.get(registry.key().resource()).right().orElseThrow().accept(extraParams);
                }
            } catch (Exception exception)
            {
                LOGGER.severe("Exception thrown when loading registry '%s'".formatted(registry.key().resource()));
                throw exception;
            }
        }
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    public <T> void add(Registry<T> registry, ResourceKey<? extends Registry<T>> name, RegistryCore.RegistryBootstrap<T> loader)
    {
        registries.register((ResourceKey<Registry<?>>) (Object) name, registry);
        loaders.put(name.resource(), Either.left(() -> loader.run(registry)));
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    public <T, E> void add(Registry<T> registry, ResourceKey<? extends Registry<T>> name, RegistryCore.RegistryBootstrapExtra<T, E> loader)
    {
        registries.register((ResourceKey<Registry<?>>) (Object) name, registry);
        loaders.put(name.resource(), Either.right((extra) -> loader.run(registry, (E) extra)));
    }
}

package steve6472.core.registry;

import com.mojang.serialization.Codec;
import steve6472.core.SteveCore;
import steve6472.core.log.Log;
import steve6472.core.module.ModuleManager;
import steve6472.core.module.ModulePart;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 5/5/2026
 * Project: SteveCore <br>
 * Order of loading should be: loadSettings, bootstrapStatic, loadDynamic
 */
public class RegistryCore
{
    private static final Logger LOGGER = Log.getLogger(RegistryCore.class);
    public static final Registry<Registry<?>> ROOT_MASTER = new SimpleRegistry<>(ResourceKey.createRegistryKey(SteveCore.ROOT_MASTER_REGISTRY));

    public static final RegistryGroup SETTINGS;
    public static final RegistryGroup STATIC;
    public static final RegistryGroup DYNAMIC;

    static
    {
        SETTINGS = createGroup(SteveCore.ROOT_SETTINGS_REGISTRY, true);
        STATIC = createGroup(SteveCore.ROOT_STATIC_REGISTRY, false);
        DYNAMIC = createGroup(SteveCore.ROOT_DYNAMIC_REGISTRY, true);
    }

    public static RegistryGroup createGroup(Key key, boolean dynamic)
    {
        RegistryGroup group = new RegistryGroup(key, dynamic);
        ROOT_MASTER.register(group.key(), group.registry());
        return group;
    }

    public static void freezeRoot()
    {
        ROOT_MASTER.freeze();
    }

    public static <T> Registry<T> createRegistry(ResourceKey<? extends Registry<T>> name, RegistryBootstrap<T> loader)
    {
        return registerStaticRegistry(SimpleRegistry::new, name, loader);
    }

    public static <T> Registry<T> createSettingsRegistry(ResourceKey<? extends Registry<T>> name, RegistryBootstrap<T> loader)
    {
        return registerSettingsRegistry(ReloadableRegistry::new, name, loader);
    }

    public static <T> Registry<T> createDynamicRegistry(ResourceKey<? extends Registry<T>> name, RegistryBootstrap<T> loader)
    {
        return registerDynamicRegistry(ReloadableRegistry::new, name, loader);
    }

    public static <T> Registry<T> createDynamicRegistry(ModulePart<T> part, Codec<T> codec, Supplier<ModuleManager> moduleManager)
    {
        return registerDynamicRegistry(ReloadableRegistry::new, part.registry(), partLoader(part, codec, moduleManager));
    }

    public static <T> Registry<T> createDynamicPersistentRegistry(ResourceKey<? extends Registry<T>> name, RegistryBootstrap<T> loader)
    {
        return registerDynamicRegistry(ReloadablePersistentRegistry::new, name, loader);
    }

    public static <T> Registry<T> registerSettingsRegistry(Function<ResourceKey<? extends Registry<T>>, ? extends Registry<T>> constructor, ResourceKey<? extends Registry<T>> name, RegistryBootstrap<T> loader)
    {
        return registerRegistry(SETTINGS, constructor.apply(name), name, loader);
    }

    public static <T> Registry<T> registerStaticRegistry(Function<ResourceKey<? extends Registry<T>>, ? extends Registry<T>> constructor, ResourceKey<? extends Registry<T>> name, RegistryBootstrap<T> loader)
    {
        return registerRegistry(STATIC, constructor.apply(name), name, loader);
    }

    public static <T> Registry<T> registerDynamicRegistry(Function<ResourceKey<? extends Registry<T>>, ? extends Registry<T>> constructor, ResourceKey<? extends Registry<T>> name, RegistryBootstrap<T> loader)
    {
        return registerRegistry(DYNAMIC, constructor.apply(name), name, loader);
    }

    public static <T> Registry<T> registerRegistry(RegistryGroup group, Function<ResourceKey<? extends Registry<T>>, ? extends Registry<T>> constructor, ResourceKey<? extends Registry<T>> name, RegistryBootstrap<T> loader)
    {
        return registerRegistry(group, constructor.apply(name), name, loader);
    }

    public static <T, E> Registry<T> registerRegistry(RegistryGroup group, Function<ResourceKey<? extends Registry<T>>, ? extends Registry<T>> constructor, ResourceKey<? extends Registry<T>> name, RegistryBootstrapExtra<T, E> loader)
    {
        return registerRegistry(group, constructor.apply(name), name, loader);
    }

    private static <T> Registry<T> registerRegistry(RegistryGroup group, Registry<T> registry, ResourceKey<? extends Registry<T>> name, RegistryBootstrap<T> loader)
    {
        //noinspection unchecked,RedundantCast
        ROOT_MASTER.register((ResourceKey<Registry<?>>) ((Object) name), registry);
        group.add(registry, name, loader);
        LOGGER.finer("Registered registry " + name);
        return registry;
    }

    private static <T, E> Registry<T> registerRegistry(RegistryGroup group, Registry<T> registry, ResourceKey<? extends Registry<T>> name, RegistryBootstrapExtra<T, E> loader)
    {
        //noinspection unchecked,RedundantCast
        ROOT_MASTER.register((ResourceKey<Registry<?>>) ((Object) name), registry);
        group.add(registry, name, loader);
        LOGGER.finer("Registered registry " + name);
        return registry;
    }

    public static <T> RegistryBootstrap<T> partLoader(ModulePart<T> part, Codec<T> codec, Supplier<ModuleManager> moduleManager)
    {
        return (registry) -> moduleManager.get().loadParts(part, codec, (value, key) -> registry.register(ResourceKey.create(registry.key().resource(), key), value));
    }

    @FunctionalInterface
    public interface RegistryBootstrap<T>
    {
        void run(Registry<T> registry);
    }

    @FunctionalInterface
    public interface RegistryBootstrapExtra<T, E>
    {
        void run(Registry<T> registry, E extraParams);
    }
}

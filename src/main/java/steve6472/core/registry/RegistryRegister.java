package steve6472.core.registry;

import steve6472.core.log.Log;
import steve6472.core.util.Preconditions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: Flare <br>
 */
public abstract class RegistryRegister
{
    private static final Logger LOGGER = Log.getLogger(RegistryRegister.class);
    protected static Map<Key, Runnable> LOADERS = new LinkedHashMap<>();

    protected static String NAMESPACE;

    /// This method simply ensures that the fields in a static class are loaded.
    public static void init(@SuppressWarnings("unused") Registry<?> dummyRegistry) { }
    /// This method simply ensures that the fields in a static class are loaded.
    public static void init(@SuppressWarnings("unused") ObjectRegistry<?> dummyRegistry) { }

    private static void checkValidity()
    {
        Preconditions.checkNotNull(NAMESPACE, "Create a static block and assign a String to NAMESPACE");
    }

    /*
     * Creators
     */

    protected static <T extends Keyable & Serializable<?>> Registry<T> createRegistry(String id, Runnable bootstrap)
    {
        checkValidity();
        Key key = Key.withNamespace(NAMESPACE, id);
        LOGGER.finest("Creating Registry '" + key + "'");
        LOADERS.put(key, bootstrap);
        return new Registry<>(key);
    }

    protected static <T extends Keyable & Serializable<?>> Registry<T> createRegistry(String id, Supplier<T> bootstrap)
    {
        return createRegistry(id, (Runnable) bootstrap::get);
    }

    protected static <T extends Keyable & Serializable<?>> Registry<T> createRegistry(Key key, Runnable bootstrap)
    {
        LOGGER.finest("Creating Registry '" + key + "'");
        LOADERS.put(key, bootstrap);
        return new Registry<>(key);
    }

    protected static <T extends Keyable & Serializable<?>> Registry<T> createRegistry(Key key, Supplier<T> bootstrap)
    {
        return createRegistry(key, (Runnable) bootstrap::get);
    }

    protected static <T extends Keyable> ObjectRegistry<T> createObjectRegistry(String id, Runnable bootstrap)
    {
        checkValidity();
        Key key = Key.withNamespace(NAMESPACE, id);
        LOGGER.finest("Creating Object Registry '" + key + "'");
        LOADERS.put(key, bootstrap);
        return new ObjectRegistry<>(key);
    }

    protected static <T extends Keyable> ObjectRegistry<T> createObjectRegistry(String id, Supplier<T> bootstrap)
    {
        return createObjectRegistry(id, (Runnable) bootstrap::get);
    }

    protected static <T extends Keyable> ObjectRegistry<T> createObjectRegistry(Key key, Runnable bootstrap)
    {
        LOGGER.finest("Creating Object Registry '" + key + "'");
        LOADERS.put(key, bootstrap);
        return new ObjectRegistry<>(key);
    }

    protected static <T extends Keyable> ObjectRegistry<T> createObjectRegistry(Key key, Supplier<T> bootstrap)
    {
        return createObjectRegistry(key, (Runnable) bootstrap::get);
    }

    protected static <T extends Keyable> ObjectRegistry<T> createObjectRegistry(String id, T defaultValue, Runnable bootstrap)
    {
        checkValidity();
        Key key = Key.withNamespace(NAMESPACE, id);
        LOGGER.finest("Creating Object Registry '" + key + "'");
        LOADERS.put(key, bootstrap);
        return new ObjectRegistry<>(key, defaultValue);
    }

    protected static <T extends Keyable> ObjectRegistry<T> createObjectRegistry(String id, T defaultValue, Supplier<T> bootstrap)
    {
        return createObjectRegistry(id, defaultValue, (Runnable) bootstrap::get);
    }

    protected static <T extends Keyable> ObjectRegistry<T> createObjectRegistry(Key key, T defaultValue, Runnable bootstrap)
    {
        LOGGER.finest("Creating Object Registry '" + key + "'");
        LOADERS.put(key, bootstrap);
        return new ObjectRegistry<>(key, defaultValue);
    }

    protected static <T extends Keyable> ObjectRegistry<T> createObjectRegistry(Key key, T defaultValue, Supplier<T> bootstrap)
    {
        return createObjectRegistry(key, defaultValue, (Runnable) bootstrap::get);
    }

    /*
     * Loading
     */

    public static void createContents()
    {
        LOGGER.finest("Creating content");
        LOADERS.forEach((key, loader) -> {
            LOGGER.finest("Bootstrapping '" + key + "'");
            loader.run();
        });
    }
}

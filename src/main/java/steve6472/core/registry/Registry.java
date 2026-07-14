package steve6472.core.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import org.jetbrains.annotations.VisibleForTesting;
import steve6472.core.log.Log;
import steve6472.core.registry.Holder.Reference;

import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Created by steve6472
 * Date: 5/5/2026
 * Project: SteveCore <br>
 *
 */
public interface Registry<T> extends Iterable<T>
{
    Logger LOGGER = Log.getLogger(Registry.class);
    Reference<T> register(ResourceKey<T> key, T value);

    Optional<Reference<T>> getAny();
    Optional<Reference<T>> get(T value);
    Optional<Reference<T>> get(Key key);
    Optional<Reference<T>> get(ResourceKey<T> key);
    /// This method is intended to be used from Holder codecs
    Optional<Reference<T>> getOrCreateReference(ResourceKey<T> key);

    Stream<Reference<T>> listElements();
    int countElements();

    boolean isFrozen();

    void freeze();

    ResourceKey<? extends Registry<T>> key();
    String defaultNamespace();

    default Codec<T> byKeyCodec()
    {
        return referenceHolder().flatComapMap(Reference::value, value -> this.safeCastToReference(this.wrapAsHolder(value)));
    }

    default Codec<Holder<T>> holderByNameCodec()
    {
        return referenceHolder().flatComapMap(holder -> holder, this::safeCastToReference);
    }

    private Codec<Reference<T>> referenceHolder()
    {
        return Key
            .makeCodec(defaultNamespace())
            .comapFlatMap(name -> this
                .get(name)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(() -> "Unknown registry key in " + this.key() + ": " + name)),
                holder -> holder.key().resource());
    }

    private DataResult<Reference<T>> safeCastToReference(final Holder<T> holder)
    {
        if (holder instanceof Reference<T> reference)
            return DataResult.success(reference);

        return DataResult.error(() -> "Unregistered holder in " + this.key() + ": " + holder);
    }

    Holder<T> wrapAsHolder(T value);

    @VisibleForTesting
    void _clear();

    /*
     * Register, return value
     */

    static <T> T register(Registry<T> registry, String name, T value)
    {
        return register(registry, ResourceKey.create(registry.key(), Key.withNamespace(registry.defaultNamespace(), name)), value);
    }

    static <T> T register(Registry<T> registry, Key key, T value)
    {
        return register(registry, ResourceKey.create(registry.key(), key), value);
    }

    static <T> T register(Registry<T> registry, ResourceKey<T> key, T value)
    {
        registry.register(key, value);
        return value;
    }

    /*
     * Register, return holder
     */

    static <T> Reference<T> registerForHolder(Registry<T> registry, String name, T value)
    {
        return registerForHolder(registry, ResourceKey.create(registry.key(), Key.withNamespace(registry.defaultNamespace(), name)), value);
    }

    static <T> Reference<T> registerForHolder(Registry<T> registry, Key key, T value)
    {
        return registerForHolder(registry, ResourceKey.create(registry.key(), key), value);
    }

    static <T> Reference<T> registerForHolder(Registry<T> registry, ResourceKey<T> key, T value)
    {
        return registry.register(key, value);
    }
}

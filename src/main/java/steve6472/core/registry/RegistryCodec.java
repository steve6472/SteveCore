package steve6472.core.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;

import java.util.Optional;

/**
 * Created by steve6472
 * Date: 5/6/2026
 * Project: SteveCore <br>
 *
 */
public class RegistryCodec<E> implements Codec<Holder<E>>
{
    private final ResourceKey<? extends Registry<E>> registryKey;

    public static <E> RegistryCodec<E> create(final ResourceKey<? extends Registry<E>> registryKey)
    {
        return new RegistryCodec<>(registryKey);
    }

    private RegistryCodec(final ResourceKey<? extends Registry<E>> registryKey)
    {
        this.registryKey = registryKey;
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    public <T> DataResult<T> encode(final Holder<E> input, final DynamicOps<T> ops, final T prefix)
    {
        Optional<Holder.Reference<Registry<?>>> registryReferenceOpt = RegistryCore.ROOT_MASTER.get((ResourceKey<Registry<?>>) ((Object) registryKey));
        if (registryReferenceOpt.isEmpty())
            return DataResult.error(() -> "Unknown registry '%s'".formatted(registryKey));

        Holder.Reference<Registry<?>> registryReference = registryReferenceOpt.get();
        Registry<?> registry = registryReference.value();

        if (!input.isOfRegistry((Registry<E>) registry))
        {
            return DataResult.error(() -> "Element '%s' is not valid for current registry".formatted(input));
        }

        if (input instanceof Holder.Reference<E> ref)
        {
            return Key.makeCodec(registry.defaultNamespace()).encode(ref.key().resource(), ops, prefix);
        } else
        {
            return DataResult.error(() -> "Elements from registry '%s' can not be serialized to a value ??".formatted(registryKey));
        }
    }

    @SuppressWarnings({"unchecked", "RedundantCast", "rawtypes"})
    public <T> DataResult<Pair<Holder<E>, T>> decode(final DynamicOps<T> ops, final T input)
    {
        Optional<Holder.Reference<Registry<?>>> registryReferenceOpt = RegistryCore.ROOT_MASTER.get((ResourceKey<Registry<?>>) ((Object) registryKey));
        if (registryReferenceOpt.isEmpty())
            return DataResult.error(() -> "Unknown registry '%s'".formatted(registryKey));

        Holder.Reference<Registry<?>> registryReference = registryReferenceOpt.get();
        Registry<E> registry = (Registry<E>) registryReference.value();

        return Key.makeCodec(registry.defaultNamespace()).decode(ops, input).flatMap(pair ->
        {
            Key id = pair.getFirst();
            ResourceKey<E> resourceKey = ResourceKey.create(this.registryKey, id);

            return ((DataResult) registry
                .getOrCreateReference(resourceKey)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(() -> "Failed to get element " + id)))
                .map(h -> Pair.of(h, pair.getSecond()))
                .setLifecycle(Lifecycle.stable());
        });
    }

    public String toString()
    {
        return "RegistryCodec[" + this.registryKey + "]";
    }
}

package steve6472.core.registry;

import com.google.common.collect.Iterators;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by steve6472
 * Date: 5/5/2026
 * Project: SteveCore <br>
 *
 */
public class SimpleRegistry<T> implements Registry<T>
{
    private final ResourceKey<? extends Registry<T>> key;

    protected final List<Holder.Reference<T>> byId = new ArrayList<>(32);
    protected final Map<T, Holder.Reference<T>> byValue = new HashMap<>();
    protected final Map<Key, Holder.Reference<T>> byKey = new HashMap<>();
    protected final Map<ResourceKey<T>, Holder.Reference<T>> byResourceKey = new HashMap<>();

    protected boolean isFrozen;

    public SimpleRegistry(ResourceKey<? extends Registry<T>> key)
    {
        this.key = key;
    }

    @Override
    public Holder.Reference<T> register(ResourceKey<T> key, T value)
    {
        if (isFrozen)
            throw new IllegalStateException("Registry '%s' is frozen, trying to add key '%s'".formatted(key(), key));
        if (byKey.containsKey(key.resource()))
            throw new IllegalStateException("Duplicate key '%s' for registry '%s'".formatted(key, key()));
        if (byValue.containsKey(value))
            throw new IllegalStateException("Duplicate Value '%s' for registry '%s'".formatted(value, key()));

        Holder.Reference<T> reference = byResourceKey.computeIfAbsent(key, k -> Holder.Reference.create(this, k));
        byResourceKey.put(key, reference);
        byValue.put(value, reference);
        byKey.put(key.resource(), reference);
        byId.add(reference);

        return reference;
    }

    @Override
    public Optional<Holder.Reference<T>> getAny()
    {
        return byId.isEmpty() ? Optional.empty() : Optional.of(byId.getFirst());
    }

    @Override
    public Optional<Holder.Reference<T>> get(T value)
    {
        return Optional.ofNullable(byValue.get(value));
    }

    @Override
    public Optional<Holder.Reference<T>> get(Key key)
    {
        return Optional.ofNullable(byKey.get(key));
    }

    @Override
    public Optional<Holder.Reference<T>> get(ResourceKey<T> key)
    {
        return Optional.ofNullable(byResourceKey.get(key));
    }

    @Override
    public Optional<Holder.Reference<T>> getOrCreateReference(ResourceKey<T> key)
    {
        return Optional.of(byResourceKey.computeIfAbsent(key, k -> Holder.Reference.create(this, k)));
    }

    @Override
    public Stream<Holder.Reference<T>> listElements()
    {
        return byId.stream();
    }

    @Override
    public int countElements()
    {
        return byId.size();
    }

    @Override
    public boolean isFrozen()
    {
        return isFrozen;
    }

    @Override
    public Holder<T> wrapAsHolder(final T value)
    {
        Holder.Reference<T> existingHolder = this.byValue.get(value);
        return existingHolder != null ? existingHolder : Holder.direct(value);
    }

    @Override
    public void freeze()
    {
        if (isFrozen())
            return;

        LOGGER.finest("Freezig registry " + key());

        isFrozen = true;
        byValue.forEach((value, reference) -> reference.bindValue(value));
        validateResourceKeys();
    }

    protected void validateResourceKeys()
    {
        Map<ResourceKey<T>, Holder.Reference<T>> unbound = new LinkedHashMap<>();

        byResourceKey.forEach((key, value) ->
        {
            if (!value.isBound())
                unbound.put(key, value);
        });

        if (unbound.isEmpty())
            return;

        LOGGER.severe("Found extra reference in non-persistent registry '%s'. Possibly wrong references in data!".formatted(key()));
        unbound.forEach((key, value) -> LOGGER.severe(" - " + key + " -> " + value));
    }

    @Override
    public ResourceKey<? extends Registry<T>> key()
    {
        return key;
    }

    @Override
    public String defaultNamespace()
    {
        return key.resource().namespace();
    }

    @Override
    public void _clear()
    {
        byId.clear();
        byValue.clear();
        byKey.clear();
        byResourceKey.clear();
        isFrozen = false;
    }

    @Override
    public @NotNull Iterator<T> iterator()
    {
        return Iterators.transform(byId.iterator(), Holder::value);
    }

    @Override
    public String toString()
    {
        return "Registry[" + "key=" + key + ']';
    }
}

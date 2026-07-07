package steve6472.core.registry;

import org.jetbrains.annotations.Nullable;

/**
 * Created by steve6472
 * Date: 5/5/2026
 * Project: SteveCore <br>
 *
 */
public interface Holder<T>
{
    boolean isBound();
    T value();
    boolean isOfRegistry(Registry<T> registry);

    static <T> Holder<T> direct(T value)
    {
        return new Direct<>(value);
    }

    record Direct<T>(T value) implements Holder<T>
    {
        @Override
        public boolean isBound()
        {
            return true;
        }

        @Override
        public boolean isOfRegistry(Registry<T> registry)
        {
            return true;
        }
    }

    final class Reference<T> implements Holder<T>
    {
        private final Registry<T> owner;
        private final ResourceKey<T> key;
        private @Nullable T value;

        private Reference(Registry<T> owner, ResourceKey<T> key, @Nullable T value)
        {
            this.owner = owner;
            this.value = value;
            this.key = key;
        }

        public static <T> Reference<T> create(Registry<T> owner, ResourceKey<T> key)
        {
            return new Reference<>(owner, key, null);
        }

        void bindValue(T value)
        {
            this.value = value;
        }

        void unbind()
        {
            this.value = null;
        }

        @Override
        public boolean isBound()
        {
            return value != null;
        }

        public ResourceKey<T> key()
        {
            return key;
        }

        @Override
        public T value()
        {
            if (value == null)
                throw new IllegalStateException("Access to unbound value '%s' from registry '%s'".formatted(key, owner));
            return value;
        }

        @Override
        public boolean isOfRegistry(Registry<T> registry)
        {
            return owner == registry;
        }

        @Override
        public String toString()
        {
            return "Reference{" + "key=" + key + ", value=" + value + '}';
        }
    }
}

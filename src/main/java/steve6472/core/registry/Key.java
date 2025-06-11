package steve6472.core.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import steve6472.core.SteveCore;
import steve6472.core.util.Preconditions;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
public class Key
{
    @Deprecated
    public static final Codec<Key> CODEC = Codec.STRING.flatXmap(string -> DataResult.success(parse(string)), id -> DataResult.success(id.toString()));

    public static Codec<Key> withDefaultNamespace(String defaultNamespace)
    {
        return Codec.STRING.flatXmap(string -> DataResult.success(parse(defaultNamespace, string)), id -> DataResult.success(id.toString()));
    }

    private static final Pattern ID_MATCH = Pattern.compile("[a-z0-9_/]*");

    private final String namespace;
    private final String id;

    private Key(String namespace, String id)
    {
        Preconditions.matchPattern(ID_MATCH, namespace, () -> "Namespace contains illegal characters, allowed: [a-z0-9_/]*   (" + namespace + ")");
        Preconditions.matchPattern(ID_MATCH, id, () -> "ID contains illegal characters, allowed: [a-z0-9_/]*   (" + id + ")");

        this.namespace = namespace;
        this.id = id;
    }

    public static Key withNamespace(String namespace, String id)
    {
        return new Key(namespace, id);
    }

    @Deprecated
    public static Key defaultNamespace(String id)
    {
        return new Key(SteveCore.DEFAULT_KEY_NAMESPACE, id);
    }

    public static Key parse(String string)
    {
        if (!string.contains(":"))
        {
            return defaultNamespace(string);
        } else
        {
            String[] split = string.split(":");
            return withNamespace(split[0], split[1]);
        }
    }

    public static Key parse(String defaultNamespace, String string)
    {
        if (!string.contains(":"))
        {
            return withNamespace(defaultNamespace, string);
        } else
        {
            String[] split = string.split(":");
            return withNamespace(split[0], split[1]);
        }
    }

    public String namespace()
    {
        return namespace;
    }

    public String id()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return namespace + ":" + id;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        Key key = (Key) object;
        return Objects.equals(namespace, key.namespace) && Objects.equals(id, key.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(namespace, id);
    }
}

package steve6472.core.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import org.joml.*;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.core.registry.ObjectRegistry;

import java.lang.Math;
import java.util.*;
import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
public class ExtraCodecs
{
    public static final Codec<Vector2f> VEC_2F = Codec.FLOAT.listOf().flatXmap(list -> {
        if (list.size() != 2)
            return DataResult.error(() -> "List size incorrect, has to be 2");
        return DataResult.success(new Vector2f(list.get(0), list.get(1)));
    }, vec2 -> DataResult.success(List.of(vec2.x, vec2.y)));

    public static final Codec<Vector2i> VEC_2I = Codec.INT.listOf().flatXmap(list -> {
        if (list.size() != 2)
            return DataResult.error(() -> "List size incorrect, has to be 2");
        return DataResult.success(new Vector2i(list.get(0), list.get(1)));
    }, vec2 -> DataResult.success(List.of(vec2.x, vec2.y)));

    public static final Codec<Vector3f> VEC_3F = Codec.FLOAT.listOf().flatXmap(list -> {
        if (list.size() != 3)
            return DataResult.error(() -> "List size incorrect, has to be 3");
        return DataResult.success(new Vector3f(list.get(0), list.get(1), list.get(2)));
    }, vec3 -> DataResult.success(List.of(vec3.x, vec3.y, vec3.z)));

    public static final Codec<Vector4f> VEC_4F = Codec.FLOAT.listOf().flatXmap(list -> {
        if (list.size() != 4)
            return DataResult.error(() -> "List size incorrect, has to be 4");
        return DataResult.success(new Vector4f(list.get(0), list.get(1), list.get(2), list.get(3)));
    }, vec4 -> DataResult.success(List.of(vec4.x, vec4.y, vec4.z, vec4.w)));

    public static final Codec<Vector4i> VEC_4I = Codec.INT.listOf().flatXmap(list -> {
        if (list.size() != 4)
            return DataResult.error(() -> "List size incorrect, has to be 4");
        return DataResult.success(new Vector4i(list.get(0), list.get(1), list.get(2), list.get(3)));
    }, vec4 -> DataResult.success(List.of(vec4.x, vec4.y, vec4.z, vec4.w)));

    public static final Codec<Vector3d> VEC_3D = Codec.DOUBLE.listOf().flatXmap(list -> {
        if (list.size() != 3)
            return DataResult.error(() -> "List size incorrect, has to be 3");
        return DataResult.success(new Vector3d(list.get(0), list.get(1), list.get(2)));
    }, vec3 -> DataResult.success(List.of(vec3.x, vec3.y, vec3.z)));

    public static final Codec<UUID> UUID = Codec.STRING.xmap(java.util.UUID::fromString, java.util.UUID::toString);

    public static <T extends Keyable> Codec<T> keyedFromRegistry(ObjectRegistry<T> registry)
    {
        return Key.CODEC.flatXmap(key -> {
            T obj = registry.get(key);
            if (obj == null)
                return DataResult.error(() -> "Could not get '" + key + "' from registry '" + registry.getRegistryKey() + "'");
            else
                return DataResult.success(obj);
        }, t -> DataResult.success(t.key()));
    }

    public static final Codec<Map<String, Key>> MAP_STRING_KEY = ExtraCodecs.mapListCodec(Codec.STRING, Key.CODEC);

    public static <K, V> Codec<Map<K, V>> mapListCodec(Codec<K> key, Codec<V> value)
    {
        return Codec.compoundList(key, value).flatXmap(
            list -> {
                Map<K, V> map = new HashMap<>(Math.min(6, list.size()));
                for (Pair<K, V> faceTypeFacePair : list)
                {
                    V put = map.put(faceTypeFacePair.getFirst(), faceTypeFacePair.getSecond());
                    if (put != null)
                    {
                        return DataResult.error(() -> "Two same values for " + faceTypeFacePair.getFirst());
                    }
                }
                return DataResult.success(map);
            },
            map -> {
                List<Pair<K, V>> list = new ArrayList<>(map.size());
                map.forEach((k, v) -> list.add(new Pair<>(k, v)));
                return DataResult.success(list);
            }
        );
    }
}

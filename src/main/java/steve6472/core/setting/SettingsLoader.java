package steve6472.core.setting;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import steve6472.core.registry.Key;
import steve6472.core.registry.ObjectRegistry;
import steve6472.core.registry.Serializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SettingsLoader
{
    public static <T> T save(DynamicOps<T> dynamicOps, ObjectRegistry<Setting<?, ?>> registry)
    {
        Map theMap = new HashMap<>();

        for (Key key : registry.keys())
        {
            Setting<?, ?> setting = registry.get(key);
            theMap.put(setting, setting);
        }

        return (T) Codec.dispatchedMap(registry.byKeyCodec(), Serializable::codec).encodeStart(dynamicOps, theMap).getOrThrow();
    }

    public static <T> void load(T object, DynamicOps<T> dynamicOps, ObjectRegistry<Setting<?, ?>> registry)
    {
        Map<Setting<?, ?>, ?> map = Codec
            .dispatchedMap(registry.byKeyCodec(), Serializable::codec)
            .decode(dynamicOps, object)
            .getOrThrow()
            .getFirst();

        map.forEach((k, v) -> {
            Setting<?, Object> setting = (Setting<?, Object>) registry.get(k.key());
            Setting<?, ?> v1 = (Setting<?, ?>) v;
            Object obj = v1.get();
            setting.set(obj);
        });
    }

    public static JsonObject saveToJson(ObjectRegistry<Setting<?, ?>> registry)
    {
        return save(JsonOps.INSTANCE, registry).getAsJsonObject();
    }

    public static void loadFromJson(JsonObject json, ObjectRegistry<Setting<?, ?>> registry)
    {
        load(json, JsonOps.INSTANCE, registry);
    }
}

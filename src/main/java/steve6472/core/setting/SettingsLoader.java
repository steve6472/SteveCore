package steve6472.core.setting;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import steve6472.core.registry.*;
import steve6472.core.util.GsonUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SettingsLoader
{
    public static <T> T save(DynamicOps<T> dynamicOps, ResourceKey<Registry<Setting<?, ?>>> registryKey)
    {
        Map theMap = new HashMap<>();

        Optional<Holder.Reference<Registry<?>>> registryReference = RegistryCore.SETTINGS.registry().get((ResourceKey<Registry<?>>) ((Object) registryKey));
        Registry<Setting<?, ?>> registry = (Registry<Setting<?, ?>>) registryReference.orElseThrow().value();

        registry.listElements().forEach(ref -> {
            Setting<?, ?> setting = ref.value();
            theMap.put(setting, setting);
        });

        return (T) Codec.dispatchedMap(registry.byKeyCodec(), Serializable::codec).encodeStart(dynamicOps, theMap).getOrThrow();
    }

    public static <T> void load(T object, DynamicOps<T> dynamicOps, ResourceKey<Registry<Setting<?, ?>>> registryKey)
    {
        Optional<Holder.Reference<Registry<?>>> registryReference = RegistryCore.SETTINGS.registry().get((ResourceKey<Registry<?>>) ((Object) registryKey));
        Registry<Setting<?, ?>> registry = (Registry<Setting<?, ?>>) registryReference.orElseThrow().value();

        Map<Setting<?, ?>, ?> map = Codec
            .dispatchedMap(registry.byKeyCodec(), Serializable::codec)
            .decode(dynamicOps, object)
            .getPartialOrThrow()
            .getFirst();

        map.forEach((k, v) -> {
            Setting<?, Object> setting = (Setting<?, Object>) registry.get(k.key()).orElseThrow().value();
            Setting<?, ?> v1 = (Setting<?, ?>) v;
            Object obj = v1.get();
            setting.set(obj);
        });
    }

    public static JsonObject saveToJson(ResourceKey<Registry<Setting<?, ?>>> registryKey)
    {
        return save(JsonOps.INSTANCE, registryKey).getAsJsonObject();
    }

    public static void loadFromJson(JsonObject json, ResourceKey<Registry<Setting<?, ?>>> registryKey)
    {
        load(json, JsonOps.INSTANCE, registryKey);
    }

    public static void saveToJsonFile(ResourceKey<Registry<Setting<?, ?>>> registryKey, File file)
    {
        GsonUtil.saveJson(save(JsonOps.INSTANCE, registryKey).getAsJsonObject(), file);
    }

    /// Does not load if file does not exist -> settings use default values
    public static void loadFromJsonFile(ResourceKey<Registry<Setting<?, ?>>> registryKey, File file)
    {
        if (file.exists())
            load(GsonUtil.loadJson(file), JsonOps.INSTANCE, registryKey);
    }
}

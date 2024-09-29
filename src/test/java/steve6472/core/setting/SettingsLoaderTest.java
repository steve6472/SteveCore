package steve6472.core.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;
import steve6472.core.registry.Key;
import steve6472.core.registry.ObjectRegistry;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
class SettingsLoaderTest
{
    @Test
    void save()
    {
        // TODO: mock registry
        ObjectRegistry<Setting<?, ?>> settingRegistry = new ObjectRegistry<>(Key.defaultNamespace("setting"));
        IntSetting obj = new IntSetting(42);
        Key settingKey = Key.defaultNamespace("test_int");
        obj.key = settingKey;
        settingRegistry.register(settingKey, obj);

        JsonElement actual = SettingsLoader.save(JsonOps.INSTANCE, settingRegistry);
        JsonObject expected = new JsonObject();
        expected.addProperty("core:test_int", 42);
        assertEquals(expected, actual);
    }

    @Test
    void load()
    {
        // TODO: mock registry
        ObjectRegistry<Setting<?, ?>> settingRegistry = new ObjectRegistry<>(Key.defaultNamespace("setting"));
        IntSetting obj = new IntSetting(0);
        Key settingKey = Key.defaultNamespace("test_int");
        obj.key = settingKey;
        settingRegistry.register(settingKey, obj);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("core:test_int", 42);

        SettingsLoader.load(jsonObject, JsonOps.INSTANCE, settingRegistry);

        assertEquals(42, obj.get());
    }
}
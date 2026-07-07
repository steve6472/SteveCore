package steve6472.core.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import steve6472.core.registry.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
class SettingsTest
{
    static final ResourceKey<Registry<Setting<?, ?>>> SETTINGS = ResourceKey.createRegistryKey(Key.withNamespace("test", "test_settings"));

    static Registry<Setting<?, ?>> SETTINGS_REGISTRY;
    static IntSetting[] TEST_INT = new IntSetting[1];

    @BeforeAll
    static void setup()
    {
        SETTINGS_REGISTRY = RegistryCore.createSettingsRegistry(SETTINGS, reg -> TEST_INT[0] = Setting.registerInt(reg, "test_int", 42));

        RegistryCore.SETTINGS.bootstrap();
    }

    @Test
    void save()
    {
        JsonElement actual = SettingsLoader.save(JsonOps.INSTANCE, SETTINGS);
        JsonObject expected = new JsonObject();
        expected.addProperty("test:test_int", 42);
        assertEquals(expected, actual);
    }

    @Test
    void load()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("test:test_int", 42);

        SettingsLoader.load(jsonObject, JsonOps.INSTANCE, SETTINGS);

        assertEquals(42, TEST_INT[0].get());
    }

    @Test
    void testExtraSetting()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("test:test_int", 42);
        jsonObject.addProperty("test:nonexisting_setting", true);

        assertDoesNotThrow(() -> SettingsLoader.load(jsonObject, JsonOps.INSTANCE, SETTINGS));

        assertEquals(42, TEST_INT[0].get());
    }
}
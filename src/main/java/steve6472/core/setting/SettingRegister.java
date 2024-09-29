package steve6472.core.setting;

import steve6472.core.registry.Key;
import steve6472.core.registry.ObjectRegistry;
import steve6472.core.registry.StringValue;
import steve6472.core.util.Preconditions;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
public class SettingRegister
{
    protected static ObjectRegistry<Setting<?, ?>> REGISTRY;

    private static void checkRegistry()
    {
        Preconditions.checkNotNull(REGISTRY, "Create a static block and assign an ObjectRegistry<Setting<?, ?>> to REGISTRY");
    }

    /*
     *
     */

    protected static IntSetting registerInt(String id, int defaultValue)
    {
        checkRegistry();
        var obj = new IntSetting(defaultValue);
        obj.key = Key.defaultNamespace(id);
        REGISTRY.register(obj);
        return obj;
    }

    protected static StringSetting registerString(String id, String defaultValue)
    {
        checkRegistry();
        var obj = new StringSetting(defaultValue);
        obj.key = Key.defaultNamespace(id);
        REGISTRY.register(obj);
        return obj;
    }

    protected static BoolSetting registerBool(String id, boolean defaultValue)
    {
        checkRegistry();
        var obj = new BoolSetting(defaultValue);
        obj.key = Key.defaultNamespace(id);
        REGISTRY.register(obj);
        return obj;
    }

    protected static FloatSetting registerFloat(String id, float defaultValue)
    {
        checkRegistry();
        var obj = new FloatSetting(defaultValue);
        obj.key = Key.defaultNamespace(id);
        REGISTRY.register(obj);
        return obj;
    }

    protected static <E extends Enum<E> & StringValue> EnumSetting<E> registerEnum(String id, E defaultValue)
    {
        checkRegistry();
        var obj = new EnumSetting<>(defaultValue);
        obj.key = Key.defaultNamespace(id);
        REGISTRY.register(obj);
        return obj;
    }

}

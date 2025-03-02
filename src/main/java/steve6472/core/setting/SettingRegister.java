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
public abstract class SettingRegister
{
    /*
     * This class is horrible.
     * REGISTRY and NAMESPACE will be replaced each time a new child class is loaded.
     * It works, but you will no longer be allowed to access these two objects.
     * But you should not do that anyways.
     */

    protected static ObjectRegistry<Setting<?, ?>> REGISTRY;
    protected static String NAMESPACE;

    private static void checkValidity()
    {
        Preconditions.checkNotNull(REGISTRY, "Create a static block and assign an ObjectRegistry<Setting<?, ?>> to REGISTRY");
        Preconditions.checkNotNull(NAMESPACE, "Create a static block and assign a String to NAMESPACE");
    }

    /*
     *
     */

    protected static IntSetting registerInt(Key key, int defaultValue)
    {
        checkValidity();
        var obj = new IntSetting(defaultValue);
        obj.key = key;
        REGISTRY.register(obj);
        return obj;
    }

    protected static IntSetting registerInt(String id, int defaultValue)
    {
        checkValidity();
        var obj = new IntSetting(defaultValue);
        obj.key = Key.withNamespace(NAMESPACE, id);
        REGISTRY.register(obj);
        return obj;
    }

    protected static StringSetting registerString(Key key, String defaultValue)
    {
        checkValidity();
        var obj = new StringSetting(defaultValue);
        obj.key = key;
        REGISTRY.register(obj);
        return obj;
    }

    protected static StringSetting registerString(String id, String defaultValue)
    {
        checkValidity();
        var obj = new StringSetting(defaultValue);
        obj.key = Key.withNamespace(NAMESPACE, id);
        REGISTRY.register(obj);
        return obj;
    }

    protected static BoolSetting registerBool(Key key, boolean defaultValue)
    {
        checkValidity();
        var obj = new BoolSetting(defaultValue);
        obj.key = key;
        REGISTRY.register(obj);
        return obj;
    }

    protected static BoolSetting registerBool(String id, boolean defaultValue)
    {
        checkValidity();
        var obj = new BoolSetting(defaultValue);
        obj.key = Key.withNamespace(NAMESPACE, id);
        REGISTRY.register(obj);
        return obj;
    }

    protected static FloatSetting registerFloat(Key key, float defaultValue)
    {
        checkValidity();
        var obj = new FloatSetting(defaultValue);
        obj.key = key;
        REGISTRY.register(obj);
        return obj;
    }

    protected static FloatSetting registerFloat(String id, float defaultValue)
    {
        checkValidity();
        var obj = new FloatSetting(defaultValue);
        obj.key = Key.withNamespace(NAMESPACE, id);
        REGISTRY.register(obj);
        return obj;
    }

    protected static <E extends Enum<E> & StringValue> EnumSetting<E> registerEnum(Key key, E defaultValue)
    {
        checkValidity();
        var obj = new EnumSetting<>(defaultValue);
        obj.key = key;
        REGISTRY.register(obj);
        return obj;
    }

    protected static <E extends Enum<E> & StringValue> EnumSetting<E> registerEnum(String id, E defaultValue)
    {
        checkValidity();
        var obj = new EnumSetting<>(defaultValue);
        obj.key = Key.withNamespace(NAMESPACE, id);
        REGISTRY.register(obj);
        return obj;
    }

}

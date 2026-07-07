package steve6472.core.setting;

import steve6472.core.registry.*;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
public abstract class Setting<SELF, T> implements Serializable<SELF>, Keyable
{
    Key key;

    @Override
    public Key key()
    {
        return key;
    }

    public abstract T get();

    public abstract void set(T value);

    /*
     * Register methods
     */

    public static IntSetting registerInt(Registry<Setting<?, ?>> registry, Key key, int defaultValue)
    {
        var obj = new IntSetting(defaultValue);
        obj.key = key;
        Registry.register(registry, key, obj);
        return obj;
    }

    public static IntSetting registerInt(Registry<Setting<?, ?>> registry, String id, int defaultValue)
    {
        var obj = new IntSetting(defaultValue);
        obj.key = Key.withNamespace(registry.defaultNamespace(), id);
        Registry.register(registry, obj.key, obj);
        return obj;
    }

    public static StringSetting registerString(Registry<Setting<?, ?>> registry, Key key, String defaultValue)
    {
        var obj = new StringSetting(defaultValue);
        obj.key = key;
        Registry.register(registry, key, obj);
        return obj;
    }

    public static StringSetting registerString(Registry<Setting<?, ?>> registry, String id, String defaultValue)
    {
        var obj = new StringSetting(defaultValue);
        obj.key = Key.withNamespace(registry.defaultNamespace(), id);
        Registry.register(registry, obj.key, obj);
        return obj;
    }

    public static BoolSetting registerBool(Registry<Setting<?, ?>> registry, Key key, boolean defaultValue)
    {
        var obj = new BoolSetting(defaultValue);
        obj.key = key;
        Registry.register(registry, key, obj);
        return obj;
    }

    public static BoolSetting registerBool(Registry<Setting<?, ?>> registry, String id, boolean defaultValue)
    {
        var obj = new BoolSetting(defaultValue);
        obj.key = Key.withNamespace(registry.defaultNamespace(), id);
        Registry.register(registry, obj.key, obj);
        return obj;
    }

    public static FloatSetting registerFloat(Registry<Setting<?, ?>> registry, Key key, float defaultValue)
    {
        var obj = new FloatSetting(defaultValue);
        obj.key = key;
        Registry.register(registry, key, obj);
        return obj;
    }

    public static FloatSetting registerFloat(Registry<Setting<?, ?>> registry, String id, float defaultValue)
    {
        var obj = new FloatSetting(defaultValue);
        obj.key = Key.withNamespace(registry.defaultNamespace(), id);
        Registry.register(registry, obj.key, obj);
        return obj;
    }

    public static <E extends Enum<E> & StringValue> EnumSetting<E> registerEnum(Registry<Setting<?, ?>> registry, Key key, E defaultValue)
    {
        var obj = new EnumSetting<>(defaultValue);
        obj.key = key;
        Registry.register(registry, key, obj);
        return obj;
    }

    public static <E extends Enum<E> & StringValue> EnumSetting<E> registerEnum(Registry<Setting<?, ?>> registry, String id, E defaultValue)
    {
        var obj = new EnumSetting<>(defaultValue);
        obj.key = Key.withNamespace(registry.defaultNamespace(), id);
        Registry.register(registry, obj.key, obj);
        return obj;
    }
}

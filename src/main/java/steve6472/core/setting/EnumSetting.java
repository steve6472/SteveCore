package steve6472.core.setting;

import com.mojang.serialization.Codec;
import steve6472.core.registry.StringValue;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
public class EnumSetting<T extends Enum<T> & StringValue> extends Setting<EnumSetting<T>, T>
{
    protected final T defaultValue;
    protected T currentValue;

    EnumSetting(T defaultValue, T currentValue)
    {
        this.defaultValue = defaultValue;
        this.currentValue = currentValue;
    }

    EnumSetting(T defaultValue)
    {
        this(defaultValue, defaultValue);
    }

    @Override
    public T get()
    {
        return currentValue;
    }

    @Override
    public void set(T value)
    {
        currentValue = value;
    }

    @Override
    public Codec<EnumSetting<T>> codec()
    {
        T[] enumConstants = defaultValue.getDeclaringClass().getEnumConstants();
        Codec<T> valueCodec = StringValue.fromValues(() -> enumConstants);
        return valueCodec.xmap(a -> new EnumSetting<>(defaultValue, a), EnumSetting::get);
    }

    @Override
    public String toString()
    {
        return "EnumSetting{" + "defaultValue=" + defaultValue + ", currentValue=" + currentValue + ", key=" + key + '}';
    }
}

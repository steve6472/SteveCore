package steve6472.core.setting;

import com.mojang.serialization.Codec;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
public class FloatSetting extends PrimitiveSetting<Float, FloatSetting>
{
    FloatSetting(float defaultValue, float currentValue)
    {
        super(defaultValue, currentValue);
    }

    FloatSetting(float defaultValue)
    {
        super(defaultValue);
    }

    @Override
    public Codec<FloatSetting> codec()
    {
        return Codec.FLOAT.xmap(s -> new FloatSetting(defaultValue, s), s -> s.currentValue);
    }
}

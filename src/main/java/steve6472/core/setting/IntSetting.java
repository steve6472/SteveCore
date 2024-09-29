package steve6472.core.setting;

import com.mojang.serialization.Codec;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
public class IntSetting extends PrimitiveSetting<Integer, IntSetting>
{
    IntSetting(int defaultValue, int currentValue)
    {
        super(defaultValue, currentValue);
    }

    IntSetting(int defaultValue)
    {
        super(defaultValue);
    }

    @Override
    public Codec<IntSetting> codec()
    {
        return Codec.INT.xmap(s -> new IntSetting(defaultValue, s), s -> s.currentValue);
    }
}

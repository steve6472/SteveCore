package steve6472.core.setting;

import com.mojang.serialization.Codec;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
public class BoolSetting extends PrimitiveSetting<Boolean, BoolSetting>
{
    BoolSetting(boolean defaultValue, boolean currentValue)
    {
        super(defaultValue, currentValue);
    }

    BoolSetting(boolean defaultValue)
    {
        super(defaultValue);
    }

    @Override
    public Codec<BoolSetting> codec()
    {
        return Codec.BOOL.xmap(s -> new BoolSetting(defaultValue, s), s -> s.currentValue);
    }
}

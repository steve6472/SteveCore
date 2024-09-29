package steve6472.core.setting;

import com.mojang.serialization.Codec;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
public class StringSetting extends PrimitiveSetting<String, StringSetting>
{
    StringSetting(String defaultValue, String currentValue)
    {
        super(defaultValue, currentValue);
    }

    StringSetting(String defaultValue)
    {
        super(defaultValue);
    }

    @Override
    public Codec<StringSetting> codec()
    {
        return Codec.STRING.xmap(s -> new StringSetting(defaultValue, s), s -> s.currentValue);
    }
}

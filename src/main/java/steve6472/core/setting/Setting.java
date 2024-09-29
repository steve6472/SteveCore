package steve6472.core.setting;

import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.core.registry.Serializable;

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
}

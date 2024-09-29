package steve6472.core.setting;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
abstract class PrimitiveSetting<V, SELF> extends Setting<SELF, V>
{
    protected final V defaultValue;
    protected V currentValue;

    PrimitiveSetting(V defaultValue, V currentValue)
    {
        this.defaultValue = defaultValue;
        this.currentValue = currentValue;
    }

    PrimitiveSetting(V defaultValue)
    {
        this(defaultValue, defaultValue);
    }

    @Override
    public V get()
    {
        return currentValue;
    }

    @Override
    public void set(V value)
    {
        currentValue = value;
    }

    @Override
    public String toString()
    {
        return "PrimitiveSetting{" + "defaultValue=" + defaultValue + ", currentValue=" + currentValue + ", key=" + key + '}';
    }
}

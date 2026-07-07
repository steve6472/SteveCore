package steve6472.core.registry;

/**
 * Created by steve6472
 * Date: 5/5/2026
 * Project: SteveCore <br>
 *
 */
public class ReloadablePersistentRegistry<T> extends ReloadableRegistry<T>
{
    public ReloadablePersistentRegistry(ResourceKey<? extends Registry<T>> key)
    {
        super(key);
    }

    @Override
    public void freeze()
    {
        if (isFrozen())
            return;

        isFrozen = true;
        byValue.forEach((value, reference) -> reference.bindValue(value));
    }
}

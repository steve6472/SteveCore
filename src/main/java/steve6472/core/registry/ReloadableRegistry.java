package steve6472.core.registry;

/**
 * Created by steve6472
 * Date: 5/5/2026
 * Project: SteveCore <br>
 *
 */
public class ReloadableRegistry<T> extends SimpleRegistry<T>
{
    public ReloadableRegistry(ResourceKey<? extends Registry<T>> key)
    {
        super(key);
    }

    @Override
    public void freeze()
    {
        super.freeze();
        byResourceKey.entrySet().removeIf(entry -> !entry.getValue().isBound());
    }

    public void reload()
    {
        isFrozen = false;

        listElements().forEach(Holder.Reference::unbind);
        byValue.clear();
        byKey.clear();
        byId.clear();
    }
}

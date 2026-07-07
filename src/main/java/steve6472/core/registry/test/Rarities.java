package steve6472.core.registry.test;

import org.joml.Vector3f;
import steve6472.core.registry.Holder;
import steve6472.core.registry.Registry;

/**
 * Created by steve6472
 * Date: 7/4/2026
 * Project: SteveCore <br>
 *
 */
class Rarities
{
    static final Holder<Rarity> COMMON = register("common", new Rarity("Common", new Vector3f(1, 1, 1)));
    static final Holder<Rarity> UNCOMMON = register("uncommon", new Rarity("Uncommon", new Vector3f(0.75f, 1, 0)));
    static final Holder<Rarity> RARE = register("rare", new Rarity("Rare", new Vector3f(0, 0, 1)));
    static final Holder<Rarity> EPIC = register("epic", new Rarity("Epic", new Vector3f(0.5f, 0, 0.5f)));
    static final Holder<Rarity> LEGENDARY = register("legendary", new Rarity("Legendary", new Vector3f(1, 0.84f, 0)));

    private static Holder<Rarity> register(String name, Rarity rarity)
    {
        return Registry.registerForHolder(Test.BuiltInRegistries.RARITY_REGISTRY, name, rarity);
    }

    static void bootstrap(Registry<Rarity> ignored) {}
}

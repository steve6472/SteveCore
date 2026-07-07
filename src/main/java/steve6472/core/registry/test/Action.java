package steve6472.core.registry.test;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Registry;
import steve6472.core.registry.Typed;

import java.util.function.Function;

interface Action extends Typed<Action>
{
    Codec<Action> CODEC = Test.BuiltInRegistries.ACTION_REGISTRY
        .byKeyCodec()
        .dispatch(Action::codec, Function.identity());

    static void bootstrap(Registry<MapCodec<? extends Action>> registry, Object... extraParams)
    {
        Registry.register(registry, "heal", Heal.CODEC);
        Registry.register(registry, "damage", Damage.CODEC);
    }

    record Heal(int hp) implements Action
    {
        public static final MapCodec<Heal> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("hp").forGetter(Heal::hp)
        ).apply(instance, Heal::new));

        @Override
        public MapCodec<? extends Action> codec()
        {
            return CODEC;
        }
    }

    record Damage(int damage, String message) implements Action
    {
        public static final MapCodec<Damage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("damage").forGetter(Damage::damage),
            Codec.STRING.optionalFieldOf("message", "generic damage").forGetter(Damage::message)
        ).apply(instance, Damage::new));

        @Override
        public MapCodec<? extends Action> codec()
        {
            return CODEC;
        }
    }
}
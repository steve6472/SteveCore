package steve6472.core.registry.test;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.SteveCore;
import steve6472.core.log.Log;
import steve6472.core.module.ModuleManager;
import steve6472.core.module.ModulePart;
import steve6472.core.registry.*;
import steve6472.core.setting.BoolSetting;
import steve6472.core.setting.IntSetting;
import steve6472.core.setting.Setting;

import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 5/5/2026
 * Project: SteveCore <br>
 *
 */
final class Test
{
    private static final Logger LOGGER = Log.getLogger(Test.class);
    static final String NAMESPACE = "test";

    static ModuleManager moduleManager;

    @SuppressWarnings("InstantiationOfUtilityClass")
    public static void main(String[] args)
    {
        new Test();
    }

    private Test()
    {
        SteveCore.CORE_MODULE = NAMESPACE;

        moduleManager = new ModuleManager();
        moduleManager.loadModules();

        BuiltInRegistries.bootstrap();
        RegistryCore.freezeRoot();
        RegistryCore.SETTINGS.bootstrap();
        RegistryCore.STATIC.bootstrap();
        RegistryCore.DYNAMIC.bootstrap();

        Holder.Reference<Foo> fooRef = BuiltInRegistries.FOO_REGISTRY.getAny().get();

        Holder<Rarity> legendary = BuiltInRegistries.RARITY_REGISTRY.get(key("legendary")).orElseThrow();
        System.out.println(legendary);
        System.out.println(legendary.value());
        System.out.println(Rarities.LEGENDARY.value());

        Scanner scanner = new Scanner(System.in);
        while (true)
        {
            String input = scanner.nextLine();
            switch (input)
            {
                case "reload" -> {
                    System.out.println("Reloading");
                    RegistryCore.DYNAMIC.bootstrap();
                    System.out.println("Reloaded");
                }
                case "ref" -> System.out.println(fooRef);
                case "printNamed" -> BuiltInRegistries.NAMED_NUMBER_REGISTRY.listElements().forEach(System.out::println);
                case "printFoo" -> BuiltInRegistries.FOO_REGISTRY.listElements().forEach(System.out::println);
                case "exit" -> System.exit(0);
                default -> System.err.println("Unexpected value: " + input);
            }
        }
    }

    static Key key(String id)
    {
        return Key.withNamespace(NAMESPACE, id);
    }

    static class Parts
    {
        static final ModulePart<NamedNumber> NAMED_NUMBER = new ModulePart<>(Registries.NAMED_NUMBER, "Named Number", "named_number");
        static final ModulePart<Foo> FOO = new ModulePart<>(Registries.FOO, "Foo", "foo");
    }

    static class Registries
    {
        static final ResourceKey<Registry<Setting<?, ?>>> TEST_SETTING = ResourceKey.createRegistryKey(key("setting"));

        static final ResourceKey<Registry<MapCodec<? extends Action>>> ACTION = ResourceKey.createRegistryKey(key("action"));
        static final ResourceKey<Registry<Rarity>> RARITY = ResourceKey.createRegistryKey(key("rarity"));

        static final ResourceKey<Registry<NamedNumber>> NAMED_NUMBER = ResourceKey.createRegistryKey(key("named_number"));
        static final ResourceKey<Registry<Foo>> FOO = ResourceKey.createRegistryKey(key("foo"));
    }

    static class BuiltInRegistries
    {
        static final Registry<Setting<?, ?>> TEST_SETTING = RegistryCore.createSettingsRegistry(Registries.TEST_SETTING, TestSettings::bootstrap);

        // Load order matters, in this case Foo class refers to Action so Action registry has to be created first otherwise we get NPE
        static final Registry<MapCodec<? extends Action>> ACTION_REGISTRY = RegistryCore.createRegistry(Registries.ACTION, Action::bootstrap);
        static final Registry<Rarity> RARITY_REGISTRY = RegistryCore.createRegistry(Registries.RARITY, Rarities::bootstrap);

        static final Registry<Foo> FOO_REGISTRY = RegistryCore.createDynamicRegistry(Registries.FOO, RegistryCore.partLoader(Parts.FOO, Foo.DIRECT_CODEC, () -> moduleManager));
        static final Registry<NamedNumber> NAMED_NUMBER_REGISTRY = RegistryCore.createDynamicRegistry(Parts.NAMED_NUMBER, NamedNumber.DIRECT_CODEC, () -> moduleManager);

        public static void bootstrap()
        {
        }
    }

    static class TestSettings
    {
        public static BoolSetting BOOL_SETTING;
        public static IntSetting INT_SETTING;

        public static void bootstrap(Registry<Setting<?, ?>> registry, Object... extraParams)
        {
            BOOL_SETTING = Setting.registerBool(registry, "bool", false);
            INT_SETTING = Setting.registerInt(registry, "int", 69);
        }
    }

    record NamedNumber(int number, String name)
    {
        public static final Codec<NamedNumber> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("number").forGetter(NamedNumber::number),
            Codec.STRING.fieldOf("name").forGetter(NamedNumber::name)
        ).apply(instance, NamedNumber::new));

        public static final Codec<Holder<NamedNumber>> CODEC = RegistryCodec.create(Registries.NAMED_NUMBER);
    }

    record Foo(Holder<NamedNumber> namedNumber, String value, Action action)
    {
        public static final Codec<Foo> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            NamedNumber.CODEC.fieldOf("named").forGetter(Foo::namedNumber),
            Codec.STRING.fieldOf("value").forGetter(Foo::value),
            Action.CODEC.fieldOf("action").forGetter(Foo::action)
        ).apply(instance, Foo::new));
    }
}

package steve6472.core.newregistry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.junit.jupiter.api.*;
import steve6472.core.SteveCore;
import steve6472.core.module.Module;
import steve6472.core.module.ModuleManager;
import steve6472.core.module.ModulePart;
import steve6472.core.registry.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by steve6472
 * Date: 5/6/2026
 * Project: SteveCore <br>
 *
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled
public class RegistryTest
{
    static final String NAMESPACE = "test";
    static final int REGISTRY_COUNT = 2;

    @BeforeAll
    static void setupCore()
    {
        SteveCore.CORE_MODULE = NAMESPACE;
    }

    /*
     * Anything that would have been statically initialized has to be done differently for the purpose of testing
     */

    /// Verify that the testing itself will work correctly
    @Test
    @Order(1)
    void testSetup()
    {
        // Expect static & dynamic registries
        Assertions.assertEquals(3, RegistryCore.ROOT_MASTER.listElements().count(), "Registries were not cleared");

        setup();
        Assertions.assertEquals(REGISTRY_COUNT, RegistryCore.ROOT_MASTER.listElements().count(), "Registries were not setup correctly");

        setup();
        Assertions.assertEquals(REGISTRY_COUNT, RegistryCore.ROOT_MASTER.listElements().count(), "Registries were not setup correctly");
    }

    /*
     * Actual tests
     */

    /// Verify that the test module "Test" (namespace: 'test') is correctly loaded
    @Test
    void testModuleLoading()
    {
        ModuleManager moduleManager = setup();

        Module module = moduleManager.getModule(NAMESPACE);
        Assertions.assertNotNull(module);
        Assertions.assertEquals("Test", module.name());
        List<String> namespaces = module.namespaces();
        Assertions.assertEquals(1, namespaces.size(), "More than one namespace found in the test module");
        Assertions.assertEquals("test", namespaces.getFirst(), "Namespace 'test' not found in the test module");
    }

    @Test
    void testModuleVersion()
    {
        ModuleManager moduleManager = setup();
        Module module = moduleManager.getModule(NAMESPACE);
        Assertions.assertNotNull(module);
        Assertions.assertEquals(1, module.version());
    }

    @Test
    void verifyReloadableRegistryLoading()
    {
        setup();
        Assertions.assertTrue(BuiltInRegistries.NAMED_NUMBER_REGISTRY.getAny().isPresent(), "No 'named_number' entry was loaded.");

        Optional<Holder.Reference<NamedNumber>> refOpt = BuiltInRegistries.NAMED_NUMBER_REGISTRY.get(key("test_1"));
        Assertions.assertTrue(refOpt.isPresent());

        Holder.Reference<NamedNumber> ref = refOpt.get();
        Assertions.assertTrue(ref.isBound());
    }











    /*
     * Test util methods
     */

    private static ModuleManager setup()
    {
        RegistryCore.ROOT_MASTER.listElements().forEach(ref -> ref.value()._clear());
        RegistryCore.ROOT_MASTER._clear();
        ResourceKey.clearCache();

        // re-create necessary static variables
        Registries.create();
        Parts.create();
        TestCodecs.create();

        ModuleManager moduleManager = new ModuleManager();
        moduleManager.loadModules();

        BuiltInRegistries.create(moduleManager);

        // Finally load everything
        RegistryCore.SETTINGS.bootstrap();
        RegistryCore.STATIC.bootstrap();

        return moduleManager;
    }











    static Key key(String id)
    {
        return Key.withNamespace(NAMESPACE, id);
    }

    record NamedNumber(int number, String name) { }
    record Foo(Holder<NamedNumber> namedNumber, String value) { }

    static class Registries
    {
        static ResourceKey<Registry<NamedNumber>> NAMED_NUMBER;
        static ResourceKey<Registry<Foo>> FOO;

        public static void create()
        {
            NAMED_NUMBER = ResourceKey.createRegistryKey(key("named_number"));
            FOO = ResourceKey.createRegistryKey(key("foo"));
        }
    }

    static class Parts
    {
        static ModulePart<NamedNumber> NAMED_NUMBER;
        static ModulePart<Foo> FOO;

        public static void create()
        {
            NAMED_NUMBER = new ModulePart<>(Registries.NAMED_NUMBER, "Named Number", "named_number");
            FOO = new ModulePart<>(Registries.FOO, "Foo", "foo");
        }
    }

    static class TestCodecs
    {
        static Codec<NamedNumber> NAMED_NUMBER_DIRECT_CODEC;
        static Codec<Holder<NamedNumber>> NAMED_NUMBER_CODEC;

        public static Codec<Foo> FOO_DIRECT_CODEC;

        public static void create()
        {
            NAMED_NUMBER_DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("number").forGetter(NamedNumber::number),
                Codec.STRING.fieldOf("name").forGetter(NamedNumber::name)
            ).apply(instance, NamedNumber::new));

            NAMED_NUMBER_CODEC = RegistryCodec.create(Registries.NAMED_NUMBER);

            FOO_DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                NAMED_NUMBER_CODEC.fieldOf("named").forGetter(Foo::namedNumber),
                Codec.STRING.fieldOf("value").forGetter(Foo::value)
            ).apply(instance, Foo::new));
        }
    }

    static class BuiltInRegistries
    {
        static Registry<Foo> FOO_REGISTRY;
        static Registry<NamedNumber> NAMED_NUMBER_REGISTRY;

        public static void create(ModuleManager moduleManager)
        {
            FOO_REGISTRY = RegistryCore.createDynamicRegistry(Registries.FOO, RegistryCore.partLoader(Parts.FOO, TestCodecs.FOO_DIRECT_CODEC, () -> moduleManager));
            NAMED_NUMBER_REGISTRY = RegistryCore.createDynamicRegistry(Registries.NAMED_NUMBER, RegistryCore.partLoader(Parts.NAMED_NUMBER, TestCodecs.NAMED_NUMBER_DIRECT_CODEC, () -> moduleManager));
        }
    }
}

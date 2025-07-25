package steve6472.core.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by steve6472
 * Date: 11/26/2024
 * Project: Flare <br>
 */
public final class Module
{
    private static final Codec<List<String>> STRING_OR_ARRAY = Codec.withAlternative(Codec.STRING.listOf(), Codec.STRING, List::of);

    public static final Codec<Module> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(Module::name),
            Codec.STRING.optionalFieldOf("version_string", "unknown").forGetter(Module::versionString),
            // TODO: bound to 0-inf
            Codec.INT.fieldOf("version").forGetter(Module::version),
            STRING_OR_ARRAY.fieldOf("description").forGetter(Module::description),
            STRING_OR_ARRAY.optionalFieldOf("authors", List.of("--unknown author(s)--")).forGetter(Module::authors),
            STRING_OR_ARRAY.optionalFieldOf("depend", List.of()).forGetter(Module::depend))
        .apply(instance, Module::new));

    private final String name;
    private final String versionString;
    private final int version;
    private final List<String> description;
    private final List<String> authors;
    private final List<String> depend;

    // Set by ModuleManager
    List<String> namespaces;
    File root;

    /**
     *
     */
    public Module(String name, String versionString, int version, List<String> description, List<String> authors, List<String> depend)
    {
        this.name = name;
        this.versionString = versionString;
        this.version = version;
        this.description = description;
        this.authors = authors;
        this.depend = depend;
    }

    public String name()
    {
        return name;
    }

    public String versionString()
    {
        return versionString;
    }

    public int version()
    {
        return version;
    }

    public List<String> description()
    {
        return description;
    }

    public List<String> authors()
    {
        return authors;
    }

    public List<String> depend()
    {
        return depend;
    }

    public List<String> namespaces()
    {
        return namespaces;
    }

    public void iterateNamespaces(BiConsumer<File, String> consumer)
    {
        for (String namespace : namespaces())
        {
            File file = new File(getRootFolder(), namespace);
            consumer.accept(file, namespace);
        }
    }

    public File getRootFolder()
    {
        return root;
    }

    // Module path: <namespace, full>
    static final Map<ModulePart, Map<String, FullModulePart>> PARTS = new HashMap<>();

    public FullModulePart createPart(ModulePart part, String namespace)
    {
        return PARTS.computeIfAbsent(part, _ -> new HashMap<>()).computeIfAbsent(namespace, n -> new FullModulePart(part, this, n));
    }
}

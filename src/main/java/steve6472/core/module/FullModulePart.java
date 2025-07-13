package steve6472.core.module;

import java.io.File;

/**
 * Created by steve6472
 * Date: 7/14/2025
 * Project: SteveCore <br>
 */
public record FullModulePart(ModulePart part, Module module, String namespace, File path)
{
    public FullModulePart(ModulePart part, Module module, String namespace)
    {
        this(part, module, namespace, new File(new File(module.getRootFolder(), namespace), part.path()));
    }

    public String name()
    {
        return part.name();
    }
}

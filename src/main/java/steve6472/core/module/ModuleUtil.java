package steve6472.core.module;

import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 3/21/2025
 * Project: SteveCore <br>
 */
public final class ModuleUtil
{
    private ModuleUtil() {}

    @FunctionalInterface
    public interface LoadedResult<T>
    {
        void accept(Module module, File file, Key key, T object);
    }

    public static <C> void loadModuleJsonCodecs(ModuleManager moduleManager, String extraPath, Codec<C> codec, LoadedResult<C> end)
    {
        for (Module module : moduleManager.getModules())
        {
            module.iterateNamespaces((folder, namespace) ->
            {
                File file = new File(folder, extraPath);

                ResourceCrawl.crawlAndLoadJsonCodec(file, codec, (object, id) ->
                {
                    Key key = Key.withNamespace(namespace, id);
                    end.accept(module, file, key, object);
                });
            });
        }
    }

    public static <C> void loadModuleJsonCodecsDebug(ModuleManager moduleManager, String extraPath, Codec<C> codec, Logger logger, String debugName, LoadedResult<C> end)
    {
        for (Module module : moduleManager.getModules())
        {
            module.iterateNamespaces((folder, namespace) ->
            {
                File file = new File(folder, extraPath);

                ResourceCrawl.crawlAndLoadJsonCodec(file, codec, (object, id) ->
                {
                    Key key = Key.withNamespace(namespace, id);
                    end.accept(module, file, key, object);
                    logger.finest("Loaded %s '%s' from module '%s'".formatted(debugName, key, module.name()));
                });
            });
        }
    }
}

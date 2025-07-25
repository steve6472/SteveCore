package steve6472.core.module;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.core.util.GsonUtil;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 11/11/2024
 * Project: Flare <br>
 */
public final class ResourceCrawl
{
    private static final Logger LOGGER = Log.getLogger(ResourceCrawl.class);

    public static void crawl(File startingDir, boolean stripExtFromRel, BiConsumer<File, String> end)
    {
        recursiveCrawl(startingDir, startingDir, stripExtFromRel, end);
    }

    /// @deprecated use [ModuleManager#loadParts(ModulePart, Codec, BiConsumer)] instead
    @Deprecated(forRemoval = true)
    public static <T> void crawlAndLoadJsonCodec(File startingDir, Codec<T> codec, BiConsumer<T, String> end)
    {
        crawl(startingDir, true, (file, relPath) ->
        {
            JsonElement jsonElement = GsonUtil.loadJson(file);
            DataResult<Pair<T, JsonElement>> decode;
            try
            {
                decode = codec.decode(JsonOps.INSTANCE, jsonElement);
            } catch (Exception ex)
            {
                LOGGER.severe("Error when decoding:\n" + jsonElement.toString());
                throw ex;
            }

            T obj = decode.getOrThrow().getFirst();
            end.accept(obj, relPath);
        });
    }

    public static <T> void crawlAndLoadJsonCodec(FullModulePart part, Codec<T> codec, BiConsumer<T, Key> end)
    {
        crawl(part.path(), true, (file, relPath) ->
        {
            JsonElement jsonElement = GsonUtil.loadJson(file);
            DataResult<Pair<T, JsonElement>> decode;
            Key key = Key.withNamespace(part.namespace(), relPath);
            LOGGER.finest("Loading %s '%s' from module '%s'".formatted(part.name(), key, part.module().name()));
            try
            {
                decode = codec.decode(JsonOps.INSTANCE, jsonElement);
            } catch (Exception ex)
            {
                LOGGER.severe("Error when decoding:\n" + jsonElement.toString());
                throw ex;
            }

            T obj = decode.getOrThrow().getFirst();
            end.accept(obj, Key.withNamespace(part.namespace(), relPath));
        });
    }

    private static void recursiveCrawl(File file, File startingDir, boolean stripExtFromRel, BiConsumer<File, String> end)
    {
        File[] files = file.listFiles();
        if (files == null)
            return;

        for (File listFile : files)
        {
            if (listFile.isDirectory())
                recursiveCrawl(listFile, startingDir, stripExtFromRel, end);
            else
            {
                String replace = listFile.getAbsolutePath().replace("\\", "/");
                String replace1 = startingDir.getAbsolutePath().replace("\\", "/");
                String substring = replace.substring(replace1.length() + 1);
                if (stripExtFromRel)
                    substring = substring.substring(0, substring.lastIndexOf('.'));
                end.accept(listFile, substring);
            }
        }
    }
}

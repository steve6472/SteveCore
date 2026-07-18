package steve6472.core.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;

import java.io.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
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

    public static <T> void crawlAndLoadJsonCodec(FullModulePart part, Codec<T> codec, BiConsumer<T, Key> end, BiConsumer<Throwable, Key> onFail)
    {
        crawl(part.path(), true, (file, relPath) ->
        {
            Key key = Key.withNamespace(part.namespace(), relPath);

            JsonElement jsonElement;
            try (Reader reader = new FileReader(file))
            {
                jsonElement = JsonParser.parseReader(reader);
            } catch (Exception exception)
            {
                LOGGER.severe("Error when loading json file '" + file + "'");
                onFail.accept(exception, key);
                return;
            }

            DataResult<Pair<T, JsonElement>> decode;
            LOGGER.finest("Loading %s '%s' from module '%s'".formatted(part.name(), key, part.module().name()));
            try
            {
                decode = codec.decode(JsonOps.INSTANCE, jsonElement);
            } catch (Exception exception)
            {
                LOGGER.severe("Error when decoding json '" + key + "'\n" + jsonElement.toString());
                onFail.accept(exception, key);
                return;
            }

            Pair<T, JsonElement> decodeGet;
            try
            {
                decodeGet = decode.getOrThrow();
            } catch (Exception exception)
            {
                LOGGER.severe("Error when decoding '" + key + "'\n" + jsonElement.toString());
                onFail.accept(exception, key);
                return;
            }

            T obj = decodeGet.getFirst();
            end.accept(obj, key);
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

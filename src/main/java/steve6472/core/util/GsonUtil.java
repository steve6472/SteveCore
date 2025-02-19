package steve6472.core.util;

import com.google.gson.*;
import steve6472.core.log.Log;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 9/30/2024
 * Project: SteveCore <br>
 */
public class GsonUtil
{
    private static final Logger LOGGER = Log.getLogger(GsonUtil.class);

    public static void saveJson(JsonElement json, File file)
    {
        try
        {
            saveJsonElementOrdered(json, file);
        } catch (IOException e)
        {
            LOGGER.severe("Could not save json to '" + file + "'");
            throw new RuntimeException(e);
        }
    }

    public static JsonElement loadJson(File file)
    {
        try (Reader reader = new FileReader(file))
        {
            return JsonParser.parseReader(reader);
        } catch (IOException e)
        {
            LOGGER.severe("Could not load json from '" + file + "'");
            throw new RuntimeException(e);
        }
    }

    public static JsonElement loadOrDefaultJson(File file, Supplier<JsonElement> defaultValue)
    {
        if (!file.exists())
            return defaultValue.get();

        return loadJson(file);
    }

    private static void saveJsonElementOrdered(JsonElement jsonElement, File file) throws IOException
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (jsonElement.isJsonObject())
        {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonObject sortedJsonObject = sortJsonObject(jsonObject);
            String jsonString = gson.toJson(sortedJsonObject);
            writeToFile(jsonString, file);
        } else
        {
            String jsonString = gson.toJson(jsonElement);
            writeToFile(jsonString, file);
        }
    }

    private static JsonObject sortJsonObject(JsonObject jsonObject)
    {
        JsonObject sortedJsonObject = new JsonObject();

        Map<String, JsonElement> sortedMap = new TreeMap<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet())
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, JsonElement> entry : sortedMap.entrySet())
        {
            sortedJsonObject.add(entry.getKey(), entry.getValue());
        }
        return sortedJsonObject;
    }

    private static void writeToFile(String jsonString, File file) throws IOException
    {
        File parentFile = file.getParentFile();

        if (parentFile != null && !parentFile.exists())
            if (!parentFile.mkdirs())
                throw new RuntimeException("Could not mkdirs for " + file);

        try (FileWriter fileWriter = new FileWriter(file))
        {
            fileWriter.write(jsonString);
        }
    }
}

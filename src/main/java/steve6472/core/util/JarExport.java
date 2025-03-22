package steve6472.core.util;

import steve6472.core.SteveCore;
import steve6472.core.log.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 3/22/2025
 * Project: SteveCore <br>
 */
public final class JarExport
{
    private static final Logger LOGGER = Log.getLogger(JarExport.class);

    private JarExport() {}

    public static void createFolderOrError(File directory)
    {
        if (!directory.exists())
        {
            if (!directory.mkdirs())
            {
                LOGGER.severe("Could not create folder at " + directory.getAbsolutePath());
                throw new RuntimeException("Could not geenrate generated folder");
            }
        }
    }

    /// This does not exactly work well, it works differently when ran from IDE and when ran as jar
    /// For now it seems to work tho
    public static void exportFolder(String path, File destination) throws IOException, URISyntaxException
    {
        String pathForListing = path;
        if (!pathForListing.endsWith("/"))
            pathForListing = pathForListing + "/";

        String[] resourceListing = ResourceListing.getResourceListing(SteveCore.class, pathForListing);
        //        System.out.println("pathForListing: " + pathForListing + " destination: " + destination + " listing: " + Arrays.toString(resourceListing) + " (" + (resourceListing == null ? "-1" : resourceListing.length) + ")");

        if (resourceListing == null || resourceListing.length == 0)
        {
            exportFile(path, destination);
            return;
        }

        if (!destination.exists())
        {
            if (!destination.mkdirs())
            {
                LOGGER.severe("Failed to create folder for export " + destination);
                return;
            }
        }

        for (String resource : resourceListing)
        {
            if (resource.isBlank())
                continue;

            exportFolder(path + "/" + resource, new File(destination, resource));
        }
    }

    public static void exportFile(String path, File destination) throws IOException
    {
        //        System.out.println("Exporting file " + path + " to " + destination);
        if (!destination.exists())
        {
            if (!path.startsWith("/"))
                path = "/" + path;
            InputStream link = SteveCore.class.getResourceAsStream(path);
            Objects.requireNonNull(link, "Path '" + path + "' not found!");
            Files.copy(link, destination.getAbsoluteFile().toPath());
            link.close();
        }
    }
}

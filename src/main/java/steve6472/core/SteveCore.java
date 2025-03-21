package steve6472.core;

import java.io.File;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
public class SteveCore
{
    /// Default namespace to use for Key
    public static String DEFAULT_KEY_NAMESPACE = "core";

    /// Root folder for modules
    public static File MODULES = new File("modules");

    /// This module will **always** be loaded first
    public static String CORE_MODULE = "core";
}

package steve6472.core;

import steve6472.core.registry.Key;

import java.io.File;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
public class SteveCore
{
    /* # TODOS
     *
     * [x] Module ordering (ty ChatGPT)
     *
     */

    /// Root folder for modules
    public static File MODULES = new File("modules");

    /// This module will **always** be loaded first
    public static String CORE_MODULE = "core";

    /// Constant used for root registry
    public static final Key ROOT_MASTER_REGISTRY = Key.parse("core", "root");
    public static final Key ROOT_SETTINGS_REGISTRY = Key.parse("core", "root_settings");
    public static final Key ROOT_STATIC_REGISTRY = Key.parse("core", "root_static");
    public static final Key ROOT_DYNAMIC_REGISTRY = Key.parse("core", "root_reloadable");
}

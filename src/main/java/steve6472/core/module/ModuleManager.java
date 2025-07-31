package steve6472.core.module;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import org.jetbrains.annotations.Nullable;
import steve6472.core.SteveCore;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.core.util.GsonUtil;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 11/26/2024
 * Project: Flare <br>
 */
public class ModuleManager
{
    private static final Logger LOGGER = Log.getLogger(ModuleManager.class);
    private static final String[] MODULE_INFO = {"module_info.json5", "module_info.json"};

    private final List<Map.Entry<String, Module>> modules = new ArrayList<>();

    public void loadModules()
    {
        File[] modules = SteveCore.MODULES.listFiles();
        Objects.requireNonNull(modules, "List files for Modules folder failed!");

        for (File moduleRoot : modules)
        {
            String rootName = moduleRoot.getName();
            File moduleInfo = getModuleInfo(moduleRoot);

            if (moduleInfo == null)
            {
                LOGGER.warning("Installed module '" + rootName + "' does not have module_info and will not be loaded!");
                continue;
            }

            JsonElement jsonElement = GsonUtil.loadJson(moduleInfo);
            DataResult<Pair<Module, JsonElement>> decode = Module.CODEC.decode(JsonOps.INSTANCE, jsonElement);
            Module module = decode.getOrThrow().getFirst();
            module.root = moduleRoot;
            module.namespaces = listNamespaces(moduleRoot);
            this.modules.add(Map.entry(rootName, module));

            LOGGER.finer("Loaded module '" + rootName + "'");
        }

        sortModules();
        LOGGER.info("Module order:");
        for (Map.Entry<String, Module> module : this.modules)
        {
            LOGGER.info(" - '%s' '%s'".formatted(module.getKey(), module.getValue().name()));
        }
    }

    private void sortModules()
    {
        LOGGER.finest("Sorting modules");
        // Step 1: Build module map and adjacency list
        Map<String, Module> moduleMap = new HashMap<>();
        Map<String, List<String>> graph = new HashMap<>();
        Set<String> allNodes = new HashSet<>();

        for (Map.Entry<String, Module> entry : modules)
        {
            String name = entry.getKey();
            moduleMap.put(name, entry.getValue());
            graph.putIfAbsent(name, new ArrayList<>());
            allNodes.add(name);
        }

        // Step 2: Build directed edges based on depend() and after()
        for (Module module : moduleMap.values())
        {
            String moduleName = module.root.getName();

            for (String dep : module.depend())
            {
                // dep -> module
                if (!moduleMap.containsKey(dep))
                {
                    throw new IllegalStateException("Missing dependency: '%s' for module '%s'".formatted(dep, moduleName));
                }
                graph.get(dep).add(moduleName);
            }

            for (String after : module.after())
            {
                if (moduleMap.containsKey(after))
                {
                    graph.get(after).add(moduleName); // after -> module
                }
            }

            for (String before : module.before())
            {
                if (moduleMap.containsKey(before))
                {
                    graph.get(moduleName).add(before); // module -> before
                }
            }
        }

        // Step 3: Ensure core module loads first
        if (!moduleMap.containsKey(SteveCore.CORE_MODULE))
        {
            throw new IllegalStateException("Missing '%s' module.".formatted(SteveCore.CORE_MODULE));
        }

        for (String mod : allNodes)
        {
            if (!mod.equals(SteveCore.CORE_MODULE))
            {
                graph.get(SteveCore.CORE_MODULE).add(mod);  // core -> all
            }
        }

        // Step 4: Topological sort using DFS and cycle detection
        List<String> sorted = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();

        for (String node : allNodes)
        {
            if (!visited.contains(node))
            {
                dfs(node, graph, visited, visiting, sorted);
            }
        }

        // Step 5: Reorder modules list based on sorted order
        Map<String, Module> nameToModule = new HashMap<>();
        for (Map.Entry<String, Module> entry : modules)
        {
            nameToModule.put(entry.getKey(), entry.getValue());
        }

        modules.clear();
        for (String name : sorted)
        {
            modules.add(Map.entry(name, nameToModule.get(name)));
        }
    }

    // Depth-First-Search
    private void dfs(String node, Map<String, List<String>> graph, Set<String> visited, Set<String> visiting, List<String> result)
    {
        if (visiting.contains(node))
        {
            throw new IllegalStateException("Circular dependency detected at module: '" + node + "'");
        }
        if (visited.contains(node))
            return;

        visiting.add(node);
        for (String neighbor : graph.getOrDefault(node, Collections.emptyList()))
        {
            dfs(neighbor, graph, visited, visiting, result);
        }
        visiting.remove(node);
        visited.add(node);
        result.addFirst(node); // prepend to build in reverse order
    }

    @Nullable
    public Module getModule(String namespace)
    {
        for (Map.Entry<String, Module> module : modules)
        {
            if (module.getKey().equals(namespace))
                return module.getValue();
        }

        return null;
    }

    /// Return a copy of a module list in the load order
    public List<Module> getModules()
    {
        return modules.stream().map(Map.Entry::getValue).toList();
    }

    public void iterateWithNamespaces(BiConsumer<Module, String> moduleNamespaceConsumer)
    {
        for (Module module : getModules())
        {
            module.iterateNamespaces((_, namespace) -> moduleNamespaceConsumer.accept(module, namespace));
        }
    }

    public <T> void loadParts(ModulePart part, Codec<T> codec, BiConsumer<T, Key> end)
    {
        iterateWithNamespaces((module, namespace) -> ResourceCrawl.crawlAndLoadJsonCodec(module.createPart(part, namespace), codec, end));
    }

    @FunctionalInterface
    public interface LoadedResult<T>
    {
        void accept(Module module, File file, Key key, T object);
    }

    public <C> void loadModuleJsonCodecs(ModulePart part, Codec<C> codec, LoadedResult<C> end)
    {
        for (Module module : getModules())
        {
            module.iterateNamespaces((folder, namespace) ->
            {
                File file = new File(folder, part.path());
                ResourceCrawl.crawlAndLoadJsonCodec(module.createPart(part, namespace), codec, (object, key) -> end.accept(module, file, key, object));
            });
        }
    }

    @Nullable
    private File getModuleInfo(File module)
    {
        for (String moduleInfoFile : MODULE_INFO)
        {
            File moduleInfo = new File(module, moduleInfoFile);
            if (!moduleInfo.exists())
                continue;
            return moduleInfo;
        }

        // No module_info found!
        return null;
    }

    private List<String> listNamespaces(File module)
    {
        File[] files = module.listFiles();
        if (files == null)
            return List.of();
        List<String> namespaces = new ArrayList<>();
        for (File file : files)
        {
            if (file.isDirectory())
                namespaces.add(file.getName());
        }
        return List.copyOf(namespaces);
    }

    public void clearPartsCache()
    {
        for (Map.Entry<String, Module> module : modules)
        {
            module.getValue().parts.clear();
        }
    }
}

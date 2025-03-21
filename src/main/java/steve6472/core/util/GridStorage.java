package steve6472.core.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.Optional;

/**********************
 * Created by steve6472
 * On date: 30.9.2018
 * Project: 3DTest
 *
 ***********************/
public class GridStorage<T>
{
    private final Long2ObjectMap<T> objects;

    public GridStorage()
    {
        this.objects = new Long2ObjectOpenHashMap<>();
    }

    public static long getKey(int x, int z)
    {
        boolean zNegative = z < 0;

        if (zNegative) z = -z;

        long l = (long) x << 32 | (long) z;
        if (zNegative) l = -l;

        return l;
    }

    public Optional<T> get(int x, int z)
    {
        return Optional.ofNullable(objects.get(getKey(x, z)));
    }

    public Optional<T> get(long key)
    {
        return Optional.ofNullable(objects.get(key));
    }

    public void put(int x, int z, T obj)
    {
        objects.put(getKey(x, z), obj);
    }

    public void remove(int x, int z)
    {
        long key = getKey(x, z);
        if (objects.containsKey(key))
            objects.remove(key);
    }

    public Long2ObjectMap<T> getMap()
    {
        return objects;
    }
}
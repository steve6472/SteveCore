package steve6472.core.registry;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.Packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
public class PacketRegistry
{
    private final Key key;
    private final Map<Key, BufferCodec<ByteBuf, Packet<?, ?>>> map = new HashMap<>();
    private final Map<Key, Integer> keyToIntKey = new HashMap<>();
    private final List<BufferCodec<ByteBuf, Packet<?, ?>>> keyIntToPacket = new ArrayList<>();
    private int counter;

    public PacketRegistry(Key key)
    {
        this.key = key;
    }

    public Key getRegistryKey()
    {
        return key;
    }

    public void register(Key key, BufferCodec<ByteBuf, Packet<?, ?>> obj)
    {
        keyToIntKey.put(key, counter);
        keyIntToPacket.add(obj);
        counter++;
        map.put(key, obj);
    }

    public int getPacketIntKey(Key key)
    {
        return keyToIntKey.get(key);
    }

    public Key getPacketKeyByIntKey(int intKey)
    {
        for (Key key1 : keyToIntKey.keySet())
        {
            if (keyToIntKey.get(key1) == intKey)
            {
                return key1;
            }
        }

        return null;
    }

    public BufferCodec<ByteBuf, Packet<?, ?>> getPacketFromIntKey(int intKey)
    {
        return keyIntToPacket.get(intKey);
    }

    public BufferCodec<ByteBuf, Packet<?, ?>> get(Key key)
    {
        return map.get(key);
    }
}

package steve6472.core.network;

import io.netty.buffer.ByteBuf;
import steve6472.core.registry.Keyable;

/**
 * Created by steve6472
 * Date: 10/8/2024
 * Project: SteveCore <br>
 */
public interface Packet<T, L extends PacketListener> extends Keyable
{
    BufferCodec<ByteBuf, T> codec();

    void handlePacket(L listener);
}

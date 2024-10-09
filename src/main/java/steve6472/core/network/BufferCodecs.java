package steve6472.core.network;

import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import steve6472.core.registry.Key;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: SteveCore <br>
 */
public interface BufferCodecs
{
    BufferCodec<ByteBuf, Boolean> BOOL = BufferCodec.of(ByteBuf::writeBoolean, ByteBuf::readBoolean);
    BufferCodec<ByteBuf, Byte> BYTE = BufferCodec.of((buff, num) -> buff.writeByte(num), ByteBuf::readByte);
    BufferCodec<ByteBuf, Short> SHORT = BufferCodec.of((buff, num) -> buff.writeShort(num), ByteBuf::readShort);
    BufferCodec<ByteBuf, Integer> INT = BufferCodec.of(ByteBuf::writeInt, ByteBuf::readInt);
    BufferCodec<ByteBuf, Long> LONG = BufferCodec.of(ByteBuf::writeLong, ByteBuf::readLong);
    BufferCodec<ByteBuf, Float> FLOAT = BufferCodec.of(ByteBuf::writeFloat, ByteBuf::readFloat);
    BufferCodec<ByteBuf, Double> DOUBLE = BufferCodec.of(ByteBuf::writeDouble, ByteBuf::readDouble);
    BufferCodec<ByteBuf, String> STRING = stringUTF8(32767);
    BufferCodec<ByteBuf, UUID> UUID = BufferCodec.of(LONG, java.util.UUID::getMostSignificantBits, LONG, java.util.UUID::getLeastSignificantBits, UUID::new);
    BufferCodec<ByteBuf, Key> KEY = BufferCodec.of(STRING, Key::toString, str -> {
        if (!str.contains(":"))
        {
            return Key.defaultNamespace(str);
        } else
        {
            String[] split = str.split(":");
            return Key.withNamespace(split[0], split[1]);
        }
    });

    BufferCodec<ByteBuf, byte[]> BYTE_ARRAY = BufferCodec.of((buff, arr) -> {
        buff.writeInt(arr.length);
        buff.writeBytes(arr);
    }, buff -> {
        int size = buff.readInt();
        byte[] bytes = new byte[size];
        buff.readBytes(bytes);
        return bytes;
    });

    static BufferCodec<ByteBuf, String> stringUTF8(int maxSize)
    {
        return BufferCodec.of((buff, str) -> {
            byte[] strBytes = str.getBytes();
            byte[] bytes = new byte[Math.min(maxSize, strBytes.length)];
            System.arraycopy(strBytes, 0, bytes, 0, bytes.length);
            buff.writeInt(bytes.length);
            buff.writeBytes(bytes);
        }, buff -> {
            int size = buff.readInt();
            byte[] bytes = new byte[size];
            buff.readBytes(bytes);
            return new String(bytes);
        });
    }
}

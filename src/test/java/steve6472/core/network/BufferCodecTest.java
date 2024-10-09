package steve6472.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: SteveCore <br>
 */
public class BufferCodecTest
{
    @Test
    void testEncodeDecode()
    {
        PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;

        record TestRecord(int value) {}
        var codec = BufferCodec.of(BufferCodecs.INT, TestRecord::value, TestRecord::new);

        ByteBuf buffer = allocator.buffer(64);
        try
        {
            codec.encode(buffer, new TestRecord(42));
            TestRecord decode = codec.decode(buffer);
            assertEquals(42, decode.value);
        } finally
        {
            buffer.release();
        }
    }
}

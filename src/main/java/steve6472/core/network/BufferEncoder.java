package steve6472.core.network;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: SteveCore <br>
 */
public interface BufferEncoder<O, T>
{
    void encode(O left, T right);
}

package steve6472.core.network;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: SteveCore <br>
 */
public interface BufferDecoder<O, T>
{
    T decode(O object);
}

package steve6472.core.util;

/**
 * Created by steve6472
 * Date: 11/28/2024
 * Project: SteveCore <br>
 */
public class BitUtil
{
    /*
     * Byte
     */

    public static byte setBit(byte b, int index, boolean flag)
    {
        if (flag)
        {
            return (byte) (b | (1 << index));
        } else
        {
            return (byte) (b & ~(1 << index));
        }
    }

    public static byte toggleBit(byte b, int index)
    {
        return (byte) (b ^ (1 << index));
    }

    public static boolean isBitSet(byte b, int index)
    {
        return (b & (1 << index)) != 0;
    }

    /*
     * Int
     */

    public static byte setBit(int b, int index, boolean flag)
    {
        if (flag)
        {
            return (byte) (b | (1 << index));
        } else
        {
            return (byte) (b & ~(1 << index));
        }
    }

    public static byte toggleBit(int b, int index)
    {
        return (byte) (b ^ (1 << index));
    }

    public static boolean isBitSet(int b, int index)
    {
        return (b & (1 << index)) != 0;
    }
}

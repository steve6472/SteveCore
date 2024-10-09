package steve6472.core.network;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: SteveCore <br>
 */
public interface BufferCodec<B, V> extends BufferEncoder<B, V>, BufferDecoder<B, V>
{
    static <B, V> BufferCodec<B, V> unit(V unit)
    {
        return new BufferCodec<B, V>()
        {
            @Override
            public V decode(B object)
            {
                return unit;
            }

            @Override
            public void encode(B left, V right)
            {}
        };
    }

    static <B, V> BufferCodec<B, V> of(BufferEncoder<B, V> encoder, BufferDecoder<B, V> decoder)
    {
        return new BufferCodec<>()
        {
            @Override
            public V decode(B object)
            {
                return decoder.decode(object);
            }

            @Override
            public void encode(B left, V right)
            {
                encoder.encode(left, right);
            }
        };
    }

    static <B, V, T1> BufferCodec<B, V> of(
        BufferCodec<B, T1> codec1, Function<V, T1> fun1,
        Function<T1, V> retFunc)
    {
        return new BufferCodec<>()
        {
            @Override
            public void encode(B buff, V obj)
            {
                codec1.encode(buff, fun1.apply(obj));
            }

            @Override
            public V decode(B object)
            {
                return retFunc.apply(
                    codec1.decode(object));
            }
        };
    }

    static <B, V, T1, T2> BufferCodec<B, V> of(
        BufferCodec<B, T1> codec1, Function<V, T1> fun1,
        BufferCodec<B, T2> codec2, Function<V, T2> fun2,
        BiFunction<T1, T2, V> retFunc)
    {
        return new BufferCodec<>()
        {
            @Override
            public void encode(B buff, V obj)
            {
                codec1.encode(buff, fun1.apply(obj));
                codec2.encode(buff, fun2.apply(obj));
            }

            @Override
            public V decode(B buff)
            {
                return retFunc.apply(
                    codec1.decode(buff),
                    codec2.decode(buff));
            }
        };
    }

    static <B, V, T1, T2, T3> BufferCodec<B, V> of(
        BufferCodec<B, T1> codec1, Function<V, T1> fun1,
        BufferCodec<B, T2> codec2, Function<V, T2> fun2,
        BufferCodec<B, T3> codec3, Function<V, T3> fun3,
        Function3<T1, T2, T3, V> retFunc)
    {
        return new BufferCodec<>()
        {
            @Override
            public void encode(B buff, V obj)
            {
                codec1.encode(buff, fun1.apply(obj));
                codec2.encode(buff, fun2.apply(obj));
                codec3.encode(buff, fun3.apply(obj));
            }

            @Override
            public V decode(B buff)
            {
                return retFunc.apply(
                    codec1.decode(buff),
                    codec2.decode(buff),
                    codec3.decode(buff));
            }
        };
    }

    static <B, V, T1, T2, T3, T4> BufferCodec<B, V> of(
        BufferCodec<B, T1> codec1, Function<V, T1> fun1,
        BufferCodec<B, T2> codec2, Function<V, T2> fun2,
        BufferCodec<B, T3> codec3, Function<V, T3> fun3,
        BufferCodec<B, T4> codec4, Function<V, T4> fun4,
        Function4<T1, T2, T3, T4, V> retFunc)
    {
        return new BufferCodec<>()
        {
            @Override
            public void encode(B buff, V obj)
            {
                codec1.encode(buff, fun1.apply(obj));
                codec2.encode(buff, fun2.apply(obj));
                codec3.encode(buff, fun3.apply(obj));
                codec4.encode(buff, fun4.apply(obj));
            }

            @Override
            public V decode(B buff)
            {
                return retFunc.apply(
                    codec1.decode(buff),
                    codec2.decode(buff),
                    codec3.decode(buff),
                    codec4.decode(buff));
            }
        };
    }

    static <B, V, T1, T2, T3, T4, T5> BufferCodec<B, V> of(
        BufferCodec<B, T1> codec1, Function<V, T1> fun1,
        BufferCodec<B, T2> codec2, Function<V, T2> fun2,
        BufferCodec<B, T3> codec3, Function<V, T3> fun3,
        BufferCodec<B, T4> codec4, Function<V, T4> fun4,
        BufferCodec<B, T5> codec5, Function<V, T5> fun5,
        Function5<T1, T2, T3, T4, T5, V> retFunc)
    {
        return new BufferCodec<>()
        {
            @Override
            public void encode(B buff, V obj)
            {
                codec1.encode(buff, fun1.apply(obj));
                codec2.encode(buff, fun2.apply(obj));
                codec3.encode(buff, fun3.apply(obj));
                codec4.encode(buff, fun4.apply(obj));
                codec5.encode(buff, fun5.apply(obj));
            }

            @Override
            public V decode(B buff)
            {
                return retFunc.apply(
                    codec1.decode(buff),
                    codec2.decode(buff),
                    codec3.decode(buff),
                    codec4.decode(buff),
                    codec5.decode(buff));
            }
        };
    }
}

package steve6472.core.util;

import java.util.Random;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
public class RandomUtil
{
	private static final Random random = new Random();
	private static final Random seededRandom = new Random();

	public static double randomRadian()
	{
		return randomDouble(-Math.PI, Math.PI);
	}

	public static double randomSin()
	{
		return Math.sin(randomRadian());
	}

	public static double randomCos()
	{
		return Math.cos(randomRadian());
	}

	public static float randomSinFloat()
	{
		return (float) Math.sin(randomRadian());
	}

	public static float randomCosFloat()
	{
		return (float) Math.cos(randomRadian());
	}

	public static int randomInt(int min, int max)
	{
		if (max == min) return max;
		if (max < min) return 0;

		return random.nextInt((max - min) + 1) + min;
	}

	public static int randomInt(int min, int max, long seed)
	{
		if (max == min) return max;
		if (max < min) return 0;

		seededRandom.setSeed(seed);
		return seededRandom.nextInt((max - min) + 1) + min;
	}

	public static double randomDouble(double min, double max)
	{
		if (max == min) return max;
		if (max < min) return 0;

		return min + (max - min) * random.nextDouble();
	}

	public static double randomDouble(double min, double max, long seed)
	{
		if (max == min) return max;
		if (max < min) return 0;

		seededRandom.setSeed(seed);
		return min + (max - min) * seededRandom.nextDouble();
	}

	public static long randomLong(long min, long max, long seed)
	{
		if (max == min) return max;
		if (max < min) return 0;

		seededRandom.setSeed(seed);
		return min + (max - min) * seededRandom.nextLong();
	}

	public static long randomLong(long min, long max)
	{
		if (max == min) return max;
		if (max < min) return 0;

		return min + (max - min) * random.nextLong();
	}

	public static float randomFloat(float min, float max, long seed)
	{
		if (max == min) return max;
		if (max < min) return 0;

		seededRandom.setSeed(seed);
		return min + (max - min) * seededRandom.nextFloat();
	}

	public static float randomFloat(float min, float max)
	{
		if (max == min) return max;
		if (max < min) return 0;

		return min + (max - min) * random.nextFloat();
	}

	public static boolean randomBoolean()
	{
		return randomInt(0, 1) == 1;
	}

	public static boolean decide(double probability)
	{
		return random.nextDouble() < probability;
	}
}

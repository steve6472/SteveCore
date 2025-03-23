package steve6472.core.util;

import java.util.stream.DoubleStream;

public final class Profiler
{
    private final double[] measurements;
    private final int measurementsToAverage;
    private int currentMeasurement;
    private long lastMeasurementTime;
    private double lastElapsed;
    private double lastAverage;
    private double maximumEver;

    public Profiler(int measurementsToAverage)
    {
        this.measurementsToAverage = measurementsToAverage;
        this.measurements = new double[measurementsToAverage];
        this.currentMeasurement = 0;
    }

    public void start()
    {
        lastMeasurementTime = System.nanoTime();
    }

    public void end()
    {
        lastElapsed = System.nanoTime() - lastMeasurementTime;
        measurements[currentMeasurement] = lastElapsed;
        maximumEver = Math.max(maximumEver, lastElapsed);
        currentMeasurement++;

        if (currentMeasurement == measurementsToAverage)
        {
            currentMeasurement = 0;
            lastAverage = calculateAverage();
        }
    }

    private double calculateAverage()
    {
        double totalElapsedTime = 0;
        for (double measurement : measurements)
        {
            totalElapsedTime += measurement;
        }
        return totalElapsedTime / measurementsToAverage;
    }

    public double averageNano()    { return lastAverage; }
    public double averageMilli()   { return lastAverage / 1e6d; }
    public double averageSeconds() { return lastAverage / 1e9d; }

    public double lastNano()    { return lastElapsed; }
    public double lastMilli()   { return lastElapsed / 1e6d; }
    public double lastSeconds() { return lastElapsed / 1e9d; }

    public double maxEverNano()    { return maximumEver; }
    public double maxEverMilli()   { return maximumEver / 1e6d; }
    public double maxEverSeconds() { return maximumEver / 1e9d; }

    public double maxNano()    { return DoubleStream.of(measurements).max().orElse(0); }
    public double maxMilli()   { return maxNano() / 1e6d; }
    public double maxSeconds() { return maxNano() / 1e9d; }
}
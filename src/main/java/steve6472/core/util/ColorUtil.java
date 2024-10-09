package steve6472.core.util;

import java.awt.*;

/**
 * Created by steve6472
 * Date: 10/5/2024
 * Project: SteveCore <br>
 */
public class ColorUtil
{
    public static int getColor(int r, int g, int b, int a)
    {
        return ((a & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
    }

    public static int getColor(int r, int g, int b)
    {
        return getColor(r, g, b, 255);
    }
    
    public static int getColor(int gray)
    {
        return getColor(gray, gray, gray, 255);
    }
    
    public static int getColor(int gray, float red, float green, float blue)
    {
        return getColor((int) (gray * red), (int) (gray * green), (int) (gray * blue));
    }
    
    public static int getColor(float red, float green, float blue, float alpha)
    {
        return getColor((int) (red * 255f), (int) (green * 255f), (int) (blue * 255f), (int) (alpha * 255f));
    }
    
    public static int getColor(double gray)
    {
        return getColor((int) gray);
    }
    
    public static float[] getColors(int color)
    {
        return new float[] { (float) getRed(color) / 255f, (float) getGreen(color) / 255f, (float) getBlue(color) / 255f, (float) getAlpha(color) / 255f };
    }
    
    public static int getRed(int color)
    {
        return (color >> 16) & 0xff;
    }
    
    public static int getGreen(int color)
    {
        return (color >> 8) & 0xff;
    }
    
    public static int getBlue(int color)
    {
        return color & 0xff;
    }
    
    public static int getAlpha(int color)
    {
        return (color >> 24) & 0xff;
    }

    public static int blend(int c1, int c2, double ratio)
    {
        if (ratio > 1) ratio = 1;
        else if (ratio < 0f) ratio = 0;

        int a1 = getAlpha(c1);
        int r1 = getRed(c1);
        int g1 = getGreen(c1);
        int b1 = getBlue(c1);

        int a2 = getAlpha(c2);
        int r2 = getRed(c2);
        int g2 = getGreen(c2);
        int b2 = getBlue(c2);

        int a = (int) (a1 + (a2 - a1) * ratio);
        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int blendNoAlpha(int c1, int c2, double ratio)
    {
        if (ratio > 1) ratio = 1;
        else if (ratio < 0f) ratio = 0;
        double iRatio = 1.0 - ratio;

        int r1 = getRed(c1);
        int g1 = getGreen(c1);
        int b1 = getBlue(c1);

        int r2 = getRed(c2);
        int g2 = getGreen(c2);
        int b2 = getBlue(c2);

        int r = (int) ((r1 * iRatio) + (r2 * ratio));
        int g = (int) ((g1 * iRatio) + (g2 * ratio));
        int b = (int) ((b1 * iRatio) + (b2 * ratio));

        return (r << 16) | (g << 8) | b;
    }

    public static int dim(int color, double dim)
    {
        int r = getRed(color);
        int g = getGreen(color);
        int b = getBlue(color);
        int a = getAlpha(color);

        return getColor((int) (r * dim), (int) (g * dim), (int) (b * dim), (int) (a * dim));
    }

    public static int setAlpha(int argb, int newAlpha)
    {
        return (argb & ~(0xff << 24)) | (newAlpha << 24);
    }

    public static int shiftHue(int argb, float hueShift)
    {
        // Convert color to HSB
        float[] hsb = Color.RGBtoHSB(getRed(argb), getGreen(argb), getBlue(argb), null);

        // Shift hue
        float modifiedHue = (hsb[0] + hueShift) % 1.0f;
        if (modifiedHue < 0)
        {
            modifiedHue += 1.0f;
        }

        // Convert back to RGB
        int rgb = Color.HSBtoRGB(modifiedHue, hsb[1], hsb[2]);

        // Get ARGB integer representation
        return setAlpha(rgb, getAlpha(argb));
    }

    public static int shiftSaturation(int argb, float saturationShift)
    {
        // Convert color to HSB
        float[] hsb = Color.RGBtoHSB(getRed(argb), getGreen(argb), getBlue(argb), null);

        // Shift saturation
        float modifiedSaturation = Math.max(0, Math.min(1, hsb[1] + saturationShift));

        // Convert back to RGB
        int rgb = Color.HSBtoRGB(hsb[0], modifiedSaturation, hsb[2]);

        // Get ARGB integer representation
        return setAlpha(rgb, getAlpha(argb));
    }

    public static int shiftBrightness(int argb, float brightnessShift)
    {
        // Convert color to HSB
        float[] hsb = Color.RGBtoHSB(getRed(argb), getGreen(argb), getBlue(argb), null);

        // Shift brightness
        float modifiedBrightness = Math.max(0, Math.min(1, hsb[2] + brightnessShift));

        // Convert back to RGB
        int rgb = Color.HSBtoRGB(hsb[0], hsb[1], modifiedBrightness);

        // Get ARGB integer representation
        return setAlpha(rgb, getAlpha(argb));
    }

    // Method to find the closest color in a gradient between startColor and endColor
    public static int limitColorToGradient(int startColor, int endColor, int currentColor, int steps)
    {
        // Calculate the step size for each color component
        int startRed = (startColor >> 16) & 0xFF;
        int startGreen = (startColor >> 8) & 0xFF;
        int startBlue = startColor & 0xFF;

        int endRed = (endColor >> 16) & 0xFF;
        int endGreen = (endColor >> 8) & 0xFF;
        int endBlue = endColor & 0xFF;

        int stepR = (endRed - startRed) / steps;
        int stepG = (endGreen - startGreen) / steps;
        int stepB = (endBlue - startBlue) / steps;

        // Initialize the closest color and its distance
        int closestColor = 0;
        double closestDistance = Double.MAX_VALUE;

        // Iterate through the gradient colors and find the closest one to the current color
        for (int i = 0; i <= steps; i++)
        {
            int red = startRed + i * stepR;
            int green = startGreen + i * stepG;
            int blue = startBlue + i * stepB;

            // Create a color from the current RGB values
            int gradientColor = (red << 16) | (green << 8) | blue;

            // Calculate the distance between the current color and the gradient color
            double distance = getColorDistance(currentColor, gradientColor);

            // Update the closest color if this one is closer
            if (distance < closestDistance)
            {
                closestColor = gradientColor;
                closestDistance = distance;
            }
        }

        return closestColor;
    }

    // Method to calculate the distance between two colors in RGB space
    private static double getColorDistance(int c1, int c2)
    {
        int redDiff = ((c1 >> 16) & 0xFF) - ((c2 >> 16) & 0xFF);
        int greenDiff = ((c1 >> 8) & 0xFF) - ((c2 >> 8) & 0xFF);
        int blueDiff = (c1 & 0xFF) - (c2 & 0xFF);
        return Math.sqrt(redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff);
    }
}
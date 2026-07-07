package steve6472.core.registry.test;

import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Created by steve6472
 * Date: 7/4/2026
 * Project: SteveCore <br>
 *
 */
record Rarity(String displayName, Vector4f color)
{
    Rarity(String displayName, Vector3f color)
    {
        this(displayName, new Vector4f(color, 1.0f));
    }
}

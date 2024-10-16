package steve6472.core.registry;

import org.junit.jupiter.api.Test;
import steve6472.core.SteveCore;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by steve6472
 * Date: 9/29/2024
 * Project: SteveCore <br>
 */
class KeyTest
{
    @Test
    void withNamespace()
    {
        Key key = Key.withNamespace("test_namespace", "test_id");
        assertEquals(key.namespace(), "test_namespace");
        assertEquals(key.id(), "test_id");
    }

    @Test
    void defaultNamespace()
    {
        Key key = Key.defaultNamespace("test_id");
        assertEquals(key.namespace(), SteveCore.DEFAULT_KEY_NAMESPACE);
        assertEquals(key.id(), "test_id");
    }

    @Test
    void testInvalidCharacter()
    {
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> Key.defaultNamespace("INVALID"));
        assertEquals(runtimeException.getMessage(), "ID contains illegal characters, allowed: [a-z0-9_/]*   (INVALID)");
    }

    @Test
    void testValidCharacter()
    {
        assertDoesNotThrow(() -> Key.defaultNamespace("abcdefghijlkmnopqrstuvwxyz0123456789_/"));
    }

    @Test
    void namespace()
    {
        Key key = Key.withNamespace("test_namespace", "test_id");
        assertEquals(key.namespace(), "test_namespace");
    }

    @Test
    void id()
    {
        Key key = Key.withNamespace("test_namespace", "test_id");
        assertEquals(key.id(), "test_id");
    }

    @Test
    void testToString()
    {
        Key key = Key.withNamespace("test_namespace", "test_id");
        assertEquals(key.toString(), "test_namespace:test_id");
    }

    @Test
    void testEquals()
    {
        Key key1 = Key.withNamespace("test_namespace", "test_id");
        Key key2 = Key.withNamespace("test_namespace", "test_id");
        assertEquals(key1, key2);
    }

    @Test
    void testHashCode()
    {
        Key key1 = Key.withNamespace("test_namespace", "test_id");
        Key key2 = Key.withNamespace("test_namespace", "test_id");
        assertEquals(key1.hashCode(), key2.hashCode());
    }
}
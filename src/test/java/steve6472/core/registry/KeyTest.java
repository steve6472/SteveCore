package steve6472.core.registry;

import org.junit.jupiter.api.Test;

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
        final String namespace = "test_namespace";
        Key key = Key.withNamespace(namespace, "test_id");
        assertEquals(namespace, key.namespace());
        assertEquals("test_id", key.id());
    }

    @Test
    void testInvalidCharacter()
    {
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> Key.withNamespace("test", "INVALID"));
        assertEquals("ID contains illegal characters, allowed: [a-z0-9_/]*   (INVALID)", runtimeException.getMessage());
    }

    @Test
    void testValidCharacter()
    {
        assertDoesNotThrow(() -> Key.withNamespace("test", "abcdefghijlkmnopqrstuvwxyz0123456789_/"));
    }

    @Test
    void namespace()
    {
        Key key = Key.withNamespace("test_namespace", "test_id");
        assertEquals("test_namespace", key.namespace());
    }

    @Test
    void id()
    {
        Key key = Key.withNamespace("test_namespace", "test_id");
        assertEquals("test_id", key.id());
    }

    @Test
    void testToString()
    {
        Key key = Key.withNamespace("test_namespace", "test_id");
        assertEquals("test_namespace:test_id", key.toString());
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
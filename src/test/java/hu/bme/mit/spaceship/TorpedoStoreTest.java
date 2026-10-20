package hu.bme.mit.spaceship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TorpedoStoreTest {

    // Shared fixtures initialized before every test method.
    private TorpedoStore stockedStore;   // never fails (FAILURE_RATE = 0.0)
    private TorpedoStore failingStore;   // always fails  (FAILURE_RATE = 1.0)

    @BeforeEach
    void init() {
        // nextDouble() returns a value in [0.0, 1.0):
        //   FAILURE_RATE = 0.0 -> r >= 0.0 is always true  -> firing always succeeds
        //   FAILURE_RATE = 1.0 -> r >= 1.0 is never true    -> firing always fails
        stockedStore = new TorpedoStore(5, 0.0);
        failingStore = new TorpedoStore(5, 1.0);
    }

    @Test
    void fire_Success() {
        // Arrange
        TorpedoStore store = new TorpedoStore(1);

        // Act
        boolean result = store.fire(1);

        // Assert
        assertEquals(true, result);
    }


    @Test
    void fire_Single_Success_ReturnsTrue() {
        // Act
        boolean result = stockedStore.fire(1);

        // Assert
        assertTrue(result);
    }

    @Test
    void fire_Single_Success_DecreasesCountByOne() {
        // Arrange
        int before = stockedStore.getTorpedoCount();

        // Act
        stockedStore.fire(1);

        // Assert
        assertEquals(before - 1, stockedStore.getTorpedoCount());
    }

    @Test
    void fire_Multiple_Success_DecreasesCountByAmount() {
        // Act
        boolean result = stockedStore.fire(3);

        // Assert
        assertTrue(result);
        assertEquals(2, stockedStore.getTorpedoCount());
    }

    @Test
    void fire_AllTorpedoes_Success_StoreBecomesEmpty() {
        // Act
        boolean result = stockedStore.fire(5);

        // Assert
        assertTrue(result);
        assertEquals(0, stockedStore.getTorpedoCount());
        assertTrue(stockedStore.isEmpty());
    }


    @Test
    void fire_Failure_ReturnsFalse() {
        // Act
        boolean result = failingStore.fire(1);

        // Assert
        assertFalse(result);
    }

    @Test
    void fire_Failure_DoesNotDecreaseCount() {
        // Arrange
        int before = failingStore.getTorpedoCount();

        // Act
        failingStore.fire(1);

        // Assert: a failed firing must not consume torpedoes
        assertEquals(before, failingStore.getTorpedoCount());
    }


    @Test
    void fire_ZeroTorpedoes_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> stockedStore.fire(0));
    }

    @Test
    void fire_NegativeTorpedoes_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> stockedStore.fire(-1));
    }

    @Test
    void fire_MoreThanAvailable_ThrowsException() {
        // stockedStore holds 5 torpedoes; requesting 6 is out of range
        assertThrows(IllegalArgumentException.class, () -> stockedStore.fire(6));
    }

    @Test
    void fire_FromEmptyStore_ThrowsException() {
        // Arrange: an empty store (0 torpedoes)
        TorpedoStore emptyStore = new TorpedoStore(0);

        // Act + Assert: any firing request exceeds the available count
        assertThrows(IllegalArgumentException.class, () -> emptyStore.fire(1));
    }


    @Test
    void isEmpty_NewStockedStore_ReturnsFalse() {
        assertFalse(stockedStore.isEmpty());
    }

    @Test
    void isEmpty_ZeroTorpedoStore_ReturnsTrue() {
        TorpedoStore emptyStore = new TorpedoStore(0);
        assertTrue(emptyStore.isEmpty());
    }

    @Test
    void getTorpedoCount_ReflectsInitialValue() {
        TorpedoStore store = new TorpedoStore(7);
        assertEquals(7, store.getTorpedoCount());
    }
}

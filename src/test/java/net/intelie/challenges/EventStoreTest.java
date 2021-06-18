package net.intelie.challenges;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.intelie.challenges.solution.EventStoreSolution;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EventStoreTest {

    @Test
	public void test_insert() {
		EventStoreSolution eventStore = EventStoreSolution.getInstance();
		eventStore.insert(new Event("type", 0));

        Event Event = eventStore.query("type", 0, 1).current();
        assertEquals("type", Event.type());
        assertEquals(0, Event.timestamp());

	}

    @Test
	public void test_removeAll() {

		EventStoreSolution eventStore = EventStoreSolution.getInstance();

        eventStore.insert(new Event("type", 1));
        eventStore.insert(new Event("type", 2));
        eventStore.insert(new Event("type", 3));
        eventStore.insert(new Event("type", 4));

        EventIterator Events = eventStore.query("type", 0, 5);
        Event curreEvent;

        int i = 0;
        while (Events.moveNext()) {
            curreEvent = Events.current();
            assertEquals("type", curreEvent.type());
            i++;
        }

        assertEquals(5, i);
        eventStore.removeAll("type");

        Events = eventStore.query("type", 0, 5);
        assertFalse(Events.moveNext());
	}
    
}

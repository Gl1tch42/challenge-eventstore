package net.intelie.challenges;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.intelie.challenges.solution.EventStoreSolution;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EventInteratorTeste {

    @BeforeClass
	public static void setup() {
		EventStore eventStore = EventStoreSolution.getInstance();
		String type = "iteratorTest0";
		long timestamp = 0;
		for (int i = 0; i < 10; i++) {

			for (int x = 0; x < 10; x++) {
				eventStore.insert(new Event(type + i, timestamp++));
			}

			timestamp = (timestamp - 10) + 100;
		}
	}

	@Test
	public void moveNext() {

		EventStore eventStore = EventStoreSolution.getInstance();
		String type = "iteratorTest0";
		int count = 0;
		EventIterator eventIterator = null;

		for (int i = 0; i < 10; i++) {
			eventIterator = eventStore.query(type + i, 0, 1000);

			while (eventIterator.moveNext()) {
				assertEquals(type + i, eventIterator.current().type());
				count++;
			}

		}

		assertEquals(100, count);
		assertFalse(eventIterator.moveNext());
	}

	@Test(expected = IllegalStateException.class)
	public void current() {

		EventIterator eventIterator = EventStoreSolution.getInstance().query("iteratorTest00", 0, 1);
		assertTrue(eventIterator.moveNext());

		Event currentEvent = eventIterator.current();
		assertNotNull(currentEvent);
		assertEquals("iteratorTest00", currentEvent.type());
		assertEquals(0, currentEvent.timestamp());

		assertFalse(eventIterator.moveNext());

		eventIterator.current();
	}

	@Test(expected = IllegalStateException.class)
	public void remove() {

		EventIterator eventIterator = EventStoreSolution.getInstance().query("iteratorTest00", 0, 1);
		assertTrue(eventIterator.moveNext());
		
		eventIterator.remove();
		assertFalse(eventIterator.moveNext());

		eventIterator.current();

	}
    
}

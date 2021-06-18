package net.intelie.challenges;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.intelie.challenges.solution.EventStoreSolution;

public class EventStoreTest {

    @Test
	public void test1_insert() {
		EventStoreSolution eventStore = new EventStoreSolution();
		eventStore.insert(new Event("type", 0));

        Event queryEvent = eventStore.query("type", 0, 0).current();
		assertEquals("type", queryEvent.type());
		assertEquals(0, queryEvent.timestamp());
	}

    @Test
	public void test20_removeAll() {
		EventStoreSolution eventStore = new  EventStoreSolution();
		eventStore.insert(new Event("type", 1));
		eventStore.insert(new Event("type", 2));
		eventStore.insert(new Event("type", 3));

		EventIterator queryEventIt = eventStore.query("type01", 0, 104);
		Event currentEvent;
		int count = 0;

		while (queryEventIt.moveNext()) {
			currentEvent = queryEventIt.current();
			assertEquals("type01", currentEvent.type());
			count++;
		}

		assertEquals(4, count);
		eventStore.removeAll("type01");

		queryEventIt = eventStore.query("type01", 0, 104);
		assertFalse(queryEventIt.moveNext());
	}
    
}

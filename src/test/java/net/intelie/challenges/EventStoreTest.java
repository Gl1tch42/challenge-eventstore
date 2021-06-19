package net.intelie.challenges;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.intelie.challenges.solution.EventStoreSolution;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EventStoreTest {

    @Test
	public void insert() {
		EventStoreSolution eventStore = EventStoreSolution.getInstance();
		eventStore.insert(new Event("type", 0));

        Event Event = eventStore.query("type", 0, 1).current();
        assertEquals("type", Event.type());
        assertEquals(0, Event.timestamp());

	}

    @Test
	public void removeAll() {

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

    @Test
	public void query() {

		EventStore eventStore = EventStoreSolution.getInstance();
		String type = "type";
		long timestamp = 0;

		for (int i = 0; i < 10; i++) {
			for (int x = 0; x < 10; x++) {
				eventStore.insert(new Event(type + i, timestamp++));
			}
			timestamp = (timestamp - 10) + 100;
		}

		int y = 0;

		for (int i = 0; i < 10; i++) {
			EventIterator eventIterator = eventStore.query(type + i, 0, 1000);
			while (eventIterator.moveNext()) {
				assertEquals(type + i, eventIterator.current().type());
				y++;
			}
		}
		assertEquals(100, y);
	}

    @Test
	public void concurrency() throws Exception {
        
        // We will have 1.000 threads adding new events concurrently
		ExecutorService executor = Executors.newFixedThreadPool(1_000);
		List<Callable<Boolean>> tasksList = new ArrayList<Callable<Boolean>>();
		long beforeStart = System.currentTimeMillis();
		long tenMinAheadTimestamp = System.currentTimeMillis() + 86_400_000;

		Callable<Boolean> task100 = () -> {
			EventStoreSolution.getInstance().insert(new Event("type100", System.currentTimeMillis()));
			return true;
		};
		
		Callable<Boolean> task101 = () -> {
			EventStoreSolution.getInstance().query("type100", beforeStart, tenMinAheadTimestamp);
			return true;
		};

		Callable<Boolean> task200 = () -> {
			EventStoreSolution.getInstance().insert(new Event("type200", System.currentTimeMillis()));
			return true;
		};
		
		Callable<Boolean> task201 = () -> {
			EventStoreSolution.getInstance().query("type200", beforeStart, tenMinAheadTimestamp);
			return true;
		};

		Callable<Boolean> task300 = () -> {
			EventStoreSolution.getInstance().insert(new Event("type300", System.currentTimeMillis()));
			return true;
		};
		
		Callable<Boolean> task301 = () -> {
			EventStoreSolution.getInstance().query("type300", beforeStart, tenMinAheadTimestamp);
			return true;
		};

		Callable<Boolean> task400 = () -> {
			EventStoreSolution.getInstance().insert(new Event("type400", System.currentTimeMillis()));
			return true;
		};
		
		Callable<Boolean> task401 = () -> {
			EventStoreSolution.getInstance().query("type400", beforeStart, tenMinAheadTimestamp);
			return true;
		};

		Callable<Boolean> task500 = () -> {
			EventStoreSolution.getInstance().insert(new Event("type500", System.currentTimeMillis()));
			return true;
		};
		
		Callable<Boolean> task501 = () -> {
			EventStoreSolution.getInstance().query("type500", beforeStart, tenMinAheadTimestamp);
			return true;
		};

		Callable<Boolean> task600 = () -> {
			EventStoreSolution.getInstance().insert(new Event("type600", System.currentTimeMillis()));
			return true;
		};
		
		Callable<Boolean> task601 = () -> {
			EventStoreSolution.getInstance().query("type600", beforeStart, tenMinAheadTimestamp);
			return true;
		};

		Callable<Boolean> task602 = () -> {
			EventStoreSolution.getInstance().removeAll("type600");
			return true;
		};
		

		/*
		 * Each task, for each event type, will be repeated 50.000 times concurrently.
		 * It means we should have 20.000 events for each type in the end.
		 */
		for (int i = 0; i < 20_000; i++) {
			tasksList.add(task100);
			tasksList.add(task101);
			tasksList.add(task200);
			tasksList.add(task201);
			tasksList.add(task300);
			tasksList.add(task301);
			tasksList.add(task400);
			tasksList.add(task401);
			tasksList.add(task500);
			tasksList.add(task501);
			tasksList.add(task600);
			tasksList.add(task601);
			tasksList.add(task602);
		}
					
		executor.invokeAll(tasksList);
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.MINUTES);

		
		EventIterator eventIterator;
		Event currentEvent;
		int count = 0;

		for (int i = 100; i < 500; i += 100) {
			eventIterator = EventStoreSolution.getInstance().query("type" + i, beforeStart, tenMinAheadTimestamp);

			while (eventIterator.moveNext()) {
				currentEvent = eventIterator.current();

				assertEquals("type" + i, currentEvent.type());
				assertTrue(currentEvent.timestamp() >= beforeStart
						&& currentEvent.timestamp() < tenMinAheadTimestamp);

				count++;
			}

			//test if we have 20_000 events for each type
			assertEquals(20_000, count);
			count = 0;
		}

    }
}

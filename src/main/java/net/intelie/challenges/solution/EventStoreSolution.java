package net.intelie.challenges.solution;

import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import net.intelie.challenges.Event;
import net.intelie.challenges.EventIterator;
import net.intelie.challenges.EventStore;

public class EventStoreSolution implements EventStore {

    ConcurrentSkipListMap<Long, Event> memory = new ConcurrentSkipListMap<Long, Event>();
	
	@Override
	public void insert(Event event) {
		memory.put(event.timestamp(), event);
	}

	@Override
	public void removeAll(String type) {
		EventIterator iterator = new EventInteratorSolution(memory.entrySet());
		while(iterator.moveNext()) {
			Event currentInteratorEvent = iterator.current();
			if(currentInteratorEvent.type().equals(type)) {
				iterator.remove();
			}
		}
		try {
			iterator.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public EventIterator query(String type, long startTime, long endTime) {

		ConcurrentNavigableMap<Long, Event> queryMap = memory.subMap(startTime, true, endTime, false);
        ConcurrentSkipListMap<Long, Event> queryEvents = new ConcurrentSkipListMap<Long, Event>();
		EventIterator iterator = new EventInteratorSolution(queryMap.entrySet());

		while(iterator.moveNext()) {
			Event currentInteratorEvent = iterator.current();
			if(currentInteratorEvent.type().equals(type)) {
				queryEvents.put(currentInteratorEvent.timestamp(), currentInteratorEvent);
			}
		}

		try {
			iterator.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		iterator = new EventInteratorSolution(queryEvents.entrySet());
		return iterator;
	}
    
}

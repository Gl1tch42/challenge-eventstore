package net.intelie.challenges.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import net.intelie.challenges.Event;
import net.intelie.challenges.EventIterator;
import net.intelie.challenges.EventStore;

public class EventStoreSolution implements EventStore {

    private static EventStoreSolution instance;

	private static final Map<String, List<Event>> STORAGE = new ConcurrentHashMap<String, List<Event>>();

	private ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

	
	public static EventStoreSolution getInstance() {
		if (instance == null) {
			synchronized (EventStoreSolution.class) {
				if (instance == null) {
					instance = new EventStoreSolution();
				}
			}
		}

		return instance;
	}

	private EventStoreSolution() {

	}

	@Override
	public void insert(Event event) {
		if (event == null || event.type() == null)
			return;

		reentrantReadWriteLock.writeLock().lock();
		try {

			if (STORAGE.containsKey(event.type())) {
				STORAGE.get(event.type()).add(event);

			} else {
				List<Event> eventsList = new ArrayList<Event>();
				eventsList.add(event);
				STORAGE.put(event.type(), eventsList);
			}

		} finally {
			reentrantReadWriteLock.writeLock().unlock();
		}

	}

	@Override
	public void removeAll(String type) {
		if (type == null)
			return;

		reentrantReadWriteLock.writeLock().lock();
		try {
			STORAGE.remove(type);
		} finally {
			reentrantReadWriteLock.writeLock().unlock();
		}

	}

	@Override
	public synchronized EventIterator query(String type, long startTime, long endTime) {
		if (type == null){
            return new EventInteratorSolution(new ArrayList<Event>());
        }

		reentrantReadWriteLock.readLock().lock();
		try {

			if (STORAGE.containsKey(type)) {

				List<Event> eventsQueryList = new ArrayList<>(STORAGE.get(type));
				return new EventInteratorSolution(eventsQueryList.stream()
						.filter(event -> event.timestamp() >= startTime && event.timestamp() < endTime)
						.collect(Collectors.toList()));

			} else {
				return new EventInteratorSolution(new ArrayList<Event>());
			}

		} finally {
			reentrantReadWriteLock.readLock().unlock();
		}

	}
    
}

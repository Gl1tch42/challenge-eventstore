package net.intelie.challenges.solution;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import net.intelie.challenges.Event;
import net.intelie.challenges.EventIterator;

public class EventInteratorSolution implements EventIterator {
    
    private Iterator<Entry<Long, Event>> iterator;
	private Event currentInteratorEvent = null;

    public EventInteratorSolution(Set<Entry<Long, Event>> events) {
        this.iterator = events.iterator();
	}

    public boolean moveNext() {
        if(iterator.hasNext()) {
			currentInteratorEvent =  iterator.next().getValue();
			return true;
		}
		return false;
	}

    public void close() throws Exception {
	}

    public Event current() {
		return currentInteratorEvent;
	}

	public void remove() {
        iterator.remove();
	}

}

package net.intelie.challenges.solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.intelie.challenges.Event;
import net.intelie.challenges.EventIterator;

public class EventInteratorSolution implements EventIterator {
    
	private final Iterator<Event> EVENTS;

	public EventInteratorSolution(Collection<Event> events) {

		if (events != null) {
			EVENTS = events.iterator();
		}	
		else {
			EVENTS = new ArrayList<Event>().iterator();
		}
			
	}

	@Override
	public boolean moveNext() {
		return EVENTS.hasNext();
	}

	@Override
	public Event current() {

		if (!this.moveNext()) {
			throw new IllegalStateException();
		}
			
		return EVENTS.next();
	}

	@Override
	public void remove() {
		EVENTS.remove();
	}

	@Override
	public void close() throws Exception {
	}

}

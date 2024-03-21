package processor.memorysystem;

import configuration.Configuration;
import generic.*;
import processor.Clock;

public class MainMemory implements Element {
	int[] memory;
	
	public MainMemory()
	{
		memory = new int[65536];
	}
	
	public int getWord(int address)
	{
		return memory[address];
	}
	
	public void setWord(int address, int value)
	{
		memory[address] = value;
	}
	
	public String getContentsAsString(int startingAddress, int endingAddress)
	{
		if(startingAddress == endingAddress)
			return "";
		
		StringBuilder sb = new StringBuilder();
		sb.append("\nMain Memory Contents:\n\n");
		for(int i = startingAddress; i <= endingAddress; i++)
		{
			sb.append(i + "\t\t: " + memory[i] + "\n");
		}
		sb.append("\n");
		return sb.toString();
	}

	@Override
	public void handleEvent(generic.Event e) {
		if (e.getEventType() == Event.EventType.MemoryRead){
			MemoryReadEvent event = (MemoryReadEvent) e;
			Simulator.getEventQueue().addEvent(new MemoryResponseEvent(
					Clock.getCurrentTime() + Configuration.mainMemoryLatency,
					(Element) this,
					(Element) event.getRequestingElement(),
					getWord(event.getAddressToReadFrom())
			));
		}

		if (e.getEventType() == Event.EventType.MemoryWrite){
			MemoryWriteEvent event = (MemoryWriteEvent) e;
			setWord(event.getAddressToWriteTo(), event.getValue());
			Simulator.getEventQueue().addEvent(new MemoryResponseEvent(
					Clock.getCurrentTime() + Configuration.mainMemoryLatency,
					(Element) this,
					(Element) event.getRequestingElement(),
					event.getAddressToWriteTo()
			));
		}

	}

}

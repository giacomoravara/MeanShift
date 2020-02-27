package meansh;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIndex {
	private AtomicInteger index;
	
	public AtomicIndex() {
		index = new AtomicInteger(0);
		}
	
	public int getIndex() throws InterruptedException{
		return index.getAndIncrement();
	}
	

}

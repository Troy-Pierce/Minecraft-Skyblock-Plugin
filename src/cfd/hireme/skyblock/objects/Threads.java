package cfd.hireme.skyblock.objects;

import java.util.ArrayList;
import java.util.List;

public class Threads {
	private static List<Thread> threads = new ArrayList<Thread>();
	
	public static Thread createThread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.setPriority(Thread.MIN_PRIORITY);
		threads.add(thread);
		return thread;
	}
	@SuppressWarnings("deprecation")
	public static void clearThreads() {
		if(threads.size()>0) {
			for(Thread thread : threads) {
				if(!thread.isInterrupted()) {
					thread.stop();
				}
				if(thread.isAlive()) {
					thread.stop();
				}
				threads.remove(thread);
			}
		}
	}
}

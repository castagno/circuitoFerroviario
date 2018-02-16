package main;

import java.util.Collection;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockModified extends ReentrantLock {

	private static final long serialVersionUID = 1L;

	public Collection<Thread> getWaitingThreadsPublic(Condition cond) {
		return this.getWaitingThreads(cond);
	}

}

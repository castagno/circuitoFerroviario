package main;

import java.util.Date;
import java.util.concurrent.locks.Condition;

public class Monitor {
	private Integer lugaresMaquina;
	private Integer lugaresVagon;
	
	private final ReentrantLockModified lock = new ReentrantLockModified();
	
	private final Condition fullTren = lock.newCondition();
	private final Condition notFullTren = lock.newCondition();
	private final Condition emptyEstacion = lock.newCondition();
	private final Condition notFullMaquina = lock.newCondition();
	private final Condition notFullVagon = lock.newCondition();
	private final Condition notEmpty = lock.newCondition();
	
	public Monitor(Integer lugaresMaquina, Integer lugaresVagon) {
		this.lugaresMaquina = lugaresMaquina;
		this.lugaresVagon = lugaresVagon;
	}
	
	public void abordarTren() throws InterruptedException {
		lock.lock();
		
		try {
			while(lugaresMaquina == 0 && lugaresVagon == 0) {
				notFullTren.await();
			}
			
			Date actual = new Date();
			Long tiempoDormido = actual.getTime() - ((SubirPasajeros)Thread.currentThread()).getSleepTimeStamp().getTime();
			
			
			/*
			if(lock.getWaitingThreadsPublic(notFullTren).contains(Thread.currentThread())) {
				emptyEstacion.signal();
		 	}
			*/
			if(lugaresMaquina == 0 && lugaresVagon == 0 ) {
				fullTren.signal();
			} else {
				notFullTren.signalAll();
			}
			
			notFullMaquina.await();
		} finally {
			lock.unlock();
		}
	}
	
	public void descenderTren() throws InterruptedException {
		lock.lock();
		
		try {
			/*
			if(lock.getWaitingThreadsPublic(notFullTren).contains(Thread.currentThread())) {
				emptyEstacion.signal();
		 	}
			*/
			if(lugaresMaquina == 0 && lugaresVagon == 0) {
				fullTren.signal();
			} else {
				notFullTren.signal();
			}
			
			notFullMaquina.await();
		} finally {
			lock.unlock();
		}
	}
	
	private Integer getPasajeros(Long tiempo) {
		
		return 0;
	}
}
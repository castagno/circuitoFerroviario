package main;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;

public class Monitor {
	private Integer lugaresMaquina;
	private Integer lugaresVagon;
	
	private ArrayList<ArrayList<Integer>> matrizMas;
	private ArrayList<ArrayList<Integer>> matrizMenos;
	
	
	private final ReentrantLockModified lock = new ReentrantLockModified();
	
	/*
	private final Condition fullTren = lock.newCondition();
	private final Condition notFullTren = lock.newCondition();
	private final Condition emptyEstacion = lock.newCondition();
	private final Condition notFullMaquina = lock.newCondition();
	private final Condition notFullVagon = lock.newCondition();
	private final Condition notEmpty = lock.newCondition();
	*/
	
	private final Condition subidaEstacionA = lock.newCondition();
	private final Condition bajadaEstacionA = lock.newCondition();
	private final Condition subidaEstacionB = lock.newCondition();
	private final Condition bajadaEstacionB = lock.newCondition();
	private final Condition subidaEstacionC = lock.newCondition();
	private final Condition bajadaEstacionC = lock.newCondition();
	private final Condition subidaEstacionD = lock.newCondition();
	private final Condition bajadaEstacionD = lock.newCondition();

	
	public Monitor(Integer lugaresMaquina, Integer lugaresVagon, ArrayList<ArrayList<Integer>> matrizMas, ArrayList<ArrayList<Integer>> matrizMenos) {
		this.matrizMas = matrizMas;
		this.matrizMenos = matrizMas;
		this.lugaresMaquina = lugaresMaquina;
		this.lugaresVagon = lugaresVagon;
	}
	
	public void abordarTren() throws InterruptedException {
		lock.lock();
		
		try {
			
			
			while(lugaresMaquina == 0 && lugaresVagon == 0) {
				if(Thread.currentThread().getName().startsWith("Estacion A")) {
					subidaEstacionA.await();
				}
				if(Thread.currentThread().getName().startsWith("Estacion B")) {
					subidaEstacionB.await();
				}
				if(Thread.currentThread().getName().startsWith("Estacion C")) {
					subidaEstacionC.await();
				}
				if(Thread.currentThread().getName().startsWith("Estacion D")) {
					subidaEstacionD.await();
				}
			}
			
			int pasajeros = ((SubirPasajeros) Thread.currentThread()).getPasajerosEsperando();
			
			
			
			/*
			if(lock.getWaitingThreadsPublic(notFullTren).contains(Thread.currentThread())) {
				emptyEstacion.signal();
		 	}
		 	
			if(lugaresMaquina == 0 && lugaresVagon == 0 ) {
				fullTren.signal();
			} else {
				notFullTren.signalAll();
			}
			
			notFullMaquina.await();
			*/
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
			if(lugaresMaquina == 0 && lugaresVagon == 0) {
				fullTren.signal();
			} else {
				notFullTren.signal();
			}
			
			notFullMaquina.await();
			*/
		} finally {
			lock.unlock();
		}
	}
	
	
	
	private Integer getPasajeros(Long tiempo) {
		
		return 0;
	}
}
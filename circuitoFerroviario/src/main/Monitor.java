package main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.Condition;

public class Monitor {
	private Integer lugaresMaquina;
	private Integer lugaresVagon;
	
	private Integer[][] matrizMas;
	private Integer[][] matrizMenos;
	private Integer[] marcado;
	private LinkedHashMap<String, Integer> marcadoInicial;
	
	private final ReentrantLockModified lock = new ReentrantLockModified();
	
	/*
	private final Condition fullTren = lock.newCondition();
	private final Condition notFullTren = lock.newCondition();
	private final Condition emptyEstacion = lock.newCondition();
	private final Condition notFullMaquina = lock.newCondition();
	private final Condition notFullVagon = lock.newCondition();
	private final Condition notEmpty = lock.newCondition();
	*/
	
	private final Condition fullTrenOrEmptyEstacion = lock.newCondition();

	private final Condition subidaEstacionA = lock.newCondition();
	private final Condition bajadaEstacionA = lock.newCondition();
	private final Condition subidaEstacionB = lock.newCondition();
	private final Condition bajadaEstacionB = lock.newCondition();
	private final Condition subidaEstacionC = lock.newCondition();
	private final Condition bajadaEstacionC = lock.newCondition();
	private final Condition subidaEstacionD = lock.newCondition();
	private final Condition bajadaEstacionD = lock.newCondition();
	
	private final String trenEstacionAPlaza = "P13";
	private final String trenEstacionBPlaza = "P7";
	private final String trenEstacionCPlaza = "P10";
	private final String trenEstacionDPlaza = "P37";

	
	public Monitor(Integer lugaresMaquina, Integer lugaresVagon, Integer[][] matrizMas, Integer[][] matrizMenos, LinkedHashMap<String, Integer> marcado) {
		this.marcadoInicial = marcado;
		this.marcado = marcado.values().toArray(new Integer[marcado.values().size()]);
		for(int i = 0; i < this.marcado.length; i++) {
			System.out.print(" "+this.marcado[i]);
		}
		System.out.println(" ");
		
		this.matrizMas = matrizMas;
		this.matrizMenos = matrizMenos;
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

	public void continuarRecorridoTren(Integer[] vectorDisparo) throws InterruptedException {
		lock.lock();
		
		try {
			while(!dispararRed(vectorDisparo)) {
				fullTrenOrEmptyEstacion.await();
			}
			if(marcado[new ArrayList<>(marcadoInicial.keySet()).indexOf(trenEstacionAPlaza)] == 1) {
				subidaEstacionA.signal();
				bajadaEstacionA.signal();
			}
			if(marcado[new ArrayList<>(marcadoInicial.keySet()).indexOf(trenEstacionBPlaza)] == 1) {
				subidaEstacionB.signal();
				bajadaEstacionB.signal();
			}
			if(marcado[new ArrayList<>(marcadoInicial.keySet()).indexOf(trenEstacionCPlaza)] == 1) {
				subidaEstacionC.signal();
				bajadaEstacionC.signal();
			}
			if(marcado[new ArrayList<>(marcadoInicial.keySet()).indexOf(trenEstacionDPlaza)] == 1) {
				subidaEstacionD.signal();
				bajadaEstacionD.signal();
			}
		} finally {
			lock.unlock();
		}
	}
	
	private boolean dispararRed(Integer[] vectorDisparo) {
		Integer sumatoriaDisparo = 0;
		for(Integer disparo: vectorDisparo) {
			sumatoriaDisparo += disparo;
		}
		if(!sumatoriaDisparo.equals(1)) {
			return false;
		}
		
		System.out.println(" ");
		
		Integer[] postDisparo = new Integer[marcado.length];
		for(int i = 0; i < matrizMenos.length; i++) {
			postDisparo[i] = new Integer(marcado[i]);
			for (int j = 0; j < matrizMenos[i].length; j++) {
				if(j == 0) {
//					System.out.print(" "+marcado[i]+" - ");
				}
//				System.out.print(matrizMenos[i][j]+"x"+vectorDisparo[j]+" ");
				postDisparo[i] = postDisparo[i] - matrizMenos[i][j] * vectorDisparo[j];
			}
//			System.out.println(" ");
			System.out.print(" "+postDisparo[i]);
			if(postDisparo[i] < 0) {
				return false;
			}
		}
		System.out.println(" ");

		for(int i = 0; i < matrizMas.length; i++) {
			for (int j = 0; j < matrizMas[i].length; j++) {
				if(j == 0) {
//					System.out.print(" "+marcado[i]+" - ");
				}
//				System.out.print(matrizMenos[i][j]+"x"+vectorDisparo[j]+" ");
				postDisparo[i] = postDisparo[i] + matrizMas[i][j] * vectorDisparo[j];
			}
//			System.out.println(" ");
			System.out.print(" "+postDisparo[i]);
			if(postDisparo[i] < 0) {
				return false;
			}
		}
		System.out.println(" ");
		
		this.marcado = postDisparo;
		
		return true;
	}
}
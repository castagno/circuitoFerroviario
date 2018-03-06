package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {
	
	private Integer[][] matrizMas;
	private Integer[][] matrizMenos;
	private Integer[] marcado;
	private LinkedHashMap<String, Integer> marcadoInicial;
	private ArrayList<String> transiciones;
	private ArrayList<String> recorridoTren;
	private LinkedHashMap<String, Condition> colaCondicion;
	
	private final ReentrantLock lock = new ReentrantLock();
	
	/*
	private final Condition fullTren = lock.newCondition();
	private final Condition notFullTren = lock.newCondition();
	private final Condition emptyEstacion = lock.newCondition();
	private final Condition notFullMaquina = lock.newCondition();
	private final Condition notFullVagon = lock.newCondition();
	private final Condition notEmpty = lock.newCondition();
	*/
	
	/*
	 * 1) Disparo red con el disparo correspondiente a la transicion representada por la condicion a la que se notifico.
	 * 2) Si el disparo es completado pido Vs y Vc intersecto y uso politicas para decidir que condicion notificar (despertar hilo).
	 * 3) Si el disparo no es completado libera el lock y sale del monitor.
	 * 
	 * Nota: Las transiciones sensibilizadas que no tienen una cola de condicion asociada se disparan inmediatamente.
	 * 
	 */
	
	private final Condition fullTrenOrEmptyEstacion = lock.newCondition();
	private final Condition tiempoDeEspera = lock.newCondition();

	private final Condition subidaEstacionA = lock.newCondition();
	private final Condition bajadaEstacionA = lock.newCondition();
	private final Condition subidaEstacionB = lock.newCondition();
	private final Condition bajadaEstacionB = lock.newCondition();
	private final Condition subidaEstacionC = lock.newCondition();
	private final Condition bajadaEstacionC = lock.newCondition();
	private final Condition subidaEstacionD = lock.newCondition();
	private final Condition bajadaEstacionD = lock.newCondition();
	
	private final String trenEstacionAEspera = "P13";
	private final String trenEstacionBEspera = "P7";
	private final String trenEstacionCEspera = "P10";
	private final String trenEstacionDEspera = "P37";

	private final String trenEstacionAPartida = "P14";
	private final String trenEstacionBPartida = "P6";
	private final String trenEstacionCPartida = "P8";
	private final String trenEstacionDPartida = "P28";

	private final String trenEstacionAArribo = "P12";
	private final String trenEstacionBArribo = "P45";
	private final String trenEstacionCArribo = "P11";
	private final String trenEstacionDArribo = "P36";

	private final String vagon = "P18";
	private final String maquina = "P30";

	
	public Monitor(Integer lugaresMaquina, Integer lugaresVagon, Integer[][] matrizMas, Integer[][] matrizMenos, LinkedHashMap<String, Integer> marcado, ArrayList<String> transiciones) {
		this.marcadoInicial = marcado;
		this.marcado = marcado.values().toArray(new Integer[marcado.values().size()]);
		for(int i = 0; i < this.marcado.length; i++) {
			System.out.print(" "+this.marcado[i]);
		}
		System.out.println(" ");
		
		this.transiciones = transiciones;
		this.matrizMas = matrizMas;
		this.matrizMenos = matrizMenos;
		this.recorridoTren = secuenciaTransiciones();
		
		this.colaCondicion = new LinkedHashMap<>();
		this.colaCondicion.put(this.transiciones.get(0), fullTrenOrEmptyEstacion);
	}
	
	public void abordarTren() throws InterruptedException {
		lock.lock();
		
		try {
			
			
//			while(lugaresMaquina == 0 && lugaresVagon == 0) {
//				if(Thread.currentThread().getName().startsWith("Estacion A")) {
//					subidaEstacionA.await();
//				}
//				if(Thread.currentThread().getName().startsWith("Estacion B")) {
//					subidaEstacionB.await();
//				}
//				if(Thread.currentThread().getName().startsWith("Estacion C")) {
//					subidaEstacionC.await();
//				}
//				if(Thread.currentThread().getName().startsWith("Estacion D")) {
//					subidaEstacionD.await();
//				}
//			}
			
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

	public void continuarRecorridoTren() throws InterruptedException {
		lock.lock();
		
		try {
			while((	marcado[new ArrayList<>(marcadoInicial.keySet()).indexOf(trenEstacionAEspera)] == 1 || 
					marcado[new ArrayList<>(marcadoInicial.keySet()).indexOf(trenEstacionBEspera)] == 1 ||
					marcado[new ArrayList<>(marcadoInicial.keySet()).indexOf(trenEstacionCEspera)] == 1 ||
					marcado[new ArrayList<>(marcadoInicial.keySet()).indexOf(trenEstacionDEspera)] == 1)&&
					(new Date().getTime() - ((Tren)Thread.currentThread()).getTimeStamp().getTime()) < 10000
					) {
				tiempoDeEspera.awaitNanos((10000 - (new Date().getTime() - ((Tren)Thread.currentThread()).getTimeStamp().getTime())) * 1000);
			}
			
			while((	marcado[new ArrayList<>(marcadoInicial.keySet()).indexOf(trenEstacionAPartida)] == 1 && lock.getWaitQueueLength(subidaEstacionA) != 0 || 
					marcado[new ArrayList<>(marcadoInicial.keySet()).indexOf(trenEstacionBPartida)] == 1 && lock.getWaitQueueLength(subidaEstacionB) != 0 ||
					marcado[new ArrayList<>(marcadoInicial.keySet()).indexOf(trenEstacionCPartida)] == 1 &&	lock.getWaitQueueLength(subidaEstacionC) != 0 ||
					marcado[new ArrayList<>(marcadoInicial.keySet()).indexOf(trenEstacionDPartida)] == 1 &&	lock.getWaitQueueLength(subidaEstacionD) != 0) && 
					(marcado[new ArrayList<>(marcadoInicial.keySet()).indexOf(vagon)] != 0 || marcado[new ArrayList<>(marcadoInicial.keySet()).indexOf(maquina)] != 0)
					) {
				fullTrenOrEmptyEstacion.await();
			}
			
			if(		marcado[new ArrayList<>(marcadoInicial.keySet()).indexOf(trenEstacionAArribo)] == 1 || 
					marcado[new ArrayList<>(marcadoInicial.keySet()).indexOf(trenEstacionBArribo)] == 1 ||
					marcado[new ArrayList<>(marcadoInicial.keySet()).indexOf(trenEstacionCArribo)] == 1 ||
					marcado[new ArrayList<>(marcadoInicial.keySet()).indexOf(trenEstacionDArribo)] == 1) {
				((Tren)Thread.currentThread()).setTimeStamp(new Date());
			}
			

			Integer[] vectorDisparo = Collections.nCopies(transiciones.size(), 0).toArray(new Integer[0]);
			
			LinkedHashMap<String, Boolean> sensibilizadas = getSensibilizadas();
			for(String transicion: transiciones) {
				if(recorridoTren.contains(transicion) && sensibilizadas.get(transicion)) {
					vectorDisparo[this.transiciones.indexOf(transicion)] = 1;
				}
			}
			
			System.out.println(" ");
			for (Integer integer : vectorDisparo) {
				System.out.print(" "+integer);
			}
			System.out.println(" ");
			
			if(!dispararRed(vectorDisparo)) {
				return;
			}
			
			LinkedHashMap<String, Boolean> vectorSensibilizadas = getSensibilizadas();
			
			
			
		} finally {
			lock.unlock();
		}
	}
	
	private static ArrayList<String> secuenciaTransiciones(){
		ArrayList<String> secuenciaTransiciones = new ArrayList<>();
		secuenciaTransiciones.add("BAr");
		secuenciaTransiciones.add("BW");
		secuenciaTransiciones.add("BDe");
		secuenciaTransiciones.add("CAr");
		secuenciaTransiciones.add("CW");
		secuenciaTransiciones.add("CDe");
		secuenciaTransiciones.add("PNCDMW");
		secuenciaTransiciones.add("PNCDMR");
		secuenciaTransiciones.add("PNCDVW");
		secuenciaTransiciones.add("PNCDVR");
		secuenciaTransiciones.add("RCD");
		secuenciaTransiciones.add("DAr");
		secuenciaTransiciones.add("DW");
		secuenciaTransiciones.add("DDe");
		secuenciaTransiciones.add("AAr");
		secuenciaTransiciones.add("AW");
		secuenciaTransiciones.add("ADe");
		secuenciaTransiciones.add("PNABMW");
		secuenciaTransiciones.add("PNABMR");
		secuenciaTransiciones.add("PNABVW");
		secuenciaTransiciones.add("PNABVR");
		secuenciaTransiciones.add("RAB");
		
		return secuenciaTransiciones;
	}
	
	private LinkedHashMap<String, Boolean> getSensibilizadas(){
		LinkedHashMap<String, Boolean> transiciones = new LinkedHashMap<>();
		
		for (String transicion : this.transiciones) {
			Integer sign = 0;
			for(int i = 0; i < matrizMenos.length; i++) {
				sign = new Integer(marcado[i]) - matrizMenos[i][this.transiciones.indexOf(transicion)];
				transiciones.put(transicion, (transiciones.get(transicion)!=null? transiciones.get(transicion) : true) && !(sign < 0));
			}
		}
		
		return transiciones;
	}
	
	private boolean dispararRed(String transicion) {
		Integer[] vectorDisparo = Collections.nCopies(transiciones.size(), 0).toArray(new Integer[0]);
		vectorDisparo[transicion.indexOf(transicion)] = 1;
		
		return dispararRed(vectorDisparo);
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
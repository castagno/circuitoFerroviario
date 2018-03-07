package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
	private HashMap<String, Condition> colaCondicion;
	private LinkedHashMap<Integer, String> politicas;
	
	private final ReentrantLock lock = new ReentrantLock();
	
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
	
	/* Plazas */
	
	private final String trenEstacionAEspera = "ATW";
	private final String trenEstacionBEspera = "BTW";
	private final String trenEstacionCEspera = "CTW";
	private final String trenEstacionDEspera = "DTW";

	private final String trenEstacionAPartida = "ATR";
	private final String trenEstacionBPartida = "BTR";
	private final String trenEstacionCPartida = "CTR";
	private final String trenEstacionDPartida = "DTR";

	private final String trenEstacionAArribo = "RDAT";
	private final String trenEstacionBArribo = "RABT";
	private final String trenEstacionCArribo = "RBCT";
	private final String trenEstacionDArribo = "RCDT";

	private final String vagon = "VAG";
	private final String maquina = "MAQ";
	
	/* Transiciones */
	
	private final String tranTrenEsperandoA = "AW";
	private final String tranTrenEsperandoB = "BW";
	private final String tranTrenEsperandoC = "CW";
	private final String tranTrenEsperandoD = "DW";
	
	private final String tranTrenLlenoEstacionVaciaA = "ADe";
	private final String tranTrenLlenoEstacionVaciaB = "BDe";
	private final String tranTrenLlenoEstacionVaciaC = "CDe";
	private final String tranTrenLlenoEstacionVaciaD = "DDe";
	
	private final String tranSubidaMaquinaEstacionA = "SMA";
	private final String tranSubidaMaquinaEstacionB = "SMB";
	private final String tranSubidaMaquinaEstacionC = "SMC";
	private final String tranSubidaMaquinaEstacionD = "SMD";

	private final String tranSubidaVagonEstacionA = "SVA";
	private final String tranSubidaVagonEstacionB = "SVB";
	private final String tranSubidaVagonEstacionC = "SVC";
	private final String tranSubidaVagonEstacionD = "SVD";

	
	private final String tranBajadaMaquinaBEstacionA = "BAMB";
	private final String tranBajadaMaquinaCEstacionA = "BAMC";
	private final String tranBajadaMaquinaDEstacionA = "BAMD";

	private final String tranBajadaVagonBEstacionA = "BAVB";
	private final String tranBajadaVagonCEstacionA = "BAVC";
	private final String tranBajadaVagonDEstacionA = "BAVD";

	
	private final String tranBajadaMaquinaAEstacionB = "BBMA";
	private final String tranBajadaMaquinaCEstacionB = "BBMC";
	private final String tranBajadaMaquinaDEstacionB = "BBMD";

	private final String tranBajadaVagonAEstacionB = "BBVA";
	private final String tranBajadaVagonCEstacionB = "BBVC";
	private final String tranBajadaVagonDEstacionB = "BBVD";

	
	private final String tranBajadaMaquinaAEstacionC = "BCMA";
	private final String tranBajadaMaquinaBEstacionC = "BCMB";
	private final String tranBajadaMaquinaDEstacionC = "BCMD";

	private final String tranBajadaVagonAEstacionC = "BCVA";
	private final String tranBajadaVagonBEstacionC = "BCVB";
	private final String tranBajadaVagonDEstacionC = "BCVD";

	
	private final String tranBajadaMaquinaAEstacionD = "BDMA";
	private final String tranBajadaMaquinaBEstacionD = "BDMB";
	private final String tranBajadaMaquinaCEstacionD = "BDMC";

	private final String tranBajadaVagonAEstacionD = "BDVA";
	private final String tranBajadaVagonBEstacionD = "BDVB";
	private final String tranBajadaVagonCEstacionD = "BDVC";

	
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
		
		this.colaCondicion = new HashMap<>();
		this.colaCondicion.put(tranTrenEsperandoA, tiempoDeEspera);
		this.colaCondicion.put(tranTrenEsperandoB, tiempoDeEspera);
		this.colaCondicion.put(tranTrenEsperandoC, tiempoDeEspera);
		this.colaCondicion.put(tranTrenEsperandoD, tiempoDeEspera);
		
		this.colaCondicion.put(tranTrenLlenoEstacionVaciaA, fullTrenOrEmptyEstacion);
		this.colaCondicion.put(tranTrenLlenoEstacionVaciaB, fullTrenOrEmptyEstacion);
		this.colaCondicion.put(tranTrenLlenoEstacionVaciaC, fullTrenOrEmptyEstacion);
		this.colaCondicion.put(tranTrenLlenoEstacionVaciaD, fullTrenOrEmptyEstacion);
		
		
		this.colaCondicion.put(tranSubidaMaquinaEstacionA, subidaEstacionA);
		this.colaCondicion.put(tranSubidaVagonEstacionA, subidaEstacionA);
		
		this.colaCondicion.put(tranSubidaMaquinaEstacionB, subidaEstacionB);
		this.colaCondicion.put(tranSubidaVagonEstacionB, subidaEstacionB);
		
		this.colaCondicion.put(tranSubidaMaquinaEstacionC, subidaEstacionC);
		this.colaCondicion.put(tranSubidaVagonEstacionC, subidaEstacionC);
		
		this.colaCondicion.put(tranSubidaMaquinaEstacionD, subidaEstacionD);
		this.colaCondicion.put(tranSubidaVagonEstacionD, subidaEstacionD);
		
		
		this.colaCondicion.put(tranBajadaMaquinaBEstacionA, bajadaEstacionA);
		this.colaCondicion.put(tranBajadaMaquinaCEstacionA, bajadaEstacionA);
		this.colaCondicion.put(tranBajadaMaquinaDEstacionA, bajadaEstacionA);
		this.colaCondicion.put(tranBajadaVagonBEstacionA, bajadaEstacionA);
		this.colaCondicion.put(tranBajadaVagonCEstacionA, bajadaEstacionA);
		this.colaCondicion.put(tranBajadaVagonDEstacionA, bajadaEstacionA);
		
		this.colaCondicion.put(tranBajadaMaquinaAEstacionB, bajadaEstacionB);
		this.colaCondicion.put(tranBajadaMaquinaCEstacionB, bajadaEstacionB);
		this.colaCondicion.put(tranBajadaMaquinaDEstacionB, bajadaEstacionB);
		this.colaCondicion.put(tranBajadaVagonAEstacionB, bajadaEstacionB);
		this.colaCondicion.put(tranBajadaVagonCEstacionB, bajadaEstacionB);
		this.colaCondicion.put(tranBajadaVagonDEstacionB, bajadaEstacionB);
		
		this.colaCondicion.put(tranBajadaMaquinaAEstacionC, bajadaEstacionC);
		this.colaCondicion.put(tranBajadaMaquinaBEstacionC, bajadaEstacionC);
		this.colaCondicion.put(tranBajadaMaquinaDEstacionC, bajadaEstacionC);
		this.colaCondicion.put(tranBajadaVagonAEstacionC, bajadaEstacionC);
		this.colaCondicion.put(tranBajadaVagonBEstacionC, bajadaEstacionC);
		this.colaCondicion.put(tranBajadaVagonDEstacionC, bajadaEstacionC);
		
		this.colaCondicion.put(tranBajadaMaquinaAEstacionD, bajadaEstacionD);
		this.colaCondicion.put(tranBajadaMaquinaBEstacionD, bajadaEstacionD);
		this.colaCondicion.put(tranBajadaMaquinaCEstacionD, bajadaEstacionD);
		this.colaCondicion.put(tranBajadaVagonAEstacionD, bajadaEstacionD);
		this.colaCondicion.put(tranBajadaVagonBEstacionD, bajadaEstacionD);
		this.colaCondicion.put(tranBajadaVagonCEstacionD, bajadaEstacionD);
		
		
		
		this.politicas = new LinkedHashMap<>();
		int index = 0;
		this.politicas.put(index++, tranBajadaMaquinaAEstacionD);
		this.politicas.put(index++, tranBajadaMaquinaBEstacionA);
		this.politicas.put(index++, tranBajadaMaquinaCEstacionB);
		this.politicas.put(index++, tranBajadaMaquinaDEstacionC);
		this.politicas.put(index++, tranBajadaVagonAEstacionD);
		this.politicas.put(index++, tranBajadaVagonBEstacionA);
		this.politicas.put(index++, tranBajadaVagonCEstacionB);
		this.politicas.put(index++, tranBajadaVagonDEstacionC);
		
		
		this.politicas.put(index++, tranBajadaMaquinaAEstacionB);
		this.politicas.put(index++, tranBajadaMaquinaAEstacionC);
		this.politicas.put(index++, tranBajadaMaquinaBEstacionC);
		this.politicas.put(index++, tranBajadaMaquinaBEstacionD);
		this.politicas.put(index++, tranBajadaMaquinaCEstacionA);
		this.politicas.put(index++, tranBajadaMaquinaCEstacionD);
		this.politicas.put(index++, tranBajadaMaquinaDEstacionA);
		this.politicas.put(index++, tranBajadaMaquinaDEstacionB);
		this.politicas.put(index++, tranBajadaVagonAEstacionB);
		this.politicas.put(index++, tranBajadaVagonAEstacionC);
		this.politicas.put(index++, tranBajadaVagonBEstacionC);
		this.politicas.put(index++, tranBajadaVagonBEstacionD);
		this.politicas.put(index++, tranBajadaVagonCEstacionA);
		this.politicas.put(index++, tranBajadaVagonCEstacionD);
		this.politicas.put(index++, tranBajadaVagonDEstacionA);
		this.politicas.put(index++, tranBajadaVagonDEstacionB);
		
		
		this.politicas.put(index++, tranSubidaMaquinaEstacionA);
		this.politicas.put(index++, tranSubidaMaquinaEstacionB);
		this.politicas.put(index++, tranSubidaMaquinaEstacionC);
		this.politicas.put(index++, tranSubidaMaquinaEstacionD);
		this.politicas.put(index++, tranSubidaVagonEstacionA);
		this.politicas.put(index++, tranSubidaVagonEstacionB);
		this.politicas.put(index++, tranSubidaVagonEstacionC);
		this.politicas.put(index++, tranSubidaVagonEstacionD);
		
		
		this.politicas.put(index++, tranTrenLlenoEstacionVaciaA);
		this.politicas.put(index++, tranTrenLlenoEstacionVaciaB);
		this.politicas.put(index++, tranTrenLlenoEstacionVaciaC);
		this.politicas.put(index++, tranTrenLlenoEstacionVaciaD);
		
		this.politicas.put(index++, tranTrenEsperandoA);
		this.politicas.put(index++, tranTrenEsperandoB);
		this.politicas.put(index++, tranTrenEsperandoC);
		this.politicas.put(index++, tranTrenEsperandoD);

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
			

			LinkedHashMap<String, Boolean> preInterseccion = getInterseccionCondicion(getSensibilizadas(), lock);
			

			String[] prioritariasPrevias = politicas.values().toArray(new String[0]);
			for(String transicion: prioritariasPrevias) {
				if(preInterseccion.get(transicion)) {
					dispararRed(transicion);
					break;
				}
			}
			
			ArrayList<String> transicionesRemanentes = new ArrayList<String>(this.transiciones);
			for(String iterada: prioritariasPrevias) {
				transicionesRemanentes.remove(transicionesRemanentes.indexOf(iterada));
			}
			
			for(String transicionInmediata: transicionesRemanentes) {
				if(preInterseccion.get(transicionInmediata)) {
					dispararRed(transicionInmediata);
					break;
				}
			}
			
			
//			if(!dispararRed(vectorDisparo)) {
//				return;
//			}
			
			
			
			
			LinkedHashMap<String, Boolean> vectorInterseccion = getInterseccionCondicion(getSensibilizadas(), lock);
			

			String[] prioritarias = politicas.values().toArray(new String[0]);
			for(String transicion: prioritarias) {
				if(vectorInterseccion.get(transicion)) {
					colaCondicion.get(transicion).notify();
					return;
				}
			}
			
			ArrayList<String> transicionesRestantes = new ArrayList<String>(this.transiciones);
			for(String iterada: prioritarias) {
				transicionesRestantes.remove(transicionesRestantes.indexOf(iterada));
			}
			
			for(String transicionInmediata: transicionesRestantes) {
				if(vectorInterseccion.get(transicionInmediata)) {
//					dispararRed(transicionInmediata);
					return;
				}
			}
			
//			int transicionesADisparar = 0;
//			for(String transicion: transiciones) {
//				if(vectorInterseccion.get(transicion)) {
//					transicionesADisparar++;
//				}
//			}
//			
//			if(transicionesADisparar > 1) {
//				colaCondicion.get(politicas(vectorInterseccion)).notify();
//			} else {
//				
//			}
			
			
			
		} finally {
			lock.unlock();
		}
	}
	
	private String politicas(LinkedHashMap<String, Boolean> interseccion) {
		String[] prioritarias = politicas.keySet().toArray(new String[0]);
		
		for(String transicion: prioritarias) {
			if(interseccion.get(transicion)) {
				return transicion;
			}
		}

		return "";
	}
	
	private LinkedHashMap<String, Boolean> getInterseccionCondicion(LinkedHashMap<String, Boolean> vectorSensibilizadas, ReentrantLock lock) {
		LinkedHashMap<String, Boolean> interseccion = new LinkedHashMap<>();
		
		for(String transicion: this.transiciones) {
			interseccion.put(transicion, vectorSensibilizadas.get(transicion) && (colaCondicion.containsKey(transicion)? lock.getWaitQueueLength(colaCondicion.get(transicion)) != 0 : true));
		}
		
		return interseccion;
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
		System.out.println(transicion);
		Integer[] vectorDisparo = Collections.nCopies(transiciones.size(), 0).toArray(new Integer[0]);
		vectorDisparo[transiciones.indexOf(transicion)] = 1;
		
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
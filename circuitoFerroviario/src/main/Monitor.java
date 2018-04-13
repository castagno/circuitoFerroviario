package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor extends ConstantesComunes {
	
	private Integer[][] matrizMas;
	private Integer[][] matrizMenos;
	
	private Integer[] marcado;
	private LinkedHashMap<String, Integer> marcadoInicial;
	private ArrayList<String> plazas;
	
	private ArrayList<String> transiciones;
	private ArrayList<String> recorridoTren;
	private HashMap<String, Condition> colaCondicion;
	private LinkedHashMap<Integer, String> politicas;
	
	private LinkedHashMap<String, String> abordarTren;
	private LinkedHashMap<String, String> descenderTren;
	
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

	private final String trenEstacion = "TE";
	private final String trenEstacionA = trenEstacion+"A";
	private final String trenEstacionB = trenEstacion+"B";
	private final String trenEstacionC = trenEstacion+"C";
	private final String trenEstacionD = trenEstacion+"D";

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
	
	private final String vagA = "VA";
	private final String vagB = "VB";
	private final String vagC = "VC";
	private final String vagD = "VD";
	
	private final String maqA = "MA";
	private final String maqB = "MB";
	private final String maqC = "MC";
	private final String maqD = "MD";
	
	
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

	
	public Monitor(Integer[][] matrizMas, Integer[][] matrizMenos, LinkedHashMap<String, Integer> marcado, ArrayList<String> transiciones) {
		this.marcadoInicial = marcado;
		this.marcado = marcado.values().toArray(new Integer[marcado.values().size()]);
		this.plazas = new ArrayList<>(this.marcadoInicial.keySet());
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

		
		this.abordarTren = new LinkedHashMap<>();
		this.abordarTren.put(tranSubidaMaquinaEstacionA, trenEstacionA);
		this.abordarTren.put(tranSubidaVagonEstacionA, trenEstacionA);
		this.abordarTren.put(tranSubidaMaquinaEstacionB, trenEstacionB);
		this.abordarTren.put(tranSubidaVagonEstacionB, trenEstacionB);
		this.abordarTren.put(tranSubidaMaquinaEstacionC, trenEstacionC);
		this.abordarTren.put(tranSubidaVagonEstacionC, trenEstacionC);
		this.abordarTren.put(tranSubidaMaquinaEstacionD, trenEstacionD);
		this.abordarTren.put(tranSubidaVagonEstacionD, trenEstacionD);

		
		this.descenderTren = new LinkedHashMap<>();
		
		this.descenderTren.put(tranBajadaMaquinaBEstacionA, trenEstacionA);
		this.descenderTren.put(tranBajadaVagonBEstacionA, trenEstacionA);

		this.descenderTren.put(tranBajadaMaquinaCEstacionA, trenEstacionA);
		this.descenderTren.put(tranBajadaMaquinaDEstacionA, trenEstacionA);
		this.descenderTren.put(tranBajadaVagonCEstacionA, trenEstacionA);
		this.descenderTren.put(tranBajadaVagonDEstacionA, trenEstacionA);
		

		this.descenderTren.put(tranBajadaMaquinaCEstacionB, trenEstacionB);
		this.descenderTren.put(tranBajadaVagonCEstacionB, trenEstacionB);
		
		this.descenderTren.put(tranBajadaMaquinaAEstacionB, trenEstacionB);
		this.descenderTren.put(tranBajadaMaquinaDEstacionB, trenEstacionB);
		this.descenderTren.put(tranBajadaVagonAEstacionB, trenEstacionB);
		this.descenderTren.put(tranBajadaVagonDEstacionB, trenEstacionB);
		

		this.descenderTren.put(tranBajadaMaquinaDEstacionC, trenEstacionC);
		this.descenderTren.put(tranBajadaVagonDEstacionC, trenEstacionC);
		
		this.descenderTren.put(tranBajadaMaquinaAEstacionC, trenEstacionC);
		this.descenderTren.put(tranBajadaMaquinaBEstacionC, trenEstacionC);
		this.descenderTren.put(tranBajadaVagonAEstacionC, trenEstacionC);
		this.descenderTren.put(tranBajadaVagonBEstacionC, trenEstacionC);
		

		this.descenderTren.put(tranBajadaMaquinaAEstacionD, trenEstacionD);
		this.descenderTren.put(tranBajadaVagonAEstacionD, trenEstacionD);
		
		this.descenderTren.put(tranBajadaMaquinaBEstacionD, trenEstacionD);
		this.descenderTren.put(tranBajadaMaquinaCEstacionD, trenEstacionD);
		this.descenderTren.put(tranBajadaVagonBEstacionD, trenEstacionD);
		this.descenderTren.put(tranBajadaVagonCEstacionD, trenEstacionD);
		
	}
	
	public void continuarRecorridoTren() throws InterruptedException {
		lock.lock();
		
		try {
			while((	marcado[plazas.indexOf(trenEstacionAEspera)] == 1 || 
					marcado[plazas.indexOf(trenEstacionBEspera)] == 1 ||
					marcado[plazas.indexOf(trenEstacionCEspera)] == 1 ||
					marcado[plazas.indexOf(trenEstacionDEspera)] == 1)&&
					(new Date().getTime() - ((Tren)Thread.currentThread()).getTimeStamp().getTime()) < 10000
					) {
				tiempoDeEspera.awaitNanos((10000 - (new Date().getTime() - ((Tren)Thread.currentThread()).getTimeStamp().getTime())) * 1000);
			}
			
			while((	marcado[plazas.indexOf(trenEstacionAPartida)] == 1 && lock.getWaitQueueLength(subidaEstacionA) != 0 || 
					marcado[plazas.indexOf(trenEstacionBPartida)] == 1 && lock.getWaitQueueLength(subidaEstacionB) != 0 ||
					marcado[plazas.indexOf(trenEstacionCPartida)] == 1 && lock.getWaitQueueLength(subidaEstacionC) != 0 ||
					marcado[plazas.indexOf(trenEstacionDPartida)] == 1 && lock.getWaitQueueLength(subidaEstacionD) != 0) && 
					(marcado[plazas.indexOf(vagon)] != 0 || marcado[plazas.indexOf(maquina)] != 0)
					) {
				fullTrenOrEmptyEstacion.await();
			}
			
			if(		marcado[plazas.indexOf(trenEstacionAArribo)] == 1 || 
					marcado[plazas.indexOf(trenEstacionBArribo)] == 1 ||
					marcado[plazas.indexOf(trenEstacionCArribo)] == 1 ||
					marcado[plazas.indexOf(trenEstacionDArribo)] == 1) {
				((Tren)Thread.currentThread()).setTimeStamp(new Date());
			}
			
			String disparada = null;
			Boolean disparoRealizado = false;
			ArrayList<String> prioritarias = new ArrayList<>(Arrays.asList(politicas.values().toArray(new String[politicas.values().size()])));
			
			LinkedHashMap<String, Boolean> preSensibilizadas = getSensibilizadas();
			for(String transicion: prioritarias) {
				if(preSensibilizadas.get(transicion) && recorridoTren.contains(transicion)) {
					disparoRealizado = dispararRed(transicion);
					disparada = transicion;
					break;
				}
			}
			
			if(!disparoRealizado) {
				for(String transicion: transiciones) {
					if(preSensibilizadas.get(transicion) && (!prioritarias.contains(transicion))) {
						dispararRed(transicion);
						disparada = transicion;
						break;
					}
				}
			}
			
			
			/* PostDisparo se busca en las colas de condicion el siguiente hilo a despertar */
			
			LinkedHashMap<String, Boolean> vectorSensicilizadas = getSensibilizadas();
			LinkedHashMap<String, Boolean> vectorInterseccion = getInterseccionCondicion(vectorSensicilizadas, lock);
			for(String transicion: prioritarias) {
				if(vectorInterseccion.get(transicion) || (transicion.equals(disparada) && vectorSensicilizadas.get(disparada))) {
					colaCondicion.get(transicion).signal();
					return;
				}
			}
			
			for(String transicion: transiciones) {
				if(vectorInterseccion.get(transicion) && !prioritarias.contains(transicion)) {
					dispararRed(transicion);
					return;
				}
			}
			
		} finally {
			lock.unlock();
		}
	}
	
	public void abordarTren() throws InterruptedException {
		lock.lock();
		
		String threadName = Thread.currentThread().getName();
		
		try {
			while(	trenEstacionA.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionA)] == 0 ||
					marcado[plazas.indexOf(maquina)] == 0 && marcado[plazas.indexOf(vagon)] == 0) ) {
				subidaEstacionA.await();
			}
			while(	trenEstacionB.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionB)] == 0 || 
					marcado[plazas.indexOf(maquina)] == 0 && marcado[plazas.indexOf(vagon)] == 0) ) {
				subidaEstacionB.await();
			}
			while(	trenEstacionC.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionC)] == 0 || 
					marcado[plazas.indexOf(maquina)] == 0 && marcado[plazas.indexOf(vagon)] == 0) ) {
				subidaEstacionC.await();
			}
			while(	trenEstacionD.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionD)] == 0 || 
					marcado[plazas.indexOf(maquina)] == 0 && marcado[plazas.indexOf(vagon)] == 0) ) {
				subidaEstacionD.await();
			}

			Integer pasajeros = ((SubirPasajeros) Thread.currentThread()).getPasajeros();
			System.out.println("\n"+pasajeros+"\n");
			if(pasajeros > 0) {
				boolean disparoExitoso = false;
				ArrayList<String> listaSubidas = new ArrayList<>(Arrays.asList(abordarTren.keySet().toArray(new String[abordarTren.keySet().size()])));
				for(String subida: listaSubidas) {
					System.out.println(abordarTren.get(subida) +" "+ threadName.substring(threadName.length() - 1));
					if(		marcado[plazas.indexOf(subida.startsWith("SM")? maquina : vagon)] != 0 && 
							marcado[plazas.indexOf(trenEstacion + threadName.substring(threadName.length() - 1))] == 1 && 
							abordarTren.get(subida).endsWith(threadName.substring(threadName.length() - 1))) {
						disparoExitoso = dispararRed(subida);
						if(disparoExitoso) {
							break;
						}
					}
				}
				
				if(disparoExitoso) {
					((SubirPasajeros) Thread.currentThread()).setPasajeros(pasajeros - 1);
				}
			}
			
			ArrayList<String> prioritarias = new ArrayList<>(Arrays.asList(politicas.values().toArray(new String[politicas.values().size()])));
			LinkedHashMap<String, Boolean> vectorInterseccion = getInterseccionCondicion(getSensibilizadas(), lock);
			for(String transicion: prioritarias) {
				if(vectorInterseccion.get(transicion)) {
					colaCondicion.get(transicion).signal();
					return;
				}
			}
			
			for(String transicion: transiciones) {
				if(vectorInterseccion.get(transicion) && !prioritarias.contains(transicion)) {
					dispararRed(transicion);
					return;
				}
			}
			
		} finally {
			lock.unlock();
		}
	}

	public void descenderTren() throws InterruptedException {
		lock.lock();

		String threadName = Thread.currentThread().getName();
		
		try {
			while(	trenEstacionA.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionA)] == 0 || 
					marcado[plazas.indexOf(maqB)] == 0 && marcado[plazas.indexOf(vagB)] == 0 && 
					marcado[plazas.indexOf(maqC)] == 0 && marcado[plazas.indexOf(vagC)] == 0 && 
					marcado[plazas.indexOf(maqD)] == 0 && marcado[plazas.indexOf(vagD)] == 0) ) {
				subidaEstacionA.await();
			}
			while(	trenEstacionB.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionB)] == 0 || 
					marcado[plazas.indexOf(maqA)] == 0 && marcado[plazas.indexOf(vagA)] == 0 && 
					marcado[plazas.indexOf(maqC)] == 0 && marcado[plazas.indexOf(vagC)] == 0 && 
					marcado[plazas.indexOf(maqD)] == 0 && marcado[plazas.indexOf(vagD)] == 0) ) {
				subidaEstacionB.await();
			}
			while(	trenEstacionC.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionC)] == 0 ||  
					marcado[plazas.indexOf(maqA)] == 0 && marcado[plazas.indexOf(vagA)] == 0 && 
					marcado[plazas.indexOf(maqB)] == 0 && marcado[plazas.indexOf(vagB)] == 0 && 
					marcado[plazas.indexOf(maqD)] == 0 && marcado[plazas.indexOf(vagD)] == 0) ) {
				subidaEstacionC.await();
			}
			while(	trenEstacionD.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionD)] == 0 || 
					marcado[plazas.indexOf(maqA)] == 0 && marcado[plazas.indexOf(vagA)] == 0 && 
					marcado[plazas.indexOf(maqB)] == 0 && marcado[plazas.indexOf(vagB)] == 0 && 
					marcado[plazas.indexOf(maqC)] == 0 && marcado[plazas.indexOf(vagC)] == 0) ) {
				subidaEstacionD.await();
			}
			
			
		} finally {
			lock.unlock();
		}
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
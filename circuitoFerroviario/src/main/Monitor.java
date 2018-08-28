package main;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
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
	private Integer[][] matrizInhibicion;
	
	private Integer[] marcado;
	private LinkedHashMap<String, Integer> marcadoInicial;
	private ArrayList<String> plazas;
	private LinkedHashMap<String, Integer> printOrder;
	
	private ArrayList<String> transiciones;
	private LinkedHashMap<String, Condition> colaCondicion;
	private ArrayList<String> transicionesGeneradoras;
	
	private LinkedHashMap<String, String> abordarTren;
	private LinkedHashMap<String, String> descenderTren;
	
	private ArrayList<String> estaciones;
	private LinkedHashMap<String, Date> ultimaSubidaEstacion;
	private Date ultimoArrivoEstacion;
	private final ReentrantLock lock = new ReentrantLock(false);
	
	private HashMap<String, String> accionPorTransicion;
	private PrintWriter printWriter;
	private String printWtiterString = new String((new String("")).getBytes(), StandardCharsets.UTF_8);
	private ArrayList<String> printWriterArray = new ArrayList<>();
	private int printWriterCount = 0;
	
	private final Condition fullTrenOrEmptyEstacion = lock.newCondition();
	private final Condition tiempoDeEspera = lock.newCondition();
	private final Condition trenArriboEstacion = lock.newCondition();

	private final Condition subidaEstacionA = lock.newCondition();
	private final Condition bajadaEstacionA = lock.newCondition();
	private final Condition subidaEstacionB = lock.newCondition();
	private final Condition bajadaEstacionB = lock.newCondition();
	private final Condition subidaEstacionC = lock.newCondition();
	private final Condition bajadaEstacionC = lock.newCondition();
	private final Condition subidaEstacionD = lock.newCondition();
	private final Condition bajadaEstacionD = lock.newCondition();

	private final Condition pasajeroGeneradorEstacionA = lock.newCondition();
	private final Condition pasajeroGeneradorEstacionB = lock.newCondition();
	private final Condition pasajeroGeneradorEstacionC = lock.newCondition();
	private final Condition pasajeroGeneradorEstacionD = lock.newCondition();

	private final Condition pasoDeNivelTransitoGeneradorAB = lock.newCondition();
	private final Condition pasoDeNivelTransitoGeneradorCD = lock.newCondition();

	private final Condition pasoDeNivelMaquina = lock.newCondition();
	
	private final Condition pasoDeNivelVagon = lock.newCondition();

	private final Condition liberarBarreraPasoNivelAB = lock.newCondition();
	private final Condition liberarBarreraPasoNivelCD = lock.newCondition();
	
	/*
	 * 1) Disparo red con el disparo correspondiente a la transicion representada por la condicion a la que se notifico.
	 * 2) Si el disparo es completado pido Vs y Vc intersecto y uso politicas para decidir que condicion notificar (despertar hilo).
	 * 3) Si el disparo no es completado libera el lock y sale del monitor.
	 * 
	 * Nota: Las transiciones sensibilizadas que no tienen una cola de condicion asociada se disparan inmediatamente.
	 * 
	 */

	public Monitor(Integer[][] matrizMas, Integer[][] matrizMenos, Integer[][] matrizInhibicion, LinkedHashMap<String, Integer> marcado, ArrayList<String> transiciones, PrintWriter printWriter) {
		this.printWriter = printWriter;
		this.marcadoInicial = marcado;
		this.marcado = marcado.values().toArray(new Integer[marcado.values().size()]);
		this.plazas = new ArrayList<>(this.marcadoInicial.keySet());
		
		this.transiciones = transiciones;
		this.matrizMas = matrizMas;
		this.matrizMenos = matrizMenos;
		this.matrizInhibicion = matrizInhibicion;
		
		this.colaCondicion = new LinkedHashMap<>();
		
		this.colaCondicion.put(tranBajadaMaquinaAEstacionD, bajadaEstacionD);
		this.colaCondicion.put(tranBajadaMaquinaBEstacionA, bajadaEstacionA);
		this.colaCondicion.put(tranBajadaMaquinaCEstacionB, bajadaEstacionB);
		this.colaCondicion.put(tranBajadaMaquinaDEstacionC, bajadaEstacionC);
		this.colaCondicion.put(tranBajadaVagonAEstacionD, bajadaEstacionD);
		this.colaCondicion.put(tranBajadaVagonBEstacionA, bajadaEstacionA);
		this.colaCondicion.put(tranBajadaVagonCEstacionB, bajadaEstacionB);
		this.colaCondicion.put(tranBajadaVagonDEstacionC, bajadaEstacionC);

		this.colaCondicion.put(tranBajadaMaquinaAEstacionB, bajadaEstacionB);
		this.colaCondicion.put(tranBajadaMaquinaAEstacionC, bajadaEstacionC);
		this.colaCondicion.put(tranBajadaMaquinaBEstacionC, bajadaEstacionC);
		this.colaCondicion.put(tranBajadaMaquinaBEstacionD, bajadaEstacionD);
		this.colaCondicion.put(tranBajadaMaquinaCEstacionA, bajadaEstacionA);
		this.colaCondicion.put(tranBajadaMaquinaCEstacionD, bajadaEstacionD);
		this.colaCondicion.put(tranBajadaMaquinaDEstacionA, bajadaEstacionA);
		this.colaCondicion.put(tranBajadaMaquinaDEstacionB, bajadaEstacionB);
		this.colaCondicion.put(tranBajadaVagonAEstacionB, bajadaEstacionB);
		this.colaCondicion.put(tranBajadaVagonAEstacionC, bajadaEstacionC);
		this.colaCondicion.put(tranBajadaVagonBEstacionC, bajadaEstacionC);
		this.colaCondicion.put(tranBajadaVagonBEstacionD, bajadaEstacionD);
		this.colaCondicion.put(tranBajadaVagonCEstacionA, bajadaEstacionA);
		this.colaCondicion.put(tranBajadaVagonCEstacionD, bajadaEstacionD);
		this.colaCondicion.put(tranBajadaVagonDEstacionA, bajadaEstacionA);
		this.colaCondicion.put(tranBajadaVagonDEstacionB, bajadaEstacionB);

		this.colaCondicion.put(tranSubidaMaquinaEstacionA, subidaEstacionA);
		this.colaCondicion.put(tranSubidaMaquinaEstacionB, subidaEstacionB);
		this.colaCondicion.put(tranSubidaMaquinaEstacionC, subidaEstacionC);
		this.colaCondicion.put(tranSubidaMaquinaEstacionD, subidaEstacionD);
		this.colaCondicion.put(tranSubidaVagonEstacionA, subidaEstacionA);
		this.colaCondicion.put(tranSubidaVagonEstacionB, subidaEstacionB);
		this.colaCondicion.put(tranSubidaVagonEstacionC, subidaEstacionC);
		this.colaCondicion.put(tranSubidaVagonEstacionD, subidaEstacionD);

		this.colaCondicion.put(tranTrenLlenoA, fullTrenOrEmptyEstacion);
		this.colaCondicion.put(tranTrenLlenoB, fullTrenOrEmptyEstacion);
		this.colaCondicion.put(tranTrenLlenoC, fullTrenOrEmptyEstacion);
		this.colaCondicion.put(tranTrenLlenoD, fullTrenOrEmptyEstacion);
		
		this.colaCondicion.put(tranEstacionVaciaA, fullTrenOrEmptyEstacion);
		this.colaCondicion.put(tranEstacionVaciaB, fullTrenOrEmptyEstacion);
		this.colaCondicion.put(tranEstacionVaciaC, fullTrenOrEmptyEstacion);
		this.colaCondicion.put(tranEstacionVaciaD, fullTrenOrEmptyEstacion);
		
		this.colaCondicion.put(tranTrenEsperandoA, tiempoDeEspera);
		this.colaCondicion.put(tranTrenEsperandoB, tiempoDeEspera);
		this.colaCondicion.put(tranTrenEsperandoC, tiempoDeEspera);
		this.colaCondicion.put(tranTrenEsperandoD, tiempoDeEspera);

		this.colaCondicion.put(tranTrenArriboA, trenArriboEstacion);
		this.colaCondicion.put(tranTrenArriboB, trenArriboEstacion);
		this.colaCondicion.put(tranTrenArriboC, trenArriboEstacion);
		this.colaCondicion.put(tranTrenArriboD, trenArriboEstacion);
		
		this.colaCondicion.put(tranRecorridoTrenAB, fullTrenOrEmptyEstacion);
		this.colaCondicion.put(tranRecorridoTrenCD, fullTrenOrEmptyEstacion);
		
		this.colaCondicion.put(tranPasajerosAGenerador, pasajeroGeneradorEstacionA);
		this.colaCondicion.put(tranPasajerosBGenerador, pasajeroGeneradorEstacionB);
		this.colaCondicion.put(tranPasajerosCGenerador, pasajeroGeneradorEstacionC);
		this.colaCondicion.put(tranPasajerosDGenerador, pasajeroGeneradorEstacionD);
		
		
		
		this.colaCondicion.put(tranPasoNivelABMaquinaReady, liberarBarreraPasoNivelAB);
		this.colaCondicion.put(tranPasoNivelCDMaquinaReady, liberarBarreraPasoNivelCD);
		this.colaCondicion.put(tranPasoNivelABMaquinaWait, pasoDeNivelMaquina);
		this.colaCondicion.put(tranPasoNivelCDMaquinaWait, pasoDeNivelMaquina);
		
		this.colaCondicion.put(tranPasoNivelABVagonReady, liberarBarreraPasoNivelAB);
		this.colaCondicion.put(tranPasoNivelCDVagonReady, liberarBarreraPasoNivelCD);
		this.colaCondicion.put(tranPasoNivelABVagonWait, pasoDeNivelVagon);
		this.colaCondicion.put(tranPasoNivelCDVagonWait, pasoDeNivelVagon);
		
		this.colaCondicion.put(tranPasoNivelABTransitoReady, liberarBarreraPasoNivelAB);
		this.colaCondicion.put(tranPasoNivelCDTransitoReady, liberarBarreraPasoNivelCD);
		this.colaCondicion.put(tranPasoNivelABTransitoWait, liberarBarreraPasoNivelAB);
		this.colaCondicion.put(tranPasoNivelCDTransitoWait, liberarBarreraPasoNivelCD);
		
		this.colaCondicion.put(tranPasoNivelABTransitoGenerador, pasoDeNivelTransitoGeneradorAB);
		this.colaCondicion.put(tranPasoNivelCDTransitoGenerador, pasoDeNivelTransitoGeneradorCD);
		
		
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

		this.descenderTren.put(tranBajadaMaquinaCEstacionB, trenEstacionB);
		this.descenderTren.put(tranBajadaVagonCEstacionB, trenEstacionB);
		
		this.descenderTren.put(tranBajadaMaquinaDEstacionC, trenEstacionC);
		this.descenderTren.put(tranBajadaVagonDEstacionC, trenEstacionC);
		
		this.descenderTren.put(tranBajadaMaquinaAEstacionD, trenEstacionD);
		this.descenderTren.put(tranBajadaVagonAEstacionD, trenEstacionD);
		
		
		this.descenderTren.put(tranBajadaMaquinaCEstacionA, trenEstacionA);
		this.descenderTren.put(tranBajadaMaquinaDEstacionA, trenEstacionA);
		this.descenderTren.put(tranBajadaVagonCEstacionA, trenEstacionA);
		this.descenderTren.put(tranBajadaVagonDEstacionA, trenEstacionA);
		
		this.descenderTren.put(tranBajadaMaquinaAEstacionB, trenEstacionB);
		this.descenderTren.put(tranBajadaMaquinaDEstacionB, trenEstacionB);
		this.descenderTren.put(tranBajadaVagonAEstacionB, trenEstacionB);
		this.descenderTren.put(tranBajadaVagonDEstacionB, trenEstacionB);

		this.descenderTren.put(tranBajadaMaquinaAEstacionC, trenEstacionC);
		this.descenderTren.put(tranBajadaMaquinaBEstacionC, trenEstacionC);
		this.descenderTren.put(tranBajadaVagonAEstacionC, trenEstacionC);
		this.descenderTren.put(tranBajadaVagonBEstacionC, trenEstacionC);

		this.descenderTren.put(tranBajadaMaquinaBEstacionD, trenEstacionD);
		this.descenderTren.put(tranBajadaMaquinaCEstacionD, trenEstacionD);
		this.descenderTren.put(tranBajadaVagonBEstacionD, trenEstacionD);
		this.descenderTren.put(tranBajadaVagonCEstacionD, trenEstacionD);


		estaciones = new ArrayList<>(Arrays.asList(estacion));
		this.ultimaSubidaEstacion = new LinkedHashMap<>();
		Date fechaActual = new Date();
		ultimaSubidaEstacion.put(trenEstacionA, fechaActual);
		ultimaSubidaEstacion.put(trenEstacionB, fechaActual);
		ultimaSubidaEstacion.put(trenEstacionC, fechaActual);
		ultimaSubidaEstacion.put(trenEstacionD, fechaActual);
		
		ultimoArrivoEstacion = fechaActual;
		
		
		printOrder = new LinkedHashMap<>();
		printOrder.put(maq, plazas.indexOf(maq));
		printOrder.put(vag, plazas.indexOf(vag));
		
		printOrder.put(trenEstacionA, plazas.indexOf(trenEstacionA));
		printOrder.put(maqA, plazas.indexOf(maqA));
		printOrder.put(vagA, plazas.indexOf(vagA));
		printOrder.put(pasajerosEsperandoSubidaA, plazas.indexOf(pasajerosEsperandoSubidaA));
		
		printOrder.put(trenEstacionB, plazas.indexOf(trenEstacionB));
		printOrder.put(maqB, plazas.indexOf(maqB));
		printOrder.put(vagB, plazas.indexOf(vagB));
		printOrder.put(pasajerosEsperandoSubidaB, plazas.indexOf(pasajerosEsperandoSubidaB));
		
		printOrder.put(trenEstacionC, plazas.indexOf(trenEstacionC));
		printOrder.put(maqC, plazas.indexOf(maqC));
		printOrder.put(vagC, plazas.indexOf(vagC));
		printOrder.put(pasajerosEsperandoSubidaC, plazas.indexOf(pasajerosEsperandoSubidaC));
		
		printOrder.put(trenEstacionD, plazas.indexOf(trenEstacionD));
		printOrder.put(maqD, plazas.indexOf(maqD));
		printOrder.put(vagD, plazas.indexOf(vagD));
		printOrder.put(pasajerosEsperandoSubidaD, plazas.indexOf(pasajerosEsperandoSubidaD));
		
		printOrder.put(trenEstacionAArribo, plazas.indexOf(trenEstacionAArribo));
		printOrder.put(trenEstacionAEspera, plazas.indexOf(trenEstacionAEspera));
		printOrder.put(trenEstacionAPartida, plazas.indexOf(trenEstacionAPartida));
		printOrder.put(pasoNivelABMaquinaEsperando, plazas.indexOf(pasoNivelABMaquinaEsperando));
		printOrder.put(pasoNivelABMaquina, plazas.indexOf(pasoNivelABMaquina));
		printOrder.put(pasoNivelABMaquinaUnion, plazas.indexOf(pasoNivelABMaquinaUnion));
		printOrder.put(pasoNivelABVagonEsperando, plazas.indexOf(pasoNivelABVagonEsperando));
		printOrder.put(pasoNivelABVagon, plazas.indexOf(pasoNivelABVagon));
		printOrder.put(pasoNivelABVagonUnion, plazas.indexOf(pasoNivelABVagonUnion));
		
		printOrder.put(trenEstacionBEspera, plazas.indexOf(trenEstacionBEspera));
		printOrder.put(trenEstacionBPartida, plazas.indexOf(trenEstacionBPartida));
		printOrder.put(trenEstacionBArribo, plazas.indexOf(trenEstacionBArribo));
		
		printOrder.put(trenEstacionCArribo, plazas.indexOf(trenEstacionCArribo));
		printOrder.put(trenEstacionCEspera, plazas.indexOf(trenEstacionCEspera));
		printOrder.put(trenEstacionCPartida, plazas.indexOf(trenEstacionCPartida));
		printOrder.put(pasoNivelCDMaquinaEsperando, plazas.indexOf(pasoNivelCDMaquinaEsperando));
		printOrder.put(pasoNivelCDMaquina, plazas.indexOf(pasoNivelCDMaquina));
		printOrder.put(pasoNivelCDMaquinaUnion, plazas.indexOf(pasoNivelCDMaquinaUnion));
		printOrder.put(pasoNivelCDVagonEsperando, plazas.indexOf(pasoNivelCDVagonEsperando));
		printOrder.put(pasoNivelCDVagon, plazas.indexOf(pasoNivelCDVagon));
		printOrder.put(pasoNivelCDVagonUnion, plazas.indexOf(pasoNivelCDVagonUnion));
		
		printOrder.put(trenEstacionDEspera, plazas.indexOf(trenEstacionDEspera));
		printOrder.put(trenEstacionDPartida, plazas.indexOf(trenEstacionDPartida));
		printOrder.put(trenEstacionDArribo, plazas.indexOf(trenEstacionDArribo));
		
		printOrder.put(pasoNivelABBarrera, plazas.indexOf(pasoNivelABBarrera));
		printOrder.put(pasoNivelABTransitoEsperando, plazas.indexOf(pasoNivelABTransitoEsperando));
		printOrder.put(pasoNivelABTransito, plazas.indexOf(pasoNivelABTransito));
		
		printOrder.put(pasoNivelCDBarrera, plazas.indexOf(pasoNivelCDBarrera));
		printOrder.put(pasoNivelCDTransitoEsperando, plazas.indexOf(pasoNivelCDTransitoEsperando));
		printOrder.put(pasoNivelCDTransito, plazas.indexOf(pasoNivelCDTransito));
		
		
		transicionesGeneradoras = new ArrayList<>();
		transicionesGeneradoras.add(tranPasajerosAGenerador);
		transicionesGeneradoras.add(tranPasajerosBGenerador);
		transicionesGeneradoras.add(tranPasajerosCGenerador);
		transicionesGeneradoras.add(tranPasajerosDGenerador);
		transicionesGeneradoras.add(tranPasoNivelABTransitoGenerador);
		transicionesGeneradoras.add(tranPasoNivelCDTransitoGenerador);
		
		
		/* Accion por transicion */
		accionPorTransicion = new HashMap<>();
		accionPorTransicion.put(tranTrenArriboA, "Arribo del tren a la estación A");
		accionPorTransicion.put(tranTrenArriboB, "Arribo del tren a la estación B");
		accionPorTransicion.put(tranTrenArriboC, "Arribo del tren a la estación C");
		accionPorTransicion.put(tranTrenArriboD, "Arribo del tren a la estación D");
		
		accionPorTransicion.put(tranTrenEsperandoA, "Transcurrió el tiempo mínimo de espera del tren en la estación A");
		accionPorTransicion.put(tranTrenEsperandoB, "Transcurrió el tiempo mínimo de espera del tren en la estación B");
		accionPorTransicion.put(tranTrenEsperandoC, "Transcurrió el tiempo mínimo de espera del tren en la estación C");
		accionPorTransicion.put(tranTrenEsperandoD, "Transcurrió el tiempo mínimo de espera del tren en la estación D");
		
		accionPorTransicion.put(tranTrenLlenoA, "El tren no tiene lugares disponibles en la estación A");
		accionPorTransicion.put(tranTrenLlenoB, "El tren no tiene lugares disponibles en la estación B");
		accionPorTransicion.put(tranTrenLlenoC, "El tren no tiene lugares disponibles en la estación C");
		accionPorTransicion.put(tranTrenLlenoD, "El tren no tiene lugares disponibles en la estación D");

		accionPorTransicion.put(tranEstacionVaciaA, "No quedan pasajeros por subir en la estación A");
		accionPorTransicion.put(tranEstacionVaciaB, "No quedan pasajeros por subir en la estación B");
		accionPorTransicion.put(tranEstacionVaciaC, "No quedan pasajeros por subir en la estación C");
		accionPorTransicion.put(tranEstacionVaciaD, "No quedan pasajeros por subir en la estación D");
		
		

		accionPorTransicion.put(tranBajadaMaquinaAEstacionD, "Bajada obligatoria en la estación D de pasajeros de la Máquina subidos en la estación A");
		accionPorTransicion.put(tranBajadaMaquinaBEstacionA, "Bajada obligatoria en la estación A de pasajeros de la Máquina subidos en la estación B");
		accionPorTransicion.put(tranBajadaMaquinaCEstacionB, "Bajada obligatoria en la estación B de pasajeros de la Máquina subidos en la estación C");
		accionPorTransicion.put(tranBajadaMaquinaDEstacionC, "Bajada obligatoria en la estación C de pasajeros de la Maquina subidos en la estación D");
		accionPorTransicion.put(tranBajadaVagonAEstacionD, "Bajada obligatoria en la estación D de pasajeros del Vagón subidos en la estación A");
		accionPorTransicion.put(tranBajadaVagonBEstacionA, "Bajada obligatoria en la estación A de pasajeros del Vagón subidos en la estación B");
		accionPorTransicion.put(tranBajadaVagonCEstacionB, "Bajada obligatoria en la estación B de pasajeros del Vagón subidos en la estación C");
		accionPorTransicion.put(tranBajadaVagonDEstacionC, "Bajada obligatoria en la estación C de pasajeros del Vagón subidos en la estación D");

		accionPorTransicion.put(tranBajadaMaquinaAEstacionB, "Bajada en la estación B de los pasajeros de la Máquina subidos en la estación A");
		accionPorTransicion.put(tranBajadaMaquinaAEstacionC, "Bajada en la estación C de los pasajeros de la Máquina subidos en la estación A");
		accionPorTransicion.put(tranBajadaMaquinaBEstacionC, "Bajada en la estación C de los pasajeros de la Máquina subidos en la estación B");
		accionPorTransicion.put(tranBajadaMaquinaBEstacionD, "Bajada en la estación D de los pasajeros de la Máquina subidos en la estación B");
		accionPorTransicion.put(tranBajadaMaquinaCEstacionA, "Bajada en la estación A de los pasajeros de la Maquina subidos en la estación C");
		accionPorTransicion.put(tranBajadaMaquinaCEstacionD, "Bajada en la estación D de los pasajeros de la Máquina subidos en la estación C");
		accionPorTransicion.put(tranBajadaMaquinaDEstacionA, "Bajada en la estación A de los pasajeros de la Maquina subidos en la estación D");
		accionPorTransicion.put(tranBajadaMaquinaDEstacionB, "Bajada en la estación B de los pasajeros de la Máquina subidos en la estación D");
		accionPorTransicion.put(tranBajadaVagonAEstacionB, "Bajada en la estación B de los pasajeros del Vagón subidos en la estación A");
		accionPorTransicion.put(tranBajadaVagonAEstacionC, "Bajada en la estación C de los pasajeros del Vagón subidos en la estación A");
		accionPorTransicion.put(tranBajadaVagonBEstacionC, "Bajada en la estación C de los pasajeros del Vagón subidos en la estación B");
		accionPorTransicion.put(tranBajadaVagonBEstacionD, "Bajada en la estación D de los pasajeros del Vagón subidos en la estación B");
		accionPorTransicion.put(tranBajadaVagonCEstacionA, "Bajada en la estación A de los pasajeros del Vagón subidos en la estación C");
		accionPorTransicion.put(tranBajadaVagonCEstacionD, "Bajada en la estación D de los pasajeros del Vagón subidos en la estación C");
		accionPorTransicion.put(tranBajadaVagonDEstacionA, "Bajada en la estación A de los pasajeros del Vagón subidos en la estación D");
		accionPorTransicion.put(tranBajadaVagonDEstacionB, "Bajada en la estación B de los pasajeros del Vagón subidos en la estación D");

		accionPorTransicion.put(tranSubidaMaquinaEstacionA, "Subida de un pasajero a la Máquina en la estación A");
		accionPorTransicion.put(tranSubidaMaquinaEstacionB, "Subida de un pasajero a la Máquina en la estación B");
		accionPorTransicion.put(tranSubidaMaquinaEstacionC, "Subida de un pasajero a la Máquina en la estación C");
		accionPorTransicion.put(tranSubidaMaquinaEstacionD, "Subida de un pasajero a la Máquina en la estación D");
		accionPorTransicion.put(tranSubidaVagonEstacionA, "Subida de un pasajero al Vagón en la estación A");
		accionPorTransicion.put(tranSubidaVagonEstacionB, "Subida de un pasajero al Vagón en la estación B");
		accionPorTransicion.put(tranSubidaVagonEstacionC, "Subida de un pasajero al Vagón en la estación C");
		accionPorTransicion.put(tranSubidaVagonEstacionD, "Subida de un pasajero al Vagón en la estación D");

		accionPorTransicion.put(tranRecorridoTrenAB, "Recorrido de la estación A a la estación B");
		accionPorTransicion.put(tranRecorridoTrenCD, "Recorrido de la estación C a la estación D");
		
		accionPorTransicion.put(tranPasajerosAGenerador, "Llega un Pasajero a la estación A");
		accionPorTransicion.put(tranPasajerosBGenerador, "Llega un Pasajero a la estación B");
		accionPorTransicion.put(tranPasajerosCGenerador, "Llega un Pasajero a la estación C");
		accionPorTransicion.put(tranPasajerosDGenerador, "Llega un Pasajero a la estación D");
		
		
		accionPorTransicion.put(tranPasoNivelABMaquinaReady, "La Máquina libera el Paso Nivel de A a B");
		accionPorTransicion.put(tranPasoNivelCDMaquinaReady, "La Máquina libera el Paso Nivel de C a D");
		accionPorTransicion.put(tranPasoNivelABMaquinaWait, "La Máquina ingresa al Paso Nivel de A a B");
		accionPorTransicion.put(tranPasoNivelCDMaquinaWait, "La Máquina ingresa al Paso Nivel de C a D");
		
		accionPorTransicion.put(tranPasoNivelABVagonReady, "El Vagón libera el Paso Nivel de A a B");
		accionPorTransicion.put(tranPasoNivelCDVagonReady, "El Vagón libera el Paso Nivel de C a D");
		accionPorTransicion.put(tranPasoNivelABVagonWait, "El Vagón ingresa al Paso Nivel de A a B");
		accionPorTransicion.put(tranPasoNivelCDVagonWait, "El Vagón ingresa al Paso Nivel de C a D");
		
		accionPorTransicion.put(tranPasoNivelABTransitoReady, "Un vehículo libera el Paso Nivel de A a B");
		accionPorTransicion.put(tranPasoNivelCDTransitoReady, "Un vehículo libera el Paso Nivel de C a D");
		accionPorTransicion.put(tranPasoNivelABTransitoWait, "Un vehículo ingresa al Paso Nivel entre A y B");
		accionPorTransicion.put(tranPasoNivelCDTransitoWait, "Un vehículo ingresa al Paso Nivel entre C y D");
		
		accionPorTransicion.put(tranPasoNivelABTransitoGenerador, "Un vehículo llega al Paso de Nivel entre A y B");
		accionPorTransicion.put(tranPasoNivelCDTransitoGenerador, "Un vehículo llega al Paso de Nivel entre C y D");
		
	}
	
	public ArrayList<String> continuarRecorridoTren() throws InterruptedException {
		lock.lock();

		String transicionDisparada = "";
		
		ArrayList<String> mensajeRespuesta = new ArrayList<>();
		mensajeRespuesta.add(0, "0");
		mensajeRespuesta.add(1, " ");
		try {
			Date fechaActual = new Date();
			while(	(
					marcado[plazas.indexOf(trenEstacionAEspera)] == 0 && marcado[plazas.indexOf(trenEstacionBEspera)] == 0 && 
					marcado[plazas.indexOf(trenEstacionCEspera)] == 0 && marcado[plazas.indexOf(trenEstacionDEspera)] == 0
					) || (
						(
						marcado[plazas.indexOf(trenEstacionAEspera)] == 1 || marcado[plazas.indexOf(trenEstacionBEspera)] == 1 || 
						marcado[plazas.indexOf(trenEstacionCEspera)] == 1 || marcado[plazas.indexOf(trenEstacionDEspera)] == 1
						) &&
						(fechaActual.getTime() - ultimoArrivoEstacion.getTime()) < 10000
					)
				) {
//				tiempoDeEspera.awaitNanos((10000 - (new Date().getTime() - ultimoArrivoEstacion.getTime())) * 1000);
				if((fechaActual.getTime() - ultimoArrivoEstacion.getTime()) < 10000) {
					mensajeRespuesta.add(0, String.valueOf( (10000L - ( (new Date()).getTime() - ultimoArrivoEstacion.getTime() ) ) ) );
					mensajeRespuesta.add(1, " ");
					return mensajeRespuesta;
				}
				tiempoDeEspera.await();
				fechaActual = new Date();
			}
			
			for(String estacion: estaciones) {
				if(marcado[plazas.indexOf(estacion + trenEstacionAEspera.substring(1, trenEstacionAEspera.length()))] == 1) {
					dispararRed(estacion + tranTrenEsperandoA.substring(1, tranTrenEsperandoA.length()));
					transicionDisparada = estacion + tranTrenEsperandoA.substring(1, tranTrenEsperandoA.length());
					break;
				}
			}
			String transicion = interseccionPrioritarias();
			if(transicion != null) {
				colaCondicion.get(transicion).signal();
			}
		} finally {
			lock.unlock();
		}
		mensajeRespuesta.add(0, "0" );
		mensajeRespuesta.add(1, (" " + accionPorTransicion.get(transicionDisparada) + "\n") );
		return mensajeRespuesta;
	}
	
	public String partidaTren() throws InterruptedException {
		lock.lock();

		String transicionDisparada = "";
		
		try {
			while(	(	marcado[plazas.indexOf(trenEstacionAPartida)] == 0 && marcado[plazas.indexOf(trenEstacionBPartida)] == 0 && 
						marcado[plazas.indexOf(trenEstacionCPartida)] == 0 && marcado[plazas.indexOf(trenEstacionDPartida)] == 0 && 
						(marcado[plazas.indexOf(pasoNivelABMaquinaUnion)] == 0 || marcado[plazas.indexOf(pasoNivelABVagonUnion)] == 0) && 
						(marcado[plazas.indexOf(pasoNivelCDMaquinaUnion)] == 0 || marcado[plazas.indexOf(pasoNivelCDVagonUnion)] == 0)
					) || 
					
					(	(marcado[plazas.indexOf(trenEstacionAPartida)] == 0 && marcado[plazas.indexOf(trenEstacionBPartida)] == 0 && 
						marcado[plazas.indexOf(trenEstacionCPartida)] == 0 && marcado[plazas.indexOf(trenEstacionDPartida)] == 0) &&
						!(marcado[plazas.indexOf(pasoNivelABMaquinaUnion)] == 1 && marcado[plazas.indexOf(pasoNivelABVagonUnion)] == 1 || 
						marcado[plazas.indexOf(pasoNivelCDMaquinaUnion)] == 1 && marcado[plazas.indexOf(pasoNivelCDVagonUnion)] == 1)
					) ||
					(
					(marcado[plazas.indexOf(trenEstacionAPartida)] == 1 || marcado[plazas.indexOf(trenEstacionBPartida)] == 1 || 
					marcado[plazas.indexOf(trenEstacionCPartida)] == 1 || marcado[plazas.indexOf(trenEstacionDPartida)] == 1) && (
						(marcado[plazas.indexOf(trenEstacionAPartida)] == 1 && marcado[plazas.indexOf(pasajerosEsperandoSubidaA)] != 0 || 
						marcado[plazas.indexOf(trenEstacionBPartida)] == 1 && marcado[plazas.indexOf(pasajerosEsperandoSubidaB)] != 0 || 
						marcado[plazas.indexOf(trenEstacionCPartida)] == 1 && marcado[plazas.indexOf(pasajerosEsperandoSubidaC)] != 0 || 
						marcado[plazas.indexOf(trenEstacionDPartida)] == 1 && marcado[plazas.indexOf(pasajerosEsperandoSubidaD)] != 0) && 
						(marcado[plazas.indexOf(vag)] != 0 || marcado[plazas.indexOf(maq)] != 0)
					) ) ) {
				fullTrenOrEmptyEstacion.await();
			}
			
			
			if(marcado[plazas.indexOf(pasoNivelABMaquinaUnion)] == 1 && marcado[plazas.indexOf(pasoNivelABVagonUnion)] == 1) {
				dispararRed(tranRecorridoTrenAB);
				transicionDisparada = tranRecorridoTrenAB;
			} else if(marcado[plazas.indexOf(pasoNivelCDMaquinaUnion)] == 1 && marcado[plazas.indexOf(pasoNivelCDVagonUnion)] == 1) {
				dispararRed(tranRecorridoTrenCD);
				transicionDisparada = tranRecorridoTrenCD;
			} else {
				for(String estacion: estaciones) {
					if(marcado[plazas.indexOf(estacion + trenEstacionAPartida.substring(1))] == 1) {
						if(marcado[plazas.indexOf(vag)] == 0 && marcado[plazas.indexOf(maq)] == 0) {
							dispararRed(estacion + tranTrenLlenoA.substring(1));
							transicionDisparada = estacion + tranTrenLlenoA.substring(1);
							break;
						}
						if(marcado[plazas.indexOf(pasajerosEsperandoSubidaA.substring(0, 1) + estacion + pasajerosEsperandoSubidaA.substring(pasajerosEsperandoSubidaA.length()-1) )] == 0) {
							dispararRed(estacion + tranEstacionVaciaA.substring(1));
							transicionDisparada = estacion + tranEstacionVaciaA.substring(1);
							break;
						}
						
					}
				}
			}
			
			String transicion = interseccionPrioritarias();
			if(transicion != null) {
				colaCondicion.get(transicion).signal();
			}
		} finally {
			lock.unlock();
		}
		return " " + accionPorTransicion.get(transicionDisparada) + "\n";
	}
	
	public String arrivoTrenEstacion() throws InterruptedException {
		lock.lock();

		String transicionDisparada = "";
		
		try {
			while((	marcado[plazas.indexOf(trenEstacionAArribo)] == 0 && 
					marcado[plazas.indexOf(trenEstacionBArribo)] == 0 && 
					marcado[plazas.indexOf(trenEstacionCArribo)] == 0 && 
					marcado[plazas.indexOf(trenEstacionDArribo)] == 0 
					) ) {
				trenArriboEstacion.await();
			}
			
			for(String estacionActual: estaciones) {
				if(marcado[plazas.indexOf(trenEstacionAArribo.substring(0 ,1) + estacion[(estaciones.indexOf(estacionActual)+3)%4] + estacionActual + trenEstacionAArribo.substring(trenEstacionAArribo.length() - 1))] == 1) {
					if(dispararRed(estacionActual + tranTrenArribo)) {
						transicionDisparada = estacionActual + tranTrenArribo;
						ultimoArrivoEstacion = new Date();
						break;
					}
				}
			}

			String transicion = interseccionPrioritarias();
			if(transicion != null) {
				colaCondicion.get(transicion).signal();
			}
		} finally {
			lock.unlock();
		}
		return " " + accionPorTransicion.get(transicionDisparada) + "\n";
	}
	
	public String abordarTren() throws InterruptedException {
		lock.lock();
		
		String threadName = Thread.currentThread().getName();

		String transicionDisparada = "";
		
		try {
			while(	trenEstacionA.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionA)] != 0 ||
					marcado[plazas.indexOf(maq)] == 0 && marcado[plazas.indexOf(vag)] == 0 || marcado[plazas.indexOf(pasajerosEsperandoSubidaA)] == 0) ) {
				subidaEstacionA.await();
			}
			while(	trenEstacionB.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionB)] != 0 || 
					marcado[plazas.indexOf(maq)] == 0 && marcado[plazas.indexOf(vag)] == 0 || marcado[plazas.indexOf(pasajerosEsperandoSubidaB)] == 0) ) {
				subidaEstacionB.await();
			}
			while(	trenEstacionC.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionC)] != 0 || 
					marcado[plazas.indexOf(maq)] == 0 && marcado[plazas.indexOf(vag)] == 0 || marcado[plazas.indexOf(pasajerosEsperandoSubidaC)] == 0) ) {
				subidaEstacionC.await();
			}
			while(	trenEstacionD.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionD)] != 0 || 
					marcado[plazas.indexOf(maq)] == 0 && marcado[plazas.indexOf(vag)] == 0 || marcado[plazas.indexOf(pasajerosEsperandoSubidaD)] == 0) ) {
				subidaEstacionD.await();
			}

			boolean disparoExitoso = false;
			ArrayList<String> listaSubidas = new ArrayList<>(Arrays.asList(abordarTren.keySet().toArray(new String[abordarTren.keySet().size()])));
			for(String subida: listaSubidas) {
				if(		marcado[plazas.indexOf(subida.startsWith("SM")? maq : vag)] != 0 && 
						marcado[plazas.indexOf(trenEstacion + threadName.substring(threadName.length() - 1))] == 0 && 
						abordarTren.get(subida).endsWith(threadName.substring(threadName.length() - 1))) {
					disparoExitoso = dispararRed(subida);
					if(disparoExitoso) {
						transicionDisparada = subida;
						ultimaSubidaEstacion.put(trenEstacion + threadName.substring(threadName.length() - 1), new Date());
						break;
					}
				}
			}
			
			
			String transicion = interseccionPrioritarias();
			if(transicion != null) {
				colaCondicion.get(transicion).signal();
			}
		} finally {
			lock.unlock();
		}
		return " " + accionPorTransicion.get(transicionDisparada) + "\n";
	}

	public ArrayList<String> descenderTren() throws InterruptedException {
		lock.lock();

		String threadName = Thread.currentThread().getName();

		ArrayList<String> mensajeRespuesta = new ArrayList<>();
		mensajeRespuesta.add(0, "");
		mensajeRespuesta.add(1, " ");
		String transicionDisparada = "";
		try {
			String estacionAnteriorTren = estacion[(estaciones.indexOf(threadName.substring(threadName.length() - 1)) + 3)%4];
			String estacionOpuestaTren = estacion[(estaciones.indexOf(threadName.substring(threadName.length() - 1)) + 2)%4];

			Date actual = new Date();
			
			while(	trenEstacionA.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionA)] != 0 || 
					(marcado[plazas.indexOf(maqB)] == 0 && marcado[plazas.indexOf(vagB)] == 0 && 
					(marcado[plazas.indexOf(maqC)] == 0 && marcado[plazas.indexOf(vagC)] == 0 || ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > actual.getTime()) && 
					(marcado[plazas.indexOf(maqD)] == 0 && marcado[plazas.indexOf(vagD)] == 0 || ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() > actual.getTime()) ) ) ) {
				
				if(ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > actual.getTime() && ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() > actual.getTime()) {
					if(ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime()) {
//						return ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime();
						mensajeRespuesta.add(0, String.valueOf( ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime() ) );
						return mensajeRespuesta;
					} else {
//						return ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime();
						mensajeRespuesta.add(0, String.valueOf( ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime() ) );
						return mensajeRespuesta;
					}
				} else if(ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > actual.getTime()) {
//					return ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime();
					mensajeRespuesta.add(0, String.valueOf( ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime() ) );
					return mensajeRespuesta;
				} else if(ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() > actual.getTime()) {
//					return ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime();
					mensajeRespuesta.add(0, String.valueOf( ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime() ) );
					return mensajeRespuesta;
				}
				bajadaEstacionA.await();
				actual = new Date();
			}
			while(	trenEstacionB.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionB)] != 0 || 
					(marcado[plazas.indexOf(maqC)] == 0 && marcado[plazas.indexOf(vagC)] == 0 && 
					(marcado[plazas.indexOf(maqD)] == 0 && marcado[plazas.indexOf(vagD)] == 0 || ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > actual.getTime()) && 
					(marcado[plazas.indexOf(maqA)] == 0 && marcado[plazas.indexOf(vagA)] == 0 || ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() > actual.getTime()) ) ) ) {

				if(ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > actual.getTime() && ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() > actual.getTime()) {
					if(ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime()) {
//						return ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime();
						mensajeRespuesta.add(0, String.valueOf( ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime() ) );
						return mensajeRespuesta;
					} else {
//						return ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime();
						mensajeRespuesta.add(0, String.valueOf( ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime() ) );
						return mensajeRespuesta;
					}
				} else if(ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > actual.getTime()) {
//					return ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime();
					mensajeRespuesta.add(0, String.valueOf( ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime() ) );
					return mensajeRespuesta;
				} else if(ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() > actual.getTime()) {
//					return ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime();
					mensajeRespuesta.add(0, String.valueOf( ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime() ) );
					return mensajeRespuesta;
				}

				bajadaEstacionB.await();
				actual = new Date();
			}
			while(	trenEstacionC.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionC)] != 0 ||  
					(marcado[plazas.indexOf(maqD)] == 0 && marcado[plazas.indexOf(vagD)] == 0 && 
					(marcado[plazas.indexOf(maqA)] == 0 && marcado[plazas.indexOf(vagA)] == 0 || ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > actual.getTime()) && 
					(marcado[plazas.indexOf(maqB)] == 0 && marcado[plazas.indexOf(vagB)] == 0 || ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() > actual.getTime()) ) ) ) {
				
				if(ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > actual.getTime() && ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() > actual.getTime()) {
					if(ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime()) {
//						return ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime();
						mensajeRespuesta.add(0, String.valueOf( ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime() ) );
						return mensajeRespuesta;
					} else {
//						return ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime();
						mensajeRespuesta.add(0, String.valueOf( ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime() ) );
						return mensajeRespuesta;
					}
				} else if(ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > actual.getTime()) {
//					return ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime();
					mensajeRespuesta.add(0, String.valueOf( ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime() ) );
					return mensajeRespuesta;
				} else if(ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() > actual.getTime()) {
//					return ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime();
					mensajeRespuesta.add(0, String.valueOf( ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime() ) );
					return mensajeRespuesta;
				}

				bajadaEstacionC.await();
				actual = new Date();
			}
			while(	trenEstacionD.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionD)] != 0 || 
					(marcado[plazas.indexOf(maqA)] == 0 && marcado[plazas.indexOf(vagA)] == 0 && 
					(marcado[plazas.indexOf(maqB)] == 0 && marcado[plazas.indexOf(vagB)] == 0 || ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > actual.getTime()) && 
					(marcado[plazas.indexOf(maqC)] == 0 && marcado[plazas.indexOf(vagC)] == 0 || ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() > actual.getTime()) ) ) ) {
				
				if(ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > actual.getTime() && ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() > actual.getTime()) {
					if(ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime()) {
//						return ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime();
						mensajeRespuesta.add(0, String.valueOf( ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime() ) );
						return mensajeRespuesta;
					} else {
//						return ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime();
						mensajeRespuesta.add(0, String.valueOf( ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime() ) );
						return mensajeRespuesta;
					}
				} else if(ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > actual.getTime()) {
//					return ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime();
					mensajeRespuesta.add(0, String.valueOf( ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime() ) );
					return mensajeRespuesta;
				} else if(ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() > actual.getTime()) {
//					return ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime();
					mensajeRespuesta.add(0, String.valueOf( ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime() ) );
					return mensajeRespuesta;
				}

				bajadaEstacionD.await();
				actual = new Date();
			}


			// pasajerosAnterior calcula bajadas estocasticas para los pasajeros subidos en la estacion anterior utilizando la hora de la ultima subida en esa estacion
			// la estacion anterior se obtiene buscando en la lista de estaciones la estacion correspondiente al indice anterior al de la estacion actual. En el caso en que el indice 
			// sea 0 la estacion anterior se encontraria en el indice -1 si ultilizamos un offset negativo por lo que se suma el tamanio del array y se calcula el modulo para que el indice
			// siempre este dentro del array. (-1 + 4) = 3 para la estacion anterior y (-2 + 4) = 2 para la estacion opuesta de esta forma usamos al array como un anillo (campo finito cerrado o campo de Galois)
			
			ArrayList<String> listaBajadas = new ArrayList<>(Arrays.asList(descenderTren.keySet().toArray(new String[descenderTren.keySet().size()])));
			for(String bajada: listaBajadas) {
				if(		bajada.startsWith("B"+ threadName.substring(threadName.length() - 1)) &&																	// Si el thread baja pasajeros en la estacion de la tansicion
						marcado[plazas.indexOf(trenEstacion + threadName.substring(threadName.length() - 1))] == 0 &&												// Si el tren se encuentra en la estacion del thread
						marcado[plazas.indexOf(bajada.substring(bajada.length() - 2, bajada.length()))] != 0 && (													// Si hay pasajeros viajando desde la estacion de la transicion
							bajada.endsWith(estacionAnteriorTren) && ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() <= actual.getTime() ||	// Si la transicion baja pasajeros de la estacion anterior
							bajada.endsWith(estacionOpuestaTren) && ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() <= actual.getTime() ||	// Si la transicion baja pasajeros de la estacion opuesta
							bajada.endsWith(estacion[(estaciones.indexOf(threadName.substring(threadName.length() - 1)) + 1)%4])									// Si la transicion baja pasajeros de la estacion siguiente
						) ) {
					if(dispararRed(bajada)) {
						transicionDisparada = bajada;
						if(bajada.endsWith(estacionAnteriorTren)) {
							int tiempoEsperadoAnterior = TiempoDeEspera.getInstance(97L).getNextRandom(5000);
							ultimaSubidaEstacion.put(trenEstacion + estacionAnteriorTren, new Date(ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() + tiempoEsperadoAnterior));
						}
						if(bajada.endsWith(estacionOpuestaTren)) {
							int tiempoEsperadoOpuesta = TiempoDeEspera.getInstance(97L).getNextRandom(5000);
							ultimaSubidaEstacion.put(trenEstacion + estacionOpuestaTren, new Date(ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() + tiempoEsperadoOpuesta));
						}
						
						break;
					}
				}
			}

			String transicion = interseccionPrioritarias();
			if(transicion != null) {
				colaCondicion.get(transicion).signal();
			}
		} finally {
			lock.unlock();
		}
		mensajeRespuesta.add(0, String.valueOf( 0L ) );
		mensajeRespuesta.add(1, " " + accionPorTransicion.get(transicionDisparada) + "\n");
		return mensajeRespuesta;
	}


	public String cruzarPasoNivel() throws InterruptedException {
		lock.lock();

		String threadName = Thread.currentThread().getName();

		String transicionDisparada = "";
		try {

			while(	threadName.endsWith(pasoNivelVagon) && (
					marcado[plazas.indexOf(pasoNivelABVagonEsperando)] == 0 && marcado[plazas.indexOf(pasoNivelCDVagonEsperando)] == 0 || 
					marcado[plazas.indexOf(pasoNivelABVagonEsperando)] != 0 && (
							marcado[plazas.indexOf(pasoNivelABBarrera)] == 0 || 
							marcado[plazas.indexOf(pasoNivelABMaquinaEsperando)] != 0
					) || 
					marcado[plazas.indexOf(pasoNivelCDVagonEsperando)] != 0 && (
							marcado[plazas.indexOf(pasoNivelCDBarrera)] == 0 || 
							marcado[plazas.indexOf(pasoNivelCDMaquinaEsperando)] != 0
					)
				)
			) {
				pasoDeNivelVagon.await();
			}
			
			while(	threadName.endsWith(pasoNivelMaquina) && (
					marcado[plazas.indexOf(pasoNivelABMaquinaEsperando)] == 0 && marcado[plazas.indexOf(pasoNivelCDMaquinaEsperando)] == 0 || 
					marcado[plazas.indexOf(pasoNivelABMaquinaEsperando)] != 0 && marcado[plazas.indexOf(pasoNivelABBarrera)] == 0 || 
					marcado[plazas.indexOf(pasoNivelCDMaquinaEsperando)] != 0 && marcado[plazas.indexOf(pasoNivelCDBarrera)] == 0
				)
			) {
				pasoDeNivelMaquina.await();
			}
			
			if(threadName.endsWith(pasoNivelMaquina)) {
				if(marcado[plazas.indexOf(pasoNivelABMaquinaEsperando)] != 0) {
					transicionDisparada = tranPasoNivelABMaquinaWait;
					dispararRed(tranPasoNivelABMaquinaWait);
				}
				if(marcado[plazas.indexOf(pasoNivelCDMaquinaEsperando)] != 0) {
					transicionDisparada = tranPasoNivelCDMaquinaWait;
					dispararRed(tranPasoNivelCDMaquinaWait);
				}
			}
			if(threadName.endsWith(pasoNivelVagon)) {
				if(marcado[plazas.indexOf(pasoNivelABVagonEsperando)] != 0) {
					transicionDisparada = tranPasoNivelABVagonWait;
					dispararRed(tranPasoNivelABVagonWait);
				}
				if(marcado[plazas.indexOf(pasoNivelCDVagonEsperando)] != 0) {
					transicionDisparada = tranPasoNivelCDVagonWait;
					dispararRed(tranPasoNivelCDVagonWait);
				}
			}
			
			String transicion = interseccionPrioritarias();
			if(transicion != null) {
				colaCondicion.get(transicion).signal();
			}
		} finally {
			lock.unlock();
		}
		return " " + accionPorTransicion.get(transicionDisparada) + "\n";
	}

	public String liberarBarreraPasoNivel() throws InterruptedException {
		lock.lock();

		String threadName = Thread.currentThread().getName();

		String transicionDisparada = "";
		try {
			while(	threadName.endsWith(pasoNivelTransitoAB) && 
					(	marcado[plazas.indexOf(pasoNivelABBarrera)] == 1 && 
						(	marcado[plazas.indexOf(pasoNivelABTransitoEsperando)] == 0 || 
							marcado[plazas.indexOf(pasoNivelABMaquinaEsperando)] != 0 || 
							marcado[plazas.indexOf(pasoNivelABVagonEsperando)] != 0 
						)
					)
				) {
				liberarBarreraPasoNivelAB.await();
			}
		
			while(	threadName.endsWith(pasoNivelTransitoCD) && 
					(	marcado[plazas.indexOf(pasoNivelCDBarrera)] == 1 && 
						(	marcado[plazas.indexOf(pasoNivelCDTransitoEsperando)] == 0 || 
							marcado[plazas.indexOf(pasoNivelCDMaquinaEsperando)] != 0 || 
							marcado[plazas.indexOf(pasoNivelCDVagonEsperando)] != 0 
						)
					)
				) {
				liberarBarreraPasoNivelCD.await();
			}

			boolean disparoExitoso = false;
			if(threadName.endsWith(pasoNivelTransitoAB)) {
				if(marcado[plazas.indexOf(pasoNivelABMaquina)] != 0 && !disparoExitoso) {
					transicionDisparada = tranPasoNivelABMaquinaReady;
					disparoExitoso = dispararRed(tranPasoNivelABMaquinaReady);
				}
				if(marcado[plazas.indexOf(pasoNivelABVagon)] != 0 && !disparoExitoso) {
					transicionDisparada = tranPasoNivelABVagonReady;
					disparoExitoso = dispararRed(tranPasoNivelABVagonReady);
				}
				if(marcado[plazas.indexOf(pasoNivelABTransito)] != 0 && !disparoExitoso) {
					transicionDisparada = tranPasoNivelABTransitoReady;
					disparoExitoso = dispararRed(tranPasoNivelABTransitoReady);
				}
				if(marcado[plazas.indexOf(pasoNivelABTransitoEsperando)] != 0 && !disparoExitoso) {
					transicionDisparada = tranPasoNivelABTransitoWait;
					disparoExitoso = dispararRed(tranPasoNivelABTransitoWait);
				}
			}
			if(threadName.endsWith(pasoNivelTransitoCD)) {
				if(marcado[plazas.indexOf(pasoNivelCDMaquina)] != 0 && !disparoExitoso) {
					transicionDisparada = tranPasoNivelCDMaquinaReady;
					disparoExitoso = dispararRed(tranPasoNivelCDMaquinaReady);
				}
				if(marcado[plazas.indexOf(pasoNivelCDVagon)] != 0 && !disparoExitoso) {
					transicionDisparada = tranPasoNivelCDVagonReady;
					disparoExitoso = dispararRed(tranPasoNivelCDVagonReady);
				}
				if(marcado[plazas.indexOf(pasoNivelCDTransito)] != 0 && !disparoExitoso) {
					transicionDisparada = tranPasoNivelCDTransitoReady;
					disparoExitoso = dispararRed(tranPasoNivelCDTransitoReady);
				}
				if(marcado[plazas.indexOf(pasoNivelCDTransitoEsperando)] != 0 && !disparoExitoso) {
					transicionDisparada = tranPasoNivelCDTransitoWait;
					disparoExitoso = dispararRed(tranPasoNivelCDTransitoWait);
				}
			}
			
			String transicion = interseccionPrioritarias();
			if(transicion != null) {
				colaCondicion.get(transicion).signal();
			}
		} finally {
			lock.unlock();
		}
		return " " + accionPorTransicion.get(transicionDisparada) + "\n";
	}


	public String generarPasajeros() throws InterruptedException {
		lock.lock();

		String threadName = Thread.currentThread().getName();
		
		String transicionDisparada = "";
		try {
			
			for(String estacion: estaciones) {
				if(threadName.endsWith(estacion)) {
					transicionDisparada = tranPasajerosAGenerador.substring(0, 1) + estacion + tranPasajerosAGenerador.substring(tranPasajerosAGenerador.length() - 1);
					dispararRed(transicionDisparada);
					break;
				}
			}
			
			String transicion = interseccionPrioritarias();
			if(transicion != null) {
				colaCondicion.get(transicion).signal();
			}
		} finally {
			lock.unlock();
		}

		return " " + accionPorTransicion.get(transicionDisparada) + "\n";
	}


	public String generarTransito() throws InterruptedException {
		lock.lock();

		String threadName = Thread.currentThread().getName();
		
		String transicionDisparada = "";
		try {
			
			if(threadName.endsWith(recorridoAB)) {
				transicionDisparada = tranPasoNivelABTransitoGenerador;
				dispararRed(tranPasoNivelABTransitoGenerador);
			}
			if(threadName.endsWith(recorridoCD)) {
				transicionDisparada = tranPasoNivelCDTransitoGenerador;
				dispararRed(tranPasoNivelCDTransitoGenerador);
			}
			
			String transicion = interseccionPrioritarias();
			if(transicion != null) {
				colaCondicion.get(transicion).signal();
			}
		} finally {
			lock.unlock();
		}
		
		return " " + accionPorTransicion.get(transicionDisparada) + "\n";
	}

	private void imprimirMarcado() {
		ArrayList<String> printOrderKeys = new ArrayList<>(Arrays.asList(printOrder.keySet().toArray(new String[printOrder.keySet().size()])));
		ArrayList<Integer> colWidthPrint = new ArrayList<>();
		for(String plaza: printOrderKeys) {
			System.out.print(" "+plaza);
			colWidthPrint.add(plaza.length());
		}
		System.out.print("\n");
		for(String plaza: printOrderKeys) {
			for(int i = 0; i < (colWidthPrint.get(printOrderKeys.indexOf(plaza)) - String.valueOf(marcado[printOrder.get(plaza)]).length()); i++) {
				System.out.print(" ");
			}
			System.out.print(" "+marcado[printOrder.get(plaza)]);
			colWidthPrint.add(plaza.length());
		}
		System.out.print("\n");
	}
	
	private String interseccionPrioritarias() {
		ArrayList<String> prioritarias = new ArrayList<>(Arrays.asList(colaCondicion.keySet().toArray(new String[colaCondicion.keySet().size()])));
		LinkedHashMap<String, Boolean> vectorInterseccion = getInterseccionCondicion(getInterseccionInhibicion(getSensibilizadas(), getInhibidas()));
		for(String transicion: prioritarias) {
			if(vectorInterseccion.get(transicion)) {
				return transicion;
			}
		}
		return null;
	}
	
	private LinkedHashMap<String, Boolean> getInterseccionCondicion(LinkedHashMap<String, Boolean> vectorSensibilizadas) {
		LinkedHashMap<String, Boolean> interseccion = new LinkedHashMap<>();
		
//		System.out.println(" ");
		for(String transicion: this.transiciones) {
			if(vectorSensibilizadas.get(transicion) && !transicionesGeneradoras.contains(transicion)) {
				System.out.print(" "+transicion+" ("+(colaCondicion.containsKey(transicion)? lock.getWaitQueueLength(colaCondicion.get(transicion)) != 0 : true)+")");
			}
			interseccion.put(transicion, vectorSensibilizadas.get(transicion) && (colaCondicion.containsKey(transicion)? lock.getWaitQueueLength(colaCondicion.get(transicion)) != 0 : true));
		}
		System.out.print(" - "+Thread.currentThread().getName());
		System.out.println(" ");
		
		return interseccion;
	}

	private LinkedHashMap<String, Boolean> getInterseccionInhibicion(LinkedHashMap<String, Boolean> vectorSensibilizadas, LinkedHashMap<String, Boolean> vectorInhibidas) {
		LinkedHashMap<String, Boolean> interseccion = new LinkedHashMap<>();
		
		for(String transicion: this.transiciones) {
			interseccion.put(transicion, vectorSensibilizadas.get(transicion) && (vectorInhibidas.get(transicion)!=null? !vectorInhibidas.get(transicion): true));
		}
		
		return interseccion;
	}
	
	private LinkedHashMap<String, Boolean> getSensibilizadas(){
		LinkedHashMap<String, Boolean> sensibilizadas = new LinkedHashMap<>();
		
		for (String transicion : transiciones) {
			Integer sign = 0;
			for(int i = 0; i < matrizMenos.length; i++) {
				sign = new Integer(marcado[i]) - matrizMenos[i][transiciones.indexOf(transicion)];
				sensibilizadas.put(transicion, (sensibilizadas.get(transicion)!=null? sensibilizadas.get(transicion) : true) && !(sign < 0));
			}
		}
		
		return sensibilizadas;
	}
	
	private LinkedHashMap<String, Boolean> getInhibidas(){
		LinkedHashMap<String, Boolean> inhibidas = new LinkedHashMap<>();
		
		for (String transicion : transiciones) {
			for(int i = 0; i < matrizInhibicion.length; i++) {
				if(matrizInhibicion[i][transiciones.indexOf(transicion)] != 0) {
					Integer sign = 0;
					sign = new Integer(marcado[i]) - matrizInhibicion[i][transiciones.indexOf(transicion)];
					inhibidas.put(transicion, (inhibidas.get(transicion)!=null? inhibidas.get(transicion) : false) || sign >= 0);
				}
			}
		}
		
		return inhibidas;
	}
	
	private boolean dispararRed(String transicion) {
		System.out.println("\n");
		System.out.println(" "+transicion+" Nro. de disparo: "+printWriterCount);
		Integer[] vectorDisparo = Collections.nCopies(transiciones.size(), 0).toArray(new Integer[0]);
		vectorDisparo[transiciones.indexOf(transicion)] = 1;
		
		return dispararRed(vectorDisparo);
	}

	private boolean dispararRed(Integer[] vectorDisparo) {
		String transicionDisparada = "";
		Integer sumatoriaDisparo = 0;
		for (int i = 0; i < vectorDisparo.length; i++) {
			if(vectorDisparo[i] != 0) {
				transicionDisparada = transiciones.get(i);
			}
			sumatoriaDisparo += vectorDisparo[i];
		}
		if(!sumatoriaDisparo.equals(1)) {
			return false;
		}
		
		Integer[] postDisparo = new Integer[marcado.length];
		for(int i = 0; i < matrizMenos.length; i++) {
			postDisparo[i] = new Integer(marcado[i]);
			for (int j = 0; j < matrizMenos[i].length; j++) {
				postDisparo[i] = postDisparo[i] - matrizMenos[i][j] * vectorDisparo[j];
			}
			if(postDisparo[i] < 0) {
				return false;
			}
		}

		for(int i = 0; i < matrizMas.length; i++) {
			for (int j = 0; j < matrizMas[i].length; j++) {
				postDisparo[i] = postDisparo[i] + matrizMas[i][j] * vectorDisparo[j];
			}
			if(postDisparo[i] < 0) {
				return false;
			}
		}
		
		
		this.marcado = postDisparo;
		
		if(printWriterCount == 12000) {
//			String fullPrintWriteString = new String((new String("")).getBytes(), StandardCharsets.UTF_8);
//			for(String transicion:printWriterArray) {
//				String byteString = new String(transicion.getBytes(), StandardCharsets.UTF_8);
//				System.out.print(byteString+" ");
//				fullPrintWriteString += byteString+" ";
//			}
			
			System.out.println(printWtiterString);
			this.printWriter.print(printWtiterString);
			this.printWriter.flush();
			this.printWriter.close(); 
			for (int i = 0; i < 100; i++) {
				System.out.println("ARCHIVO CERRADO!!!");
			}
		} else if(printWriterCount < 12000) {
			printWriterArray.add(transicionDisparada);
			printWtiterString += new String(transicionDisparada.getBytes(), StandardCharsets.UTF_8) + new String((new String(" ")).getBytes(), StandardCharsets.UTF_8);
		}
//		System.out.println(printWtiterString);
		
		printWriterCount += 1;
		
		imprimirMarcado();
		return true;
	}
}
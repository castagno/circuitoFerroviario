package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
	
	
	/* Plazas */

	/* Tren en Estacion */
	private final String trenEstacion = "TE";
	private final String trenEstacionA = trenEstacion+"A";
	private final String trenEstacionB = trenEstacion+"B";
	private final String trenEstacionC = trenEstacion+"C";
	private final String trenEstacionD = trenEstacion+"D";
	
	/* Tren en Espera */
	private final String trenEstacionAEspera = "ATW";
	private final String trenEstacionBEspera = "BTW";
	private final String trenEstacionCEspera = "CTW";
	private final String trenEstacionDEspera = "DTW";

	/* Tren esparando condicion de partida*/
	private final String trenEstacionAPartida = "ATR";
	private final String trenEstacionBPartida = "BTR";
	private final String trenEstacionCPartida = "CTR";
	private final String trenEstacionDPartida = "DTR";

	/* Tren en recorrido llegando a estacion */
	private final String trenEstacionAArribo = "RDAT";
	private final String trenEstacionBArribo = "RABT";
	private final String trenEstacionCArribo = "RBCT";
	private final String trenEstacionDArribo = "RCDT";
	
	/* Pasajeros esperando subida */
	private final String pasajerosEsperandoSubidaA = "PAQ";
	private final String pasajerosEsperandoSubidaB = "PBQ";
	private final String pasajerosEsperandoSubidaC = "PCQ";
	private final String pasajerosEsperandoSubidaD = "PDQ";

	/* Lugares del tren compuesto por Vagon y Maquina */
	private final String vagon = "VAG";
	private final String maquina = "MAQ";
	
	/* Lugares del vagon ocupados por pasajeros de la estacion indicada */
	private final String vagA = "VA";
	private final String vagB = "VB";
	private final String vagC = "VC";
	private final String vagD = "VD";
	
	/* Lugares de la maquina ocupados por pasajeros de la estacion indicada */
	private final String maqA = "MA";
	private final String maqB = "MB";
	private final String maqC = "MC";
	private final String maqD = "MD";
	
	/* Barrera del paso de nivel, recurso compartido */
	private final String pasoNivelABBarrera = "PNABB";
	private final String pasoNivelCDBarrera = "PNCDB";
	
	/* Transito en el paso de nivel Cruzando */
	private final String pasoNivelABTransito = "PNABT";
	private final String pasoNivelCDTransito = "PNCDT";
	
	/* Maquina en el paso de nivel Cruzando */
	private final String pasoNivelABMaquina = "PNABM";
	private final String pasoNivelCDMaquina = "PNCDM";
	
	/* Vagon en el paso de nivel Cruzando */
	private final String pasoNivelABVagon = "PNABV";
	private final String pasoNivelCDVagon = "PNCDV";
	
	/* Transito en el paso de nivel Esperando */
	private final String pasoNivelABTransitoEsperando = "PNABTQ";
	private final String pasoNivelCDTransitoEsperando = "PNCDTQ";
	
	/* Maquina en el paso de nivel Cruzando */
	private final String pasoNivelABMaquinaEsperando = "RABM";
	private final String pasoNivelABVagonEsperando = "RABV";
	
	/* Vagon en el paso de nivel Cruzando */
	private final String pasoNivelCDMaquinaEsperando = "RCDM";
	private final String pasoNivelCDVagonEsperando = "RCDV";
	
	/* Maquina fuera del paso de nivel esperando Union */
	private final String pasoNivelABMaquinaUnion = "RABMR";
	private final String pasoNivelABVagonUnion = "RABVR";
	
	/* Vagon fuera del paso de nivel esperando Union */
	private final String pasoNivelCDMaquinaUnion = "RCDMR";
	private final String pasoNivelCDVagonUnion = "RCDVR";
	
	
	
	
	/* Transiciones */
	
	/* Estacion */
	private final String tranTrenArriboA = "AAr";
	private final String tranTrenArriboB = "BAr";
	private final String tranTrenArriboC = "CAr";
	private final String tranTrenArriboD = "DAr";
	private final String tranTrenArribo = "Ar";
	
	private final String tranTrenEsperandoA = "AW";
	private final String tranTrenEsperandoB = "BW";
	private final String tranTrenEsperandoC = "CW";
	private final String tranTrenEsperandoD = "DW";
	
	private final String tranTrenLlenoA = "ADe";
	private final String tranTrenLlenoB = "BDe";
	private final String tranTrenLlenoC = "CDe";
	private final String tranTrenLlenoD = "DDe";
	
	private final String tranEstacionVaciaA = "ADV";
	private final String tranEstacionVaciaB = "BDV";
	private final String tranEstacionVaciaC = "CDV";
	private final String tranEstacionVaciaD = "DDV";
	
	/* Subida de pasajeros */
	private final String tranSubidaMaquinaEstacionA = "SMA";
	private final String tranSubidaMaquinaEstacionB = "SMB";
	private final String tranSubidaMaquinaEstacionC = "SMC";
	private final String tranSubidaMaquinaEstacionD = "SMD";

	private final String tranSubidaVagonEstacionA = "SVA";
	private final String tranSubidaVagonEstacionB = "SVB";
	private final String tranSubidaVagonEstacionC = "SVC";
	private final String tranSubidaVagonEstacionD = "SVD";
	
	/* Bajada de pasajeros */
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

	
	/* Paso de Nivel Transito */
	private final String tranPasoNivelABTransitoWait = "PNABTW";
	private final String tranPasoNivelABTransitoReady = "PNABTR";
	private final String tranPasoNivelABTransitoGenerador = "PNABTG";
	private final String tranPasoNivelABMaquinaWait = "PNABMW";
	private final String tranPasoNivelABMaquinaReady = "PNABMR";
	private final String tranPasoNivelABVagonWait = "PNABVW";
	private final String tranPasoNivelABVagonReady = "PNABVR";
	
	private final String tranPasoNivelCDTransitoWait = "PNCDTW";
	private final String tranPasoNivelCDTransitoReady = "PNCDTR";
	private final String tranPasoNivelCDTransitoGenerador = "PNCDTG";
	private final String tranPasoNivelCDMaquinaWait = "PNCDMW";
	private final String tranPasoNivelCDMaquinaReady = "PNCDMR";
	private final String tranPasoNivelCDVagonWait = "PNCDVW";
	private final String tranPasoNivelCDVagonReady = "PNCDVR";
	
	/* Recorrido Tren */
	private final String tranRecorridoTrenAB = "RAB";
	private final String tranRecorridoTrenCD = "RCD";
	
	/* Generador de Pasajeros para abordar tren */
	private final String tranPasajerosAGenerador = "PAG";
	private final String tranPasajerosBGenerador = "PBG";
	private final String tranPasajerosCGenerador = "PCG";
	private final String tranPasajerosDGenerador = "PDG";
	
	
	
	public Monitor(Integer[][] matrizMas, Integer[][] matrizMenos, Integer[][] matrizInhibicion, LinkedHashMap<String, Integer> marcado, ArrayList<String> transiciones) {
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
		printOrder.put(maquina, plazas.indexOf(maquina));
		printOrder.put(vagon, plazas.indexOf(vagon));
		
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
		
		printOrder.put(pasoNivelABTransitoEsperando, plazas.indexOf(pasoNivelABTransitoEsperando));
		printOrder.put(pasoNivelABTransito, plazas.indexOf(pasoNivelABTransito));
		printOrder.put(pasoNivelCDTransitoEsperando, plazas.indexOf(pasoNivelCDTransitoEsperando));
		printOrder.put(pasoNivelCDTransito, plazas.indexOf(pasoNivelCDTransito));
		
		transicionesGeneradoras = new ArrayList<>();
		transicionesGeneradoras.add(tranPasajerosAGenerador);
		transicionesGeneradoras.add(tranPasajerosBGenerador);
		transicionesGeneradoras.add(tranPasajerosCGenerador);
		transicionesGeneradoras.add(tranPasajerosDGenerador);
		transicionesGeneradoras.add(tranPasoNivelABTransitoGenerador);
		transicionesGeneradoras.add(tranPasoNivelCDTransitoGenerador);
	}
	
	public Long continuarRecorridoTren() throws InterruptedException {
		lock.lock();
		
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
					return (10000L - ( (new Date()).getTime() - ultimoArrivoEstacion.getTime() ) );
				}
				tiempoDeEspera.await();
				fechaActual = new Date();
			}
			
			for(String estacion: estaciones) {
				if(marcado[plazas.indexOf(estacion + trenEstacionAEspera.substring(1, trenEstacionAEspera.length()))] == 1) {
					dispararRed(estacion + tranTrenEsperandoA.substring(1, tranTrenEsperandoA.length()));
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
		return 0L;
	}
	
	public void partidaTren() throws InterruptedException {
		lock.lock();
		
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
						(marcado[plazas.indexOf(vagon)] != 0 || marcado[plazas.indexOf(maquina)] != 0)
					) ) ) {
				fullTrenOrEmptyEstacion.await();
			}
			
			
			if(marcado[plazas.indexOf(pasoNivelABMaquinaUnion)] == 1 && marcado[plazas.indexOf(pasoNivelABVagonUnion)] == 1) {
				dispararRed(tranRecorridoTrenAB);
			} else if(marcado[plazas.indexOf(pasoNivelCDMaquinaUnion)] == 1 && marcado[plazas.indexOf(pasoNivelCDVagonUnion)] == 1) {
				dispararRed(tranRecorridoTrenCD);
			} else {
				for(String estacion: estaciones) {
					if(marcado[plazas.indexOf(estacion + trenEstacionAPartida.substring(1))] == 1) {
						if(marcado[plazas.indexOf(vagon)] == 0 && marcado[plazas.indexOf(maquina)] == 0) {
							dispararRed(estacion + tranTrenLlenoA.substring(1));
							break;
						}
						if(marcado[plazas.indexOf(pasajerosEsperandoSubidaA.substring(0, 1) + estacion + pasajerosEsperandoSubidaA.substring(pasajerosEsperandoSubidaA.length()-1) )] == 0) {
							dispararRed(estacion + tranEstacionVaciaA.substring(1));
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
	}
	
	public void arrivoTrenEstacion() throws InterruptedException {
		lock.lock();
		
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
	}
	
	public void abordarTren() throws InterruptedException {
		lock.lock();
		
		String threadName = Thread.currentThread().getName();
		
		try {
			while(	trenEstacionA.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionA)] != 0 ||
					marcado[plazas.indexOf(maquina)] == 0 && marcado[plazas.indexOf(vagon)] == 0 || marcado[plazas.indexOf(pasajerosEsperandoSubidaA)] == 0) ) {
				subidaEstacionA.await();
			}
			while(	trenEstacionB.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionB)] != 0 || 
					marcado[plazas.indexOf(maquina)] == 0 && marcado[plazas.indexOf(vagon)] == 0 || marcado[plazas.indexOf(pasajerosEsperandoSubidaB)] == 0) ) {
				subidaEstacionB.await();
			}
			while(	trenEstacionC.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionC)] != 0 || 
					marcado[plazas.indexOf(maquina)] == 0 && marcado[plazas.indexOf(vagon)] == 0 || marcado[plazas.indexOf(pasajerosEsperandoSubidaC)] == 0) ) {
				subidaEstacionC.await();
			}
			while(	trenEstacionD.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionD)] != 0 || 
					marcado[plazas.indexOf(maquina)] == 0 && marcado[plazas.indexOf(vagon)] == 0 || marcado[plazas.indexOf(pasajerosEsperandoSubidaD)] == 0) ) {
				subidaEstacionD.await();
			}

			boolean disparoExitoso = false;
			ArrayList<String> listaSubidas = new ArrayList<>(Arrays.asList(abordarTren.keySet().toArray(new String[abordarTren.keySet().size()])));
			for(String subida: listaSubidas) {
				if(		marcado[plazas.indexOf(subida.startsWith("SM")? maquina : vagon)] != 0 && 
						marcado[plazas.indexOf(trenEstacion + threadName.substring(threadName.length() - 1))] == 0 && 
						abordarTren.get(subida).endsWith(threadName.substring(threadName.length() - 1))) {
					disparoExitoso = dispararRed(subida);
					if(disparoExitoso) {
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
	}

	public Long descenderTren() throws InterruptedException {
		lock.lock();

		String threadName = Thread.currentThread().getName();
		
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
						return ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime();
					} else {
						return ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime();
					}
				} else if(ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > actual.getTime()) {
					return ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime();
				} else if(ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() > actual.getTime()) {
					return ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime();
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
						return ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime();
					} else {
						return ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime();
					}
				} else if(ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > actual.getTime()) {
					return ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime();
				} else if(ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() > actual.getTime()) {
					return ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime();
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
						return ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime();
					} else {
						return ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime();
					}
				} else if(ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > actual.getTime()) {
					return ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime();
				} else if(ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() > actual.getTime()) {
					return ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime();
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
						return ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime();
					} else {
						return ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime();
					}
				} else if(ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() > actual.getTime()) {
					return ultimaSubidaEstacion.get(trenEstacion + estacionOpuestaTren).getTime() - actual.getTime();
				} else if(ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() > actual.getTime()) {
					return ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() - actual.getTime();
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
						if(bajada.endsWith(estacionAnteriorTren)) {
							int tiempoEsperadoAnterior = TiempoDeEspera.getInstance(5000, 97L).getNextRandom();
							ultimaSubidaEstacion.put(trenEstacion + estacionAnteriorTren, new Date(ultimaSubidaEstacion.get(trenEstacion + estacionAnteriorTren).getTime() + tiempoEsperadoAnterior));
						}
						if(bajada.endsWith(estacionOpuestaTren)) {
							int tiempoEsperadoOpuesta = TiempoDeEspera.getInstance(5000, 97L).getNextRandom();
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
		return 0L;
	}


	public void cruzarPasoNivel() throws InterruptedException {
		lock.lock();

		String threadName = Thread.currentThread().getName();
		
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
					dispararRed(tranPasoNivelABMaquinaWait);
				}
				if(marcado[plazas.indexOf(pasoNivelCDMaquinaEsperando)] != 0) {
					dispararRed(tranPasoNivelCDMaquinaWait);
				}
			}
			if(threadName.endsWith(pasoNivelVagon)) {
				if(marcado[plazas.indexOf(pasoNivelABVagonEsperando)] != 0) {
					dispararRed(tranPasoNivelABVagonWait);
				}
				if(marcado[plazas.indexOf(pasoNivelCDVagonEsperando)] != 0) {
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
	}

	public void liberarBarreraPasoNivel() throws InterruptedException {
		lock.lock();

		String threadName = Thread.currentThread().getName();
		
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
					disparoExitoso = dispararRed(tranPasoNivelABMaquinaReady);
				}
				if(marcado[plazas.indexOf(pasoNivelABVagon)] != 0 && !disparoExitoso) {
					disparoExitoso = dispararRed(tranPasoNivelABVagonReady);
				}
				if(marcado[plazas.indexOf(pasoNivelABTransito)] != 0 && !disparoExitoso) {
					disparoExitoso = dispararRed(tranPasoNivelABTransitoReady);
				}
				if(marcado[plazas.indexOf(pasoNivelABTransitoEsperando)] != 0 && !disparoExitoso) {
					disparoExitoso = dispararRed(tranPasoNivelABTransitoWait);
				}
			}
			if(threadName.endsWith(pasoNivelTransitoCD)) {
				if(marcado[plazas.indexOf(pasoNivelCDMaquina)] != 0 && !disparoExitoso) {
					disparoExitoso = dispararRed(tranPasoNivelCDMaquinaReady);
				}
				if(marcado[plazas.indexOf(pasoNivelCDVagon)] != 0 && !disparoExitoso) {
					disparoExitoso = dispararRed(tranPasoNivelCDVagonReady);
				}
				if(marcado[plazas.indexOf(pasoNivelCDTransito)] != 0 && !disparoExitoso) {
					disparoExitoso = dispararRed(tranPasoNivelCDTransitoReady);
				}
				if(marcado[plazas.indexOf(pasoNivelCDTransitoEsperando)] != 0 && !disparoExitoso) {
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
	}


	public void generarPasajeros() throws InterruptedException {
		lock.lock();

		String threadName = Thread.currentThread().getName();
		
		try {
			
			for(String estacion: estaciones) {
				if(threadName.endsWith(estacion)) {
					dispararRed(tranPasajerosAGenerador.substring(0, 1) + estacion + tranPasajerosAGenerador.substring(tranPasajerosAGenerador.length() - 1));
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
	}


	public void generarTransito() throws InterruptedException {
		lock.lock();

		String threadName = Thread.currentThread().getName();
		
		try {
			
			if(threadName.endsWith(recorridoAB)) {
				dispararRed(tranPasoNivelABTransitoGenerador);
			}
			if(threadName.endsWith(recorridoCD)) {
				dispararRed(tranPasoNivelCDTransitoGenerador);
			}
			
			String transicion = interseccionPrioritarias();
			if(transicion != null) {
				colaCondicion.get(transicion).signal();
			}
		} finally {
			lock.unlock();
		}
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
		
		System.out.println(" ");
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
		System.out.println(" "+transicion);
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
		imprimirMarcado();
		return true;
	}
}
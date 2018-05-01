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
	
	private Integer[] marcado;
	private LinkedHashMap<String, Integer> marcadoInicial;
	private ArrayList<String> plazas;
	
	private ArrayList<String> transiciones;
	private ArrayList<String> recorridoTren;
	private LinkedHashMap<String, Condition> colaCondicion;
//	private LinkedHashMap<Integer, String> politicas;
	
	private LinkedHashMap<String, String> abordarTren;
	private LinkedHashMap<String, String> descenderTren;
	
	private ArrayList<String> estaciones;
	private LinkedHashMap<String, Date> ultimaSubidaEstacion;
	private LinkedHashMap<String, Date> arriboEstacion;
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
	private final Condition trenArriboEstacion = lock.newCondition();
	private final Condition trenRecorridoAB = lock.newCondition();
	private final Condition trenRecorridoCD = lock.newCondition();

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

	private final Condition pasoDeNivelTransitoAB = lock.newCondition();
	private final Condition pasoDeNivelTransitoCD = lock.newCondition();
	
	private final Condition pasoDeNivelMaquinaAB = lock.newCondition();
	private final Condition pasoDeNivelMaquinaCD = lock.newCondition();
	
	private final Condition pasoDeNivelVagonAB = lock.newCondition();
	private final Condition pasoDeNivelVagonCD = lock.newCondition();

	private final Condition liberarBarreraPasoNivel = lock.newCondition();
	
	
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
	
	/* Transito en el paso de nivel Esperando */
	private final String pasoNivelABTransitoEsperando = "PNABTQ";
	private final String pasoNivelCDTransitoEsperando = "PNCDTQ";
	
	/* Maquina en el paso de nivel Cruzando */
	private final String pasoNivelABMaquina = "RABM";
	private final String pasoNivelABVagon = "RABV";
	
	/* Vagon en el paso de nivel Cruzando */
	private final String pasoNivelCDMaquina = "RCDM";
	private final String pasoNivelCDVagon = "RCDV";
	
	
	
	
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
		
		this.colaCondicion.put(tranRecorridoTrenAB, trenRecorridoAB);
		this.colaCondicion.put(tranRecorridoTrenCD, trenRecorridoCD);
		
		this.colaCondicion.put(tranPasajerosAGenerador, pasajeroGeneradorEstacionA);
		this.colaCondicion.put(tranPasajerosBGenerador, pasajeroGeneradorEstacionB);
		this.colaCondicion.put(tranPasajerosCGenerador, pasajeroGeneradorEstacionC);
		this.colaCondicion.put(tranPasajerosDGenerador, pasajeroGeneradorEstacionD);
		
		
		
		this.colaCondicion.put(tranPasoNivelABMaquinaReady, pasoDeNivelMaquinaAB);
		this.colaCondicion.put(tranPasoNivelCDMaquinaReady, pasoDeNivelMaquinaCD);
		this.colaCondicion.put(tranPasoNivelABMaquinaWait, pasoDeNivelMaquinaAB);
		this.colaCondicion.put(tranPasoNivelCDMaquinaWait, pasoDeNivelMaquinaCD);
		
		this.colaCondicion.put(tranPasoNivelABVagonReady, pasoDeNivelVagonAB);
		this.colaCondicion.put(tranPasoNivelCDVagonReady, pasoDeNivelVagonCD);
		this.colaCondicion.put(tranPasoNivelABVagonWait, pasoDeNivelVagonAB);
		this.colaCondicion.put(tranPasoNivelCDVagonWait, pasoDeNivelVagonCD);
		
		this.colaCondicion.put(tranPasoNivelABTransitoReady, pasoDeNivelTransitoAB);
		this.colaCondicion.put(tranPasoNivelCDTransitoReady, pasoDeNivelTransitoCD);
		this.colaCondicion.put(tranPasoNivelABTransitoWait, pasoDeNivelTransitoAB);
		this.colaCondicion.put(tranPasoNivelCDTransitoWait, pasoDeNivelTransitoCD);
		
		this.colaCondicion.put(tranPasoNivelABTransitoGenerador, pasoDeNivelTransitoGeneradorAB);
		this.colaCondicion.put(tranPasoNivelCDTransitoGenerador, pasoDeNivelTransitoGeneradorCD);
		
		
//		this.politicas = new LinkedHashMap<>();
//		int index = 0;
//		this.politicas.put(index++, tranBajadaMaquinaAEstacionD);
//		this.politicas.put(index++, tranBajadaMaquinaBEstacionA);
//		this.politicas.put(index++, tranBajadaMaquinaCEstacionB);
//		this.politicas.put(index++, tranBajadaMaquinaDEstacionC);
//		this.politicas.put(index++, tranBajadaVagonAEstacionD);
//		this.politicas.put(index++, tranBajadaVagonBEstacionA);
//		this.politicas.put(index++, tranBajadaVagonCEstacionB);
//		this.politicas.put(index++, tranBajadaVagonDEstacionC);
//		
//		
//		this.politicas.put(index++, tranBajadaMaquinaAEstacionB);
//		this.politicas.put(index++, tranBajadaMaquinaAEstacionC);
//		this.politicas.put(index++, tranBajadaMaquinaBEstacionC);
//		this.politicas.put(index++, tranBajadaMaquinaBEstacionD);
//		this.politicas.put(index++, tranBajadaMaquinaCEstacionA);
//		this.politicas.put(index++, tranBajadaMaquinaCEstacionD);
//		this.politicas.put(index++, tranBajadaMaquinaDEstacionA);
//		this.politicas.put(index++, tranBajadaMaquinaDEstacionB);
//		this.politicas.put(index++, tranBajadaVagonAEstacionB);
//		this.politicas.put(index++, tranBajadaVagonAEstacionC);
//		this.politicas.put(index++, tranBajadaVagonBEstacionC);
//		this.politicas.put(index++, tranBajadaVagonBEstacionD);
//		this.politicas.put(index++, tranBajadaVagonCEstacionA);
//		this.politicas.put(index++, tranBajadaVagonCEstacionD);
//		this.politicas.put(index++, tranBajadaVagonDEstacionA);
//		this.politicas.put(index++, tranBajadaVagonDEstacionB);
//		
//		
//		this.politicas.put(index++, tranSubidaMaquinaEstacionA);
//		this.politicas.put(index++, tranSubidaMaquinaEstacionB);
//		this.politicas.put(index++, tranSubidaMaquinaEstacionC);
//		this.politicas.put(index++, tranSubidaMaquinaEstacionD);
//		this.politicas.put(index++, tranSubidaVagonEstacionA);
//		this.politicas.put(index++, tranSubidaVagonEstacionB);
//		this.politicas.put(index++, tranSubidaVagonEstacionC);
//		this.politicas.put(index++, tranSubidaVagonEstacionD);
//		
//		
//		this.politicas.put(index++, tranTrenLlenoEstacionVaciaA);
//		this.politicas.put(index++, tranTrenLlenoEstacionVaciaB);
//		this.politicas.put(index++, tranTrenLlenoEstacionVaciaC);
//		this.politicas.put(index++, tranTrenLlenoEstacionVaciaD);
//		
//		this.politicas.put(index++, tranTrenEsperandoA);
//		this.politicas.put(index++, tranTrenEsperandoB);
//		this.politicas.put(index++, tranTrenEsperandoC);
//		this.politicas.put(index++, tranTrenEsperandoD);
//
//		this.politicas.put(index++, tranPasoNivelABTransitoReady);
//		this.politicas.put(index++, tranPasoNivelCDTransitoReady);
//		this.politicas.put(index++, tranPasoNivelABTransitoWait);
//		this.politicas.put(index++, tranPasoNivelCDTransitoWait);

		
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
		
		arriboEstacion = new LinkedHashMap<>();
		arriboEstacion.put(trenEstacionA, fechaActual);
		arriboEstacion.put(trenEstacionB, fechaActual);
		arriboEstacion.put(trenEstacionC, fechaActual);
		arriboEstacion.put(trenEstacionD, fechaActual);
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
			
			while((	marcado[plazas.indexOf(trenEstacionAPartida)] == 1 && marcado[plazas.indexOf(pasajerosEsperandoSubidaA)] != 0 || 
					marcado[plazas.indexOf(trenEstacionBPartida)] == 1 && marcado[plazas.indexOf(pasajerosEsperandoSubidaB)] != 0 || 
					marcado[plazas.indexOf(trenEstacionCPartida)] == 1 && marcado[plazas.indexOf(pasajerosEsperandoSubidaC)] != 0 || 
					marcado[plazas.indexOf(trenEstacionDPartida)] == 1 && marcado[plazas.indexOf(pasajerosEsperandoSubidaD)] != 0) && 
					(marcado[plazas.indexOf(vagon)] != 0 || marcado[plazas.indexOf(maquina)] != 0)
					) {
				fullTrenOrEmptyEstacion.await();
			}
			
			
//			if(		marcado[plazas.indexOf(trenEstacionAArribo)] == 1 || 
//					marcado[plazas.indexOf(trenEstacionBArribo)] == 1 ||
//					marcado[plazas.indexOf(trenEstacionCArribo)] == 1 ||
//					marcado[plazas.indexOf(trenEstacionDArribo)] == 1) {
//				((Tren)Thread.currentThread()).setTimeStamp(new Date());
//			}
			

			ArrayList<String> prioritarias = new ArrayList<>(Arrays.asList(colaCondicion.keySet().toArray(new String[colaCondicion.keySet().size()])));
			
			LinkedHashMap<String, Boolean> preSensibilizadas = getSensibilizadas();
			for(String transicion: prioritarias) {
				if(preSensibilizadas.get(transicion) && recorridoTren.contains(transicion)) {
					dispararRed(transicion);
					break;
				}
			}
			
//			if(!disparoRealizado) {
//				for(String transicion: transiciones) {
//					if(preSensibilizadas.get(transicion) && (!prioritarias.contains(transicion))) {
//						dispararRed(transicion);
//						disparada = transicion;
//						break;
//					}
//				}
//			}
			
//			boolean ningunaCondicionNotificada = true;
//			while (ningunaCondicionNotificada) {
				LinkedHashMap<String, Boolean> vectorInterseccion = getInterseccionCondicion(getSensibilizadas(), lock);
				for(String transicion: prioritarias) {
					if(vectorInterseccion.get(transicion)) {
						colaCondicion.get(transicion).signal();
//						ningunaCondicionNotificada = false;
						return;
					}
				}
				
//				if(ningunaCondicionNotificada) {
//					for(String transicion: transiciones) {
//						if(vectorInterseccion.get(transicion) && !prioritarias.contains(transicion)) {
//							dispararRed(transicion);
//							break;
//						}
//					}
//				}
//			}
				
				/* PostDisparo se busca en las colas de condicion el siguiente hilo a despertar */

//				if(!disparoRealizado) {
//					LinkedHashMap<String, Boolean> vectorSensicilizadas = getSensibilizadas();
//					LinkedHashMap<String, Boolean> vectorInterseccion = getInterseccionCondicion(vectorSensicilizadas, lock);
//					for(String transicion: prioritarias) {
//						if(vectorInterseccion.get(transicion) || (transicion.equals(disparada) && vectorSensicilizadas.get(disparada))) {
//							colaCondicion.get(transicion).signal();
//							return;
//						}
//					}
//					
//					for(String transicion: transiciones) {
//						if(vectorInterseccion.get(transicion) && !prioritarias.contains(transicion)) {
//							dispararRed(transicion);
//							return;
//						}
//					}
//				}
			
			
		} finally {
			lock.unlock();
		}
	}
	
	public void arrivoTrenEstacion() throws InterruptedException {
		lock.lock();
		
		try {
			while((	marcado[plazas.indexOf(trenEstacionAArribo)] == 0 || 
					marcado[plazas.indexOf(trenEstacionBArribo)] == 0 || 
					marcado[plazas.indexOf(trenEstacionCArribo)] == 0 || 
					marcado[plazas.indexOf(trenEstacionDArribo)] == 0 
					) ) {
				trenArriboEstacion.await();
			}
			
			for(String estacion: estaciones) {
				if(marcado[plazas.indexOf(trenEstacionAArribo.substring(0 ,trenEstacionAArribo.length() - 2) + estacion + trenEstacionAArribo.substring(trenEstacionAArribo.length() - 1))] == 1) {
					arriboEstacion.put(trenEstacion + estacion, new Date());
					dispararRed(estacion + tranTrenArribo);
					break;
				}
			}

			ArrayList<String> prioritarias = new ArrayList<>(Arrays.asList(colaCondicion.keySet().toArray(new String[colaCondicion.keySet().size()])));
			LinkedHashMap<String, Boolean> vectorInterseccion = getInterseccionCondicion(getSensibilizadas(), lock);
			for(String transicion: prioritarias) {
				if(vectorInterseccion.get(transicion)) {
					colaCondicion.get(transicion).signal();
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
					marcado[plazas.indexOf(maquina)] == 0 && marcado[plazas.indexOf(vagon)] == 0 || marcado[plazas.indexOf(pasajerosEsperandoSubidaA)] == 0) ) {
				subidaEstacionA.await();
			}
			while(	trenEstacionB.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionB)] == 0 || 
					marcado[plazas.indexOf(maquina)] == 0 && marcado[plazas.indexOf(vagon)] == 0 || marcado[plazas.indexOf(pasajerosEsperandoSubidaB)] == 0) ) {
				subidaEstacionB.await();
			}
			while(	trenEstacionC.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionC)] == 0 || 
					marcado[plazas.indexOf(maquina)] == 0 && marcado[plazas.indexOf(vagon)] == 0 || marcado[plazas.indexOf(pasajerosEsperandoSubidaC)] == 0) ) {
				subidaEstacionC.await();
			}
			while(	trenEstacionD.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionD)] == 0 || 
					marcado[plazas.indexOf(maquina)] == 0 && marcado[plazas.indexOf(vagon)] == 0 || marcado[plazas.indexOf(pasajerosEsperandoSubidaD)] == 0) ) {
				subidaEstacionD.await();
			}

			boolean disparoExitoso = false;
			ArrayList<String> listaSubidas = new ArrayList<>(Arrays.asList(abordarTren.keySet().toArray(new String[abordarTren.keySet().size()])));
			for(String subida: listaSubidas) {
				System.out.println(abordarTren.get(subida) +" "+ threadName.substring(threadName.length() - 1));
				if(		marcado[plazas.indexOf(subida.startsWith("SM")? maquina : vagon)] != 0 && 
						marcado[plazas.indexOf(trenEstacion + threadName.substring(threadName.length() - 1))] == 1 && 
						abordarTren.get(subida).endsWith(threadName.substring(threadName.length() - 1))) {
					disparoExitoso = dispararRed(subida);
					if(disparoExitoso) {
						ultimaSubidaEstacion.put(trenEstacion + threadName.substring(threadName.length() - 1), new Date());
						break;
					}
				}
			}
			
			
			ArrayList<String> prioritarias = new ArrayList<>(Arrays.asList(colaCondicion.keySet().toArray(new String[colaCondicion.keySet().size()])));

			LinkedHashMap<String, Boolean> vectorInterseccion = getInterseccionCondicion(getSensibilizadas(), lock);
			for(String transicion: prioritarias) {
				if(vectorInterseccion.get(transicion)) {
					colaCondicion.get(transicion).signal();
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
				bajadaEstacionA.await();
			}
			while(	trenEstacionB.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionB)] == 0 || 
					marcado[plazas.indexOf(maqA)] == 0 && marcado[plazas.indexOf(vagA)] == 0 && 
					marcado[plazas.indexOf(maqC)] == 0 && marcado[plazas.indexOf(vagC)] == 0 && 
					marcado[plazas.indexOf(maqD)] == 0 && marcado[plazas.indexOf(vagD)] == 0) ) {
				bajadaEstacionB.await();
			}
			while(	trenEstacionC.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionC)] == 0 ||  
					marcado[plazas.indexOf(maqA)] == 0 && marcado[plazas.indexOf(vagA)] == 0 && 
					marcado[plazas.indexOf(maqB)] == 0 && marcado[plazas.indexOf(vagB)] == 0 && 
					marcado[plazas.indexOf(maqD)] == 0 && marcado[plazas.indexOf(vagD)] == 0) ) {
				bajadaEstacionC.await();
			}
			while(	trenEstacionD.endsWith(threadName.substring(threadName.length() - 1)) && (marcado[plazas.indexOf(trenEstacionD)] == 0 || 
					marcado[plazas.indexOf(maqA)] == 0 && marcado[plazas.indexOf(vagA)] == 0 && 
					marcado[plazas.indexOf(maqB)] == 0 && marcado[plazas.indexOf(vagB)] == 0 && 
					marcado[plazas.indexOf(maqC)] == 0 && marcado[plazas.indexOf(vagC)] == 0) ) {
				bajadaEstacionD.await();
			}


			// pasajerosAnterior calcula bajadas estocasticas para los pasajeros subidos en la estacion anterior utilizando la hora de la ultima subida en esa estacion
			// la estacion anterior se obtiene buscando en la lista de estaciones la estacion correspondiente al indice anterior al de la estacion actual. En el caso en que el indice 
			// sea 0 la estacion anterior se encontraria en el indice -1 si ultilizamos un offset negativo por lo que se suma el tamanio del array y se calcula el modulo para que el indice
			// siempre este dentro del array. (-1 + 4) = 3 para la estacion anterior y (-2 + 4) = 2 para la estacion opuesta de esta forma usamos al array como un anillo (campo finito cerrado o campo de Galois)
			Integer pasajerosAnterior = ((BajarPasajeros) Thread.currentThread()).getPasajeros(ultimaSubidaEstacion.get(trenEstacion + estacion[(estaciones.indexOf(threadName.substring(threadName.length() - 1)) + 3)%4]));
			Integer pasajerosOpuesta = ((BajarPasajeros) Thread.currentThread()).getPasajeros(ultimaSubidaEstacion.get(trenEstacion + estacion[(estaciones.indexOf(threadName.substring(threadName.length() - 1)) + 2)%4]));
			System.out.println("\n"+"PasajerosAnterior : "+pasajerosAnterior+" -  PasajerosOpuesta : "+pasajerosOpuesta+"\n");
			
			//TODO
			boolean disparoExitoso = false;
			ArrayList<String> listaBajadas = new ArrayList<>(Arrays.asList(descenderTren.keySet().toArray(new String[descenderTren.keySet().size()])));
			for(String bajada: listaBajadas) {
				System.out.println(descenderTren.get(bajada) +" "+ threadName.substring(threadName.length() - 1));
				if(		bajada.startsWith("B"+ threadName.substring(threadName.length() - 1)) &&																	// Si el thread baja pasajeros en la estacion de la tansicion
						marcado[plazas.indexOf(trenEstacion + threadName.substring(threadName.length() - 1))] == 1 &&												// Si el tren se encuentra en la estacion del thread
						marcado[plazas.indexOf(bajada.substring(2, bajada.length()))] != 0 && (																		// Si hay pasajeros viajando desde la estacion de la transicion
								bajada.endsWith(estacion[(estaciones.indexOf(threadName.substring(threadName.length() - 1)) + 3)%4]) && pasajerosAnterior > 0 ||	// Si la transicion baja pasajeros de la estacion anterior
								bajada.endsWith(estacion[(estaciones.indexOf(threadName.substring(threadName.length() - 1)) + 2)%4]) && pasajerosOpuesta > 0 ||		// Si la transicion baja pasajeros de la estacion opuesta
								bajada.endsWith(estacion[(estaciones.indexOf(threadName.substring(threadName.length() - 1)) + 1)%4])								// Si la transicion baja pasajeros de la estacion siguiente
						) ) {
					disparoExitoso = dispararRed(bajada);
					if(disparoExitoso) {
						break;
					}
				}
			}
			

			ArrayList<String> prioritarias = new ArrayList<>(Arrays.asList(colaCondicion.keySet().toArray(new String[colaCondicion.keySet().size()])));
			LinkedHashMap<String, Boolean> vectorInterseccion = getInterseccionCondicion(getSensibilizadas(), lock);
			for(String transicion: prioritarias) {
				if(vectorInterseccion.get(transicion)) {
					colaCondicion.get(transicion).signal();
					return;
				}
			}

			
		} finally {
			lock.unlock();
		}
	}


	public void cruzarPasoNivel() throws InterruptedException {
		lock.lock();

		String threadName = Thread.currentThread().getName();
		
		try {
			while(	pasoNivelTransitoAB.equalsIgnoreCase(threadName) && 
					(	marcado[plazas.indexOf(pasoNivelABTransitoEsperando)] == 0 || marcado[plazas.indexOf(pasoNivelABBarrera)] == 0 || 
						marcado[plazas.indexOf(pasoNivelABMaquina)] != 0 || marcado[plazas.indexOf(pasoNivelABVagon)] != 0
					) ) {
				pasoDeNivelTransitoAB.await();
			}
			while(	pasoNivelTransitoCD.equalsIgnoreCase(threadName) && 
					(	marcado[plazas.indexOf(pasoNivelCDTransitoEsperando)] == 0 || marcado[plazas.indexOf(pasoNivelCDBarrera)] == 0 || 
						marcado[plazas.indexOf(pasoNivelCDMaquina)] != 0 || marcado[plazas.indexOf(pasoNivelCDVagon)] != 0
					) ) {
				pasoDeNivelTransitoCD.await();
			}
			while(	pasoNivelVagonAB.equalsIgnoreCase(threadName) && 
					(	marcado[plazas.indexOf(pasoNivelABVagon)] == 0 || marcado[plazas.indexOf(pasoNivelABBarrera)] == 0|| 
						marcado[plazas.indexOf(pasoNivelABMaquina)] != 0
					) ) {
				pasoDeNivelVagonAB.await();
			}
			while(	pasoNivelVagonCD.equalsIgnoreCase(threadName) && 
					(	marcado[plazas.indexOf(pasoNivelCDVagon)] == 0 || marcado[plazas.indexOf(pasoNivelCDBarrera)] == 0|| 
						marcado[plazas.indexOf(pasoNivelCDMaquina)] != 0
					) ) {
				pasoDeNivelVagonCD.await();
			}
			while(	pasoNivelMaquinaAB.equalsIgnoreCase(threadName) && 
					(	marcado[plazas.indexOf(pasoNivelABMaquina)] == 0 || marcado[plazas.indexOf(pasoNivelABBarrera)] == 0
					) ) {
				pasoDeNivelMaquinaAB.await();
			}
			while(	pasoNivelMaquinaCD.equalsIgnoreCase(threadName) && 
					(	marcado[plazas.indexOf(pasoNivelCDMaquina)] == 0 || marcado[plazas.indexOf(pasoNivelCDBarrera)] == 0
					) ) {
				pasoDeNivelMaquinaCD.await();
			}
		
			
			if(pasoNivelTransitoAB.equalsIgnoreCase(threadName)) {
				dispararRed(tranPasoNivelABTransitoWait);
//				disparoExitoso = dispararRed(tranPasoNivelABTransitoReady);
			}
			if(pasoNivelTransitoCD.equalsIgnoreCase(threadName)) {
				dispararRed(tranPasoNivelCDTransitoWait);
			}
			if(pasoNivelMaquinaAB.equalsIgnoreCase(threadName)) {
				dispararRed(tranPasoNivelABMaquinaWait);
			}
			if(pasoNivelMaquinaCD.equalsIgnoreCase(threadName)) {
				dispararRed(tranPasoNivelCDMaquinaWait);
			}
			if(pasoNivelVagonAB.equalsIgnoreCase(threadName)) {
				dispararRed(tranPasoNivelABVagonWait);
			}
			if(pasoNivelVagonCD.equalsIgnoreCase(threadName)) {
				dispararRed(tranPasoNivelCDVagonWait);
			}
			
			ArrayList<String> prioritarias = new ArrayList<>(Arrays.asList(colaCondicion.keySet().toArray(new String[colaCondicion.keySet().size()])));
			LinkedHashMap<String, Boolean> vectorInterseccion = getInterseccionCondicion(getSensibilizadas(), lock);
			for(String transicion: prioritarias) {
				if(vectorInterseccion.get(transicion)) {
					colaCondicion.get(transicion).signal();
					return;
				}
			}
			
		} finally {
			lock.unlock();
		}
	}

	

	public void liberarBarreraPasoNivel() throws InterruptedException {
		lock.lock();

		String threadName = Thread.currentThread().getName();
		
		try {
			while(	pasoNivelTransitoAB.equalsIgnoreCase(threadName) && 
					(	marcado[plazas.indexOf(pasoNivelABTransitoEsperando)] == 0 || marcado[plazas.indexOf(pasoNivelABBarrera)] == 0 || 
						marcado[plazas.indexOf(pasoNivelABMaquina)] != 0 || marcado[plazas.indexOf(pasoNivelABVagon)] != 0
					) ) {
				liberarBarreraPasoNivel.await();
			}
			while(	pasoNivelTransitoCD.equalsIgnoreCase(threadName) && 
					(	marcado[plazas.indexOf(pasoNivelCDTransitoEsperando)] == 0 || marcado[plazas.indexOf(pasoNivelCDBarrera)] == 0 || 
						marcado[plazas.indexOf(pasoNivelCDMaquina)] != 0 || marcado[plazas.indexOf(pasoNivelCDVagon)] != 0
					) ) {
				liberarBarreraPasoNivel.await();
			}
			while(	pasoNivelVagonAB.equalsIgnoreCase(threadName) && 
					(	marcado[plazas.indexOf(pasoNivelABVagon)] == 0 || marcado[plazas.indexOf(pasoNivelABBarrera)] == 0|| 
						marcado[plazas.indexOf(pasoNivelABMaquina)] != 0
					) ) {
				liberarBarreraPasoNivel.await();
			}
			while(	pasoNivelVagonCD.equalsIgnoreCase(threadName) && 
					(	marcado[plazas.indexOf(pasoNivelCDVagon)] == 0 || marcado[plazas.indexOf(pasoNivelCDBarrera)] == 0|| 
						marcado[plazas.indexOf(pasoNivelCDMaquina)] != 0
					) ) {
				liberarBarreraPasoNivel.await();
			}
			while(	pasoNivelMaquinaAB.equalsIgnoreCase(threadName) && 
					(	marcado[plazas.indexOf(pasoNivelABMaquina)] == 0 || marcado[plazas.indexOf(pasoNivelABBarrera)] == 0
					) ) {
				liberarBarreraPasoNivel.await();
			}
			while(	pasoNivelMaquinaCD.equalsIgnoreCase(threadName) && 
					(	marcado[plazas.indexOf(pasoNivelCDMaquina)] == 0 || marcado[plazas.indexOf(pasoNivelCDBarrera)] == 0
					) ) {
				liberarBarreraPasoNivel.await();
			}
		
			
			if(pasoNivelTransitoAB.equalsIgnoreCase(threadName)) {
				dispararRed(tranPasoNivelABTransitoWait);
//				disparoExitoso = dispararRed(tranPasoNivelABTransitoReady);
			}
			if(pasoNivelTransitoCD.equalsIgnoreCase(threadName)) {
				dispararRed(tranPasoNivelCDTransitoWait);
			}
			if(pasoNivelMaquinaAB.equalsIgnoreCase(threadName)) {
				dispararRed(tranPasoNivelABMaquinaWait);
			}
			if(pasoNivelMaquinaCD.equalsIgnoreCase(threadName)) {
				dispararRed(tranPasoNivelCDMaquinaWait);
			}
			if(pasoNivelVagonAB.equalsIgnoreCase(threadName)) {
				dispararRed(tranPasoNivelABVagonWait);
			}
			if(pasoNivelVagonCD.equalsIgnoreCase(threadName)) {
				dispararRed(tranPasoNivelCDVagonWait);
			}
			
			ArrayList<String> prioritarias = new ArrayList<>(Arrays.asList(colaCondicion.keySet().toArray(new String[colaCondicion.keySet().size()])));
			LinkedHashMap<String, Boolean> vectorInterseccion = getInterseccionCondicion(getSensibilizadas(), lock);
			for(String transicion: prioritarias) {
				if(vectorInterseccion.get(transicion)) {
					colaCondicion.get(transicion).signal();
					return;
				}
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
//		secuenciaTransiciones.add("PNCDMW");
//		secuenciaTransiciones.add("PNCDMR");
//		secuenciaTransiciones.add("PNCDVW");
//		secuenciaTransiciones.add("PNCDVR");
//		secuenciaTransiciones.add("RCD");
		secuenciaTransiciones.add("DAr");
		secuenciaTransiciones.add("DW");
		secuenciaTransiciones.add("DDe");
		secuenciaTransiciones.add("AAr");
		secuenciaTransiciones.add("AW");
		secuenciaTransiciones.add("ADe");
//		secuenciaTransiciones.add("PNABMW");
//		secuenciaTransiciones.add("PNABMR");
//		secuenciaTransiciones.add("PNABVW");
//		secuenciaTransiciones.add("PNABVR");
//		secuenciaTransiciones.add("RAB");
		
		return secuenciaTransiciones;
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
package main;

public class ConstantesComunes {
	public static final String estacionA = "Estacion A";
	public static final String estacionB = "Estacion B";
	public static final String estacionC = "Estacion C";
	public static final String estacionD = "Estacion D";

	public static final String subida = "Subida";

	public static final String bajada = "Bajada";
	
	public static final String[] estacion = {"A", "B", "C", "D"};

	public static final String tren = "Tren";
	
	public static final String maquinaTren = "Maquina";
	
	public static final String vagonTren = "Vagon";

	public static final String pasajeros = "Pasajeros";
	
	public static final String transito = "Transito";
	
	public static final String recorridoAB = "AB";
	public static final String recorridoCD = "CD";
	
	public static final String pasoNivelTransitoAB = transito+recorridoAB;
	public static final String pasoNivelTransitoCD = transito+recorridoCD;
	
	public static final String pasoNivel = "PasoNivel";
	
	public static final String pasoNivelMaquina = pasoNivel+maquinaTren;
	
	public static final String pasoNivelVagon = pasoNivel+vagonTren;
	
	public static final String precedenciaPrincipal = "Principal";
	public static final String precedenciaAuxiliar = "Auxiliar";
	public static final String precedenciaAuxiliarArrivo = precedenciaAuxiliar+"Arrivo";
	public static final String precedenciaAuxiliarPartida = precedenciaAuxiliar+"Partida";
	
	public static final String generador = "Generador";
	public static final String generadorTransito = generador+transito;
	public static final String generadorPasajeros = generador+pasajeros;
	
	

	/* Plazas */

	/* Tren en Estacion */
	public static final String trenEstacion = "TE";
	public static final String trenEstacionA = trenEstacion+"A";
	public static final String trenEstacionB = trenEstacion+"B";
	public static final String trenEstacionC = trenEstacion+"C";
	public static final String trenEstacionD = trenEstacion+"D";
	
	/* Tren en Espera */
	public static final String trenEstacionAEspera = "ATW";
	public static final String trenEstacionBEspera = "BTW";
	public static final String trenEstacionCEspera = "CTW";
	public static final String trenEstacionDEspera = "DTW";

	/* Tren esparando condicion de partida*/
	public static final String trenEstacionAPartida = "ATR";
	public static final String trenEstacionBPartida = "BTR";
	public static final String trenEstacionCPartida = "CTR";
	public static final String trenEstacionDPartida = "DTR";

	/* Tren en recorrido llegando a estacion */
	public static final String trenEstacionAArribo = "RDAT";
	public static final String trenEstacionBArribo = "RABT";
	public static final String trenEstacionCArribo = "RBCT";
	public static final String trenEstacionDArribo = "RCDT";
	
	/* Pasajeros esperando subida */
	public static final String pasajerosEsperandoSubidaA = "PAQ";
	public static final String pasajerosEsperandoSubidaB = "PBQ";
	public static final String pasajerosEsperandoSubidaC = "PCQ";
	public static final String pasajerosEsperandoSubidaD = "PDQ";

	/* Lugares del tren compuesto por Vagon y Maquina */
	public static final String vag = "VAG";
	public static final String maq = "MAQ";
	
	/* Lugares del vagon ocupados por pasajeros de la estacion indicada */
	public static final String vagA = "VA";
	public static final String vagB = "VB";
	public static final String vagC = "VC";
	public static final String vagD = "VD";
	
	/* Lugares de la maquina ocupados por pasajeros de la estacion indicada */
	public static final String maqA = "MA";
	public static final String maqB = "MB";
	public static final String maqC = "MC";
	public static final String maqD = "MD";
	
	/* Barrera del paso de nivel, recurso compartido */
	public static final String pasoNivelABBarrera = "PNABB";
	public static final String pasoNivelCDBarrera = "PNCDB";
	
	/* Transito en el paso de nivel Cruzando */
	public static final String pasoNivelABTransito = "PNABT";
	public static final String pasoNivelCDTransito = "PNCDT";
	
	/* Maquina en el paso de nivel Cruzando */
	public static final String pasoNivelABMaquina = "PNABM";
	public static final String pasoNivelCDMaquina = "PNCDM";
	
	/* Vagon en el paso de nivel Cruzando */
	public static final String pasoNivelABVagon = "PNABV";
	public static final String pasoNivelCDVagon = "PNCDV";
	
	/* Transito en el paso de nivel Esperando */
	public static final String pasoNivelABTransitoEsperando = "PNABTQ";
	public static final String pasoNivelCDTransitoEsperando = "PNCDTQ";
	
	/* Maquina en el paso de nivel Cruzando */
	public static final String pasoNivelABMaquinaEsperando = "RABM";
	public static final String pasoNivelABVagonEsperando = "RABV";
	
	/* Vagon en el paso de nivel Cruzando */
	public static final String pasoNivelCDMaquinaEsperando = "RCDM";
	public static final String pasoNivelCDVagonEsperando = "RCDV";
	
	/* Maquina fuera del paso de nivel esperando Union */
	public static final String pasoNivelABMaquinaUnion = "RABMR";
	public static final String pasoNivelABVagonUnion = "RABVR";
	
	/* Vagon fuera del paso de nivel esperando Union */
	public static final String pasoNivelCDMaquinaUnion = "RCDMR";
	public static final String pasoNivelCDVagonUnion = "RCDVR";
	
	
	
	
	/* Transiciones */
	
	/* Estacion */
	public static final String tranTrenArriboA = "AAr";
	public static final String tranTrenArriboB = "BAr";
	public static final String tranTrenArriboC = "CAr";
	public static final String tranTrenArriboD = "DAr";
	public static final String tranTrenArribo = "Ar";
	
	public static final String tranTrenEsperandoA = "AW";
	public static final String tranTrenEsperandoB = "BW";
	public static final String tranTrenEsperandoC = "CW";
	public static final String tranTrenEsperandoD = "DW";
	
	public static final String tranTrenLlenoA = "ADe";
	public static final String tranTrenLlenoB = "BDe";
	public static final String tranTrenLlenoC = "CDe";
	public static final String tranTrenLlenoD = "DDe";
	
	public static final String tranEstacionVaciaA = "ADV";
	public static final String tranEstacionVaciaB = "BDV";
	public static final String tranEstacionVaciaC = "CDV";
	public static final String tranEstacionVaciaD = "DDV";
	
	/* Subida de pasajeros */
	public static final String tranSubidaMaquinaEstacionA = "SMA";
	public static final String tranSubidaMaquinaEstacionB = "SMB";
	public static final String tranSubidaMaquinaEstacionC = "SMC";
	public static final String tranSubidaMaquinaEstacionD = "SMD";

	public static final String tranSubidaVagonEstacionA = "SVA";
	public static final String tranSubidaVagonEstacionB = "SVB";
	public static final String tranSubidaVagonEstacionC = "SVC";
	public static final String tranSubidaVagonEstacionD = "SVD";
	
	/* Bajada de pasajeros */
	public static final String tranBajadaMaquinaBEstacionA = "BAMB";
	public static final String tranBajadaMaquinaCEstacionA = "BAMC";
	public static final String tranBajadaMaquinaDEstacionA = "BAMD";

	public static final String tranBajadaVagonBEstacionA = "BAVB";
	public static final String tranBajadaVagonCEstacionA = "BAVC";
	public static final String tranBajadaVagonDEstacionA = "BAVD";

	
	public static final String tranBajadaMaquinaAEstacionB = "BBMA";
	public static final String tranBajadaMaquinaCEstacionB = "BBMC";
	public static final String tranBajadaMaquinaDEstacionB = "BBMD";

	public static final String tranBajadaVagonAEstacionB = "BBVA";
	public static final String tranBajadaVagonCEstacionB = "BBVC";
	public static final String tranBajadaVagonDEstacionB = "BBVD";

	
	public static final String tranBajadaMaquinaAEstacionC = "BCMA";
	public static final String tranBajadaMaquinaBEstacionC = "BCMB";
	public static final String tranBajadaMaquinaDEstacionC = "BCMD";

	public static final String tranBajadaVagonAEstacionC = "BCVA";
	public static final String tranBajadaVagonBEstacionC = "BCVB";
	public static final String tranBajadaVagonDEstacionC = "BCVD";

	
	public static final String tranBajadaMaquinaAEstacionD = "BDMA";
	public static final String tranBajadaMaquinaBEstacionD = "BDMB";
	public static final String tranBajadaMaquinaCEstacionD = "BDMC";

	public static final String tranBajadaVagonAEstacionD = "BDVA";
	public static final String tranBajadaVagonBEstacionD = "BDVB";
	public static final String tranBajadaVagonCEstacionD = "BDVC";

	/* Paso de Nivel Transito */
	public static final String tranPasoNivelABTransitoWait = "PNABTW";
	public static final String tranPasoNivelABTransitoReady = "PNABTR";
	public static final String tranPasoNivelABTransitoGenerador = "PNABTG";
	public static final String tranPasoNivelABMaquinaWait = "PNABMW";
	public static final String tranPasoNivelABMaquinaReady = "PNABMR";
	public static final String tranPasoNivelABVagonWait = "PNABVW";
	public static final String tranPasoNivelABVagonReady = "PNABVR";
	
	public static final String tranPasoNivelCDTransitoWait = "PNCDTW";
	public static final String tranPasoNivelCDTransitoReady = "PNCDTR";
	public static final String tranPasoNivelCDTransitoGenerador = "PNCDTG";
	public static final String tranPasoNivelCDMaquinaWait = "PNCDMW";
	public static final String tranPasoNivelCDMaquinaReady = "PNCDMR";
	public static final String tranPasoNivelCDVagonWait = "PNCDVW";
	public static final String tranPasoNivelCDVagonReady = "PNCDVR";
	
	/* Recorrido Tren */
	public static final String tranRecorridoTrenAB = "RAB";
	public static final String tranRecorridoTrenCD = "RCD";
	
	/* Generador de Pasajeros para abordar tren */
	public static final String tranPasajerosAGenerador = "PAG";
	public static final String tranPasajerosBGenerador = "PBG";
	public static final String tranPasajerosCGenerador = "PCG";
	public static final String tranPasajerosDGenerador = "PDG";
	
}
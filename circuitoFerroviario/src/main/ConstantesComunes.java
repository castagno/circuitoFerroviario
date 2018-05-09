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
	
	public static final String maquina = "Maquina";
	
	public static final String vagon = "Vagon";

	public static final String pasajeros = "Pasajeros";
	
	public static final String transito = "Transito";
	
	public static final String recorridoAB = "AB";
	public static final String recorridoCD = "CD";
	
	public static final String pasoNivelTransitoAB = transito+recorridoAB;
	public static final String pasoNivelTransitoCD = transito+recorridoCD;
	
	public static final String pasoNivelMaquinaAB = maquina+recorridoAB;
	public static final String pasoNivelMaquinaCD = maquina+recorridoCD;
	
	public static final String pasoNivelVagonAB = vagon+recorridoAB;
	public static final String pasoNivelVagonCD = vagon+recorridoCD;
	
	public static final String pasoNivel = "PasoNivel";
	
	public static final String precedenciaPrincipal = "Principal";
	public static final String precedenciaAuxiliar = "Auxiliar";
	public static final String precedenciaAuxiliarArrivo = precedenciaAuxiliar+"Arrivo";
	public static final String precedenciaAuxiliarPartida = precedenciaAuxiliar+"Partida";
	
	public static final String generador = "Generador";
	public static final String generadorTransito = generador+transito;
	public static final String generadorPasajeros = generador+pasajeros;
}
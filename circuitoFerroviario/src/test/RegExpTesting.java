package test;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.ConstantesComunes;

public class RegExpTesting extends ConstantesComunes {
	private static final String testOutput = "./src/test/testOutput.txt";
	
	private static final ArrayList<String> recorridoOriginal = new ArrayList<>();
	private static final LinkedHashMap<String, ArrayList<String>> transicionesValidasPorTransicion = new LinkedHashMap<>();
	private static final LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> generadorasPorSubidasPorBajadas = new LinkedHashMap<>();
	
	public static void main(String[] args) {
		getRecorridoOriginal();
		getTransicionesValidasPorTransicion();
		getGeneradorasPorSubidasPorBajadas();
		
		Scanner scanFile = null;
		try {
			scanFile = new Scanner(new FileReader(testOutput));
			String originalLog = scanFile.nextLine();
			System.out.println(originalLog);
			
			System.out.println("\nTest:\n");

			String recorridosRestantes = new String(originalLog);
			recorridosRestantes = recorrido(recorridosRestantes, new ArrayList<>(Arrays.asList(new String[]{tranTrenLlenoA, tranTrenLlenoB, tranTrenLlenoC, tranTrenLlenoD})));
			recorridosRestantes = recorrido(recorridosRestantes, new ArrayList<>(Arrays.asList(new String[]{tranTrenLlenoA, tranTrenLlenoB, tranTrenLlenoC, tranEstacionVaciaD})));
			recorridosRestantes = recorrido(recorridosRestantes, new ArrayList<>(Arrays.asList(new String[]{tranTrenLlenoA, tranTrenLlenoB, tranEstacionVaciaC, tranTrenLlenoD})));
			recorridosRestantes = recorrido(recorridosRestantes, new ArrayList<>(Arrays.asList(new String[]{tranTrenLlenoA, tranTrenLlenoB, tranEstacionVaciaC, tranEstacionVaciaD})));
			recorridosRestantes = recorrido(recorridosRestantes, new ArrayList<>(Arrays.asList(new String[]{tranTrenLlenoA, tranEstacionVaciaB, tranTrenLlenoC, tranTrenLlenoD})));
			recorridosRestantes = recorrido(recorridosRestantes, new ArrayList<>(Arrays.asList(new String[]{tranTrenLlenoA, tranEstacionVaciaB, tranTrenLlenoC, tranEstacionVaciaD})));
			recorridosRestantes = recorrido(recorridosRestantes, new ArrayList<>(Arrays.asList(new String[]{tranTrenLlenoA, tranEstacionVaciaB, tranEstacionVaciaC, tranTrenLlenoD})));
			recorridosRestantes = recorrido(recorridosRestantes, new ArrayList<>(Arrays.asList(new String[]{tranTrenLlenoA, tranEstacionVaciaB, tranEstacionVaciaC, tranEstacionVaciaD})));
			recorridosRestantes = recorrido(recorridosRestantes, new ArrayList<>(Arrays.asList(new String[]{tranEstacionVaciaA, tranTrenLlenoB, tranTrenLlenoC, tranTrenLlenoD})));
			recorridosRestantes = recorrido(recorridosRestantes, new ArrayList<>(Arrays.asList(new String[]{tranEstacionVaciaA, tranTrenLlenoB, tranTrenLlenoC, tranEstacionVaciaD})));
			recorridosRestantes = recorrido(recorridosRestantes, new ArrayList<>(Arrays.asList(new String[]{tranEstacionVaciaA, tranTrenLlenoB, tranEstacionVaciaC, tranTrenLlenoD})));
			recorridosRestantes = recorrido(recorridosRestantes, new ArrayList<>(Arrays.asList(new String[]{tranEstacionVaciaA, tranTrenLlenoB, tranEstacionVaciaC, tranEstacionVaciaD})));
			recorridosRestantes = recorrido(recorridosRestantes, new ArrayList<>(Arrays.asList(new String[]{tranEstacionVaciaA, tranEstacionVaciaB, tranTrenLlenoC, tranTrenLlenoD})));
			recorridosRestantes = recorrido(recorridosRestantes, new ArrayList<>(Arrays.asList(new String[]{tranEstacionVaciaA, tranEstacionVaciaB, tranTrenLlenoC, tranEstacionVaciaD})));
			recorridosRestantes = recorrido(recorridosRestantes, new ArrayList<>(Arrays.asList(new String[]{tranEstacionVaciaA, tranEstacionVaciaB, tranEstacionVaciaC, tranTrenLlenoD})));
			recorridosRestantes = recorrido(recorridosRestantes, new ArrayList<>(Arrays.asList(new String[]{tranEstacionVaciaA, tranEstacionVaciaB, tranEstacionVaciaC, tranEstacionVaciaD})));
			
			System.out.println("\n"+recorridosRestantes);
			
			ArrayList<String> transicionesGeneradorasPasajeros = getTransicionesGeneradorasPasajeros();
			for (String generadora : transicionesGeneradorasPasajeros) {
				LinkedHashMap<String, ArrayList<String>> subidaPorBajada = generadorasPorSubidasPorBajadas.get(generadora);
				ArrayList<String> listaSubidas = new ArrayList<>(Arrays.asList(subidaPorBajada.keySet().toArray(new String[subidaPorBajada.keySet().size()])));
				for (String subida : listaSubidas) {
					ArrayList<String> bajadas = subidaPorBajada.get(subida);
					for (String bajada : bajadas) {
						subidaBajadaPasajero(originalLog, generadora, subida, bajada);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scanFile.close();
		}
	}


	private static void subidaBajadaPasajero(String secuencia, String transicionGeneradora, String subida, String bajada) {
		System.out.println(transicionGeneradora +" "+ subida +" "+ bajada);
		ArrayList<String> transiciones = getTransiciones();
		ArrayList<String> transicionesValidasBajada = new ArrayList<>();
		
		for(String transicion: transiciones) {
			if(!transicion.equals(subida)) {
				transicionesValidasBajada.add(transicion);
			}
		}
		
		String tranValidasBajada = "";
		for(String transicion: transicionesValidasBajada) {
			tranValidasBajada += transicion + "\\s|";
		}
		if(tranValidasBajada.endsWith("\\s|")) {
			tranValidasBajada = tranValidasBajada.substring(0, tranValidasBajada.length() - 1);
		}
		
		ArrayList<String> transicionesValidasSubida = new ArrayList<>();
		
		for(String transicion: transiciones) {
			if(!transicion.equals(subida)) {
				transicionesValidasSubida.add(transicion);
			}
		}
		
		String tranValidasSubida = "";
		for(String transicion: transicionesValidasSubida) {
			tranValidasSubida += transicion + "\\s|";
		}
		if(tranValidasSubida.endsWith("\\s|")) {
			tranValidasSubida = tranValidasSubida.substring(0, tranValidasSubida.length() - 1);
		}
		
		
		String patternCompile = transicionGeneradora +"\\s("+ tranValidasSubida +"){0,}"+ subida +"\\s("+ tranValidasSubida +"){0,}"+ bajada +"\\s";
		Pattern pattern = Pattern.compile(patternCompile);
		Matcher matcherTemp = pattern.matcher(secuencia);
		
		while(matcherTemp.find()) {
			System.out.println(matcherTemp.start());
			System.out.println(matcherTemp.end());
			System.out.println(matcherTemp.group());
		}
	}
	
	
	private static String recorrido(String secuencia, ArrayList<String> transicionesExcluidas) {
		Matcher matcherTemp = Pattern.compile(getPatternRecorrido(transicionesExcluidas)).matcher(secuencia);
		
		String recorridoRestante = "";
		int lastEnd = 0;
		while(matcherTemp.find()) {
			recorridoRestante += secuencia.substring(lastEnd, matcherTemp.start());
			System.out.println(matcherTemp.start());
			lastEnd = matcherTemp.end();
			System.out.println(lastEnd);
			System.out.println(matcherTemp.group());
		}
		recorridoRestante += secuencia.substring(lastEnd, secuencia.length());
		
//		System.out.println("recorridoRestante.length() = "+recorridoRestante.length());
		return recorridoRestante;
	}
	
	
	private static String getPatternRecorrido(ArrayList<String> transicionesExcluidasRecorrido) {
		ArrayList<String> transiciones = getTransiciones();
		ArrayList<String> recorridoTren = new ArrayList<>();
		
		for(String transicion: recorridoOriginal) {
			if(!transicionesExcluidasRecorrido.contains(transicion)) {
				recorridoTren.add(transicion);
			}
		}
		System.out.println("");
		
		String pattern = "";
		for(String trancisionRecorrido: recorridoTren) {
			System.out.print(trancisionRecorrido+" ");
			
			ArrayList<String> transicionesValidas = transicionesValidasPorTransicion.get(trancisionRecorrido);
			
			String transicionesProhibidas = "";
			for(String transicion: transiciones) {
				if(!transicionesValidas.contains(transicion) && !recorridoTren.get((recorridoTren.indexOf(trancisionRecorrido) + 1) % recorridoTren.size()).equals(transicion) ) {
					transicionesProhibidas += transicion + "\\s|";
				}
			}
			if(transicionesProhibidas.endsWith("\\s|")) {
				transicionesProhibidas = transicionesProhibidas.substring(0, transicionesProhibidas.length() - 1);
			}
			
			String tranValidasRegExp = "";
			for(String transicion: transicionesValidas) {
				tranValidasRegExp += transicion + "\\s|";
			}
			if(tranValidasRegExp.endsWith("\\s|")) {
				tranValidasRegExp = tranValidasRegExp.substring(0, tranValidasRegExp.length() - 1);
			}
			
			pattern = pattern + trancisionRecorrido+"\\s("+tranValidasRegExp+"){0,}(?!"+transicionesProhibidas+")"; 
		}
		System.out.println("\n"+pattern);
		
		return pattern;
	}
	

	private static void getRecorridoOriginal(){
		recorridoOriginal.add(tranPasoNivelABMaquinaWait);
		recorridoOriginal.add(tranPasoNivelABMaquinaReady);
		recorridoOriginal.add(tranPasoNivelABVagonWait);
		recorridoOriginal.add(tranPasoNivelABVagonReady);
		recorridoOriginal.add(tranRecorridoTrenAB);
		recorridoOriginal.add(tranTrenArriboB);
		recorridoOriginal.add(tranTrenEsperandoB);
		recorridoOriginal.add(tranTrenLlenoB);
		recorridoOriginal.add(tranEstacionVaciaB);
		recorridoOriginal.add(tranTrenArriboC);
		recorridoOriginal.add(tranTrenEsperandoC);
		recorridoOriginal.add(tranTrenLlenoC);
		recorridoOriginal.add(tranEstacionVaciaC);
		recorridoOriginal.add(tranPasoNivelCDMaquinaWait);
		recorridoOriginal.add(tranPasoNivelCDMaquinaReady);
		recorridoOriginal.add(tranPasoNivelCDVagonWait);
		recorridoOriginal.add(tranPasoNivelCDVagonReady);
		recorridoOriginal.add(tranRecorridoTrenCD);
		recorridoOriginal.add(tranTrenArriboD);
		recorridoOriginal.add(tranTrenEsperandoD);
		recorridoOriginal.add(tranTrenLlenoD);
		recorridoOriginal.add(tranEstacionVaciaD);
		recorridoOriginal.add(tranTrenArriboA);
		recorridoOriginal.add(tranTrenEsperandoA);
		recorridoOriginal.add(tranTrenLlenoA);
		recorridoOriginal.add(tranEstacionVaciaA);
	}
	
	
	private static void getTransicionesValidasPorTransicion(){
		/* Transiciones del Recorrido */

		/* Transiciones validas para el recorrido cuando el tren no se encuentra en una estacion ni en un paso de nivel */
		ArrayList<String> tranRecorridoValidas = new ArrayList<>();
		tranRecorridoValidas.addAll(getTransicionesPasoNivelTransito());
		tranRecorridoValidas.addAll(getTransicionesGeneradoras());
		
		/* Transiciones validas para el recorrido cuando el tren se encuentra en el paso de nivel AB */
		ArrayList<String> tranPasoNivelABValidas = new ArrayList<>();
		tranPasoNivelABValidas.addAll(getTransicionesPasoNivelCDTransito());
		tranPasoNivelABValidas.addAll(getTransicionesGeneradoras());
		transicionesValidasPorTransicion.put(tranPasoNivelABMaquinaWait, tranPasoNivelABValidas);
		transicionesValidasPorTransicion.put(tranPasoNivelABMaquinaReady, tranPasoNivelABValidas);
		transicionesValidasPorTransicion.put(tranPasoNivelABVagonWait, tranPasoNivelABValidas);
		transicionesValidasPorTransicion.put(tranPasoNivelABVagonReady, tranPasoNivelABValidas);
		
		transicionesValidasPorTransicion.put(tranRecorridoTrenAB, tranRecorridoValidas);
		
		/* Trabsucuibes validas para el recorrido cuando el tren se encuentra en la estacionB */
		ArrayList<String> tranValidasTrenB = getTransicionesSubidaBajadaEstacionB();
		tranValidasTrenB.addAll(getTransicionesGeneradoras());
		tranValidasTrenB.addAll(getTransicionesPasoNivelTransito());
		transicionesValidasPorTransicion.put(tranTrenArriboB, tranValidasTrenB);
		transicionesValidasPorTransicion.put(tranTrenEsperandoB, tranValidasTrenB);
		
		transicionesValidasPorTransicion.put(tranTrenLlenoB, tranRecorridoValidas);
		transicionesValidasPorTransicion.put(tranEstacionVaciaB, tranRecorridoValidas);
		
		/* Trabsucuibes validas para el recorrido cuando el tren se encuentra en la estacionC */
		ArrayList<String> tranValidasTrenC = getTransicionesSubidaBajadaEstacionC();
		tranValidasTrenC.addAll(getTransicionesGeneradoras());
		tranValidasTrenC.addAll(getTransicionesPasoNivelTransito());
		transicionesValidasPorTransicion.put(tranTrenArriboC, tranValidasTrenC);
		transicionesValidasPorTransicion.put(tranTrenEsperandoC, tranValidasTrenC);
		
		transicionesValidasPorTransicion.put(tranTrenLlenoC, tranRecorridoValidas);
		transicionesValidasPorTransicion.put(tranEstacionVaciaC, tranRecorridoValidas);
		
		/* Transiciones validas para el recorrido cuando el tren se encuentra en el paso de nivel CD */
		ArrayList<String> tranPasoNivelCDValidas = new ArrayList<>();
		tranPasoNivelCDValidas.addAll(getTransicionesPasoNivelABTransito());
		tranPasoNivelCDValidas.addAll(getTransicionesGeneradoras());
		transicionesValidasPorTransicion.put(tranPasoNivelCDMaquinaWait, tranPasoNivelCDValidas);
		transicionesValidasPorTransicion.put(tranPasoNivelCDMaquinaReady, tranPasoNivelCDValidas);
		transicionesValidasPorTransicion.put(tranPasoNivelCDVagonWait, tranPasoNivelCDValidas);
		transicionesValidasPorTransicion.put(tranPasoNivelCDVagonReady, tranPasoNivelCDValidas);
		
		transicionesValidasPorTransicion.put(tranRecorridoTrenCD, tranRecorridoValidas);
		
		/* Trabsucuibes validas para el recorrido cuando el tren se encuentra en la estacionD */
		ArrayList<String> tranValidasTrenD = getTransicionesSubidaBajadaEstacionD();
		tranValidasTrenD.addAll(getTransicionesGeneradoras());
		tranValidasTrenD.addAll(getTransicionesPasoNivelTransito());
		transicionesValidasPorTransicion.put(tranTrenArriboD, tranValidasTrenD);
		transicionesValidasPorTransicion.put(tranTrenEsperandoD, tranValidasTrenD);
		
		transicionesValidasPorTransicion.put(tranTrenLlenoD, tranRecorridoValidas);
		transicionesValidasPorTransicion.put(tranEstacionVaciaD, tranRecorridoValidas);
		
		/* Trabsucuibes validas para el recorrido cuando el tren se encuentra en la estacionA */
		ArrayList<String> tranValidasTrenA = getTransicionesSubidaBajadaEstacionA();
		tranValidasTrenA.addAll(getTransicionesGeneradoras());
		tranValidasTrenA.addAll(getTransicionesPasoNivelTransito());
		transicionesValidasPorTransicion.put(tranTrenArriboA, tranValidasTrenA);
		transicionesValidasPorTransicion.put(tranTrenEsperandoA, tranValidasTrenA);
		
		transicionesValidasPorTransicion.put(tranTrenLlenoA, tranRecorridoValidas);
		transicionesValidasPorTransicion.put(tranEstacionVaciaA, tranRecorridoValidas);
		
	}
	
	
	private static void getGeneradorasPorSubidasPorBajadas(){
		ArrayList<String> transicionesGeneradoras = getTransicionesGeneradorasPasajeros();
		ArrayList<String> transicionesSubidas = getTransicionesSubidaEstacionA();
		ArrayList<String> estaciones = new ArrayList<>(Arrays.asList(estacion));
		
		for (String transicionGeneradora: transicionesGeneradoras) {
			LinkedHashMap<String, ArrayList<String>> subidasPorBajadas = new LinkedHashMap<>();
			for(String transicionSubida: transicionesSubidas) {
				ArrayList<String> bajadas = new ArrayList<>();
				for(String estacion: estaciones) {
					if(!estacion.equalsIgnoreCase(transicionGeneradora.substring(1, transicionGeneradora.length() - 1))) {
						bajadas.add("B"+ estacion + transicionSubida.substring(1, transicionSubida.length() - 1) + transicionGeneradora.substring(1, transicionGeneradora.length() - 1));
					}
				}
				subidasPorBajadas.put(transicionSubida.substring(0, transicionSubida.length() - 1) + transicionGeneradora.substring(1, transicionGeneradora.length() - 1), bajadas);
			}
			generadorasPorSubidasPorBajadas.put(transicionGeneradora, subidasPorBajadas);
		}
		
	}
	
	
	private static ArrayList<String> getTransiciones(){
		ArrayList<String> transiciones = new ArrayList<>();
		
		transiciones.addAll(getTransicionesSubidaBajadaEstacionA());
		transiciones.addAll(getTransicionesSubidaBajadaEstacionB());
		transiciones.addAll(getTransicionesSubidaBajadaEstacionC());
		transiciones.addAll(getTransicionesSubidaBajadaEstacionD());
		
		transiciones.addAll(getTransicionesPasoNivelTransito());
		transiciones.addAll(getTransicionesGeneradoras());
		
		transiciones.addAll(getRecorridoTrenEstacionA());
		transiciones.addAll(getRecorridoTrenEstacionB());
		transiciones.addAll(getRecorridoTrenEstacionC());
		transiciones.addAll(getRecorridoTrenEstacionD());
		
		return transiciones;
	}
	
	
	private static ArrayList<String> getTransicionesSubidaBajadaEstacionA(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.addAll(getTransicionesSubidaEstacionA());
		transiciones.addAll(getTransicionesBajadaEstacionA());
		
		return transiciones;
	}

	
	private static ArrayList<String> getTransicionesSubidaEstacionA(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.add(tranSubidaMaquinaEstacionA);
		transiciones.add(tranSubidaVagonEstacionA);
		
		return transiciones;
	}

	
	private static ArrayList<String> getTransicionesBajadaEstacionA(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.add(tranBajadaMaquinaBEstacionA);
		transiciones.add(tranBajadaMaquinaCEstacionA);
		transiciones.add(tranBajadaMaquinaDEstacionA);

		transiciones.add(tranBajadaVagonBEstacionA);
		transiciones.add(tranBajadaVagonCEstacionA);
		transiciones.add(tranBajadaVagonDEstacionA);
		
		return transiciones;
	}
	
	
	private static ArrayList<String> getTransicionesSubidaBajadaEstacionB(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.addAll(getTransicionesSubidaEstacionB());
		transiciones.addAll(getTransicionesBajadaEstacionB());

		return transiciones;
	}
	
	
	private static ArrayList<String> getTransicionesSubidaEstacionB(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.add(tranSubidaMaquinaEstacionB);
		transiciones.add(tranSubidaVagonEstacionB);

		return transiciones;
	}
	
	
	private static ArrayList<String> getTransicionesBajadaEstacionB(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.add(tranBajadaMaquinaAEstacionB);
		transiciones.add(tranBajadaMaquinaCEstacionB);
		transiciones.add(tranBajadaMaquinaDEstacionB);

		transiciones.add(tranBajadaVagonAEstacionB);
		transiciones.add(tranBajadaVagonCEstacionB);
		transiciones.add(tranBajadaVagonDEstacionB);
		
		return transiciones;
	}
	
	
	private static ArrayList<String> getTransicionesSubidaBajadaEstacionC(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.addAll(getTransicionesSubidaEstacionC());
		transiciones.addAll(getTransicionesBajadaEstacionC());
		
		return transiciones;
	}
	
	
	private static ArrayList<String> getTransicionesSubidaEstacionC(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.add(tranSubidaMaquinaEstacionC);
		transiciones.add(tranSubidaVagonEstacionC);
		
		return transiciones;
	}
	
	
	private static ArrayList<String> getTransicionesBajadaEstacionC(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.add(tranBajadaMaquinaAEstacionC);
		transiciones.add(tranBajadaMaquinaBEstacionC);
		transiciones.add(tranBajadaMaquinaDEstacionC);

		transiciones.add(tranBajadaVagonAEstacionC);
		transiciones.add(tranBajadaVagonBEstacionC);
		transiciones.add(tranBajadaVagonDEstacionC);
		
		return transiciones;
	}
	
	
	private static ArrayList<String> getTransicionesSubidaBajadaEstacionD(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.addAll(getTransicionesSubidaEstacionD());
		transiciones.addAll(getTransicionesBajadaEstacionD());

		return transiciones;
	}
	
	
	private static ArrayList<String> getTransicionesSubidaEstacionD(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.add(tranSubidaMaquinaEstacionD);
		transiciones.add(tranSubidaVagonEstacionD);

		return transiciones;
	}
	
	
	private static ArrayList<String> getTransicionesBajadaEstacionD(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.add(tranBajadaMaquinaAEstacionD);
		transiciones.add(tranBajadaMaquinaBEstacionD);
		transiciones.add(tranBajadaMaquinaCEstacionD);

		transiciones.add(tranBajadaVagonAEstacionD);
		transiciones.add(tranBajadaVagonBEstacionD);
		transiciones.add(tranBajadaVagonCEstacionD);
		
		return transiciones;
	}
	
	
	private static ArrayList<String> getTransicionesPasoNivelTransito(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.addAll(getTransicionesPasoNivelABTransito());
		transiciones.addAll(getTransicionesPasoNivelCDTransito());
		
		return transiciones;
	}
	
	
	private static ArrayList<String> getTransicionesPasoNivelABTransito(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.add(tranPasoNivelABTransitoWait);
		transiciones.add(tranPasoNivelABTransitoReady);
		
		return transiciones;
	}
	
	
	private static ArrayList<String> getTransicionesPasoNivelCDTransito(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.add(tranPasoNivelCDTransitoWait);
		transiciones.add(tranPasoNivelCDTransitoReady);
		
		return transiciones;
	}
	
	
	private static ArrayList<String> getTransicionesGeneradoras(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.addAll(getTransicionesGeneradorasPasajeros());
		transiciones.addAll(getTransicionesGeneradorasTransito());
		
		return transiciones;
	}
	
	
	private static ArrayList<String> getTransicionesGeneradorasPasajeros(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.add(tranPasajerosAGenerador);
		transiciones.add(tranPasajerosBGenerador);
		transiciones.add(tranPasajerosCGenerador);
		transiciones.add(tranPasajerosDGenerador);
		
		return transiciones;
	}
	
	
	private static ArrayList<String> getTransicionesGeneradorasTransito(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.add(tranPasoNivelABTransitoGenerador);
		transiciones.add(tranPasoNivelCDTransitoGenerador);
		
		return transiciones;
	}
	
	
	private static ArrayList<String> getRecorridoTrenEstacionA(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.add(tranTrenArriboA);
		transiciones.add(tranTrenEsperandoA);
		transiciones.add(tranTrenLlenoA);
		transiciones.add(tranEstacionVaciaA);
		transiciones.add(tranPasoNivelABMaquinaWait);
		transiciones.add(tranPasoNivelABMaquinaReady);
		transiciones.add(tranPasoNivelABVagonWait);
		transiciones.add(tranPasoNivelABVagonReady);
		transiciones.add(tranRecorridoTrenAB);
		
		return transiciones;
	}
	
	
	private static ArrayList<String> getRecorridoTrenEstacionB(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.add(tranTrenArriboB);
		transiciones.add(tranTrenEsperandoB);
		transiciones.add(tranTrenLlenoB);
		transiciones.add(tranEstacionVaciaB);
		
		return transiciones;
	}
	
	
	private static ArrayList<String> getRecorridoTrenEstacionC(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.add(tranTrenArriboC);
		transiciones.add(tranTrenEsperandoC);
		transiciones.add(tranTrenLlenoC);
		transiciones.add(tranEstacionVaciaC);
		transiciones.add(tranPasoNivelCDMaquinaWait);
		transiciones.add(tranPasoNivelCDMaquinaReady);
		transiciones.add(tranPasoNivelCDVagonWait);
		transiciones.add(tranPasoNivelCDVagonReady);
		transiciones.add(tranRecorridoTrenCD);
		
		return transiciones;
	}
	
	
	private static ArrayList<String> getRecorridoTrenEstacionD(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.add(tranTrenArriboD);
		transiciones.add(tranTrenEsperandoD);
		transiciones.add(tranTrenLlenoD);
		transiciones.add(tranEstacionVaciaD);
		
		return transiciones;
	}
}
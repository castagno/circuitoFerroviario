package test;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.ConstantesComunes;

public class RegExpTesting extends ConstantesComunes {
	private static final String testOutput = "./src/test/testOutput.txt";
	
	private static final ArrayList<String> recorridoOriginal = new ArrayList<>();
	private static final LinkedHashMap<String, ArrayList<String>> transicionesValidasPorTransicion = new LinkedHashMap<>();
	
	public static void main(String[] args) {
		getRecorridoOriginal();
		getTransicionesValidasPorTransicion();
		
		Scanner scanFile = null;
		try {
			scanFile = new Scanner(new FileReader(testOutput));
			String tempString = scanFile.nextLine();
			
			System.out.println("\nTest:\n");
			
			recorrido0x0(tempString);
			recorrido0x1(tempString);
			recorrido0x2(tempString);
			recorrido0x3(tempString);
			recorrido0x4(tempString);
			recorrido0x5(tempString);
			recorrido0x6(tempString);
			recorrido0x7(tempString);
			recorrido0x8(tempString);
			recorrido0x9(tempString);
			recorrido0xA(tempString);
			recorrido0xB(tempString);
			recorrido0xC(tempString);
			recorrido0xD(tempString);
			recorrido0xE(tempString);
			recorrido0xF(tempString);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scanFile.close();
		}
	}
	
	
	private static AssertionError recorrido0x0(String secuencia) {
		System.out.println(secuencia);
		
		ArrayList<String> transicionesExcluidas = new ArrayList<>();
		transicionesExcluidas.add(tranTrenLlenoA);
		transicionesExcluidas.add(tranTrenLlenoB);
		transicionesExcluidas.add(tranTrenLlenoC);
		transicionesExcluidas.add(tranTrenLlenoD);
		
		String patternCompile = getPatternRecorrido(transicionesExcluidas);
		Pattern pattern = Pattern.compile(patternCompile);
		Matcher matcherTemp = pattern.matcher(secuencia);
		
		while(matcherTemp.find()) {
			System.out.println(matcherTemp.start());
			System.out.println(matcherTemp.end());
			System.out.println(matcherTemp.group());
		}
		
		return null;
	}
	
	
	private static AssertionError recorrido0x1(String secuencia) {
		System.out.println(secuencia);
		
		ArrayList<String> transicionesExcluidas = new ArrayList<>();
		transicionesExcluidas.add(tranTrenLlenoA);
		transicionesExcluidas.add(tranTrenLlenoB);
		transicionesExcluidas.add(tranTrenLlenoC);
		transicionesExcluidas.add(tranEstacionVaciaD);
		
		String patternCompile = getPatternRecorrido(transicionesExcluidas);
		Pattern pattern = Pattern.compile(patternCompile);
		Matcher matcherTemp = pattern.matcher(secuencia);
		
		while(matcherTemp.find()) {
			System.out.println(matcherTemp.start());
			System.out.println(matcherTemp.end());
			System.out.println(matcherTemp.group());
		}
		
		return null;
	}
	
	
	private static AssertionError recorrido0x2(String secuencia) {
		System.out.println(secuencia);
		
		ArrayList<String> transicionesExcluidas = new ArrayList<>();
		transicionesExcluidas.add(tranTrenLlenoA);
		transicionesExcluidas.add(tranTrenLlenoB);
		transicionesExcluidas.add(tranEstacionVaciaC);
		transicionesExcluidas.add(tranTrenLlenoD);
		
		String patternCompile = getPatternRecorrido(transicionesExcluidas);
		Pattern pattern = Pattern.compile(patternCompile);
		Matcher matcherTemp = pattern.matcher(secuencia);
		
		while(matcherTemp.find()) {
			System.out.println(matcherTemp.start());
			System.out.println(matcherTemp.end());
			System.out.println(matcherTemp.group());
		}
		
		return null;
	}
	
	
	private static AssertionError recorrido0x3(String secuencia) {
		System.out.println(secuencia);
		
		ArrayList<String> transicionesExcluidas = new ArrayList<>();
		transicionesExcluidas.add(tranTrenLlenoA);
		transicionesExcluidas.add(tranTrenLlenoB);
		transicionesExcluidas.add(tranEstacionVaciaC);
		transicionesExcluidas.add(tranEstacionVaciaD);
		
		String patternCompile = getPatternRecorrido(transicionesExcluidas);
		Pattern pattern = Pattern.compile(patternCompile);
		Matcher matcherTemp = pattern.matcher(secuencia);
		
		while(matcherTemp.find()) {
			System.out.println(matcherTemp.start());
			System.out.println(matcherTemp.end());
			System.out.println(matcherTemp.group());
		}
		
		return null;
	}
	
	
	private static AssertionError recorrido0x4(String secuencia) {
		System.out.println(secuencia);
		
		ArrayList<String> transicionesExcluidas = new ArrayList<>();
		transicionesExcluidas.add(tranTrenLlenoA);
		transicionesExcluidas.add(tranEstacionVaciaB);
		transicionesExcluidas.add(tranTrenLlenoC);
		transicionesExcluidas.add(tranTrenLlenoD);
		
		String patternCompile = getPatternRecorrido(transicionesExcluidas);
		Pattern pattern = Pattern.compile(patternCompile);
		Matcher matcherTemp = pattern.matcher(secuencia);
		
		while(matcherTemp.find()) {
			System.out.println(matcherTemp.start());
			System.out.println(matcherTemp.end());
			System.out.println(matcherTemp.group());
		}
		
		return null;
	}
	
	
	private static AssertionError recorrido0x5(String secuencia) {
		System.out.println(secuencia);
		
		ArrayList<String> transicionesExcluidas = new ArrayList<>();
		transicionesExcluidas.add(tranTrenLlenoA);
		transicionesExcluidas.add(tranEstacionVaciaB);
		transicionesExcluidas.add(tranTrenLlenoC);
		transicionesExcluidas.add(tranEstacionVaciaD);
		
		String patternCompile = getPatternRecorrido(transicionesExcluidas);
		Pattern pattern = Pattern.compile(patternCompile);
		Matcher matcherTemp = pattern.matcher(secuencia);
		
		while(matcherTemp.find()) {
			System.out.println(matcherTemp.start());
			System.out.println(matcherTemp.end());
			System.out.println(matcherTemp.group());
		}
		
		return null;
	}
	
	
	private static AssertionError recorrido0x6(String secuencia) {
		System.out.println(secuencia);
		
		ArrayList<String> transicionesExcluidas = new ArrayList<>();
		transicionesExcluidas.add(tranTrenLlenoA);
		transicionesExcluidas.add(tranEstacionVaciaB);
		transicionesExcluidas.add(tranEstacionVaciaC);
		transicionesExcluidas.add(tranTrenLlenoD);
		
		String patternCompile = getPatternRecorrido(transicionesExcluidas);
		Pattern pattern = Pattern.compile(patternCompile);
		Matcher matcherTemp = pattern.matcher(secuencia);
		
		while(matcherTemp.find()) {
			System.out.println(matcherTemp.start());
			System.out.println(matcherTemp.end());
			System.out.println(matcherTemp.group());
		}
		
		return null;
	}
	
	
	private static AssertionError recorrido0x7(String secuencia) {
		System.out.println(secuencia);
		
		ArrayList<String> transicionesExcluidas = new ArrayList<>();
		transicionesExcluidas.add(tranTrenLlenoA);
		transicionesExcluidas.add(tranEstacionVaciaB);
		transicionesExcluidas.add(tranEstacionVaciaC);
		transicionesExcluidas.add(tranEstacionVaciaD);
		
		String patternCompile = getPatternRecorrido(transicionesExcluidas);
		Pattern pattern = Pattern.compile(patternCompile);
		Matcher matcherTemp = pattern.matcher(secuencia);
		
		while(matcherTemp.find()) {
			System.out.println(matcherTemp.start());
			System.out.println(matcherTemp.end());
			System.out.println(matcherTemp.group());
		}
		
		return null;
	}
	
	
	private static AssertionError recorrido0x8(String secuencia) {
		System.out.println(secuencia);
		
		ArrayList<String> transicionesExcluidas = new ArrayList<>();
		transicionesExcluidas.add(tranEstacionVaciaA);
		transicionesExcluidas.add(tranTrenLlenoB);
		transicionesExcluidas.add(tranTrenLlenoC);
		transicionesExcluidas.add(tranTrenLlenoD);
		
		String patternCompile = getPatternRecorrido(transicionesExcluidas);
		Pattern pattern = Pattern.compile(patternCompile);
		Matcher matcherTemp = pattern.matcher(secuencia);
		
		while(matcherTemp.find()) {
			System.out.println(matcherTemp.start());
			System.out.println(matcherTemp.end());
			System.out.println(matcherTemp.group());
		}
		
		return null;
	}
	
	
	private static AssertionError recorrido0x9(String secuencia) {
		System.out.println(secuencia);
		
		ArrayList<String> transicionesExcluidas = new ArrayList<>();
		transicionesExcluidas.add(tranEstacionVaciaA);
		transicionesExcluidas.add(tranTrenLlenoB);
		transicionesExcluidas.add(tranTrenLlenoC);
		transicionesExcluidas.add(tranEstacionVaciaD);
		
		String patternCompile = getPatternRecorrido(transicionesExcluidas);
		Pattern pattern = Pattern.compile(patternCompile);
		Matcher matcherTemp = pattern.matcher(secuencia);
		
		while(matcherTemp.find()) {
			System.out.println(matcherTemp.start());
			System.out.println(matcherTemp.end());
			System.out.println(matcherTemp.group());
		}
		
		return null;
	}
	
	
	private static AssertionError recorrido0xA(String secuencia) {
		System.out.println(secuencia);
		
		ArrayList<String> transicionesExcluidas = new ArrayList<>();
		transicionesExcluidas.add(tranEstacionVaciaA);
		transicionesExcluidas.add(tranTrenLlenoB);
		transicionesExcluidas.add(tranEstacionVaciaC);
		transicionesExcluidas.add(tranTrenLlenoD);
		
		String patternCompile = getPatternRecorrido(transicionesExcluidas);
		Pattern pattern = Pattern.compile(patternCompile);
		Matcher matcherTemp = pattern.matcher(secuencia);
		
		while(matcherTemp.find()) {
			System.out.println(matcherTemp.start());
			System.out.println(matcherTemp.end());
			System.out.println(matcherTemp.group());
		}
		
		return null;
	}
	
	
	private static AssertionError recorrido0xB(String secuencia) {
		System.out.println(secuencia);
		
		ArrayList<String> transicionesExcluidas = new ArrayList<>();
		transicionesExcluidas.add(tranEstacionVaciaA);
		transicionesExcluidas.add(tranTrenLlenoB);
		transicionesExcluidas.add(tranEstacionVaciaC);
		transicionesExcluidas.add(tranEstacionVaciaD);
		
		String patternCompile = getPatternRecorrido(transicionesExcluidas);
		Pattern pattern = Pattern.compile(patternCompile);
		Matcher matcherTemp = pattern.matcher(secuencia);
		
		while(matcherTemp.find()) {
			System.out.println(matcherTemp.start());
			System.out.println(matcherTemp.end());
			System.out.println(matcherTemp.group());
		}
		
		return null;
	}
	
	
	private static AssertionError recorrido0xC(String secuencia) {
		System.out.println(secuencia);
		
		ArrayList<String> transicionesExcluidas = new ArrayList<>();
		transicionesExcluidas.add(tranEstacionVaciaA);
		transicionesExcluidas.add(tranEstacionVaciaB);
		transicionesExcluidas.add(tranTrenLlenoC);
		transicionesExcluidas.add(tranTrenLlenoD);
		
		String patternCompile = getPatternRecorrido(transicionesExcluidas);
		Pattern pattern = Pattern.compile(patternCompile);
		Matcher matcherTemp = pattern.matcher(secuencia);
		
		while(matcherTemp.find()) {
			System.out.println(matcherTemp.start());
			System.out.println(matcherTemp.end());
			System.out.println(matcherTemp.group());
		}
		
		return null;
	}
	
	
	private static AssertionError recorrido0xD(String secuencia) {
		System.out.println(secuencia);
		
		ArrayList<String> transicionesExcluidas = new ArrayList<>();
		transicionesExcluidas.add(tranEstacionVaciaA);
		transicionesExcluidas.add(tranEstacionVaciaB);
		transicionesExcluidas.add(tranTrenLlenoC);
		transicionesExcluidas.add(tranEstacionVaciaD);
		
		String patternCompile = getPatternRecorrido(transicionesExcluidas);
		Pattern pattern = Pattern.compile(patternCompile);
		Matcher matcherTemp = pattern.matcher(secuencia);
		
		while(matcherTemp.find()) {
			System.out.println(matcherTemp.start());
			System.out.println(matcherTemp.end());
			System.out.println(matcherTemp.group());
		}
		
		return null;
	}
	
	
	private static AssertionError recorrido0xE(String secuencia) {
		System.out.println(secuencia);
		
		ArrayList<String> transicionesExcluidas = new ArrayList<>();
		transicionesExcluidas.add(tranEstacionVaciaA);
		transicionesExcluidas.add(tranEstacionVaciaB);
		transicionesExcluidas.add(tranEstacionVaciaC);
		transicionesExcluidas.add(tranTrenLlenoD);
		
		String patternCompile = getPatternRecorrido(transicionesExcluidas);
		Pattern pattern = Pattern.compile(patternCompile);
		Matcher matcherTemp = pattern.matcher(secuencia);
		
		while(matcherTemp.find()) {
			System.out.println(matcherTemp.start());
			System.out.println(matcherTemp.end());
			System.out.println(matcherTemp.group());
		}
		
		return null;
	}
	
	
	private static AssertionError recorrido0xF(String secuencia) {
		System.out.println(secuencia);
		
		ArrayList<String> transicionesExcluidas = new ArrayList<>();
		transicionesExcluidas.add(tranEstacionVaciaA);
		transicionesExcluidas.add(tranEstacionVaciaB);
		transicionesExcluidas.add(tranEstacionVaciaC);
		transicionesExcluidas.add(tranEstacionVaciaD);
		
		String patternCompile = getPatternRecorrido(transicionesExcluidas);
		Pattern pattern = Pattern.compile(patternCompile);
		Matcher matcherTemp = pattern.matcher(secuencia);
		
		while(matcherTemp.find()) {
			System.out.println(matcherTemp.start());
			System.out.println(matcherTemp.end());
			System.out.println(matcherTemp.group());
		}
		
		return null;
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
		
		String prePattern = "";
		String postPattern = "";
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
			
			prePattern = prePattern + trancisionRecorrido+"\\s("+tranValidasRegExp+"){0,}(?!"+transicionesProhibidas+")(?="; 
			postPattern = ")" + postPattern;
		}
		System.out.println("");
		
		prePattern = ( (prePattern.endsWith("(?="))? prePattern.substring(0, prePattern.length() - (new String("(?=")).length()) : prePattern ) ;
		postPattern = ( (postPattern.startsWith(")"))? postPattern.substring(String.valueOf(")").length()) : postPattern ) ;
		String pattern = prePattern + postPattern;

		System.out.println(pattern);
		
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
	
	
//	private static ArrayList<String> getRecorridoTren(){
//		ArrayList<String> transiciones = new ArrayList<>();
//		
//		transiciones.addAll(getRecorridoTrenEstacionA());
//		transiciones.addAll(getRecorridoTrenEstacionB());
//		transiciones.addAll(getRecorridoTrenEstacionC());
//		transiciones.addAll(getRecorridoTrenEstacionD());
//		
//		return transiciones;
//	}

	
	private static ArrayList<String> getTransicionesSubidaBajadaEstacionA(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.add(tranSubidaMaquinaEstacionA);
		transiciones.add(tranSubidaVagonEstacionA);
		
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

		transiciones.add(tranSubidaMaquinaEstacionB);
		transiciones.add(tranSubidaVagonEstacionB);

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

		transiciones.add(tranSubidaMaquinaEstacionC);
		transiciones.add(tranSubidaVagonEstacionC);
		
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

		transiciones.add(tranSubidaMaquinaEstacionD);
		transiciones.add(tranSubidaVagonEstacionD);

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

		transiciones.add(tranPasoNivelABTransitoGenerador);
		transiciones.add(tranPasoNivelCDTransitoGenerador);
		transiciones.add(tranPasajerosAGenerador);
		transiciones.add(tranPasajerosBGenerador);
		transiciones.add(tranPasajerosCGenerador);
		transiciones.add(tranPasajerosDGenerador);
		
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
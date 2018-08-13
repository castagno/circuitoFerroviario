package test;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.ConstantesComunes;

public class RegExpTesting extends ConstantesComunes {
	private static final String testOutput = "./src/test/testOutput.txt";
	
	public static void main(String[] args) {
		Scanner scanFile = null;
		try {
			scanFile = new Scanner(new FileReader(testOutput));
			String tempString = scanFile.nextLine();
			System.out.println(tempString);
			String tempCopy = new String(tempString);
			
			System.out.println(tempCopy.matches(".*"+tranTrenArriboB+".*"+tranTrenEsperandoB+".*("+tranTrenLlenoB+"|"+tranEstacionVaciaB+").*"));
			
			/* Cuando el tren se encuentra en una estacion se pueden disparar las transiciones de espera, partida, subida y bajada de dicha estacion y las
			 * transiciones de cruze de trancito en pasos de nivel y todas las generadoras de pasajeros y transito. */
			
			
			ArrayList<String> transiciones = getTransiciones();
			ArrayList<String> recorridoTren = getRecorridoTren();
			ArrayList<String> complemento = new ArrayList<>();
			for(String transicion: transiciones) {
				if(!recorridoTren.contains(transicion)) {
					complemento.add(transicion);
				}
			}
			
			for(String transicion: complemento) {
				tempCopy = tempCopy.replaceAll(transicion+" ", "");
			}
//			System.out.println(tempCopy);
			
//			Pattern pattern = Pattern.compile(".*("+tranTrenArriboB+"(?="+tranTrenEsperandoB+")).*");
			
			String complementoRecorrido = "";
			for(String transicion: complemento) {
				complementoRecorrido += transicion + "\\s|";
			}
			if(complementoRecorrido.endsWith("\\s|")) {
				complementoRecorrido = complementoRecorrido.substring(0, complementoRecorrido.length() - 1);
			}
			
			String patternCompile = tranTrenArriboB+"\\s("+complementoRecorrido+"){0,}(?="+tranTrenEsperandoB+"\\s("+complementoRecorrido+"){0,}(?="+tranEstacionVaciaB+"))";
			System.out.println(patternCompile);
			Pattern pattern = Pattern.compile(patternCompile);
			Matcher matcherTemp = pattern.matcher(tempString);
			
			while(matcherTemp.find()) {
				System.out.println(matcherTemp.start());
				System.out.println(matcherTemp.end());
				System.out.println(matcherTemp.group());
			}
			
//			while (tempCopy.contains("  ")) {
//				tempCopy = tempCopy.replaceAll("\\s\\s", " ");
//			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scanFile.close();
		}
	}
	
	
	
	private static ArrayList<String> getTransicionesPasoNivelTransito(){
		ArrayList<String> transiciones = new ArrayList<>();

		transiciones.add(tranPasoNivelABTransitoWait);
		transiciones.add(tranPasoNivelABTransitoReady);
		transiciones.add(tranRecorridoTrenAB);
		
		transiciones.add(tranPasoNivelCDTransitoWait);
		transiciones.add(tranPasoNivelCDTransitoReady);
		transiciones.add(tranRecorridoTrenCD);
		
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
	
	private static ArrayList<String> getTransiciones(){
		ArrayList<String> transiciones = new ArrayList<>();

		/* Transiciones */
		
		/* Estacion */
		transiciones.add(tranTrenArriboA);
		transiciones.add(tranTrenArriboB);
		transiciones.add(tranTrenArriboC);
		transiciones.add(tranTrenArriboD);
		
		transiciones.add(tranTrenEsperandoA);
		transiciones.add(tranTrenEsperandoB);
		transiciones.add(tranTrenEsperandoC);
		transiciones.add(tranTrenEsperandoD);
		
		transiciones.add(tranTrenLlenoA);
		transiciones.add(tranTrenLlenoB);
		transiciones.add(tranTrenLlenoC);
		transiciones.add(tranTrenLlenoD);
		
		transiciones.add(tranEstacionVaciaA);
		transiciones.add(tranEstacionVaciaB);
		transiciones.add(tranEstacionVaciaC);
		transiciones.add(tranEstacionVaciaD);
		
		/* Subida de pasajeros */
		transiciones.add(tranSubidaMaquinaEstacionA);
		transiciones.add(tranSubidaMaquinaEstacionB);
		transiciones.add(tranSubidaMaquinaEstacionC);
		transiciones.add(tranSubidaMaquinaEstacionD);

		transiciones.add(tranSubidaVagonEstacionA);
		transiciones.add(tranSubidaVagonEstacionB);
		transiciones.add(tranSubidaVagonEstacionC);
		transiciones.add(tranSubidaVagonEstacionD);
		
		/* Bajada de pasajeros */
		transiciones.add(tranBajadaMaquinaBEstacionA);
		transiciones.add(tranBajadaMaquinaCEstacionA);
		transiciones.add(tranBajadaMaquinaDEstacionA);

		transiciones.add(tranBajadaVagonBEstacionA);
		transiciones.add(tranBajadaVagonCEstacionA);
		transiciones.add(tranBajadaVagonDEstacionA);

		
		transiciones.add(tranBajadaMaquinaAEstacionB);
		transiciones.add(tranBajadaMaquinaCEstacionB);
		transiciones.add(tranBajadaMaquinaDEstacionB);

		transiciones.add(tranBajadaVagonAEstacionB);
		transiciones.add(tranBajadaVagonCEstacionB);
		transiciones.add(tranBajadaVagonDEstacionB);

		
		transiciones.add(tranBajadaMaquinaAEstacionC);
		transiciones.add(tranBajadaMaquinaBEstacionC);
		transiciones.add(tranBajadaMaquinaDEstacionC);

		transiciones.add(tranBajadaVagonAEstacionC);
		transiciones.add(tranBajadaVagonBEstacionC);
		transiciones.add(tranBajadaVagonDEstacionC);

		
		transiciones.add(tranBajadaMaquinaAEstacionD);
		transiciones.add(tranBajadaMaquinaBEstacionD);
		transiciones.add(tranBajadaMaquinaCEstacionD);

		transiciones.add(tranBajadaVagonAEstacionD);
		transiciones.add(tranBajadaVagonBEstacionD);
		transiciones.add(tranBajadaVagonCEstacionD);

		/* Paso de Nivel Transito */
		transiciones.add(tranPasoNivelABTransitoWait);
		transiciones.add(tranPasoNivelABTransitoReady);
		transiciones.add(tranPasoNivelABTransitoGenerador);
		transiciones.add(tranPasoNivelABMaquinaWait);
		transiciones.add(tranPasoNivelABMaquinaReady);
		transiciones.add(tranPasoNivelABVagonWait);
		transiciones.add(tranPasoNivelABVagonReady);
		
		transiciones.add(tranPasoNivelCDTransitoWait);
		transiciones.add(tranPasoNivelCDTransitoReady);
		transiciones.add(tranPasoNivelCDTransitoGenerador);
		transiciones.add(tranPasoNivelCDMaquinaWait);
		transiciones.add(tranPasoNivelCDMaquinaReady);
		transiciones.add(tranPasoNivelCDVagonWait);
		transiciones.add(tranPasoNivelCDVagonReady);
		
		/* Recorrido Tren */
		transiciones.add(tranRecorridoTrenAB);
		transiciones.add(tranRecorridoTrenCD);
		
		/* Generador de Pasajeros para abordar tren */
		transiciones.add(tranPasajerosAGenerador);
		transiciones.add(tranPasajerosBGenerador);
		transiciones.add(tranPasajerosCGenerador);
		transiciones.add(tranPasajerosDGenerador);
		
		return transiciones;
	}
	
	
	private static ArrayList<String> getRecorridoTren(){
		ArrayList<String> transiciones = new ArrayList<>();
		
		transiciones.addAll(getRecorridoTrenEstacionA());
		transiciones.addAll(getRecorridoTrenEstacionB());
		transiciones.addAll(getRecorridoTrenEstacionC());
		transiciones.addAll(getRecorridoTrenEstacionD());
		
		/*
		transiciones.add(tranTrenArriboA);
		transiciones.add(tranTrenArriboB);
		transiciones.add(tranTrenArriboC);
		transiciones.add(tranTrenArriboD);
		
		transiciones.add(tranTrenEsperandoA);
		transiciones.add(tranTrenEsperandoB);
		transiciones.add(tranTrenEsperandoC);
		transiciones.add(tranTrenEsperandoD);
		
		transiciones.add(tranTrenLlenoA);
		transiciones.add(tranTrenLlenoB);
		transiciones.add(tranTrenLlenoC);
		transiciones.add(tranTrenLlenoD);
		
		transiciones.add(tranEstacionVaciaA);
		transiciones.add(tranEstacionVaciaB);
		transiciones.add(tranEstacionVaciaC);
		transiciones.add(tranEstacionVaciaD);
		
		transiciones.add(tranPasoNivelABMaquinaWait);
		transiciones.add(tranPasoNivelABMaquinaReady);
		transiciones.add(tranPasoNivelABVagonWait);
		transiciones.add(tranPasoNivelABVagonReady);
		
		transiciones.add(tranPasoNivelCDMaquinaWait);
		transiciones.add(tranPasoNivelCDMaquinaReady);
		transiciones.add(tranPasoNivelCDVagonWait);
		transiciones.add(tranPasoNivelCDVagonReady);
		
		transiciones.add(tranRecorridoTrenAB);
		transiciones.add(tranRecorridoTrenCD);
		*/
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
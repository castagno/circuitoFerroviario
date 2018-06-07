package test;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

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
			
			ArrayList<String> transiciones = getTransiciones();
			ArrayList<String> recorridoTren = getRecorridoTren();
			ArrayList<String> complemento = new ArrayList<>();
			for(String transicion: transiciones) {
				if(!recorridoTren.contains(transicion)) {
					complemento.add(transicion);
				}
			}
			
			tempCopy = tempCopy.replaceAll(".*"+tranTrenArriboB+".*"+tranTrenEsperandoB+".*("+tranTrenLlenoB+"|"+tranEstacionVaciaB+").*", "");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scanFile.close();
		}
	}
	
	private static ArrayList<String> getTransiciones(){
		ArrayList<String> transiciones = new ArrayList<>();

		/* Transiciones */
		
		/* Estacion */
		transiciones.add(tranTrenArriboA);
		transiciones.add(tranTrenArriboB);
		transiciones.add(tranTrenArriboC);
		transiciones.add(tranTrenArriboD);
		transiciones.add(tranTrenArribo);
		
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
		
		/* Paso de Nivel Transito */
		transiciones.add(tranPasoNivelABMaquinaWait);
		transiciones.add(tranPasoNivelABMaquinaReady);
		transiciones.add(tranPasoNivelABVagonWait);
		transiciones.add(tranPasoNivelABVagonReady);
		
		transiciones.add(tranPasoNivelCDMaquinaWait);
		transiciones.add(tranPasoNivelCDMaquinaReady);
		transiciones.add(tranPasoNivelCDVagonWait);
		transiciones.add(tranPasoNivelCDVagonReady);
		
		/* Recorrido Tren */
		transiciones.add(tranRecorridoTrenAB);
		transiciones.add(tranRecorridoTrenCD);
		
		return transiciones;
	}
}
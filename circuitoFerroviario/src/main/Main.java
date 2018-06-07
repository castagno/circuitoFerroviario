package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class Main extends ConstantesComunes {
	private static final String marcadoInicial = "./src/main/MarcadoInicial.html";
	private static final String matrizInhibicion = "./src/main/MatrizInhibicion.html";
	private static final String matrizIMas = "./src/main/MatrizIMas.html";
	private static final String matrizIMenos = "./src/main/MatrizIMenos.html";
	private static final String testOutput = "./src/test/testOutput.txt";

	public static void main(String[] args) {
		
		PrintWriter printWriter = null;
		
		try {
			File file = new File(testOutput);
			if(file.exists()) {
				file.delete();
			}
			file.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			printWriter = new PrintWriter(fileOutputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("\nMatriz de Incidencia Positiva");
		Integer[][] matrizMas = getMatrix(matrizIMas);
		System.out.println("\nMatriz de Incidencia Negativa");
		Integer[][] matrizMenos = getMatrix(matrizIMenos);
		System.out.println("\nMatriz de Arcos Inhibidores");
		Integer[][] matrizInhibidora = getMatrix(matrizInhibicion);
		
		ArrayList<String> transiciones = getTransiciones(matrizIMas);
		
		LinkedHashMap<String, Integer> marcadoInicial = marcadoInicial(Main.marcadoInicial);
		
		
		Monitor monitor = new Monitor(matrizMas, matrizMenos, matrizInhibidora, marcadoInicial, transiciones, printWriter);
		
		SubirPasajeros subirPasajerosA = new SubirPasajeros(monitor, estacionA, precedenciaPrincipal);
		subirPasajerosA.start();
		SubirPasajeros subirPasajerosB = new SubirPasajeros(monitor, estacionB, precedenciaPrincipal);
		subirPasajerosB.start();
		SubirPasajeros subirPasajerosC = new SubirPasajeros(monitor, estacionC, precedenciaPrincipal);
		subirPasajerosC.start();
		SubirPasajeros subirPasajerosD = new SubirPasajeros(monitor, estacionD, precedenciaPrincipal);
		subirPasajerosD.start();
		
		SubirPasajeros subirPasajerosAuxA = new SubirPasajeros(monitor, estacionA, precedenciaAuxiliar);
		subirPasajerosAuxA.start();
		SubirPasajeros subirPasajerosAuxB = new SubirPasajeros(monitor, estacionB, precedenciaAuxiliar);
		subirPasajerosAuxB.start();
		SubirPasajeros subirPasajerosAuxC = new SubirPasajeros(monitor, estacionC, precedenciaAuxiliar);
		subirPasajerosAuxC.start();
		SubirPasajeros subirPasajerosAuxD = new SubirPasajeros(monitor, estacionD, precedenciaAuxiliar);
		subirPasajerosAuxD.start();
		
		BajarPasajeros bajarPasajerosA = new BajarPasajeros(monitor, estacionA, precedenciaPrincipal);
		bajarPasajerosA.start();
		BajarPasajeros bajarPasajerosB = new BajarPasajeros(monitor, estacionB, precedenciaPrincipal);
		bajarPasajerosB.start();
		BajarPasajeros bajarPasajerosC = new BajarPasajeros(monitor, estacionC, precedenciaPrincipal);
		bajarPasajerosC.start();
		BajarPasajeros bajarPasajerosD = new BajarPasajeros(monitor, estacionD, precedenciaPrincipal);
		bajarPasajerosD.start();
		
		BajarPasajeros bajarPasajerosAuxA = new BajarPasajeros(monitor, estacionA, precedenciaAuxiliar);
		bajarPasajerosAuxA.start();
		BajarPasajeros bajarPasajerosAuxB = new BajarPasajeros(monitor, estacionB, precedenciaAuxiliar);
		bajarPasajerosAuxB.start();
		BajarPasajeros bajarPasajerosAuxC = new BajarPasajeros(monitor, estacionC, precedenciaAuxiliar);
		bajarPasajerosAuxC.start();
		BajarPasajeros bajarPasajerosAuxD = new BajarPasajeros(monitor, estacionD, precedenciaAuxiliar);
		bajarPasajerosAuxD.start();
		
		Tren tren = new Tren(monitor, precedenciaPrincipal);
		tren.start();
		Tren trenAuxiliarArrivo = new Tren(monitor, precedenciaAuxiliarArrivo);
		trenAuxiliarArrivo.start();
		Tren trenAuxiliarPartida = new Tren(monitor, precedenciaAuxiliarPartida);
		trenAuxiliarPartida.start();
		
		Generador generadorPasajerosEstacionA = new Generador(monitor, generadorPasajeros, estacionA);
		generadorPasajerosEstacionA.start();
		Generador generadorPasajerosEstacionB = new Generador(monitor, generadorPasajeros, estacionB);
		generadorPasajerosEstacionB.start();
		Generador generadorPasajerosEstacionC = new Generador(monitor, generadorPasajeros, estacionC);
		generadorPasajerosEstacionC.start();
		Generador generadorPasajerosEstacionD = new Generador(monitor, generadorPasajeros, estacionD);
		generadorPasajerosEstacionD.start();
		
		Generador generadorTransitoEstacionA = new Generador(monitor, generadorTransito, recorridoAB);
		generadorTransitoEstacionA.start();
		Generador generadorTransitoEstacionB = new Generador(monitor, generadorTransito, recorridoCD);
		generadorTransitoEstacionB.start();
		
		PasoNivel pasoDeNivelMaquina = new PasoNivel(monitor, maquinaTren, precedenciaPrincipal);
		pasoDeNivelMaquina.start();
		PasoNivel pasoDeNivelVagon = new PasoNivel(monitor, vagonTren, precedenciaPrincipal);
		pasoDeNivelVagon.start();
		PasoNivel pasoDeNivelTransitoAuxiliarAB = new PasoNivel(monitor, pasoNivelTransitoAB, precedenciaAuxiliar);
		pasoDeNivelTransitoAuxiliarAB.start();
		PasoNivel pasoDeNivelTransitoAuxiliarCD = new PasoNivel(monitor, pasoNivelTransitoCD, precedenciaAuxiliar);
		pasoDeNivelTransitoAuxiliarCD.start();
		
	}
	
	
	static private Integer[][] getMatrix(String pathName) {
		Integer[][] matriz = new Integer[100][100];
		Integer[][] matrizIncidencia = null;
		
		FileReader matrizFile;
		try {
			matrizFile = new FileReader(pathName);
			Scanner scanFile = new Scanner(matrizFile);
//			System.out.println(scanFile.hasNext());
			
			String tempString = scanFile.nextLine();
			int i = 0;
			int j = 0;
			for (i = 0; !tempString.contains("</table"); i++) {
				while(!tempString.contains("<tr")) {
					tempString = scanFile.nextLine();
				}
				for (j = 0; !tempString.contains("</tr"); j++) {
					while(!tempString.contains("<td")) {
						tempString = scanFile.nextLine();
					}
					
					while(!tempString.contains("</td")) {
						if(tempString.contains("<td class=\"cell\">")) {
							tempString = scanFile.nextLine();
							matriz[i][j] = Integer.valueOf(tempString.trim());
						} else {
							tempString = scanFile.nextLine();
						}
					}
					tempString = scanFile.nextLine();
				}
				tempString = scanFile.nextLine();
			}

			System.out.println("i: "+(i-1)+ " - j:"+(j-1));
			matrizIncidencia = new Integer[(i-1)][(j-1)];
			
			for(int n = 0; n < (i-1); n++) {
				for(int m = 0; m < (j-1); m++) {
					matrizIncidencia[n][m] = matriz[n+1][m+1];
					System.out.print(" "+matrizIncidencia[n][m]);
				}
				System.out.println(" ");
			}
			
			scanFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return matrizIncidencia;
	}
	
	static private LinkedHashMap<String, Integer> marcadoInicial(String pathName) {
		LinkedHashMap<String, Integer> marcadoInicial = new LinkedHashMap<>();
		
		FileReader vectorMarcado;
		try {
			vectorMarcado = new FileReader(pathName);
			Scanner scanFile = new Scanner(vectorMarcado);
			
			if(!scanFile.nextLine().contains("<table")) {
				scanFile.close();
				return null;
			}
			String tempString = scanFile.nextLine();
			while(!tempString.contains("</table")) {
				if(!tempString.contains("<tr")) {
					tempString = scanFile.nextLine();
					continue;
				}
				tempString = scanFile.nextLine();
				int index = 0;
				while(!tempString.contains("</tr")) {
					if(!tempString.contains("<td")) {
						tempString = scanFile.nextLine();
						continue;
					}
					while(!tempString.contains("</td")) {
						if(tempString.contains("<td class=\"colhead\">")) {
							tempString = scanFile.nextLine();
//							System.out.print(" "+tempString.trim());
							marcadoInicial.put(tempString.trim(), null);
							index = index + 1;
						} else if(tempString.contains("<td class=\"cell\">")) {
							tempString = scanFile.nextLine();
//							System.out.print(" "+tempString.trim());
							marcadoInicial.put((String)marcadoInicial.keySet().toArray()[index], Integer.valueOf(tempString.trim()));
							index = index + 1;
						} else {
							tempString = scanFile.nextLine();
						}
					}
				}
//				System.out.println(" ");
			}
			
			System.out.println("");
			scanFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return marcadoInicial;
	}
	
	static private ArrayList<String> getTransiciones(String pathName) {
		ArrayList<String> transiciones = new ArrayList<>();
		
		FileReader matrizIPlus;
		try {
			matrizIPlus = new FileReader(pathName);
			Scanner scanFile = new Scanner(matrizIPlus);
			
			if(!scanFile.nextLine().contains("<table")) {
				scanFile.close();
				return null;
			}
			String tempString = scanFile.nextLine();
			while(!tempString.contains("</table")) {
				if(!tempString.contains("<tr")) {
					tempString = scanFile.nextLine();
					continue;
				}
				tempString = scanFile.nextLine();
				while(!tempString.contains("</tr")) {
					if(!tempString.contains("<td")) {
						tempString = scanFile.nextLine();
						continue;
					}
					while(!tempString.contains("</td")) {
						if(tempString.contains("<td class=\"colhead\">")) {
							tempString = scanFile.nextLine();
//							System.out.print(" "+tempString.trim());
							transiciones.add(tempString.trim());
						} else {
							tempString = scanFile.nextLine();
						}
					}
				}
				break;
			}

			System.out.println("");
			scanFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return transiciones;
	}
	
}
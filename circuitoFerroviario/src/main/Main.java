package main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
	private static final String estacionA = "Estacion A";
	private static final String estacionB = "Estacion B";
	private static final String estacionC = "Estacion C";
	private static final String estacionD = "Estacion D";
	
	private static final String matrizIMas = "/home/chloe/git/circuitoFerroviario/circuitoFerroviario/src/main/MatrizIMas.html";
	private static final String matrizIMenos = "/home/chloe/git/circuitoFerroviario/circuitoFerroviario/src/main/MatrizIMenos.html";

	public static void main(String[] args) {
		
//		ArrayList<ArrayList<Integer>> matrizMas = parseIncidenceMatrix(matrizIMas);
//		ArrayList<ArrayList<Integer>> matrizMenos = parseIncidenceMatrix(matrizIMenos);
		
		int[][] matrizMas = getIncidenceMatrix(matrizIMas);
		int[][] matrizMenos = getIncidenceMatrix(matrizIMenos);

		
		ArrayList<HashMap<String, Integer>> matrizSecuencia = secuenciaDisparo(secuenciaTransiciones(), matrizIMas);
		
		
		Monitor monitor = new Monitor(30, 20, matrizMas, matrizMenos);

		
		SubirPasajeros subirPasajerosA = new SubirPasajeros(monitor, estacionA);
		subirPasajerosA.start();
		SubirPasajeros subirPasajerosB = new SubirPasajeros(monitor, estacionB);
		subirPasajerosB.start();
		SubirPasajeros subirPasajerosC = new SubirPasajeros(monitor, estacionC);
		subirPasajerosC.start();
		SubirPasajeros subirPasajerosD = new SubirPasajeros(monitor, estacionD);
		subirPasajerosD.start();
	}
	
	static private ArrayList<ArrayList<Integer>> parseIncidenceMatrix(String pathName) {
		ArrayList<ArrayList<Integer>> matrizPlus = new ArrayList<>();
		
		FileReader matrizIPlus;
		try {
			matrizIPlus = new FileReader(pathName);
			Scanner scanFile = new Scanner(matrizIPlus);
			System.out.println(scanFile.hasNext());
			
			if(!scanFile.nextLine().contains("<table")) {
				scanFile.close();
				return null;
			}
			String tempString = scanFile.nextLine();
			while(!tempString.contains("</table")) {
				//System.out.println(tempString);
				if(!tempString.contains("<tr")) {
					tempString = scanFile.nextLine();
					continue;
				}
				tempString = scanFile.nextLine();
				ArrayList<Integer> filaPlaza = new ArrayList<>();
				while(!tempString.contains("</tr")) {
					if(!tempString.contains("<td")) {
						tempString = scanFile.nextLine();
						continue;
					}
					while(!tempString.contains("</td")) {
						if(tempString.contains("<td class=\"cell\">")) {
							tempString = scanFile.nextLine();
							System.out.print(" "+tempString.trim());
							filaPlaza.add(Integer.valueOf(tempString.trim()));
						} else {
							tempString = scanFile.nextLine();
						}
					}
					
				}
				matrizPlus.add(filaPlaza);
				System.out.println(" ");
				
			}
			scanFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return matrizPlus;
	}
	
	static private int[][] getIncidenceMatrix(String pathName) {
		int[][] matriz = new int[100][100];
		int[][] matrizIncidencia = null;
		
		FileReader matrizIPlus;
		try {
			matrizIPlus = new FileReader(pathName);
			Scanner scanFile = new Scanner(matrizIPlus);
			System.out.println(scanFile.hasNext());
			
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
							System.out.print(" "+tempString.trim());
						} else {
							tempString = scanFile.nextLine();
						}
					}
					tempString = scanFile.nextLine();
				}
				System.out.println(" ");
				tempString = scanFile.nextLine();
			}

			System.out.println("i: "+i+ " - j:"+j);
			matrizIncidencia = new int[(i+1)][(j+1)];
			
			for(int n = 0; n < i; n++) {
				for(int m = 0; m < i; m++) {
					matrizIncidencia[n][m] = matriz[n][m];
				}
			}
			
			scanFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return matrizIncidencia;
	}
	
	static private ArrayList<HashMap<String, Integer>> secuenciaDisparo(ArrayList<String> secuenciaTransiciones, String pathName) {
		ArrayList<HashMap<String, Integer>> secuenciaDisparo = new ArrayList<>();
		
		FileReader matrizIPlus;
		try {
			matrizIPlus = new FileReader(pathName);
			Scanner scanFile = new Scanner(matrizIPlus);
			System.out.println(scanFile.hasNext());
			
			ArrayList<String> filaPlaza = new ArrayList<>();
			
			if(!scanFile.nextLine().contains("<table")) {
				scanFile.close();
				return null;
			}
			String tempString = scanFile.nextLine();
			while(!tempString.contains("</table")) {
				//System.out.println(tempString);
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
							System.out.print(" "+tempString.trim());
							filaPlaza.add(tempString.trim());
						} else {
							tempString = scanFile.nextLine();
						}
					}
				}
				break;
			}
			
			for(String transicion: secuenciaTransiciones) {
				HashMap<String, Integer> disparo = new HashMap<>();
				for (String columna: filaPlaza) {
					if(transicion.equals(columna)) {
						disparo.put(columna, 1);
						System.out.print(" "+1);
					} else {
						disparo.put(columna, 0);
						System.out.print(" "+0);
					}
				}
				secuenciaDisparo.add(disparo);
				System.out.println("");
			}
			

			System.out.println("");
			System.out.println(filaPlaza.size());
			System.out.println(filaPlaza.get(0));
			scanFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static ArrayList<String> secuenciaTransiciones(){
		ArrayList<String> secuenciaTransiciones = new ArrayList<>();
		secuenciaTransiciones.add("BAr");
		secuenciaTransiciones.add("BW");
		secuenciaTransiciones.add("BDe");
		secuenciaTransiciones.add("CAr");
		secuenciaTransiciones.add("CW");
		secuenciaTransiciones.add("CDe");
		secuenciaTransiciones.add("PNCDMW");
		secuenciaTransiciones.add("PNCDMR");
		secuenciaTransiciones.add("PNCDVW");
		secuenciaTransiciones.add("PNCDVR");
		secuenciaTransiciones.add("RCD");
		secuenciaTransiciones.add("DAr");
		secuenciaTransiciones.add("DW");
		secuenciaTransiciones.add("DDe");
		secuenciaTransiciones.add("PNABMW");
		secuenciaTransiciones.add("PNABMR");
		secuenciaTransiciones.add("PNABVW");
		secuenciaTransiciones.add("PNABVR");
		secuenciaTransiciones.add("RAB");
		secuenciaTransiciones.add("AAr");
		secuenciaTransiciones.add("AW");
		secuenciaTransiciones.add("ADe");
		
		return secuenciaTransiciones;
	}
}
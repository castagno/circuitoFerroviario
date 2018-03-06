package main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class Main {
	private static final String estacionA = "Estacion A";
	private static final String estacionB = "Estacion B";
	private static final String estacionC = "Estacion C";
	private static final String estacionD = "Estacion D";
	
	private static final String marcadoInicial = "/home/chloe/git/circuitoFerroviario/circuitoFerroviario/src/main/MarcadoInicial.html";
	private static final String matrizIMas = "/home/chloe/git/circuitoFerroviario/circuitoFerroviario/src/main/MatrizIMas.html";
	private static final String matrizIMenos = "/home/chloe/git/circuitoFerroviario/circuitoFerroviario/src/main/MatrizIMenos.html";

	public static void main(String[] args) {
		
		Integer[][] matrizMas = getIncidenceMatrix(matrizIMas);
		Integer[][] matrizMenos = getIncidenceMatrix(matrizIMenos);
		
		ArrayList<String> transiciones = getTransiciones(matrizIMas);
		
		LinkedHashMap<String, Integer> marcadoInicial = marcadoInicial(Main.marcadoInicial);
		
		
		Monitor monitor = new Monitor(30, 20, matrizMas, matrizMenos, marcadoInicial, transiciones);

		
		SubirPasajeros subirPasajerosA = new SubirPasajeros(monitor, estacionA);
		subirPasajerosA.start();
		SubirPasajeros subirPasajerosB = new SubirPasajeros(monitor, estacionB);
		subirPasajerosB.start();
		SubirPasajeros subirPasajerosC = new SubirPasajeros(monitor, estacionC);
		subirPasajerosC.start();
		SubirPasajeros subirPasajerosD = new SubirPasajeros(monitor, estacionD);
		subirPasajerosD.start();
		
		
		Tren tren = new Tren(monitor);
		tren.start();
	}
	
	/*
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
	*/
	
	static private Integer[][] getIncidenceMatrix(String pathName) {
		Integer[][] matriz = new Integer[100][100];
		Integer[][] matrizIncidencia = null;
		
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
//							System.out.print(" "+tempString.trim());
						} else {
							tempString = scanFile.nextLine();
						}
					}
					tempString = scanFile.nextLine();
				}
//				System.out.println(" ");
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
		
		FileReader matrizIPlus;
		try {
			matrizIPlus = new FileReader(pathName);
			Scanner scanFile = new Scanner(matrizIPlus);
			System.out.println(scanFile.hasNext());
			
//			ArrayList<String> keySet = new ArrayList<>();
			
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
				int index = 0;
				while(!tempString.contains("</tr")) {
					if(!tempString.contains("<td")) {
						tempString = scanFile.nextLine();
						continue;
					}
					while(!tempString.contains("</td")) {
						if(tempString.contains("<td class=\"colhead\">")) {
							tempString = scanFile.nextLine();
							System.out.print(" "+tempString.trim());
							marcadoInicial.put(tempString.trim(), null);
//							keySet.add(tempString.trim());
							index = index + 1;
						} else if(tempString.contains("<td class=\"cell\">")) {
							tempString = scanFile.nextLine();
							System.out.print(" "+tempString.trim());
							marcadoInicial.put((String)marcadoInicial.keySet().toArray()[index], Integer.valueOf(tempString.trim()));
							index = index + 1;
						} else {
							tempString = scanFile.nextLine();
						}
					}
				}
				System.out.println(" ");
//				break;
			}
			
			System.out.println("");
			System.out.println(marcadoInicial.size());
			System.out.println(marcadoInicial.get(marcadoInicial.keySet().toArray()[0]));
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
				while(!tempString.contains("</tr")) {
					if(!tempString.contains("<td")) {
						tempString = scanFile.nextLine();
						continue;
					}
					while(!tempString.contains("</td")) {
						if(tempString.contains("<td class=\"colhead\">")) {
							tempString = scanFile.nextLine();
							System.out.print(" "+tempString.trim());
							transiciones.add(tempString.trim());
						} else {
							tempString = scanFile.nextLine();
						}
					}
				}
				break;
			}
			

			System.out.println("");
			System.out.println(transiciones.size());
			System.out.println(transiciones.get(0));
			scanFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return transiciones;
	}
	
	/*

	
	static private ArrayList<LinkedHashMap<String, Integer>> secuenciaDisparo(ArrayList<String> secuenciaTransiciones, String pathName) {
		ArrayList<LinkedHashMap<String, Integer>> secuenciaDisparo = new ArrayList<>();
		
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
				System.out.println("");

				LinkedHashMap<String, Integer> disparo = new LinkedHashMap<>();
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
			}
			

			System.out.println("");
			System.out.println(filaPlaza.size());
			System.out.println(filaPlaza.get(0));
			scanFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return secuenciaDisparo;
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
		secuenciaTransiciones.add("AAr");
		secuenciaTransiciones.add("AW");
		secuenciaTransiciones.add("ADe");
		secuenciaTransiciones.add("PNABMW");
		secuenciaTransiciones.add("PNABMR");
		secuenciaTransiciones.add("PNABVW");
		secuenciaTransiciones.add("PNABVR");
		secuenciaTransiciones.add("RAB");
		
		return secuenciaTransiciones;
	}
	*/
}
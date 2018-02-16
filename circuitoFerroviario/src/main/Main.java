package main;

public class Main {
	private static final String estacionA = "Estacion A";
	private static final String estacionB = "Estacion B";
	private static final String estacionC = "Estacion C";
	private static final String estacionD = "Estacion D";

	public static void main(String[] args) {
		Monitor monitor = new Monitor(30, 20);

		
		SubirPasajeros subirPasajerosA = new SubirPasajeros(monitor, estacionA);
		subirPasajerosA.start();
		SubirPasajeros subirPasajerosB = new SubirPasajeros(monitor, estacionB);
		subirPasajerosB.start();
		SubirPasajeros subirPasajerosC = new SubirPasajeros(monitor, estacionC);
		subirPasajerosC.start();
		SubirPasajeros subirPasajerosD = new SubirPasajeros(monitor, estacionD);
		subirPasajerosD.start();
	}
}
package main;

import java.util.Date;

public class SubirPasajeros extends Thread  {
	
	private Monitor monitorTren;
	private Date sleepTimeStamp;
	private String subida = "Subida";
	
	public SubirPasajeros(Monitor monitor, String estacion) {
		monitorTren = monitor;
		sleepTimeStamp = new Date();
		setName(subida + " " + estacion);
	}

	@Override
	public void run() {
		try {
			int tiempo = TiempoDeEspera.getInstance(5000, 97L).getNextRandom();
//			System.out.println("Tiempo de espera ("+Thread.currentThread().getName()+"): "+tiempo);
			sleep(tiempo);
			sleepTimeStamp = new Date();
			monitorTren.abordarTren();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public int getPasajerosEsperando() {
		Date actual = new Date();
		Long tiempoDormido = actual.getTime() - sleepTimeStamp.getTime();
		int pasajeros = 0;
		int tiempoEsperado = TiempoDeEspera.getInstance(5000, 97L).getNextRandom();
		while(tiempoDormido > tiempoEsperado) {
			tiempoDormido = tiempoDormido - tiempoEsperado;
			tiempoEsperado = TiempoDeEspera.getInstance(5000, 97L).getNextRandom();
			pasajeros = pasajeros + 1;
		}
		
		return pasajeros;
	}
}
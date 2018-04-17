package main;

import java.util.Date;

public class SubirPasajeros extends Thread  {
	
	private Date sleepTimeStamp;
	private Monitor monitorTren;
	private Integer pasajeros;
	
	public SubirPasajeros(Monitor monitor, String estacion) {
		monitorTren = monitor;
		sleepTimeStamp = new Date();
		setName(ConstantesComunes.subida + " " + estacion);
	}

	@Override
	public void run() {
		try {
			while(true) {
//				if(Integer.valueOf(0).equals(pasajeros)) {
//					int tiempo = TiempoDeEspera.getInstance(5000, 97L).getNextRandom();
//					sleep(tiempo);
//					pasajeros = 1;
//				}
				sleepTimeStamp = new Date();
				monitorTren.abordarTren();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public Integer getPasajeros() {
		pasajeros = pasajeros==null? getPasajerosEsperando(): pasajeros + getPasajerosEsperando();
		return pasajeros;
	}
	
	public void setPasajeros(int pasajeros) {
		this.pasajeros = pasajeros;
	}
	
	private int getPasajerosEsperando() {
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
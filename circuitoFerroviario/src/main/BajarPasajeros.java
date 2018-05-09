package main;

import java.util.Date;

public class BajarPasajeros extends Thread  {
	
	private Date sleepTimeStamp;
	private Monitor monitorTren;
	private Integer pasajeros;
	
	public BajarPasajeros(Monitor monitor, String estacion, String precedencia) {
		monitorTren = monitor;
		sleepTimeStamp = new Date();
		setName(ConstantesComunes.bajada + precedencia + estacion);
	}

	@Override
	public void run() {
		try {
			while(true) {
				sleepTimeStamp = new Date();
				monitorTren.descenderTren();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public Integer getPasajeros(Date fechaSubida) {
		pasajeros = pasajeros==null? getPasajerosEsperando(fechaSubida): pasajeros + getPasajerosEsperando(fechaSubida);
		return pasajeros;
	}
	
	public void setPasajeros(int pasajeros) {
		this.pasajeros = pasajeros;
	}
	
	private int getPasajerosEsperando(Date fechaSubida) {
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
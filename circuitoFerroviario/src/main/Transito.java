package main;

import java.util.Date;

public class Transito extends Thread  {
	
	private Date sleepTimeStamp;
	private Monitor monitorTren;
	private Integer vehiculos;
	
	public Transito(Monitor monitor) {
		monitorTren = monitor;
		sleepTimeStamp = new Date();
		setName(ConstantesComunes.transito);
	}

	@Override
	public void run() {
		/*
		try {
			while(true) {
				if(Integer.valueOf(0).equals(pasajeros)) {
//					int tiempo = TiempoDeEspera.getInstance(5000, 97L).getNextRandom();
//					System.out.println("Tiempo de espera ("+Thread.currentThread().getName()+"): "+tiempo);
//					sleep(tiempo);
					pasajeros = 1;
				}
				sleepTimeStamp = new Date();
				monitorTren.descenderTren();
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
	}
	
	public Integer getVehiculos() {
		vehiculos = vehiculos==null? getVehiculosEsperando(): vehiculos + getVehiculosEsperando();
		return vehiculos;
	}
	
	public void setVehiculos(int vehiculos) {
		this.vehiculos = vehiculos;
	}
	
	private int getVehiculosEsperando() {
		Date actual = new Date();
		Long tiempoDormido = actual.getTime() - sleepTimeStamp.getTime();
		int vehiculos = 0;
		int tiempoEsperado = TiempoDeEspera.getInstance(5000, 97L).getNextRandom();
		while(tiempoDormido > tiempoEsperado) {
			tiempoDormido = tiempoDormido - tiempoEsperado;
			tiempoEsperado = TiempoDeEspera.getInstance(5000, 97L).getNextRandom();
			vehiculos = vehiculos + 1;
		}
		
		return vehiculos;
	}
}
package main;


public class Generador extends Thread  {
	
//	private Date sleepTimeStamp;
	private Monitor monitorTren;
	
	public Generador(Monitor monitor, String transitoPasajeros, String estacionRecorrido) {
		monitorTren = monitor;
//		sleepTimeStamp = new Date();
		setName(transitoPasajeros + estacionRecorrido);
	}

	@Override
	public void run() {
		try {
			while(true) {
//				if(Integer.valueOf(0).equals(pasajeros)) {
				int tiempo = TiempoDeEspera.getInstance(5000, 97L).getNextRandom();
				sleep(tiempo);
//					pasajeros = 1;
//				}
//				sleepTimeStamp = new Date();
				
				
//				if(Thread.currentThread().getName().startsWith(ConstantesComunes.pasajeros)) {
//					monitorTren.generarPasajeros();
//				}	
//				if(Thread.currentThread().getName().startsWith(ConstantesComunes.transito)) {
//					monitorTren.generarPasajeros();
//				}	
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
//	
//	private int getPasajerosEsperando() {
//		Date actual = new Date();
//		Long tiempoDormido = actual.getTime() - sleepTimeStamp.getTime();
//		int pasajeros = 0;
//		int tiempoEsperado = TiempoDeEspera.getInstance(5000, 97L).getNextRandom();
//		while(tiempoDormido > tiempoEsperado) {
//			tiempoDormido = tiempoDormido - tiempoEsperado;
//			tiempoEsperado = TiempoDeEspera.getInstance(5000, 97L).getNextRandom();
//			pasajeros = pasajeros + 1;
//		}
//		
//		return pasajeros;
//	}
}
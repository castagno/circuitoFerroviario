package main;


public class Generador extends Thread  {
	
	private Monitor monitorTren;
	
	public Generador(Monitor monitor, String transitoPasajeros, String estacionRecorrido) {
		monitorTren = monitor;
		setName(transitoPasajeros + estacionRecorrido);
	}

	@Override
	public void run() {
		try {
			while(true) {
				
				if(Thread.currentThread().getName().startsWith(ConstantesComunes.generadorPasajeros)) {
					sleep(TiempoDeEspera.getInstance(97L).getNextRandom(3900));
					monitorTren.generarPasajeros();
				}	
				if(Thread.currentThread().getName().startsWith(ConstantesComunes.generadorTransito)) {
					sleep(TiempoDeEspera.getInstance(97L).getNextRandom(5000));
					monitorTren.generarTransito();
				}	
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
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
				sleep(TiempoDeEspera.getInstance(5000, 97L).getNextRandom());
				
				if(Thread.currentThread().getName().startsWith(ConstantesComunes.generadorPasajeros)) {
					monitorTren.generarPasajeros();
				}	
				if(Thread.currentThread().getName().startsWith(ConstantesComunes.generadorTransito)) {
					monitorTren.generarTransito();
				}	
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
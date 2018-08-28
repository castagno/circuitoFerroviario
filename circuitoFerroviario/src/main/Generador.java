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
				String mensaje = "";
				if(Thread.currentThread().getName().startsWith(ConstantesComunes.generadorPasajeros)) {
					sleep(TiempoDeEspera.getInstance(97L).getNextRandom(3900));
					mensaje = monitorTren.generarPasajeros();
					System.out.print(mensaje);
				}	
				if(Thread.currentThread().getName().startsWith(ConstantesComunes.generadorTransito)) {
					sleep(TiempoDeEspera.getInstance(97L).getNextRandom(5000));
					mensaje = monitorTren.generarTransito();
					System.out.print(mensaje);
				}	
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
package main;

public class SubirPasajeros extends Thread  {
	
	private Monitor monitorTren;
	
	public SubirPasajeros(Monitor monitor, String estacion, String precedencia) {
		monitorTren = monitor;
		setName(ConstantesComunes.subida + precedencia + estacion);
	}

	@Override
	public void run() {
		try {
			while(true) {
				monitorTren.abordarTren();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
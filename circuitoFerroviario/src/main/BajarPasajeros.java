package main;

public class BajarPasajeros extends Thread  {
	
	private Monitor monitorTren;
	
	public BajarPasajeros(Monitor monitor, String estacion, String precedencia) {
		monitorTren = monitor;
		setName(ConstantesComunes.bajada + precedencia + estacion);
	}

	@Override
	public void run() {
		try {
			while(true) {
				Long bajadaSleep = monitorTren.descenderTren();
				sleep(bajadaSleep);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
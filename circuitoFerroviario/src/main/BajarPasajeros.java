package main;

import java.util.ArrayList;

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
				ArrayList<String> mensajeRespuesta = monitorTren.descenderTren();
				Long bajadaSleep = Long.valueOf(mensajeRespuesta.get(0));
				System.out.print(mensajeRespuesta.get(1));
				sleep(bajadaSleep);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
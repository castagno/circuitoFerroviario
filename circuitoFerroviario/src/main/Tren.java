package main;

public class Tren extends Thread {
	
	private Monitor monitorTren;
	
	public Tren(Monitor monitor, String precedencia) {
		monitorTren = monitor;
		setName(ConstantesComunes.tren + precedencia);
	}

	@Override
	public void run() {
		try {
			while(true) {
				if(Thread.currentThread().getName().endsWith(ConstantesComunes.precedenciaAuxiliarArrivo)) {
					monitorTren.arrivoTrenEstacion();
				}
				if(Thread.currentThread().getName().endsWith(ConstantesComunes.precedenciaAuxiliarPartida)) {
					monitorTren.partidaTren();
				}
				if(Thread.currentThread().getName().endsWith(ConstantesComunes.precedenciaPrincipal)) {
					monitorTren.continuarRecorridoTren();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
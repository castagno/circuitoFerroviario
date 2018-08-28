package main;

import java.util.ArrayList;

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
				String mensaje = new String();
				if(Thread.currentThread().getName().endsWith(ConstantesComunes.precedenciaAuxiliarArrivo)) {
					mensaje = monitorTren.arrivoTrenEstacion();
					System.out.print(mensaje);
				}
				if(Thread.currentThread().getName().endsWith(ConstantesComunes.precedenciaAuxiliarPartida)) {
					mensaje = monitorTren.partidaTren();
					System.out.print(mensaje);
				}
				if(Thread.currentThread().getName().endsWith(ConstantesComunes.precedenciaPrincipal)) {
					ArrayList<String> mensajeRespuesta = monitorTren.continuarRecorridoTren();
					Long tiempoEsperaContinuarRecorrido = Long.valueOf(mensajeRespuesta.get(0));
					System.out.print(mensajeRespuesta.get(1));
					sleep(tiempoEsperaContinuarRecorrido);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
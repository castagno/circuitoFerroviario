package main;

public class PasoNivel extends Thread  {
	
	private Monitor monitorTren;
	
	public PasoNivel(Monitor monitor, String maquinaVagonRecorrido, String precedencia) {
		monitorTren = monitor;
		setName(precedencia + ConstantesComunes.pasoNivel + maquinaVagonRecorrido);
	}

	@Override
	public void run() {
		try {
			while(true) {
				String mensaje = "";
				if(Thread.currentThread().getName().endsWith(ConstantesComunes.maquinaTren) || Thread.currentThread().getName().endsWith(ConstantesComunes.vagonTren)) {
					mensaje = monitorTren.cruzarPasoNivel();
				} else {
					mensaje = monitorTren.liberarBarreraPasoNivel();
				}
				System.out.print(mensaje);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
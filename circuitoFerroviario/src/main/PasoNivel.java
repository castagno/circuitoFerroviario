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
				if(Thread.currentThread().getName().endsWith(ConstantesComunes.maquina) || Thread.currentThread().getName().endsWith(ConstantesComunes.vagon)) {
					monitorTren.cruzarPasoNivel();
				} else {
					monitorTren.liberarBarreraPasoNivel();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
package main;

import java.util.Date;

public class Tren extends Thread  {
	
	private Monitor monitorTren;
	private Date sleepTimeStamp;
	private String tren = "Tren";
	
	public Tren(Monitor monitor, String estacion) {
		monitorTren = monitor;
		sleepTimeStamp = new Date();
		setName(tren + " " + estacion);
	}

	@Override
	public void run() {
		try {
			System.out.println("Tiempo de espera ("+Thread.currentThread().getName()+"): ");
			sleepTimeStamp = new Date();
			sleep(10000);
//			monitorTren.abordarTren();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
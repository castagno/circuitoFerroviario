package main;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

public class Tren extends Thread  {
	
	private Monitor monitorTren;
	ArrayList<LinkedHashMap<String, Integer>> matrizSecuencia;
	private Date sleepTimeStamp;
	private String tren = "Tren";
	
	public Tren(Monitor monitor, ArrayList<LinkedHashMap<String, Integer>> matrizSecuencia, String tren) {
		this.matrizSecuencia = matrizSecuencia;
		monitorTren = monitor;
		sleepTimeStamp = new Date();
		setName(tren + " " + tren);
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
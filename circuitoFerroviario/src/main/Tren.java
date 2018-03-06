package main;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

public class Tren extends Thread  {
	
	private Monitor monitorTren;
	private int currentIndex;
//	private ArrayList<LinkedHashMap<String, Integer>> matrizSecuencia;
	private Date sleepTimeStamp;
	private String tren = "Tren";
	
	public Tren(Monitor monitor) {
//		this.matrizSecuencia = matrizSecuencia;
		currentIndex = 0;
		monitorTren = monitor;
		sleepTimeStamp = new Date();
		setName(tren);
	}

	@Override
	public void run() {
		try {
			while(true) {
//				System.out.println("Tiempo de espera ("+Thread.currentThread().getName()+"): ");
				sleep(1000);
//				monitorTren.continuarRecorridoTren(matrizSecuencia.get(currentIndex).values().toArray(new Integer[matrizSecuencia.get(currentIndex).values().size()]));
//				currentIndex = (currentIndex + 1) % matrizSecuencia.size();
				monitorTren.continuarRecorridoTren();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public Date getTimeStamp() {
		return (sleepTimeStamp!=null? sleepTimeStamp : new Date());
	}
	
	public void setTimeStamp(Date time) {
		this.sleepTimeStamp = time;
	}
}
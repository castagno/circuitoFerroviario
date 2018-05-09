package main;

import java.util.Date;

public class Tren extends Thread {
	
	private Monitor monitorTren;
//	private int currentIndex;
//	private ArrayList<LinkedHashMap<String, Integer>> matrizSecuencia;
	private Date sleepTimeStamp;
	
	public Tren(Monitor monitor, String precedencia) {
//		this.matrizSecuencia = matrizSecuencia;
//		currentIndex = 0;
		monitorTren = monitor;
		sleepTimeStamp = new Date();
		setName(ConstantesComunes.tren + precedencia);
	}

	@Override
	public void run() {
		try {
			while(true) {
//				System.out.println("Tiempo de espera ("+Thread.currentThread().getName()+"): ");
//				sleep(1000);
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
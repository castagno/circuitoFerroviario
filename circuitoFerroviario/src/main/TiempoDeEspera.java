package main;

import java.util.Random;

public class TiempoDeEspera {
	private static TiempoDeEspera timepoDeEspera;
	
	private Random random;
	
	private TiempoDeEspera(Long seed) {
		this.random = new Random(seed);
	}
	
	public static synchronized TiempoDeEspera getInstance(Long seed) {
		if(timepoDeEspera == null) {
			timepoDeEspera = new TiempoDeEspera(seed);
		}
		return timepoDeEspera;
	}
	
	public synchronized int getNextRandom(Integer tiempoMaximoCustom) {
		return random.nextInt(tiempoMaximoCustom);
	}
}
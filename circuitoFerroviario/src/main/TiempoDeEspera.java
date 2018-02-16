package main;

import java.util.Random;

public class TiempoDeEspera {
	private static TiempoDeEspera timepoDeEspera;
	
	private Integer tiempoMaximo;
	private Random random;
	
	private TiempoDeEspera(Integer tiempoMaximo, Long seed) {
		this.tiempoMaximo = tiempoMaximo;
		this.random = new Random(seed);
	}
	
	public static synchronized TiempoDeEspera getInstance(Integer tiempoMaximo, Long seed) {
		if(timepoDeEspera == null) {
			timepoDeEspera = new TiempoDeEspera(tiempoMaximo, seed);
		}
		return timepoDeEspera;
	}
	
	public synchronized int getNextRandom() {
		return random.nextInt(tiempoMaximo);
	}
}
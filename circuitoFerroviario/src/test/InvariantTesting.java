package test;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Scanner;
import main.ConstantesComunes;

public class InvariantTesting extends ConstantesComunes {
	private static final String testOutput = "./src/test/invariantTestOutput.txt";
		
	public static void main(String[] args) {
		
		Scanner scanFile = null;
		try {
			scanFile = new Scanner(new FileReader(testOutput));
			
			String tempString = scanFile.nextLine();
			
			ArrayList<String> keyList = new ArrayList<>(Arrays.asList(tempString.split(",")));
			LinkedHashMap<String, ArrayList<Integer>> marcadoPorPlaza = new LinkedHashMap<>();
			for(String key: keyList) {
				marcadoPorPlaza.put(key, new ArrayList<>());
			}
			while (scanFile.hasNext()) {
				String[] marcado = scanFile.nextLine().split(",");
				for (int i = 0; i < marcado.length; i++) {
					marcadoPorPlaza.get(keyList.get(i)).add(Integer.valueOf(marcado[i]));
				}
			}

			System.out.println("\nTest:\n");
			
			ArrayList<String> plazasMaquina = new ArrayList<>(Arrays.asList(new String[]{maq, maqA, maqB, maqC, maqD}));
			invariantTest(marcadoPorPlaza, plazasMaquina, 30);
			
			ArrayList<String> plazasVagon = new ArrayList<>(Arrays.asList(new String[]{vag, vagA, vagB, vagC, vagD}));
			invariantTest(marcadoPorPlaza, plazasVagon, 20);
			
			ArrayList<String> plazasPasoNivelAB = new ArrayList<>(Arrays.asList(new String[]{pasoNivelABBarrera, pasoNivelABMaquina, pasoNivelABVagon, pasoNivelABTransito}));
			invariantTest(marcadoPorPlaza, plazasPasoNivelAB, 1);
			
			ArrayList<String> plazasPasoNivelCD = new ArrayList<>(Arrays.asList(new String[]{pasoNivelCDBarrera, pasoNivelCDMaquina, pasoNivelCDVagon, pasoNivelCDTransito}));
			invariantTest(marcadoPorPlaza, plazasPasoNivelCD, 1);
			
			ArrayList<String> plazasEstacionA = new ArrayList<>(Arrays.asList(new String[]{trenEstacionAPartida, trenEstacionAEspera, trenEstacionA}));
			invariantTest(marcadoPorPlaza, plazasEstacionA, 1);
			
			ArrayList<String> plazasEstacionB = new ArrayList<>(Arrays.asList(new String[]{trenEstacionBPartida, trenEstacionBEspera, trenEstacionB}));
			invariantTest(marcadoPorPlaza, plazasEstacionB, 1);
			
			ArrayList<String> plazasEstacionC = new ArrayList<>(Arrays.asList(new String[]{trenEstacionCPartida, trenEstacionCEspera, trenEstacionC}));
			invariantTest(marcadoPorPlaza, plazasEstacionC, 1);
			
			ArrayList<String> plazasEstacionD = new ArrayList<>(Arrays.asList(new String[]{trenEstacionDPartida, trenEstacionDEspera, trenEstacionD}));
			invariantTest(marcadoPorPlaza, plazasEstacionD, 1);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scanFile.close();
		}
	}
	
	private static void invariantTest(LinkedHashMap<String, ArrayList<Integer>> marcadoPorPlaza, ArrayList<String> plazas, int tokens) {
		ArrayList<String> keyList = new ArrayList<>(Arrays.asList(marcadoPorPlaza.keySet().toArray(new String[marcadoPorPlaza.keySet().size()])));
		boolean errorEncontrado = false;
		for (int i = 0; i < marcadoPorPlaza.get(keyList.get(0)).size(); i++) {
			int sumatoria = 0;
			for (String key: plazas) {
				sumatoria += marcadoPorPlaza.get(key).get(i);
			}
			if(tokens != sumatoria) {
				errorEncontrado = true;
				System.out.print("Error: Disparo "+i+" invariante ");
				for (String key: plazas) {
					System.out.print(key);
					if((plazas.indexOf(key) + 1) < plazas.size()) {
						System.out.print(" + ");
					}
				}
				System.out.print(" = "+ tokens +" no se cumple, "+ sumatoria +" es el resultado\n");
			}
		}
		if(!errorEncontrado) {
			System.out.print("Exito: El invariante ");
			for (String key: plazas) {
				System.out.print(key);
				if((plazas.indexOf(key) + 1) < plazas.size()) {
					System.out.print(" + ");
				}
			}
			System.out.print(" = "+ tokens +" se cumple para todos los disparos\n");
		}
	}
}
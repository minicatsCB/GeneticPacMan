package pacman.entries.pacman;

import java.awt.font.NumericShaper.Range;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import com.fuzzylite.term.Trapezoid;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */

/*
 * Hay cambios en Executor.java y DataCollectorController.java
 */
public class MyGeneticPacMan extends Controller<MOVE>
{
	private MOVE myMove=MOVE.NEUTRAL;
	public final int NUMERO_PARAMETROS = 12;	// Array = [0.000, 0.000, 25.000, 50.000][25.000,50.000, 75.000, 150.000][25.000][50.000, 75.000, 150.000]
	
	private ArrayList<Double> array = new ArrayList<Double>();	// Array representando el individuo con los diferentes valores para el motor fuzzy
	private ArrayList<Double> range = new ArrayList<Double>();	// Array con los valores mínimo y máximo del rango en el que se pueden mover los valores, en este caso, la distancia, [0, 150]
	
	private MyFuzzyPacMan myFuzzyPacMan;
	
	/**
	 * Asigna valores al individuo que le va a pasar al motor Fuzzy en cada iteración, en este caso, partida
	 * @param rangeMinimum el mínimo valor del rango (en este caso la distancia)
	 * @param rangeMaximum el máximo valor del rango (en este caso la distancia)
	 */
	public MyGeneticPacMan(double rangeMinimum, double rangeMaximum) {
		System.out.println("Constructor MyGeneticPacMan");
		for(double i = 0; i < NUMERO_PARAMETROS; i++) {
			array.add(i + 1.0);	// Por ahora, a 0. Se pueden probar diferentes inicializaciones
		}
		range.add(rangeMinimum);
		range.add(rangeMaximum);
		myFuzzyPacMan = new MyFuzzyPacMan(range, array);
	}
	
	public MOVE getMove(Game game, long timeDue) 
	{
		/////// System.out.println("getMove myGeneticPacMan");
		// Aqui vamos a llamar al Fuzzy para que nos devuelva un valor con los valores que le hayamos pasado: los arrays o indiviudos (la población)
		// Es un wrapper
		return myFuzzyPacMan.getMove(game, timeDue);
		
	}
	
	/***
	 * Esto cambia el los valores del array o individuo
	 * Por ahora, hace un cruce
	 */
	public void mutate(int mutationOperator) {
		switch(mutationOperator) {
		case 0:
			System.out.println("Mutation operator: Random Full Resetting");
			randomFullResetting();
			break;
		case 1:
			System.out.println("Mutation operator: Random Gene Resetting");
			randomGeneResetting();
			break;
		case 2:
			System.out.println("Mutation operator: Swap Mutation");
			swapMutation();
			break;
		case 3:
			System.out.println("Mutation operator: Scramble Mutation");
			int randomGene1 = new Random().nextInt(NUMERO_PARAMETROS);	// [0, 12)
			int randomGene2 = new Random().nextInt(NUMERO_PARAMETROS);	// [0, 12)
			// Si resulta que hemos cogido el mismo valor para el final que el inicial,
			// volvemos a coger otro valor final aleatorio, hasta que sea distinto del valor inicial escogido
			while(randomGene1 == randomGene2) {
				System.out.println("Genes coinciden. Cogiendo otro...");
				randomGene2 = new Random().nextInt(NUMERO_PARAMETROS);	// [0, 12)
			}
			// Tenemos que pasar los parámetros de mayor a menor
			// Aquí ya estamos seguros de que son distintos porque
			// han pasado por el while y no hace falta comprobarlo
			if(randomGene1 < randomGene2) {
				scrambleMutation(randomGene1, randomGene2);
			}
			else {
				scrambleMutation(randomGene2, randomGene1);
			}
			break;
		case 4:
			System.out.println("Mutation operator: Inversion Mutation");
			randomGene1 = new Random().nextInt(NUMERO_PARAMETROS);	// [0, 12)
			randomGene2 = new Random().nextInt(NUMERO_PARAMETROS);	// [0, 12)
			// Si resulta que hemos cogido el mismo valor para el final que el inicial,
			// volvemos a coger otro valor final aleatorio, hasta que sea distinto del valor inicial escogido
			while(randomGene1 == randomGene2) {
				System.out.println("Genes coinciden. Cogiendo otro...");
				randomGene2 = new Random().nextInt(NUMERO_PARAMETROS);	// [0, 12)
			}
			// Tenemos que pasar los parámetros de mayor a menor
			// Aquí ya estamos seguros de que son distintos porque
			// han pasado por el while y no hace falta comprobarlo
			if(randomGene1 < randomGene2) {
				inversionMutation(randomGene1, randomGene2);
			}
			else {
				inversionMutation(randomGene2, randomGene1);
			}
			break;
		default:
			System.out.println("Opcion no valida. Inténtalo de nuevo");
			break;
		}
	}
	
	/***
	 * Resets the entire array or chromosome with random values
	 */
	public void randomFullResetting() {
		double random;
		for(int i = 0; i < NUMERO_PARAMETROS; i++) {
			random = new Random().nextDouble() * range.get(1);
			////// System.out.println("----> Random: " + random);
			array.set(i, random);
		}
	}
	
	/***
	 * A valid random value is assigned to a randomly chosen gene (del array o individuo)
	 */
	public void randomGeneResetting() {
		int randomGene;	// The gen to reset
		double randomValue;	// The value to reset the gen
		randomGene = new Random().nextInt(NUMERO_PARAMETROS);	// [0, 12)
		randomValue = new Random().nextDouble() * range.get(1);	// [0.0, 150.0]
		System.out.println("Cambiando Array[" + randomGene + "] a " + randomValue);
		array.set(randomGene, randomValue);
	}
	
	/***
	 * We select two positions on the chromosome at random,
	 * and interchange their values
	 * Array cannot contain all 0 values <---- BE AWARE OF THIS
	 */
	public void swapMutation() {
		int randomGen1, randomGen2;	// The genes whose values we are going to inerchange
		double randomValue1, randomValue2;
		randomGen1 = new Random().nextInt(NUMERO_PARAMETROS);	// [0, 12)
		randomGen2 = new Random().nextInt(NUMERO_PARAMETROS);	// [0, 12)
		
		// Si resulta que hemos cogido el mismo gen dentro del cromosoma del individuo
		// volvemos a coger otro gen aleatorio, hasta que sea distinto del primer gen escogido
		while(randomGen1 == randomGen2) {
			System.out.println("Genes coinciden. Cogiendo otro...");
			randomGen2 = new Random().nextInt(NUMERO_PARAMETROS);	// [0, 12)
		}
		
		randomValue1 = array.get(randomGen1);
		array.set(randomGen1, array.get(randomGen2));
		array.set(randomGen2, randomValue1);
		
		System.out.println("Array[" + randomGen1 + "] cambiado por array [" + randomGen2 + "] que vale " + array.get(randomGen2));
		System.out.println("Array[" + randomGen2 + "] cambiado por array [" + randomGen1 + "] que vale " + array.get(randomGen1));
		System.out.println("Array[" + randomGen1 + "] ahora vale " + array.get(randomGen1));
		System.out.println("Array[" + randomGen2 + "] ahora vale " + array.get(randomGen2));
	}

	/***
	 * From the entire chromosome, a subset of genes is chosen and their
	 * values are scrambled or shuffled randomly
	 * Array cannot contain all 0 values <---- BE AWARE OF THIS
	 * @param start low endpoint (inclusive) of the subList
	 * @param end high endpoint (exclusive) of the subList
	 */
	public void scrambleMutation(int start, int end) {
		Collections.shuffle(array.subList(start, end + 1));
	}
	
	/***
	 * From the entire chromosome, a subset of genes is chosen and
	 * we merely invert the entire string in the subset
	 * Array cannot contain all 0 values <---- BE AWARE OF THIS
	 */
	public void inversionMutation(int start, int end) {
		Collections.reverse(array.subList(start, end + 1));
	}
	
	public ArrayList<Double> getArray() {
		return array;
	}
}
package pacman.entries.pacman;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Gene {
    // --- variables:

    /**
     * Fitness evaluates to how "close" the current gene is to the
     * optimal solution (i.e. contains only 1s in its chromosome)
     * A gene with higher fitness value from another signifies that
     * it has more 1s in its chromosome, and is thus a better solution
     * While it is common that fitness is a floating point between 0..1
     * this is not necessary: the only constraint is that a better solution
     * must have a strictly higher fitness than a worse solution
     */
    private float mFitness;
    /**
     * The chromosome contains only integers 0 or 1 (we choose to avoid
     * using a boolean type to make computations easier)
     */
    private double mChromosome[];
    
    // We are going to give each individual an ID
    private static int cont = 0;
    private int id = 0;
    
    // --- functions:
    /**
     * Allocates memory for the mChromosome array and initializes any other data, such as fitness
     * We chose to use a constant variable as the chromosome size, but it can also be
     * passed as a variable in the constructor
     */
    public Gene() {
        // Allocate memory for the chromosome array
        mChromosome = new double[GeneticAlgorithm.CHROMOSOME_SIZE];
        
        // Initialize fitness
        mFitness = 0.f;
        
        // Initialize ID
        id = cont;
        cont++;
    }

    /**
     * Randomizes the numbers on the mChromosome array to values from 0 to 150.0
     */
    public void randomizeChromosome(){
    	double randomValue = 0;
    	
		for(int i = 0; i < GeneticAlgorithm.CHROMOSOME_SIZE; i++) {
			randomValue = new Random().nextDouble() * 150.0;	// TODO quitar número mágico que indica el rango (150.0)
			mChromosome[i] = BigDecimal.valueOf(randomValue).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();	// TODO quitar el número mágico que indica el rango
		}
		
		System.out.println("Id: " + id);
		System.out.println(Arrays.toString(mChromosome));
    }

    /**
     * Creates a number of offspring by combining (using crossover) the current
     * Gene's chromosome with another Gene's chromosome.
     * Usually two parents will produce an equal amount of offpsring, although
     * in other reproduction strategies the number of offspring produced depends
     * on the fitness of the parents.
     * @param other: the other parent we want to create offpsring from
     * @return Array of Gene offspring (default length of array is 2).
     * These offspring will need to be added to the next generation.
     */
    public ArrayList<Gene> reproduce(Gene other, int crossoverMethod){
        ArrayList<Gene> result = new ArrayList<Gene>();	// This array list will contain the children created
        // Initialize result array
        Gene gene1 = new Gene();
        Gene gene2 = new Gene();
        result.add(gene1);
        result.add(gene2);
        
    	switch(crossoverMethod) {
    	case 0:
    		System.out.println("Metodo de cruce: Uniforme");
    		result = uniformCrossover(other, result);
    		break;
    	case 1:
    		System.out.println("Metodo de cruce: Plano");
    		result = flatCrossover(other, result);
    		break;
    	case 2:
    		System.out.println("Metodo de cruce: Aritmético");
    		result = arithmeticCrossover(other, result, 0.4);
    		break;
    	default:
    		System.out.println("Metodo de cruce no disponible. Inténtelo de nuevo");
    		break;
    	}
        
        return result;
    }
    
    /***
     * Uniform crossover. We don't divie de chromosome into segments, rather
     * we treat each gene separately. We use a mask that contains 0s and 1s
     * and we choose a gene from one parent or another base on the value of
     * the mask for the position of the gene in the chromosome
     * @param other the second parent
     * @param result array list where store the children created
     * @return the children created
     */
    public ArrayList<Gene> uniformCrossover(Gene other, ArrayList<Gene> result) {
        // Create mask
        int mask[] = new int[GeneticAlgorithm.CHROMOSOME_SIZE];
        for(int i = 0; i < GeneticAlgorithm.CHROMOSOME_SIZE; i++) {
        	mask[i] = new Random().nextBoolean() ? 1 : 0;
        }
        
        /////// System.out.println("Mascara de cruce uniforme: " + Arrays.toString(mask));
        
        // Create children
        for(int i = 0; i < GeneticAlgorithm.CHROMOSOME_SIZE; i++) {
        	if(mask[i] == 0) {
        		result.get(0).setChromosomeElement(i, mChromosome[i]);
        		result.get(1).setChromosomeElement(i, other.getChromosomeElement(i));
        	}else if(mask[i] == 1) {
        		result.get(0).setChromosomeElement(i, other.getChromosomeElement(i));
        		result.get(1).setChromosomeElement(i, mChromosome[i]);
        	}
        }
        
        /////// System.out.println("id1 result: " + result.get(0).getId());
        /////// System.out.println("id1 chromosome result: " + result.get(0).getPhenotype());
        
        /////// System.out.println("id2 result: " + result.get(1).getId());
        /////// System.out.println("id1 chromosome result: " + result.get(1).getPhenotype());
        
        return result;
    }

    /***
     * Flat crossover. By default, it create two children.
     * @param other the second parent
     * @param result array list where store the children created
     * @return the children created
     */
    public ArrayList<Gene> flatCrossover(Gene other, ArrayList<Gene> result) {
    	ArrayList<Double> range = new ArrayList<Double>();	// El rango de valores que pueden coger los genes de los hijos para cada posición
    	double randomValue = 0;
    	
    	// Child 1
    	for(int i = 0; i < mChromosome.length; i++) {
    		range.add(mChromosome[i]);
    		range.add(other.getChromosomeElement(i));
    		Collections.sort(range);	// Para asegurarnos de que el mínimo está situado en la primera posición del array y el máximo en la segunda
    		/////// System.out.println("Gene " + i + " entre [" + range.get(0) + ", " + range.get(1) + "]");
    		randomValue = new Random().nextDouble();	// [low, high)
    		randomValue = range.get(0) + (randomValue * (range.get(1) - range.get(0)));
    		/////// System.out.println("Gene " + i + ": " + randomValue);
    		randomValue = BigDecimal.valueOf(randomValue).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();	// Redondeamos a dos decimales
    		result.get(0).setChromosomeElement(i, randomValue);
    	}
    	
    	System.out.println();
    	
    	// Child 2
    	for(int i = 0; i < mChromosome.length; i++) {
    		/////// System.out.println("Gene " + i + " entre [" + range.get(0) + ", " + range.get(1) + "]");
    		randomValue = new Random().nextDouble();	// [low, high)
    		randomValue = range.get(0) + (randomValue * (range.get(1) - range.get(0)));
    		/////// System.out.println("Gene " + i + ": " + randomValue);
    		randomValue = BigDecimal.valueOf(randomValue).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    		result.get(1).setChromosomeElement(i, randomValue);
    	}
    	
    	/////// System.out.println("Hijo 1: " + result.get(0).getPhenotype());
    	/////// System.out.println("Hijo 2: " + result.get(1).getPhenotype());
    	
    	return result;
    }
    
    /***
     * Arithmetic crossover. It takes a weighted average of the two parents by using
     * the following formula: Child 1: r * x + (1 - r) * y, Child 2: r * y + (1-r) * x
     * If r = 0.5, both the children will be identical
     * @param other the second parent
     * @param result array list where store the children created
     * @param r a coefficient indicating the weight to use in the weight
     * @return the children created
     */
    public ArrayList<Gene> arithmeticCrossover(Gene other, ArrayList<Gene> result, double r) {
    	double gene0 = 0.0;
    	double gene1 = 0.0;
    	double x = 0.0;
    	double y = 0.0;
    	
    	for(int i = 0; i < mChromosome.length; i++) {
    		// Get gene value by position
    		gene0 = mChromosome[i];
    		gene1 = other.getChromosomeElement(i);
    		
    		/////// System.out.println("x[" + i + "] = " + r + " * " + gene0 + " + " + (1.0 - r) + " * " + gene1);
    		/////// System.out.println("y[" + i + "] = " + r + " * " + gene1 + " + " + (1.0 - r) + " * " + gene0);
    		
    		// Perform arithmetic
    		x = r * gene0 + (1.0 - r) * gene1;
    		y = r * gene1 + (1.0 - r) * gene0;
    		
    		// Round numbers
    		x = BigDecimal.valueOf(x).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    		y = BigDecimal.valueOf(y).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    		
    		// Define offspring values
    		result.get(0).setChromosomeElement(i, x);
    		result.get(1).setChromosomeElement(i, y);
    	}
    	
    	return result;
    }
    
    /**
     * Mutates a gene using inversion, random mutation or other methods.
     * This function is called after the mutation chance is rolled.
     * Mutation can occur (depending on the designer's wishes) to a parent
     * before reproduction takes place, an offspring at the time it is created,
     * or (more often) on a gene which will not produce any offspring afterwards.
     */
    public void mutate(int mutationOperator){
    	ArrayList<Integer> range = new ArrayList<Integer>();	// For using in scramble and inversion mutation
    	
    	switch(mutationOperator) {
		case 0:
			System.out.println("Operador de mutacion: Random Full Resetting");
			randomFullResetting();
			break;
		case 1:
			System.out.println("Operador de mutacion: Random Gene Resetting");
			randomGeneResetting();
			break;
		case 2:
			System.out.println("Operador de mutacion: Swap Mutation");
			swapMutation();
			break;
		case 3:
			System.out.println("Operador de mutacion: Scramble Mutation");
			range.add(new Random().nextInt(GeneticAlgorithm.CHROMOSOME_SIZE));	// [0, 12)
			range.add(new Random().nextInt(GeneticAlgorithm.CHROMOSOME_SIZE));	// [0, 12)
			
			// If start and end indices are the same, try to choose a different end index
			while(range.get(0) == range.get(1)) {
				/////// System.out.println("Genes coinciden. Cogiendo otro...");
				range.set(1, new Random().nextInt(GeneticAlgorithm.CHROMOSOME_SIZE));	// [0, 12)
			}
			
			// Make sure that the start index if positioned before end index
			Collections.sort(range);
			
			// Scramble!
			scrambleMutation(range.get(0), range.get(1));
			
			break;
		case 4:
			System.out.println("Operador de mutacion: Inversion Mutation");
			range.clear();
			range.add(new Random().nextInt(GeneticAlgorithm.CHROMOSOME_SIZE));	// [0, 12)
			range.add(new Random().nextInt(GeneticAlgorithm.CHROMOSOME_SIZE));	// [0, 12)
			
			// If start and end of range are the same, try to choose a different end for the range
			while(range.get(0) == range.get(1)) {
				/////// System.out.println("Genes coinciden. Cogiendo otro...");
				range.set(1, new Random().nextInt(GeneticAlgorithm.CHROMOSOME_SIZE));	// [0, 12)
			}
			
			// Make sure that the start index if positioned before end index
			Collections.sort(range);
			
			// Inverse!
			inversionMutation(range.get(0), range.get(1));
			
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
		double randomValue;
		for(int i = 0; i < GeneticAlgorithm.CHROMOSOME_SIZE; i++) {
			randomValue = new Random().nextDouble() * GeneticAlgorithm.RANGE_MAX;
			////// System.out.println("----> Random: " + randomValue);
			mChromosome[i] = randomValue;
		}
	}
	
	/***
	 * A valid random value is assigned to a randomly chosen gene
	 */
	public void randomGeneResetting() {
		int randomGeneIndex;	// The gen index to reset
		double randomValue;	// The value to reset the gen to
		
		randomGeneIndex = new Random().nextInt(GeneticAlgorithm.CHROMOSOME_SIZE);	// [0, 12)
		randomValue = new Random().nextDouble() * GeneticAlgorithm.RANGE_MAX;	// [0.0, 150.0]
		
		/////// System.out.println("Cambiando array[" + randomGeneIndex + "] de " + mChromosome[randomGeneIndex] + " a " + randomValue);
		
		// Set the gen to its new value
		mChromosome[randomGeneIndex] = randomValue;
	}
	
	/***
	 * We select two positions on the chromosome randomly
	 * and interchange their values
	 */
	public void swapMutation() {
		int randomGen1Index, randomGen2Index;	// The genes whose values we are going to interchange
		double randomValue1, randomValue2;
		randomGen1Index = new Random().nextInt(GeneticAlgorithm.CHROMOSOME_SIZE);	// [0, 12)
		randomGen2Index = new Random().nextInt(GeneticAlgorithm.CHROMOSOME_SIZE);	// [0, 12)
		
		// If start and end of range are the same, try to choose a different end for the range
		while(randomGen1Index == randomGen2Index) {
			System.out.println("Genes coinciden. Cogiendo otro...");
			randomGen2Index = new Random().nextInt(GeneticAlgorithm.CHROMOSOME_SIZE);	// [0, 12)
		}
		
		// Interchange values
		randomValue1 = mChromosome[randomGen1Index];
		mChromosome[randomGen1Index] = mChromosome[randomGen2Index];
		mChromosome[randomGen2Index] = randomValue1;
		
		/////// System.out.println("[" + randomGen1Index + "] que vale " + mChromosome[randomGen2Index] + " cambiado por [" + randomGen2Index + "] que vale " + mChromosome[randomGen1Index]);
		/////// System.out.println("[" + randomGen2Index + "] que vale " + mChromosome[randomGen1Index] + " cambiado por [" + randomGen1Index + "] que vale " + mChromosome[randomGen2Index]);
	}

	/***
	 * From the entire chromosome, a subset of genes is chosen and their
	 * values are scrambled or shuffled randomly
	 * @param start low endpoint (inclusive) of the subList
	 * @param end high endpoint (exclusive) of the subList
	 */
	public void scrambleMutation(int start, int end) {
		/////// System.out.println("Start index: " + start + ", end index: " + end);
		
		// Select sublist from the chromosome array
		double array[] = Arrays.copyOfRange(mChromosome, start, end + 1);
		
		/////// System.out.println("Sublista a barajar: " + Arrays.toString(array));
		
		// Convert the array to an array list
		// For convenience to use in the shuffle method
		ArrayList<Double> result = new ArrayList<Double>();
		result.clear();
		for(int i = 0; i < array.length; i++) {
			result.add(array[i]);
		}
		
		// Shuffle the sublist!
		Collections.shuffle(result);
		
		/////// System.out.println("Sublista barajada: " + result);
		
		// Replace old values by scrambled values in the chromosome
		int j = 0;
		for(int i = start; j < result.size(); i++) {
			mChromosome[i] = result.get(j);
			j++;
		}
	}
	
	/***
	 * From the entire chromosome, a subset of genes is chosen and
	 * we merely reverse the entire string in the subset
	 * Array cannot contain all 0 values <---- BE AWARE OF THIS
	 */
	public void inversionMutation(int start, int end) {
		/////// System.out.println("Start index: " + start + ", end index: " + end);
		
		// Select sublist from the chromosome array
		double array[] = Arrays.copyOfRange(mChromosome, start, end + 1);
		
		/////// System.out.println("Sublista a invertir: " + Arrays.toString(array));
		
		// Convert the array to an array list
		// For convenience to use in the inversion method
		ArrayList<Double> result = new ArrayList<Double>();
		result.clear();
		for(int i = 0; i < array.length; i++) {
			result.add(array[i]);
		}
		
		// Reverse the sublist!
		Collections.reverse(result);
		
		/////// System.out.println("Sublista invertida: " + result);
		
		// Replace old values by reversed values in the chromosome
		int j = 0;
		for(int i = start; j < result.size(); i++) {
			mChromosome[i] = result.get(j);
			j++;
		}
	}
	
	public int getId() {
    	return id;
    }
    
    public void setId(int id) {
    	this.id = id;
    }
    
    
    /**
     * Sets the fitness, after it is evaluated in the GeneticAlgorithm class.
     * @param value: the fitness value to be set
     */
    public void setFitness(float value) { mFitness = value; }
    
    /**
     * @return the gene's fitness value
     */
    public float getFitness() { return mFitness; }
    
    /**
     * Returns the element at position <b>index</b> of the mChromosome array
     * @param index: the position on the array of the element we want to access
     * @return the value of the element we want to access (0 or 1)
     */
    public double getChromosomeElement(int index){ return mChromosome[index]; }

    /**
     * Sets a <b>value</b> to the element at position <b>index</b> of the mChromosome array
     * @param index: the position on the array of the element we want to access
     * @param value: the value we want to set at position <b>index</b> of the mChromosome array (0 or 1)
     */
    public void setChromosomeElement(int index, double value){ mChromosome[index]=value; }
    
    /**
     * Returns the size of the chromosome (as provided in the Gene constructor)
     * @return the size of the mChromosome array
     */
    public int getChromosomeSize() { return mChromosome.length; }
    
    /**
     * Corresponds the chromosome encoding to the phenotype, which is a representation
     * that can be read, tested and evaluated by the main program.
     * @return a String with a length equal to the chromosome size, composed of A's
     * at the positions where the chromosome is 1 and a's at the posiitons
     * where the chromosme is 0
     */
    public String getPhenotype() { return Arrays.toString(mChromosome); }
    
    
}
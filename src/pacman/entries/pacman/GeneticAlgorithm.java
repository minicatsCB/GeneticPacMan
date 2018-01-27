package pacman.entries.pacman;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;        // for generating random numbers

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.math.BigDecimal;
import java.util.ArrayList;     // arrayLists are more versatile than arrays
import java.util.Arrays;


/**
 * Genetic Algorithm sample class <br/>
 * <b>The goal of this GA sample is to maximize the number of capital letters in a String</b> <br/>
 * compile using "javac GeneticAlgorithm.java" <br/>
 * test using "java GeneticAlgorithm" <br/>
 *
 * @author A.Liapis
 */

public class GeneticAlgorithm extends Controller<MOVE>{
    // --- constants
    static int CHROMOSOME_SIZE=12;	// Number of parameters to try in the Fuzzy Logic Controler
    static int POPULATION_SIZE=10;	// Number of individuals or genes
    static double RANGE_MIN=0.0;	// Minimum range for the parameters
    static double RANGE_MAX=150.0;	// Maximum range for the parameters


    // --- variables:

    /**
     * The population contains an ArrayList of genes (the choice of arrayList over
     * a simple array is due to extra functionalities of the arrayList, such as sorting)
     */
    ArrayList<Gene> mPopulation;
    
    //////// For use in Executor.java
    double avgFitness=0.f;
    double minFitness=Float.POSITIVE_INFINITY;
	double maxFitness=Float.NEGATIVE_INFINITY;
    int generationCount = 0;
    
    private ArrayList<Double> range = new ArrayList<Double>();
    private MyFuzzyPacMan myFuzzyPacMan;

    // --- functions:

    /**
     * Creates the starting population of Gene classes, whose chromosome contents are random
     * @param size: The size of the popultion is passed as an argument from the main class
     */
    public GeneticAlgorithm(int size, double rangeMinimum, double rangeMaximum){
    	System.out.println("Constructor GeneticAlgorithm");
    	
        mPopulation = new ArrayList<Gene>();
        for(int i = 0; i < size; i++){
            Gene entry = new Gene();
            entry.randomizeChromosome();
            mPopulation.add(entry);
        }
		RANGE_MIN = rangeMinimum;
		RANGE_MAX = rangeMaximum;
    }
    
	public MOVE getMove(Game game, long timeDue) {
		return myFuzzyPacMan.getMove(game, timeDue);
	}
    
	public void setGeneIndex(int index) {
		myFuzzyPacMan = new MyFuzzyPacMan(RANGE_MIN, RANGE_MAX, mPopulation.get(index));
	}
	
    /**
     * For all members of the population, runs a heuristic that evaluates their fitness
     * based on their phenotype. The evaluation of this problem's phenotype is fairly simple,
     * and can be done in a straightforward manner. In other cases, such as agent
     * behavior, the phenotype may need to be used in a full simulation before getting
     * evaluated (e.g based on its performance)
     */
    public void evaluateGeneration(){
        for(int i = 0; i < mPopulation.size(); i++){
        	System.out.println("Fitness del individuo " + i + ": " + mPopulation.get(i).getFitness());
        }
    }
    /**
     * With each gene's fitness as a guide, chooses which genes should mate and produce offspring.
     * The offspring are added to the population, replacing the previous generation's Genes either
     * partially or completely. The population size, however, should always remain the same.
     * If you want to use mutation, this function is where any mutation chances are rolled and mutation takes place.
     */
    public void produceNextGeneration(int selectionMethod, int crossoverMethod){
    	System.out.println("\nProducir nueva generacion");
    	System.out.println("Seleccionar progenitores");
    	ArrayList<Gene> parents = null;	// The parents
    	ArrayList<Gene> offspring = null;	// The children
    	
    	switch(selectionMethod) {
    	case 0:
    		System.out.println("Metodo de seleccion: Ranking");
    		parents = rankSelection();
    		break;
    	case 1:
    		System.out.println("Metodo de seleccion: Torneo");
    		parents = tournamentSelection();
        	System.out.println("Padres: ");
        	printGeneArrayList(parents);
    		break;
    	case 2:
    		System.out.println("Metodo de seleccion: Ruleta");
    		break;
    	default:
    		System.out.println("Metodo de seleccion no disponible. Inténtelo de nuevo");
    		break;
    	}
    	
		offspring = parents.get(0).reproduce(parents.get(1), crossoverMethod);
		/////// printGeneArrayList(offspring);
    	
    	// Mutate
		// In this case, we mutate first or second child based on a random 0 or 1, respectively
		int randomChild = new Random().nextBoolean() ? 1 : 0;
		/////// System.out.println("Hijos antes de la mutación: ");
		/////// printGeneArrayList(offspring);
		if(randomChild == 0) {
			System.out.println("Mutando hijo 1");
			offspring.get(0).mutate(4);
		}else if(randomChild == 1) {
			System.out.println("Mutando hijo 2");
			offspring.get(1).mutate(4);
		}
		
		/////// System.out.println("Hijos después de la mutación: ");
		/////// printGeneArrayList(offspring);
		
    	replace(parents, offspring);
    	
    	System.out.println("\nPoblacion actual (cromosoma/fitness)");
    	
    	printGeneArrayList(mPopulation);
    	
    }
    
    /***
     * Rank selection method. First, it ranks the population by fitness and then
     * the two individuals with the best fitness are selected
     * Note: this is not the intended implementation. I pretended to do a rank selection and
     * then a roulette wheel selection
     * @return the selected parents
     */
    public ArrayList<Gene> rankSelection() {
    	ArrayList<Gene> parents = new ArrayList<Gene>();	// The two genes to be selected
    	
    	// Make a copy because we want to keep the original one
    	ArrayList<Gene> mPopulationCopy = new ArrayList<Gene>(mPopulation);
    	
    	// Sort by fitness in descending order
    	Collections.sort(mPopulationCopy, new Comparator<Gene>() {
			@Override
			public int compare(Gene g1, Gene g2) {
				return (int) (g2.getFitness() - g1.getFitness());
			}
		});
    	
    	/////// printGeneArrayList(mPopulationCopy);
    	
    	// Choose the two first genes to be parents that are identified by their ID
    	int parent1Index = mPopulationCopy.get(0).getId();
    	int parent2Index = mPopulationCopy.get(1).getId();
    	
    	/////// System.out.println("Id padre 1: " + parent1Index);
    	/////// System.out.println("Id padre 2: " + parent2Index);
    	
    	parents.add(mPopulation.get(parent1Index));
    	parents.add(mPopulation.get(parent2Index));
    	
    	return parents;
    }
    
    /***
     * Tournament selection method. It involves running several "tournaments" among
     * a few individuals chosen at random form the population. The winner of each
     * tournament (the one with the best fitness) is selected for crossover
     * @return the selected parents
     */
    public ArrayList<Gene> tournamentSelection() {
    	int randomPick;	// The number of random n genes to select the genes from
    	ArrayList<Gene> randomList;	// The random n genes to select the genes from
    	int id1, id2;	// The id of the genes to be selected
    	ArrayList<Gene> parents = new ArrayList<Gene>();	// The two genes to be selected
    	
    	randomPick = new Random().nextInt(mPopulation.size() - 1) + 1;	// [1, 10)
    	randomList = pickNRandom(mPopulation, randomPick);
    	id1 = selectGeneWithBestFitness(randomList).getId();
    	
    	/////// System.out.println("Random pick (" + randomPick + "):");
    	/////// printGeneArrayList(randomList);
    	/////// System.out.println("Fitness id1: " + mPopulation.get(id1).getFitness());
    	/////// System.out.println("id1:" + id1);
    	
    	// If the second parent is the same as the previous one, try to choose a different one again   	
    	do {
    		randomPick = new Random().nextInt(mPopulation.size() - 1) + 1;
    		randomList = pickNRandom(mPopulation, randomPick);
    		id2 = selectGeneWithBestFitness(randomList).getId();
    		
    		if(id2 == id1) {
        		/////// System.out.println("Mismos padres. Repetir selección del segundo progenitor");
        	}
    	}while(id2 == id1);
    	
    	/////// System.out.println("Random pick (" + randomPick + "):");
    	/////// printGeneArrayList(randomList);
    	/////// System.out.println("Fintess id1: " + mPopulation.get(id2).getFitness());
    	
    	parents.add(mPopulation.get(id1));
    	parents.add(mPopulation.get(id2));
    	
    	return parents;
    }
    
    /***
     * Pick a pair of genes to be parents
     * @param list the population
     * @param n number of elements to pick
     * @return a list with the picked elements
     */
    public ArrayList<Gene> pickNRandom(ArrayList<Gene> list, int n) {
    	ArrayList<Gene> copy = new ArrayList<Gene>(list);
        Collections.shuffle(copy);
        return new ArrayList<Gene>(copy.subList(0, n));
    }
    
    /***
     * Select the individual with best fitness in the population
     * @param list the list of individuals
     * @return
     */
    public Gene selectGeneWithBestFitness(ArrayList<Gene> list) {
    	Gene geneWithBestFitness = list.get(0);
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getFitness() > geneWithBestFitness.getFitness()){
            	geneWithBestFitness = list.get(i);
            }
        }
                
    	return geneWithBestFitness;
    }


    /***
     * Round all values in the chromosome to 2 decimals
     * @param list the list to round its values
     * @return the list with all its values rounded
     */
    public ArrayList<Gene> roundGeneArrayListNumbers(ArrayList<Gene> list) {
    	double roundedValue = 0;
    	for(int i = 0; i < list.size(); i++) {
    		for(int j = 0; j < list.get(0).getChromosomeSize(); j++) {
    			roundedValue = BigDecimal.valueOf(list.get(i).getChromosomeElement(j)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    			list.get(i).setChromosomeElement(j, roundedValue);
    		}
    	}
    	
    	return list;
    }
    
    /***
     * Replace the parents by the children
     * @param parents the parents to replace
     * @param offspring the children that are going to replace their parents
     */
    public void replace(ArrayList<Gene> parents, ArrayList<Gene> offspring) {
    	// We recognize the parents by their ID
    	/*
    	System.out.println("Padres antes de reemplazar: ");
    	System.out.println(mPopulation.get(parents.get(0).getId()).getPhenotype());
    	System.out.println(mPopulation.get(parents.get(0).getId()).getId());
    	System.out.println(mPopulation.get(parents.get(1).getId()).getPhenotype());
    	System.out.println(mPopulation.get(parents.get(1).getId()).getId());
    	*/
    	
    	// Keep the parents ID
    	mPopulation.set(parents.get(0).getId(), offspring.get(0));
    	mPopulation.get(parents.get(0).getId()).setId(parents.get(0).getId());
    	mPopulation.set(parents.get(1).getId(), offspring.get(1));
    	mPopulation.get(parents.get(1).getId()).setId(parents.get(1).getId());
    	
    	/*
    	System.out.println("Padres después de reemplazar: ");
    	System.out.println(mPopulation.get(parents.get(0).getId()).getPhenotype());
    	System.out.println(mPopulation.get(parents.get(0).getId()).getId());
    	System.out.println(mPopulation.get(parents.get(1).getId()).getPhenotype());
    	System.out.println(mPopulation.get(parents.get(1).getId()).getId());
    	*/
    }
    
    /***
     * Print the phenotype and fitness of a list of individuals
     * It rounds the number before printing (for convenience while reading)
     * @param list the list of individuals
     */
    public void printGeneArrayList(ArrayList<Gene> list) {
    	list = roundGeneArrayListNumbers(list);
    	
    	for(int i = 0; i < list.size(); i++) {
    		System.out.println(list.get(i).getPhenotype());
    		System.out.println(list.get(i).getFitness());
    	}
    }    
   
    
    // accessors
    /**
     * @return the size of the population
     */
    public int size(){
    	return mPopulation.size();
    }
    
    /**
     * Returns the Gene at position <b>index</b> of the mPopulation arrayList
     * @param index: the position in the population of the Gene we want to retrieve
     * @return the Gene at position <b>index</b> of the mPopulation arrayList
     */
    public Gene getGene(int index){
    	return mPopulation.get(index);
    }
    
    public void setGene(float fitness) {
    	this.mPopulation.get(generationCount).setFitness(fitness);
    }
    
    public double getAverageFitness() {
    	return avgFitness;
    }
    
    public void addAverageFitness(double avgFitness) {
    	this.avgFitness += avgFitness;
    }
	
	public int getGenerationCount() {
		return generationCount;
	}
	
	public void setGenerationCount(int generationCount) {
		this.generationCount = generationCount;
	}
    
    public int getPopulationSize() {
    	return POPULATION_SIZE;
    }
};


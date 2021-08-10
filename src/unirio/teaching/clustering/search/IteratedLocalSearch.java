package unirio.teaching.clustering.search;

import unirio.teaching.clustering.model.Project;
import unirio.teaching.clustering.model.ProjectClass;
import unirio.teaching.clustering.search.constructive.ConstrutiveAbstract;
import unirio.teaching.clustering.search.model.ClusterMetrics;
import unirio.teaching.clustering.search.model.ModuleDependencyGraph;
import unirio.teaching.clustering.search.utils.PseudoRandom;

/**
 * Iterated Local Search for the next release problem
 */
public class IteratedLocalSearch
{
	private int PERTURBATION_SIZE = 5;
	
	/**
	 * Constructive method
	 */
	private ConstrutiveAbstract constructor;

	/**
	 * Number of classes in the project
	 */
	private int classCount;
	
	/**
	 * Dependency graph for the project
	 */
	private ModuleDependencyGraph mdg;
	
	/**
	 * Number of fitness evaluations available in the budget
	 */
	private int maxEvaluations;

	/**
	 * Number of fitness evaluations executed
	 */
	private int evaluationsConsumed;

	/**
	 * Number of iterations to best solution
	 */
	private int iterationBestFound;
	
	/**
	 * MQ calculator
	 */
	private ClusterMetrics metrics;

	/**
	 * Fitness of the best solution
	 */
	private double bestFitness; 

	/**
	 * Initializes the ILS search process
	 */
	public IteratedLocalSearch(ConstrutiveAbstract constructor, Project project, int maxEvaluations) throws Exception
	{
		this.constructor = constructor;
		this.classCount = project.getClassCount();
		this.mdg = buildGraph(project, this.classCount);
		this.maxEvaluations = maxEvaluations;
		this.evaluationsConsumed = 0;
		this.iterationBestFound = 0;
		this.metrics = null;
		this.bestFitness = -1_000_000_000_000.0;
	}
	
	/**
	 * Builds the project's dependency graph from its representation
	 */
	private ModuleDependencyGraph buildGraph(Project project, int classCount) throws Exception
	{
		ModuleDependencyGraph mdg = new ModuleDependencyGraph(classCount);
		
		for (int i = 0; i < classCount; i++)
		{
			ProjectClass _class = project.getClassIndex(i);

			for (int j = 0; j < _class.getDependencyCount(); j++)
			{
				String targetName = _class.getDependencyIndex(j).getElementName();
				int classIndex = project.getClassIndex(targetName);
				
//				if (classIndex == -1)
//					throw new Exception ("Class not registered in project: " + targetName);
				
				if (classIndex != -1)
				mdg.addModuleDependency(i, classIndex, 1);
			}
		}
		
		return mdg;
	}

	/**
	 * Returns the number of evaluations consumed during the search
	 */
	public int getEvaluationsConsumed()
	{
		return evaluationsConsumed;
	}

	/**
	 * Returns the maximum number of evaluations to be consumed
	 */
	public int getMaximumEvaluations()
	{
		return maxEvaluations;
	}
	
	/**
	 * Returns the iteration on which the best solution was found
	 */
	public int getIterationBestFound()
	{
		return iterationBestFound;
	}

	/**
	 * Returns the best fitness found
	 */
	public double getBestFitness()
	{
		return bestFitness;
	}
	
	/**
	 * Main loop of the algorithm
	 */
	public int[] execute() throws Exception
	{
		int[] bestSolution = constructor.createSolution(mdg);
		this.metrics = new ClusterMetrics(mdg, bestSolution);
		
		this.bestFitness = metrics.calculateMQ();
		++evaluationsConsumed;

		localSearch(this.metrics);
		double fitness = metrics.calculateMQ();
		++evaluationsConsumed;
		
		if (fitness > bestFitness)
		{
			bestSolution = this.metrics.cloneSolution();
			this.bestFitness = fitness;
		}
		
		while (getEvaluationsConsumed() < getMaximumEvaluations())
		{
			applyPerturbation(this.metrics, PERTURBATION_SIZE);
			
			localSearch(this.metrics);
			fitness = metrics.calculateMQ();
			++evaluationsConsumed;
			
			if (fitness > this.bestFitness)
			{
				bestSolution = this.metrics.cloneSolution();
				this.bestFitness = fitness;
			}
		}

		return bestSolution;
	}

	/**
	 * Applies the perturbation operator upon a solution
	 */
	private void applyPerturbation(ClusterMetrics calculator, int amount)
	{
		for (int i = 0; i < amount; i++)
		{
			int source = PseudoRandom.randInt(0, classCount-1);
			int target = PseudoRandom.randInt(0, classCount-1);
			calculator.makeMoviment(source, target);
		}
	}

	/**
	 * Performs the local search starting from a given solution
	 */
	private void localSearch(ClusterMetrics calculator)
	{
		while (visitNeighbors(calculator))
			;
	}

	/**
	 * Runs a neighborhood visit starting from a given solution
	 */
	private boolean visitNeighbors(ClusterMetrics calculator)
	{
		if (evaluationsConsumed > maxEvaluations)
			return false;

		int source = -1;
		int target = -1;
		double bestGain = Double.NEGATIVE_INFINITY;
		
		for (int i = 0; i < classCount; i++)
		{
			int newPackage = PseudoRandom.randInt(0, classCount-1);
			double gain = calculator.calculateMovimentDelta(i, newPackage);
			++evaluationsConsumed;

			if (gain > bestGain)
			{
				source = i;
				target = newPackage;
				bestGain = gain;
			}
		}

		if (bestGain > 0)
		{
			calculator.makeMoviment(source, target);
			return true;
		}
		
		return false;
	}
}
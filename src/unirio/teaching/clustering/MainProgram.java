package unirio.teaching.clustering;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import unirio.teaching.clustering.model.Project;
import unirio.teaching.clustering.reader.CDAReader;
import unirio.teaching.clustering.search.IteratedLocalSearch;
import unirio.teaching.clustering.search.constructive.ConstrutiveAbstract;
import unirio.teaching.clustering.search.constructive.ConstrutiveAglomerativeMQ;

public class MainProgram
{
	private static String BASE_DIRECTORY = "C:\\Users\\User\\Desktop\\Codigos\\ils-clustering\\data\\clustering\\odem-marlon";
	
	public static final void main(String[] args) throws Exception
	{
		File file = new File(BASE_DIRECTORY);
		DecimalFormat df4 = new DecimalFormat("0.0000");
		
		ConstrutiveAbstract constructor = new ConstrutiveAglomerativeMQ();
		//ConstrutiveAbstract constructor = new ConstrutiveRandom();

        for (String projectName : file.list()) 
        {
        	long startTimestamp = System.currentTimeMillis();
        	
    		//DependencyReader reader = new DependencyReader();
        	CDAReader reader = new CDAReader();
    		Project project = reader.load(BASE_DIRECTORY + "\\" + projectName);

    		IteratedLocalSearch ils = new IteratedLocalSearch(constructor, project, 100_000);
    		int[] solution = ils.execute();
    		
    		long finishTimestamp = System.currentTimeMillis();
    		long seconds = (finishTimestamp - startTimestamp);
    		
    		long memory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
    		System.out.println(padLeft(projectName, 20) + " " + padRight("" + project.getClassCount(), 10) + padRight("" + countClusters(solution), 10) + " " + padRight(df4.format(ils.getBestFitness()), 10) + " " + padRight("" + seconds, 10) + " ms " + padRight("" + memory, 10) + " MB");
        }
	}

	private static int countClusters(int[] solution)
	{
		List<Integer> clusters = new ArrayList<Integer>();
		
		for (int i = 0; i < solution.length; i++)
		{
			int cluster = solution[i];
			
			if (!clusters.contains(cluster))
				clusters.add(cluster);
		}
		
		return clusters.size();
	}

	public static String padLeft(String s, int length) 
	{
	    StringBuilder sb = new StringBuilder();
	    sb.append(s);
	    
	    while (sb.length() < length)
	        sb.append(' ');
	    
	    return sb.toString();
	}

	public static String padRight(String s, int length) 
	{
	    StringBuilder sb = new StringBuilder();
	    
	    while (sb.length() < length - s.length())
	        sb.append(' ');
	    
	    sb.append(s);
	    return sb.toString();
	}
}
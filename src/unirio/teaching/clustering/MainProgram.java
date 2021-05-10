package unirio.teaching.clustering;

import java.io.File;
import java.text.DecimalFormat;

import unirio.teaching.clustering.model.Project;
import unirio.teaching.clustering.reader.DependencyReader;
import unirio.teaching.clustering.search.IteratedLocalSearch;
import unirio.teaching.clustering.search.constructive.ConstrutiveAbstract;
import unirio.teaching.clustering.search.constructive.ConstrutiveRandom;

public class MainProgram
{
	private static String BASE_DIRECTORY = "C:\\Users\\User\\Desktop\\Codigos\\ILS\\data\\clustering\\";
	
	public static final void main(String[] args) throws Exception
	{
		File file = new File(BASE_DIRECTORY + "dependencies");
		DecimalFormat df4 = new DecimalFormat("0.0000");
		
		//ConstrutiveAbstract constructor = new ConstrutiveAglomerativeMQ();
		ConstrutiveAbstract constructor = new ConstrutiveRandom();

        for (String projectName : file.list()) 
        {
        	long startTimestamp = System.currentTimeMillis();
        	
    		DependencyReader reader = new DependencyReader();
    		Project project = reader.load(BASE_DIRECTORY + "dependencies\\" + projectName);

    		IteratedLocalSearch ils = new IteratedLocalSearch(constructor, project, 100_000);
    		ils.execute();
    		
    		long finishTimestamp = System.currentTimeMillis();
    		long seconds = (finishTimestamp - startTimestamp);
    		
    		long memory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
    		System.out.println(padLeft(projectName, 20) + " " + padRight("" + project.getClassCount(), 10) + " " + padRight(df4.format(ils.getBestFitness()), 10) + " " + padRight("" + seconds, 10) + " ms " + padRight("" + memory, 10) + " MB");
    		//System.out.println(projectName + ";" + project.getClassCount() + ";" + df4.format(ils.getBestFitness()));
        }
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
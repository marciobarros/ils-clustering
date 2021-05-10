package unirio.teaching.clustering.reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import unirio.teaching.clustering.model.Project;
import unirio.teaching.clustering.model.ProjectClass;

public class DependencyReader
{
	public Project load(String filename) throws FileNotFoundException
	{
		FileInputStream fis = new FileInputStream(filename);
		Scanner sc = new Scanner(fis);
		Project project = new Project(filename);

		while (sc.hasNextLine())
		{
			String line = sc.nextLine();
			
			if (line.length() > 0)
			{
				int index = line.indexOf(' ');
				
				if (index != -1)
				{
					String firstClass = line.substring(0, index).trim();
					String secondClass = line.substring(index).trim();
					
					int firstIndex = project.getClassIndex(firstClass);
					
					if (firstIndex == -1)
					{
						project.addClass(new ProjectClass(firstClass));
						firstIndex = project.getClassCount() - 1;
					}
					
					int secondIndex = project.getClassIndex(secondClass);
					
					if (secondIndex == -1)
					{
						project.addClass(new ProjectClass(secondClass));
//						secondIndex = project.getClassCount() - 1;
					}
					
					ProjectClass firstProjectClass = project.getClassIndex(firstIndex);
					firstProjectClass.addDependency(secondClass);
				}
			}
		}

		sc.close();
		return project;
	}
}
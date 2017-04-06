import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import bwapi.UnitType;

public class BuildOrderParser {
	private BuildOrder bo;
	String build;

	public BuildOrderParser(){
		build = "";
		bo = new BuildOrder();
		read("src/data/4pool.txt");
		
	}
	public BuildOrder getBuild()
	{
		return this.bo;
	}
	public void read(String filename)
	{
		try {
			
			Scanner scan = new Scanner(new FileReader(filename));
		
			while(scan.hasNextLine())
			{
				String n = scan.nextLine();
				if (n.length() == 0) continue;

				String[] parts = n.split("[\\s]+");
				UnitType unit = parseString(parts[1]);
				int supply = Integer.parseInt(parts[0]);
				bo.insert(supply, unit);
				
			}
			scan.close();
		}
		catch (IOException e){
			System.err.println("Err: IOEXCEPTION");
		}
		
	}
	
	public UnitType parseString(String s)
	{
		s = s.toLowerCase();
		switch (s) {
		case "pool":
			return UnitType.Zerg_Spawning_Pool;
		case "hatchery":
			return UnitType.Zerg_Hatchery;
		case "zergling":
			return UnitType.Zerg_Zergling;
		case "extractor":
			return UnitType.Zerg_Extractor;
		case "overlord":
			return UnitType.Zerg_Overlord;
		case "hydra":
			return UnitType.Zerg_Hydralisk;
		case "drone":
			return UnitType.Zerg_Drone;
		default:
			return UnitType.None;
		}
	}
}

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import bwapi.UnitType;

public class BuildOrder {
	private HashMap<Integer, ArrayList<UnitType>> buildOrder;
	UnitType[] units;
	public BuildOrder()
	{
		buildOrder = new HashMap<Integer, ArrayList<UnitType>>();
		
		
	}
	
	public void insert(Integer supply, UnitType unit)
	{
		ArrayList<UnitType> current = null;
		if (buildOrder == null)
		{
			buildOrder = new HashMap<Integer, ArrayList<UnitType>>();
			current = new ArrayList<UnitType>();
		}
		
		if (buildOrder.containsKey(supply))
		{
			current = buildOrder.get(supply);
			current.add(unit);
			buildOrder.put(supply, current);
		}
		else if (!buildOrder.containsKey(supply))
		{
			current = new ArrayList<UnitType>();
			current.add(unit);
			buildOrder.put(supply, current);
		}
	}
	public void print()
	{
		Iterator<Integer> itr = buildOrder.keySet().iterator();
		while( itr.hasNext())
		{
			System.out.println(itr + "," + buildOrder.get(itr).toString());
			
			itr.next();
			itr.remove();
			
		}
	}
	
	
	
	
	

}

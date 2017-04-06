import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import bwapi.Color;
import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.PositionOrUnit;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;

public class UnitHandler {
	Player self;
	Game game;
	UnitType uType;
	BuildUtils buildController;
	BuildOrder buildOrder;
	int newSupply;
	private HashSet<Position> enemyBuildingMemory;
	private HashMap<UnitType, Integer> armyCount;
	public UnitHandler(Player s, Game g, BuildUtils b, BuildOrder bo)
	{
		this.self = s;
		this.game = g;
		this.buildController = b;
		this.buildOrder = bo;
		this.enemyBuildingMemory = new HashSet<Position>();
		//this.newSupply = b.getSupply();
		armyCount = new HashMap<UnitType, Integer>();
	}

	public void run()
	{
		//if (this.newSupply == 0) this.newSupply = this.buildController.getSupply();

        //iterate through my units
/*        for (Unit myUnit : self.getUnits()) {
            units.append(myUnit.getType()).append(" ").append(myUnit.getTilePosition()).append("\n");
            
            //if there's enough minerals, train an SCV
            if (myUnit.getType() == UnitType.Zerg_Larva && self.minerals() >= 50)
            {
                myUnit.morph(UnitType.Zerg_Drone);//myUnit.train(UnitType.Terran_SCV);
            }
            
            //if it's a worker and it's idle, send it to the closest mineral patch

        }*/

		for (Unit u : self.getUnits())
		{
			uType = u.getType();
			build(u);
			harvest(u);
			scout(u);
			enemyVision();
			/*if (u.getType() == UnitType.Zerg_Larva && self.minerals() >= 50)
            {
                u.morph(UnitType.Zerg_Drone);//myUnit.train(UnitType.Terran_SCV);
            }*/
		}
		
	}
	public void scout(Unit u)
	{
		if (uType == UnitType.Zerg_Overlord)
		{
			if (u.isIdle())
			{
				searchBases(u, 0);
			}
			else
			{
				game.drawLineMap(u.getPosition(), u.getOrderTargetPosition(), Color.Red);
			}
		}
		if (uType == UnitType.Zerg_Zergling)
		{
			if (enemyBuildingMemory.size() == 0)
			{
				//System.out.println("Empty building memory");
				if (u.isIdle())
				{
					searchBases(u, 1);
				}
			}
			else
			{
				if (u.isIdle())
				{
					game.drawLineMap(u.getPosition(), u.getOrderTargetPosition(), Color.Green);
					for (Position p : enemyBuildingMemory) 
					{
						PositionOrUnit pos = new PositionOrUnit(p);
						u.attack(pos);
						break;
					}
				}

/*				List<Unit> units = game.getUnitsInRadius(u.getPosition(), 4);
				enemyBuildingMemory.
				u.attack(units.get(0).getPosition());
*/				
			}
		}
		
	}
	public void build(Unit u)
	{
		/*
		 * Check build Order
		 * if supply count >= hashmap key
		 *  --> add into building que
		 * 
		 */
		if (u.getType().isWorker())
		{
			if (!self.hasUnitTypeRequirement(UnitType.Zerg_Spawning_Pool) && checkCost(UnitType.Zerg_Spawning_Pool))
			{
				if (buildController.que.contains(UnitType.Zerg_Spawning_Pool))
				{
					TilePosition toBuild = buildController.getBuildTile(u,UnitType.Zerg_Spawning_Pool, u.getTilePosition());
					u.build(UnitType.Zerg_Spawning_Pool, toBuild);
					buildController.que.remove(UnitType.Zerg_Spawning_Pool);
				}
				/*	if (u.canMorph(UnitType.Zerg_Spawning_Pool)){
					
				}*/
			}
		}
		if (uType == UnitType.Zerg_Larva)
		{
			//System.out.println(buildController.getSupply());
			if (buildController.getSupply() <= 3  
			&& checkCost(UnitType.Zerg_Overlord)
			&& buildController.unitQue.contains(UnitType.Zerg_Overlord))
			{
				u.morph(UnitType.Zerg_Overlord);
				buildController.unitQue.remove(UnitType.Zerg_Overlord);
			}
			if (buildController.unitQue.contains(UnitType.Zerg_Zergling) 
			    && self.hasUnitTypeRequirement(UnitType.Zerg_Spawning_Pool) 
				&& checkCost(UnitType.Zerg_Zergling))
			{
				u.morph(UnitType.Zerg_Zergling);
			}
			else if (checkCost(UnitType.Zerg_Drone))
			{
				u.morph(UnitType.Zerg_Drone);
			}
		}

		
	}
	public void harvest(Unit u)
	{
        if (u.getType().isWorker() && u.isIdle()) {
            Unit closestMineral = null;
            
            //find the closest mineral
            for (Unit neutralUnit : game.neutral().getUnits()) {
                if (neutralUnit.getType().isMineralField()) {
                    if (closestMineral == null || u.getDistance(neutralUnit) < u.getDistance(closestMineral)) {
                        closestMineral = neutralUnit;
                    }
                }
            }

            //if a mineral patch was found, send the worker to gather it
            if (closestMineral != null) {
                u.gather(closestMineral, false);
            }
        }
	}
	public void searchBases(Unit u, int attack)
	{
		for(BaseLocation baseLocation : BWTA.getStartLocations())
		{
			Position dest = baseLocation.getPosition();
			if (!game.isExplored(dest.toTilePosition()))
		    {
				if (attack == 1)
				{
					u.attack(dest);
				}
				else
				{
					u.move(dest);
				}
    		 
    		 break;
		    }
		 //System.out.println("Base location #" + (++i) + ". Printing location's region polygon:");
		 //will move along the edges of the base
/*	        	 for(Position position : baseLocation.getRegion().getPolygon().getPoints())
    	 {

    	 }*/
    	if (!u.isIdle()) break;
	   }	
		
	}
	public void enemyVision()
	{
		//always loop over all currently visible enemy units (even though this set is usually empty)
		for (Unit u : game.enemy().getUnits()) {
		  //if this unit is in fact a building
		  if (u.getType().isBuilding()) {
		    //check if we have it's position in memory and add it if we don't
		    if (!enemyBuildingMemory.contains(u.getPosition())) enemyBuildingMemory.add(u.getPosition());
		  }
		}
		 
		//loop over all the positions that we remember
		for (Position p : enemyBuildingMemory) {
		  // compute the TilePosition corresponding to our remembered Position p
		  TilePosition tileCorrespondingToP = new TilePosition(p.getX()/32 , p.getY()/32);
		 
		  //if that tile is currently visible to us...
		  if (game.isVisible(tileCorrespondingToP)) {
		    //loop over all the visible enemy buildings and find out if at least 
		    //one of them is still at that remembered position 
		    boolean buildingStillThere = false;
		    for (Unit u : game.enemy().getUnits()) {
		      if ((u.getType().isBuilding()) && (u.getPosition() == p)) {
		        buildingStillThere = true;
		        break;
		      }
		    }
		 
		    //if there is no more any building, remove that position from our memory 
		    if (buildingStillThere == false) {
		      enemyBuildingMemory.remove(p);
		      break;
		    }
		  }
		}
	}
	public boolean checkCost(UnitType u)
	{		
		return self.minerals() >= u.mineralPrice() 
			&& self.gas() >= u.gasPrice();
	}
}

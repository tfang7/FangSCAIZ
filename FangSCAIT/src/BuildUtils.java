import java.util.ArrayList;

import bwapi.Game;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class BuildUtils {
	ArrayList<Position> baseLocations;
	private Game game;
	ArrayList<UnitType> que;
	ArrayList<UnitType> unitQue;
	private int supplyCount;
	public BuildUtils(Game g)
	{
		this.game = g;
		que = new ArrayList<UnitType>();
		unitQue = new ArrayList<UnitType>();
	}
	
	public int getSupply()
	{
		return supplyCount;
	}
	public void setSupply(int s)
	{
		this.supplyCount = s;
	}
	public void addBase(Position p)
	{
		baseLocations.add(p);
	}
	public TilePosition getBuildTile(Unit builder, UnitType buildingType, TilePosition aroundTile) {
		  TilePosition ret = null;
		  int maxDist = 3;
		  int stopDist = 40;
		 
		  // Refinery, Assimilator, Extractor
		  if (buildingType.isRefinery()) {
		    for (Unit n : game.neutral().getUnits()) {
		      if ((n.getType() == UnitType.Resource_Vespene_Geyser) && 
		          ( Math.abs(n.getTilePosition().getX() - aroundTile.getX()) < stopDist ) &&
		          ( Math.abs(n.getTilePosition().getY() - aroundTile.getY()) < stopDist ) ) {
		              return n.getTilePosition();
		      }
		    }
		  }
		 
		  while ((maxDist < stopDist) && (ret == null)) {
		    for (int i=aroundTile.getX()-maxDist; i<=aroundTile.getX()+maxDist; i++) {
		      for (int j=aroundTile.getY()-maxDist; j<=aroundTile.getY()+maxDist; j++) {
		        if (game.canBuildHere(new TilePosition(i,j), buildingType)) {
		          // units that are blocking the tile
		          boolean unitsInWay = false;
		          for (Unit u : game.getAllUnits()) {
		            if (u.getID() == builder.getID()) continue;
		            if ((Math.abs(u.getTilePosition().getX()-i) < 4)  && (Math.abs(u.getTilePosition().getY()-j) < 4)) {
		              unitsInWay = true;
		            }
		          }
		          if (!unitsInWay) {
		            return new TilePosition(i, j);
		          }
		          // creep for Zerg
		          if (buildingType.requiresCreep()) {
		            boolean creepMissing = false;
		            for (int k=i; k<=i+buildingType.tileWidth(); k++) {
		              for (int l=j; l<=j+buildingType.tileHeight(); l++) {
		                if (!game.hasCreep(k, l)) creepMissing = true;
		                break;
		              }
		            }
		            if (creepMissing) continue; 
		          }
		        }
		      }
		    }
		    maxDist += 2;
		  }
		 
		  if (ret == null) game.printf("Unable to find suitable build position for "+buildingType.toString());
		  return ret;
		}
}

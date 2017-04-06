import bwapi.Color;
import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;

public class TerrainHandler {
	private Game game;
	private Player self;
	Position[] basePerimeter;
	public TerrainHandler(Game g, Player s){
		this.game = g;
		this.self = s;
		init();
	}
	public void init()
	{
		int i = 0;
		BaseLocation start =  BWTA.getStartLocation(self);
    	for(Position position :  start.getRegion().getPolygon().getPoints())
    	{
    		if (basePerimeter == null) 
    		{
    			basePerimeter = new Position[start.getRegion().getPolygon().getPoints().size()];
    		}
    		basePerimeter[i] = position;
    		i++;
    		//System.out.print(i + ":" + position.toString());
	    }
    }
		
	public void run(){
        for (Chokepoint cp : BWTA.getChokepoints() ) 
        {
        	Position f = cp.getSides().first;
        	Position s = cp.getSides().second;
        	game.drawLineMap(f, s, Color.Blue);
        }
        if (basePerimeter != null && basePerimeter.length >= 2)
        {
        	for (int i = 0; i < basePerimeter.length-1; i++)
        	{
        		game.drawLineMap(basePerimeter[i], basePerimeter[i+1], Color.Blue);
        	}
        }
	}
}

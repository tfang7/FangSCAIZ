import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;

public class FangSCAIZ extends DefaultBWListener {

    private Mirror mirror = new Mirror();

    private Game game;

    private Player self;
    private UnitHandler UnitController;
    private TerrainHandler TerrainController;
    private BuildUtils BuildingControls;
    public void run() {
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }

    @Override
    public void onUnitCreate(Unit unit)
    {
    	if (BuildingControls.que.contains(unit.getType()))
    	{
    		System.out.println("Building Finished: removing from build que");
    		BuildingControls.que.remove(unit.getType());
    	}
/*    	if (BuildingControls.unitQue.contains(unit.getType()))
    	{
    		BuildingControls.unitQue.remove(unit.getType());
    	}*/
        int supply = ( (self.supplyTotal()/2) - (self.supplyUsed()/2));
        BuildingControls.setSupply(supply);
        //System.out.println("Supply Left " + supply);
       // System.out.println("New unit discovered " + unit.getType());
    }
    
    @Override
    public void onUnitDestroy(Unit unit)
    {
        BuildingControls.setSupply( ( (self.supplyTotal()/2) - (self.supplyUsed()/2)));
    }

    @Override
    public void onStart() {
        game = mirror.getGame();
        self = game.self();
        game.setLocalSpeed(10);
        BuildingControls = new BuildUtils(game);
        BuildOrderParser bo = new BuildOrderParser();
        //Use BWTA to analyze map
        //This may take a few minutes if the map is processed first time!
        BWTA.readMap();
        BWTA.analyze();
        TerrainController = new TerrainHandler(game, self);
        int supply = ((self.supplyTotal()/2) - (self.supplyUsed()/2));
        BuildingControls.setSupply(supply);
        UnitController = new UnitHandler(self, game, BuildingControls, bo.getBuild());

        //game.setTextSize(10);
        //game.drawTextScreen(10, 10, "Playing as " + self.getName() + " - " + self.getRace());
        //StringBuilder units = new StringBuilder("My units:\n");
        //draw my units on screen
        //game.drawTextScreen(10, 25, units.toString());



    }

    @Override
    public void onFrame() {

        process();

    }
    private void process()
    {
    	 TerrainController.run();
         UnitController.run();
    }
    public static void main(String[] args) {
        new FangSCAIZ().run();
    }
}
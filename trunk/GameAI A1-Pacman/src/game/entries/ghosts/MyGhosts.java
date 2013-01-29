package game.entries.ghosts;

import java.awt.Color;

import game.controllers.GhostController;
import game.core.Game;
import game.core.GameView;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getActions() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.ghosts.mypackage).
 */
public class MyGhosts implements GhostController
{
	
	public GhostAI GhostCurrentAI = new GhostScatterState();
	public GhostAI GhostPrevAI = new GhostScatterState();
	public long stateChangeCurrentTime= System.currentTimeMillis();
	public long stateChangeShiftTime = stateChangeCurrentTime + (7*1000);
	public int numberOfScatterOccured = 0;
	public int numberOfChaseOccured=0;
	public final int NUM_SCATTERS_PER_LEVEL = 4;
	public final int SCATTER = 0;
	public final int CHASE = 1;
	public final int FRIGHTENED = 2;
	public GhostScatterState ScatterHandler = new GhostScatterState();
	public GhostChaseState ChaseHandler = new GhostChaseState();
	public GhostFrightenedState FrightHandler = new GhostFrightenedState();
	public int currentGlobalState = SCATTER;
	public boolean Debugging = false;
	int X = 0;
	int Y = 1;
	int[] ghostTarget = {0,0};
	
	public MyGhosts(boolean debugging){
		Debugging = debugging;
	}
	
	public int[] GhostState = {SCATTER, SCATTER, SCATTER, SCATTER};
		//RED, PINK, ORANGE, BLUE
	
	//Place your game logic here to play the game as the ghosts
	public int[] getActions(Game game,long timeDue)
	{
		if(game.getLevelTime()<=10){
			numberOfScatterOccured = 0;
			numberOfChaseOccured = 0;
			stateChangeShiftTime = System.currentTimeMillis();
		}
		
		int[] directions=new int[Game.NUM_GHOSTS];
		long stateChangeTimer = stateChangeShiftTime - System.currentTimeMillis();
		if(stateChangeTimer<0 && numberOfScatterOccured<NUM_SCATTERS_PER_LEVEL){
			int nextStateTimeinSec = 0;
			if(currentGlobalState == SCATTER){
				System.out.println("Global State is Chase");
				currentGlobalState = CHASE;
				numberOfScatterOccured ++;
				if(game.getCurLevel() == 1){
					nextStateTimeinSec = 20;
				}
				else if(game.getCurLevel()<5){
					if(numberOfChaseOccured<2){
						nextStateTimeinSec = 20;
					}
					else{
						nextStateTimeinSec = 1033;
					}
				}
				else{
					if(numberOfChaseOccured<2){
						nextStateTimeinSec = 20;
					}
					else{
						nextStateTimeinSec = 1037;
					}
				}
				 
				
			}
			else{
				currentGlobalState = SCATTER;
				System.out.println("Global State is Scatter");
				
				if(game.getCurLevel() == 1){
					if(numberOfScatterOccured<2){
						nextStateTimeinSec = 7;
					}
					else{
						nextStateTimeinSec = 5;
					}
				}
				else if(game.getCurLevel()<5){
					if(numberOfScatterOccured<2){
						nextStateTimeinSec = 7;
					}
					else if(numberOfScatterOccured == 2){
						
						nextStateTimeinSec = 5;
					}
					else{
						nextStateTimeinSec = 1;
					}
				}
				else{
					if(numberOfScatterOccured<2){
						nextStateTimeinSec = 5;
					}
					else if(numberOfScatterOccured == 2){
						
						nextStateTimeinSec = 5;
					}
					else{
						nextStateTimeinSec = 1;
					}
				
				}
				numberOfChaseOccured++;
			}
			stateChangeShiftTime = System.currentTimeMillis() + (nextStateTimeinSec*1000);
//			for(int i=0; i< directions.length; i++){
//				directions[i]= game.getReverse(game.getCurGhostDir(i));
//				System.out.println("Reverse Ghost" + i + " : " + directions[i] );
//			}
			return directions;
		}
		
		
		for(int i =0; i<Game.NUM_GHOSTS; i++){
			if(game.getEdibleTime(i)>0){
//				if(GhostState[i]!=FRIGHTENED){
//					directions[i] = game.getReverse(game.getCurGhostDir(i));
//					GhostState[i]=FRIGHTENED;
//					continue;
//				}
				GhostState[i]=FRIGHTENED;
				//System.out.println("In fright for " + i + "at edible time" + game.getEdibleTime(i));
			}
			else if(GhostState[i]!= currentGlobalState){
				
				GhostState[i] = currentGlobalState;
			}
	
			
			if(GhostState[i]== SCATTER){
				ghostTarget = ScatterHandler.execute(i, game, timeDue);
			}
			if(GhostState[i]== CHASE){
				ghostTarget= ChaseHandler.execute(i, game, timeDue);
			}
			
			if(GhostState[i]!= FRIGHTENED){
				Color color;
				if(i==0){
					color = Color.RED;
				}
				else if(i==1){
					color = Color.PINK;
				}
				else if (i==2){
					color = Color.ORANGE;
				}
				else{
					color = Color.CYAN;
				}
				GameView.addLines(game, color, game.getX(game.getCurGhostLoc(i)), game.getY(game.getCurGhostLoc(i)), ghostTarget[X], ghostTarget[Y]);
			}
			int chosenDirection = -1;
			if(game.ghostRequiresAction(i) && GhostState[i] != FRIGHTENED){
				int[] possibleDirections = game.getPossibleGhostDirs(i);
				//double[] distancesFromPossibleMoves = new double[possibleDirections.length];
				double chosenDirectionDistance = 100000;
				boolean equalPathsCheck = false;
				for(int j=0; j<possibleDirections.length;j++){
					int directionNodeNum = game.getNeighbour(game.getCurGhostLoc(i), possibleDirections[j]);
					double distanceBetweenNeighbor = MyGhosts.getEuclideanDistance(game.getX(directionNodeNum), game.getY(directionNodeNum),ghostTarget[X],ghostTarget[Y]);
					
					if(Double.compare(distanceBetweenNeighbor, chosenDirectionDistance) < 0){
						equalPathsCheck = false;
						chosenDirectionDistance = distanceBetweenNeighbor;
						chosenDirection = possibleDirections[j];
					}
					else if(Double.compare(distanceBetweenNeighbor, chosenDirectionDistance) == 0){
						equalPathsCheck = true;
					}
					
				}
				if (equalPathsCheck){
					boolean leftPriority = false;
					for(int j=0; j<possibleDirections.length; i++){
						if(possibleDirections[j] == Game.UP){
							chosenDirection = Game.UP;
							break;
						}
						else if( possibleDirections[j] == Game.LEFT){
							chosenDirection = Game.LEFT;
							leftPriority = true;
						}
						else if(possibleDirections[j] == Game.DOWN  && !leftPriority){
							chosenDirection = Game.DOWN;
						}
						
					}
				}
				
			}
			else if(game.ghostRequiresAction(i) && GhostState[i] == FRIGHTENED){
				//System.out.println("Getting ghost "+ i + "fright dir");
				int[] ghostPossibleDirs = game.getPossibleGhostDirs(i);
				//System.out.println(ghostPossibleDirs.length);
				if(ghostPossibleDirs.length >0){
					
					int numOfPossibleDirs = Math.abs(Game.rnd.nextInt())%ghostPossibleDirs.length;
					//System.out.println(numOfPossibleDirs);
					if(numOfPossibleDirs < 0){
						chosenDirection = ghostPossibleDirs[numOfPossibleDirs];
					}
				}
			}
			directions[i]=chosenDirection;
		}

		return directions;

	}
	
	public static double getEuclideanDistance(int x1, int y1, int x2, int y2){
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2-y1), 2));
	}
	
	

}
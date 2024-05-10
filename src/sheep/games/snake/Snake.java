package sheep.games.snake;

import sheep.features.Feature;
import sheep.games.random.RandomCell;
import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;
import sheep.ui.Perform;
import sheep.ui.Prompt;
import sheep.ui.Tick;
import sheep.ui.UI;

import java.util.LinkedList;

/**
 * This is the snake game on a sheet
 */
public class Snake implements Tick, Feature {

    //setup
    private final Sheet sheet;
    private final RandomCell randomCell;

    //snake stuff
    private final int[][] directions = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
    private int[] currentDirection = directions[2];
    private final LinkedList<CellLocation> snakeBody = new LinkedList<>();
    private boolean ate = false;

    //game stuff
    private boolean running = false;
    private final Perform startGame = new Perform() {
        @Override
        public void perform(int row, int column, Prompt prompt) {
            running = true;

            //if no cell chosen start at 0,0
            if (!sheet.contains(new CellLocation(row, column))) {
                snakeBody.add(new CellLocation(0, 0));
            } else {
                snakeBody.add(new CellLocation(row, column));
            }
            sheet.update(snakeBody.getFirst().getRow(), snakeBody.getFirst().getColumn(), "1");
        }
    };

    /**
     * Constructor for the snake
     *
     * @param sheet          A sheet upon which the snake is to be played on
     * @param randomCell A random cell instance which is used to create a new berry
     *                       when one disappears on the sheet
     */
    public Snake(Sheet sheet, RandomCell randomCell) {
        this.sheet = sheet;
        this.randomCell = randomCell;
    }

    @Override
    public void register(UI ui) {
        ui.onTick(this);
        ui.addFeature("snake", "Start Snake", startGame);
        ui.onKey("w", "Move North", this.getMove(directions[0]));
        ui.onKey("a", "Move West", this.getMove(directions[1]));
        ui.onKey("s", "Move South", this.getMove(directions[2]));
        ui.onKey("d", "Move East", this.getMove(directions[3]));
    }

    @Override
    public boolean onTick(Prompt prompt) {
        if (!running) {
            return false;
        }

        if (!moveSnake()) {
            prompt.message("Game Over!");
            running = false;
        }

        //will be return true when changes are done on the board.
        return true;
    }

    /**
     * gets the move that the player plays
     *
     * @param direction direction that the move dictates going in
     * @return A new Move with the given direction
     */
    public Perform getMove(int[] direction) {
        return new Perform() {
            @Override
            public void perform(int row, int column, Prompt prompt) {
                if (!running) {
                    return;
                }
                currentDirection = direction;
            }
        };
    }

    /**
     * Passively moves the snake
     *
     * @return true, if the snake can be moved in that direction, false if the new location of
     * the body part is not valid (i.e. head hits body part, or head hits wall).
     */
    private boolean moveSnake() {

        CellLocation newLoc = new CellLocation(snakeBody.getFirst().getRow() + currentDirection[0],
                snakeBody.getFirst().getColumn() + currentDirection[1]);

        //ensuring new location is in the sheet, and that it is not another body part
        if (!sheet.contains(newLoc) || sheet.valueAt(newLoc).render().equals("1")) {
            return false;
        }

        //FIXME This needs to be fixed, basically as soon as the food is 'eaten' a new food tile should spawn
        //Grade-scope error, need to generate food after moved on to next cell
        //if the snake just ate something wait a tick before removing the last tile
        if (!ate) {
            sheet.update(snakeBody.getLast().getRow(), snakeBody.getLast().getColumn(), "");
            snakeBody.removeLast();
        } else {
            newFood();
        }

        if (!sheet.valueAt(newLoc).render().isEmpty()) {
            ate = true;
        }

        snakeBody.addFirst(newLoc);
        sheet.update(newLoc.getRow(), newLoc.getColumn(), "1");

        //if successful
        return true;
    }

    private void newFood() {
        //ensure the sheet has the current snake position rendered so that new food is not on snake
        for (CellLocation loc : snakeBody) {
            sheet.update(loc.getRow(), loc.getColumn(), "1");
        }

        CellLocation food = randomCell.pick();

        if (!sheet.valueAt(food).render().equals("1")) {
            sheet.update(food.getRow(), food.getColumn(), "2");
        }
        ate = false;
    }
}
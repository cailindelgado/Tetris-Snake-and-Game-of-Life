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
    private final SnakeG game;

    //game stuff
    private boolean running = false;
    private final Perform startGame = new Perform() {
        @Override
        public void perform(int row, int column, Prompt prompt) {
            running = true;

            game.startingPos(row, column);
        }
    };

    /**
     * Constructor for the snake
     *
     * @param sheet      A sheet upon which the snake is to be played on
     * @param randomCell A random cell instance which is used to create a new berry
     *                   when one disappears on the sheet
     */
    public Snake(Sheet sheet, RandomCell randomCell) {
        game = new SnakeG(sheet, randomCell);
    }

    @Override
    public void register(UI ui) {
        ui.onTick(this);
        ui.addFeature("snake", "Start Snake", startGame);
        ui.onKey("w", "Move North", this.getMove(new int[]{-1, 0}));
        ui.onKey("a", "Move West", this.getMove(new int[]{0, -1}));
        ui.onKey("s", "Move South", this.getMove(new int[]{1, 0}));
        ui.onKey("d", "Move East", this.getMove(new int[]{0, 1}));
    }

    @Override
    public boolean onTick(Prompt prompt) {
        if (!running) {
            return false;
        }

        if (!game.moveSnake()) {
            prompt.message("Game Over!");
            running = false;
        }

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
                game.setDirection(direction);
            }
        };
    }
}
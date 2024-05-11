package sheep.games.tetros;

import sheep.features.Feature;
import sheep.games.random.RandomTile;
import sheep.sheets.Sheet;
import sheep.ui.*;

/**
 * A game of Tetros that runs within the sheep.sheet package
 */
public class Tetros implements Tick, Feature {

    private final Sheet sheet;
    private final TetrosGame game;
    private boolean started = false;

    private final RandomTile randomTile;

    private final Perform gameStart = new Perform() {
        @Override
        public void perform(int row, int column, Prompt prompt) {
            started = true;
            game.drop();
        }
    };

    /**
     * The constructor class
     *
     * @param sheet      A sheet to run the game on.
     * @param randomTile A randomTile object to generate random tiles with
     */
    public Tetros(Sheet sheet, RandomTile randomTile) {
        this.sheet = sheet;
        this.randomTile = randomTile;
        game = new TetrosGame(sheet, randomTile);

    }

    @Override
    public void register(UI ui) {
        ui.onTick(this);
        ui.addFeature("tetros", "Start Tetros", gameStart);
        ui.onKey("a", "Move Left", this.getMove(-1, false));
        ui.onKey("d", "Move Right", this.getMove(1, false));
        ui.onKey("q", "Rotate Left", this.getMove(-1, true));
        ui.onKey("e", "Rotate Right", this.getMove(1, true));
        ui.onKey("s", "Drop", this.getMove(0, false));
    }

    @Override
    public boolean onTick(Prompt prompt) {
        if (!started) {
            return false;
        }

        if (game.dropTile()) {
            if (game.drop()) {
                prompt.message("Game Over!");
                started = false;
            }
        }

        game.clear();
        return true;
    }

    /**
     * gets the move that the player plays
     *
     * @param direction direction that the move dictates going in
     * @return A new Move with the given direction
     */
    public Perform getMove(int direction, boolean rotate) {
        return new Perform() {
            @Override
            public void perform(int row, int column, Prompt prompt) {
                if (!started) {
                    return;
                }
                if (rotate) {
                    game.flip(direction);
                } else {
                    game.shift(direction);
                }
            }
        };
    }
}
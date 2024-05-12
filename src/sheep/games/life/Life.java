package sheep.games.life;

import sheep.features.Feature;
import sheep.sheets.Sheet;
import sheep.ui.Perform;
import sheep.ui.Prompt;
import sheep.ui.Tick;
import sheep.ui.UI;

/**
 * The UI side of the Game of life
 */
public class Life implements Tick, Feature {

    private boolean running;
    private final LifeG game;

    private final Perform start = new Perform() {
        @Override
        public void perform(int row, int column, Prompt prompt) {
            running = true;
        }
    };
    private final Perform end = new Perform() {
        @Override
        public void perform(int row, int column, Prompt prompt) {
            running = false;
        }
    };

    /**
     * Constructor class for the game of life to begin
     *
     * @param sheet a sheet upon which the game of life is played on
     */
    public Life(Sheet sheet) {
        game = new LifeG(sheet);
    }

    @Override
    public boolean onTick(Prompt prompt) {
        if (!running) {
            return false;
        }

        game.updateSheet();

        return true;
    }

    @Override
    public void register(UI ui) {
        ui.onTick(this);
        ui.addFeature("gol-start", "Start Game of Life", start);
        ui.addFeature("gol-end", "End Game of Life", end);
    }
}
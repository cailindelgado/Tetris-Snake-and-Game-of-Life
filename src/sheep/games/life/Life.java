package sheep.games.life;

import sheep.features.Feature;
import sheep.games.tetros.Tetros;
import sheep.sheets.Sheet;
import sheep.ui.Prompt;
import sheep.ui.Tick;
import sheep.ui.UI;

/**
 * Conways game of life.
 * Rules:
 * The rules are as follows where a neighbour is any tile one step away horizontally, vertically, or
 * diagonally (giving each tile not on a boundary 8 possible neighbours):
 * 1. Any on cell with fewer than two on neighbours turns off .
 * 2. Any on cell with two or three on neighbours stays on.
 * 3. Any on cell with more than three on neighbours turns off .
 * 4. Any off cell with exactly three on neighbours turns on, otherwise it stays off.
 */
public class Life implements Tick, Feature {
    private Sheet sheet;

    /**
     * Constructor class for the game of life to begin
     *
     * @param sheet
     */
    public Life(Sheet sheet) {
        this.sheet = sheet;
    }

    //TODO fix this docstring

    /**
     * This method will be called when a 'tick' occurs
     *
     * @param prompt Provide a mechanism to interact with the user interface
     *               after a tick occurs, if required.
     * @return
     */
    @Override
    public boolean onTick(Prompt prompt) {

        return true;
//        if (!started) { return false; }
//
//        if (dropTile()) {
//            if (drop()) {
//                prompt.message("Game Over!");
//                started = false;
//            }
//        }
//        clear();
//        return true;
    }

    @Override
    public void register(UI ui) {
        ui.onTick(this);
//        ui.addFeature("life", "Start game of life", new Life.GameStart());

    }

    private void clear() {
        for (int row = 0; row < sheet.getRows(); row++) {
            for (int col = 0; col < sheet.getColumns(); col++) {
              
            }
        }
    }
}

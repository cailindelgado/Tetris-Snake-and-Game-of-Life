package sheep.games.life;

import sheep.expression.basic.Constant;
import sheep.features.Feature;
import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;
import sheep.ui.Perform;
import sheep.ui.Prompt;
import sheep.ui.Tick;
import sheep.ui.UI;

/**
 * Conways game of life.
 * Rules:
 * The rules are as follows where a neighbour is any tile one step away horizontally, vertically, or
 * diagonally (giving each tile not on a boundary 8 possible neighbours):
 * 1. Any cell that is on, and has less than 2 and more than 3 neighbors that are on, turns off.
 * 2. Any cell that is on, with 2 or 3 neighbors on, remains on.
 * 3. Any cell that is off, with exactly 3 neighbors that are on, turns on.
 */
public class Life implements Tick, Feature {

    private final Sheet sheet;
    private boolean running;

    //TODO ask tutors why checkstyle mad
    private final Perform Start = new Perform() {
        @Override
        public void perform(int row, int column, Prompt prompt) {
            running = true;
        }
    };
    private final Perform End = new Perform() {
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
        this.sheet = sheet;
    }

    @Override
    public boolean onTick(Prompt prompt) {
        if (!running) {
            return false;
        }

        updateBoard();

        return true;
    }

    @Override
    public void register(UI ui) {
        ui.onTick(this);
        ui.addFeature("lifeS", "Start Game of Life", Start);
        ui.addFeature("lifeE", "End Game of Life", End);

    }

    //FIXME replace the name with something more descriptive
    private void updateBoard() {
        for (int row = 0; row < sheet.getRows(); row++) {
            for (int col = 0; col < sheet.getColumns(); col++) {
                int neighborsOn = onCounter(row, col);

                //Any cell that is on, with 2 or 3 neighbors on, remains on.
                if (sheet.valueAt(new CellLocation(row, col)).render().equals("1")
                        && neighborsOn == 2) {
                    continue;
                }

                //Any cell that is on, and has less than 2 and more than 3 neighbors that are on,
                // turns off.
                if (neighborsOn < 2 || neighborsOn > 3) {
                    sheet.update(row, col, "");
                }

                //Any cell that is off, with exactly 3 neighbors that are on, turns on.
                if (neighborsOn == 3) {
                    sheet.update(row, col, "1");
                }
            }
        }
    }

    private int onCounter(int row, int column) {
        int neighborsOn = 0;
        int[][] surroundings = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 1},
                {1, -1}, {1, 0}, {1, 1}};

        for (int[] neighborCell : surroundings) {
            CellLocation location = new CellLocation(row + neighborCell[0],
                    column + neighborCell[1]);

            if (sheet.contains(location)
                    && sheet.valueAt(location).render().equals("1")) {
                neighborsOn++;
            }
        }
        //check if the given cell location is within an existing
        return neighborsOn;
    }
}
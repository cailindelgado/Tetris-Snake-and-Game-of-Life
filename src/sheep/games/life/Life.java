package sheep.games.life;

import sheep.features.Feature;
import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;
import sheep.ui.Perform;
import sheep.ui.Prompt;
import sheep.ui.Tick;
import sheep.ui.UI;

import java.util.ArrayList;

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
    private ArrayList<CellLocation> locationsOn = new ArrayList<>();
    private ArrayList<CellLocation> locationsOff = new ArrayList<>();

    //TODO ask tutors why checkstyle mad
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
        this.sheet = sheet;
    }

    @Override
    public boolean onTick(Prompt prompt) {
        if (!running) {
            return false;
        }

        updateSheet();

        return true;
    }

    @Override
    public void register(UI ui) {
        ui.onTick(this);
        ui.addFeature("gol-start", "Start Game of Life", start);
        ui.addFeature("gol-end", "End Game of Life", end);
    }

    /**
     * Updates each cell, depending on rules stated.
     */
    private void updateSheet() {
        for (int row = 0; row < sheet.getRows(); row++) {
            for (int col = 0; col < sheet.getColumns(); col++) {
                CellLocation location = new CellLocation(row, col);
                int neighborsOn = onCounter(row, col);

                //Any cell that is on, with 2 or 3 neighbors on, remains on.
                if (sheet.valueAt(location).render().equals("1")
                        && neighborsOn == 2) {
                    continue;
                }

                //Any cell that is on, and has less than 2 and more than 3 neighbors that are on,
                // turns off.
                if (neighborsOn < 2 || neighborsOn > 3) {
                    locationsOff.add(location);
                }

                //Any cell that is off, with exactly 3 neighbors that are on, turns on.
                if (neighborsOn == 3) {
                    if (sheet.valueAt(location).render().equals("1")) {
                        sheet.update(row, col, "1");
                    } else {
                        locationsOn.add(location);
                    }
                }
            }
        }
        addLocations();
    }

    /**
     * Counts how many cells around the given co-ordinates are on
     * @param row row location to check around
     * @param column column location to check around
     * @return the number of neighbors that are 'on'
     */
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

    /**
     * Updates the sheet by setting each cell location in locationsOn and locationsOff,
     * to on/off respectively within the sheet.
     */
    private void addLocations() {
        if (!locationsOn.isEmpty() && !locationsOff.isEmpty()) {
            for (CellLocation location : locationsOn) {
                sheet.update(location.getRow(), location.getColumn(), "1");
            }
            locationsOn.clear();
        }

        if (!locationsOff.isEmpty()) {
            for (CellLocation location : locationsOff) {
                sheet.update(location.getRow(), location.getColumn(), "");
            }
            locationsOff.clear();
        }
    }
}
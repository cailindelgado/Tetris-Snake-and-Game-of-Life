package sheep.games.life;

import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;

import java.util.ArrayList;

/**
 * The Game Logic of Conways game of life without using the UI
 */
public class LifeG {

    private final Sheet sheet;
    private final ArrayList<CellLocation> locationsOn = new ArrayList<>();
    private final ArrayList<CellLocation> locationsOff = new ArrayList<>();

    /**
     * Constructor class for the game of life
     *
     * @param sheet the sheet upon which the game of life is played on
     */
    public LifeG(Sheet sheet) {
        this.sheet = sheet;
    }

    /**
     * Updates each cell, depending on rules stated.
     */
    public void updateSheet() {
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
     *
     * @param row    row location to check around
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
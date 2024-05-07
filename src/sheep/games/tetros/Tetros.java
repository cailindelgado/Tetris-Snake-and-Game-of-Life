package sheep.games.tetros;

import sheep.expression.TypeError;
import sheep.expression.basic.Constant;
import sheep.expression.basic.Nothing;
import sheep.features.Feature;
import sheep.games.random.RandomTile;
import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;
import sheep.ui.*;

import java.util.*;

/**
 * A game of Tetros that runs within the sheep.sheet package
 */
public class Tetros implements Tick, Feature {
    // class variables

    private final Sheet sheet;
    private boolean started = false;

    private final RandomTile randomTile;
    private int fallingType = 1;
    private List<CellLocation> contents = new ArrayList<>();

    /**
     * The constructor class
     *
     * @param sheet      A sheet to run the game on.
     * @param randomTile A randomTile object to generate random tiles with
     */
    public Tetros(Sheet sheet, RandomTile randomTile) {
        this.sheet = sheet;
        this.randomTile = randomTile;
    }

    /**
     * This method overrides the register method in the Feature interface, and
     * sets moves which are used in the game.
     *
     * @param ui A user graphical user interface.
     */
    @Override
    public void register(UI ui) {
        ui.onTick(this);
        ui.addFeature("tetros", "Start Tetros", new GameStart());
        ui.onKey("a", "Move Left", new Move(-1));
        ui.onKey("d", "Move Right", this.getMove(1));
        ui.onKey("q", "Rotate Left", new Rotate(-1));
        ui.onKey("e", "Rotate Right", this.getRotate(1));
        ui.onKey("s", "Drop", this.getMove(0));
    }

    /**
     * this method checks if the given cell location is within the sheet,
     * or if the
     *
     * @param location A location in the sheet
     * @return true, iff location is within the sheet AND value at that location is empty, false otherwise
     */
    private boolean isStopper(CellLocation location) {
        if ((location.getRow() >= sheet.getRows())
                || (location.getColumn() >= sheet.getColumns())) {
            return true;
        }

        return !sheet.valueAt(location.getRow(), location.getColumn()).getContent().isEmpty();
    }

    /**
     * checks if the given locations are within the sheet
     *
     * @param locations a list of cell locations to check
     * @return true if all locations are within the sheet, false otherwise
     */
    public boolean inBounds(List<CellLocation> locations) {
        for (CellLocation location : locations) {
            if (!sheet.contains(location)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method drops a tile when called
     *
     * @return false after shifting a tile down one row, true if it cannot be done.
     */
    public boolean dropTile() {
        List<CellLocation> newContents = new ArrayList<>();
        for (CellLocation tile : contents) {
            newContents.add(new CellLocation(tile.getRow() + 1, tile.getColumn()));
        }
        unRender();
        for (CellLocation newLoc : newContents) {
            if (isStopper(newLoc)) { //newLoc is a new cell location
                reRender(contents);
                return true;
            }
        }
        reRender(newContents);
        this.contents = newContents;
        return false;
    }

    /**
     * This method runs the dropTile method until the tile hits the bottom of the sheet
     */
    public void fullDrop() {
        while (!dropTile()) { }
    }

    /**
     * using x, this horizontally translates the tile
     *
     * @param x 0 if drop, 1 if shift right, -1 if shift left.
     */
    public void shift(int x) {
        if (x == 0) {
            fullDrop();
        }
       List<CellLocation> newContents = new ArrayList<>();
        for (CellLocation tile : contents) {
            newContents.add(new CellLocation(tile.getRow(), tile.getColumn() + x));
        }
        if (!inBounds(newContents)) {
            return;
        }
        unRender();
        reRender(newContents);
        this.contents = newContents;
    }

    /**
     * Unrenders the tile when called
     */
    public void unRender() {
        for (CellLocation cell : contents) {
            try {
                sheet.update(cell, new Nothing());
            } catch (TypeError e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * renders a collection of cell locations in the sheet
     *
     * @param items a collection of locations within the sheet to be rendered
     */
    public void reRender(List<CellLocation> items) {
        for (CellLocation cell : items) {
            try { // renders each item in the list.
                sheet.update(cell, new Constant(fallingType));
            } catch (TypeError e) {
                throw new RuntimeException(e);
            }
        }
    }

    //TODO fix Docstring

    /**
     * drops a new tile into the sheet.
     * @return true
     */
    private boolean drop() {
        contents = new ArrayList<>();
        newPiece();
        for (CellLocation location : contents) {
            if (!sheet.valueAt(location).render().isEmpty()) {
                return true;
            }
        }
        reRender(contents);

        return false;
    }

    /**
     * Creates a random new tile to drop
     */
    private void newPiece() {
        int value = randomTile.pick();
        switch (value) {
            case 1 -> {
                contents.add(new CellLocation(0, 0));
                contents.add(new CellLocation(1, 0));
                contents.add(new CellLocation(2, 0));
                contents.add(new CellLocation(2, 1));
                fallingType = 7;
            }
            case 2 -> {
                contents.add(new CellLocation(0, 1));
                contents.add(new CellLocation(1, 1));
                contents.add(new CellLocation(2, 1));
                contents.add(new CellLocation(2, 0));
                fallingType = 5;
            }
            case 3 -> {
                contents.add(new CellLocation(0, 0));
                contents.add(new CellLocation(0, 1));
                contents.add(new CellLocation(0, 2));
                contents.add(new CellLocation(1, 1));
                fallingType = 8;
            }
            case 4 -> {
                contents.add(new CellLocation(0, 0));
                contents.add(new CellLocation(0, 1));
                contents.add(new CellLocation(1, 0));
                contents.add(new CellLocation(1, 1));
                fallingType = 3;
            }
            case 5 -> {
                contents.add(new CellLocation(0, 0));
                contents.add(new CellLocation(1, 0));
                contents.add(new CellLocation(2, 0));
                contents.add(new CellLocation(3, 0));
                fallingType = 6;
            }
            case 6 -> {
                contents.add(new CellLocation(0, 1));
                contents.add(new CellLocation(0, 2));
                contents.add(new CellLocation(1, 1));
                contents.add(new CellLocation(0, 1));
                fallingType = 2;
            }
            case 0 -> {
                contents.add(new CellLocation(0, 0));
                contents.add(new CellLocation(0, 1));
                contents.add(new CellLocation(1, 1));
                contents.add(new CellLocation(1, 2));
                fallingType = 4;
            }
        }
    }

    /**
     * Flips the tile in a given direction?
     *
     * @param direction a direction to flip the tile in
     */
    private void flip(int direction) {
        int x = 0;
        int y = 0;
        for (CellLocation cellLocation : contents) {
            x += cellLocation.getColumn();
            y += cellLocation.getRow();
        }
        x /= contents.size();
        y /= contents.size();
        List<CellLocation> newCells = new ArrayList<>();
        for (CellLocation location : contents) {
            int lx = x + ((y - location.getRow()) * direction);
            int ly = y + ((x - location.getColumn()) * direction);
            CellLocation replacement = new CellLocation(ly, lx);
            newCells.add(replacement);
        }
        if (!inBounds(newCells)) {
            return;
        }
        unRender();
        contents = newCells;
        reRender(newCells);
    }

    @Override
    public boolean onTick(Prompt prompt) {
        if (!started) {
            return false;
        }

        if (dropTile()) {
            if (drop()) {
                prompt.message("Game Over!");
                started = false;
            }
        }
        clear();
        return true;
    }

    //FIXME this needs to be fixed, as something is wrong with it.
    /**
     * This clears lines when a whole line is made, NOTE BUGGY
     */
    private void clear() {
        for (int row = sheet.getRows() - 1; row >= 0; row--) {
            boolean full = true;
            //go over each row from the bottom and check if it is full
            for (int col = 0; col < sheet.getColumns(); col++) {
                full = !(sheet.valueAt(row, col).getContent().isEmpty());
            }

            //!(row is full) -> (skip if code)
            if (full) {
                clearHelper(row);
                row = row + 1;
            }
        }
    }

    /**
     * Helps the clear() method by clearing each cell
     * @param row
     */
    private void clearHelper(int row) {
        for (int rowX = row; rowX > 0; rowX--) {
            for (int col = 0; col < sheet.getColumns(); col++) {
                try {
                    if (contents.contains(new CellLocation(rowX - 1, col))) {
                        continue;
                    }
                    sheet.update(new CellLocation(rowX, col),
                            sheet.valueAt(new CellLocation(rowX - 1, col)));
                } catch (TypeError e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    //TODO Fix this Docstring
    /**
     * Starts the game
     * @return
     */
    public Perform getStart() {
        return new GameStart();
    }

    /**
     * TODO class to be removed and shortened so that getStart() can work properly
     */
    public class GameStart implements Perform {
        @Override
        public void perform(int row, int column, Prompt prompt) {
            started = true;
            drop();
        }
    }

    //TODO Fix this Docstring
    /**
     * gets the move that the player plays
     * @param direction
     * @return
     */
    public Perform getMove(int direction) {
        return new Move(direction);
    }

    /**
     * TODO class to be removed and shortened so that getMove() can work properly
     */
    public class Move implements Perform {
        private final int direction;

        public Move(int direction) {
            this.direction = direction;
        }

        @Override
        public void perform(int row, int column, Prompt prompt) {
            if (!started) { //this method performs an action with a given prompt
                return;
            }
            shift(direction);
        }
    }

    //TODO Fix this Docstring
    /**
     * gets the tile rotation that the player designates
     * @param direction
     * @return
     */
    public Perform getRotate(int direction) {
        return new Rotate(direction);
    }

    /**
     * TODO class to be removed and shortened so that getStart() can work properly
     */
    public class Rotate implements Perform {
        private final int direction;

        public Rotate(int direction) {
            this.direction = direction;
        }

        @Override
        public void perform(int row, int column, Prompt prompt) {
            if (!started) {
                return;
            }
            flip(direction);
        }
    }
}
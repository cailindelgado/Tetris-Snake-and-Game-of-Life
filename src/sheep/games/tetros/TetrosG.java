package sheep.games.tetros;

import sheep.expression.TypeError;
import sheep.expression.basic.Constant;
import sheep.expression.basic.Nothing;
import sheep.games.random.RandomTile;
import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;

import java.util.*;

/**
 * The game logic (GL) of the Tetros game without the UI components
 */
public class TetrosG {

    private final Sheet sheet;
    private final RandomTile randomTile;

    private int fallingType = 1;
    private List<CellLocation> tile = new ArrayList<>();

    /**
     * constructor for the tetros game
     *
     * @param sheet a sheet upon the tetros game logic will work on
     */
    public TetrosG(Sheet sheet, RandomTile randomTile) {
        this.sheet = sheet;
        this.randomTile = randomTile;
    }

    /**
     * This method drops a tile when called
     *
     * @return false after shifting a tile down one row, true if it cannot be done.
     */
    public boolean dropTile() {
        List<CellLocation> newTile = new ArrayList<>();
        for (CellLocation tilePart : tile) {
            newTile.add(new CellLocation(tilePart.getRow() + 1, tilePart.getColumn()));
        }

        unRender();

        for (CellLocation newLoc : newTile) {
            if (stopper(newLoc)) { //newLoc is a new cell location
                render(tile);
                return true;
            }
        }

        render(newTile);
        tile = newTile;
        return false;
    }

    /**
     * Unrenders the tile when called
     */
    public void unRender() {
        for (CellLocation tilePart : tile) {
            try {
                sheet.update(tilePart, new Nothing());
            } catch (TypeError e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * renders a collection of cell locations in the sheet
     *
     * @param tile a collection of locations within the sheet to be rendered
     */
    public void render(List<CellLocation> tile) {
        for (CellLocation tilePart : tile) {
            try {
                sheet.update(tilePart, new Constant(fallingType));
            } catch (TypeError e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * drops a new tile into the sheet.
     *
     * @return true if the tile's spawn location is full
     */
    public boolean drop() {
        tile = new ArrayList<>();
        newPiece();
        for (CellLocation tilePart : tile) {
            if (!sheet.valueAt(tilePart).render().isEmpty()) {
                return true;
            }
        }
        render(tile);

        return false;
    }

    /**
     * clears a whole row, when it has been completely filled
     */
    public void clear() {
        for (int row = sheet.getRows() - 1; row >= 0; row--) {
            int full = sheet.getColumns();

            //go over each row from the bottom and check if it is full
            for (int col = 0; col < sheet.getColumns(); col++) {
                if (!(sheet.valueAt(row, col).getContent().isEmpty())) {
                    full--;
                }
            }

            //!(row is full) -> (skip if code)
            if (full == 0) {
                clearHelper(row);
                row = row + 1;
            }
        }
    }

    /**
     * Helps the clear() method by clearing each cell in a given row
     *
     * @param row the row to clear when full
     */
    private void clearHelper(int row) {
        for (int rowX = row; rowX > 0; rowX--) {
            for (int col = 0; col < sheet.getColumns(); col++) {

                if (tile.contains(new CellLocation(rowX - 1, col))) {
                    continue;
                }

                try {
                    sheet.update(new CellLocation(rowX, col),
                            sheet.valueAt(new CellLocation(rowX - 1, col)));
                } catch (TypeError e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Horizontally translates the tile with the given direction
     *
     * @param direction 0 if dropping, 1 if shift right, -1 if shift left.
     */
    public void shift(int direction) {
        if (direction == 0) {
            boolean tileDropped = false;
            do {
                tileDropped = dropTile();
            } while (!tileDropped);
        }

        List<CellLocation> newTile = new ArrayList<>();

        for (CellLocation tilePart : tile) {
            newTile.add(new CellLocation(tilePart.getRow(), tilePart.getColumn() + direction));
        }

        if (inBounds(newTile)) {
            unRender();
            render(newTile);
            this.tile = newTile;
        }
    }

    /**
     * this method checks if the given cell location is within the sheet,
     * or if the value at that location is empty
     *
     * @param location A location in the sheet
     * @return true, iff location is within the sheet AND value at that location is empty, false otherwise
     */
    private boolean stopper(CellLocation location) {
        if ((location.getRow() >= sheet.getRows())
                || (location.getColumn() >= sheet.getColumns())) {
            return true;
        }

        return !sheet.valueAt(location.getRow(), location.getColumn()).getContent().isEmpty();
    }

    /**
     * Flips the tile in a given direction
     *
     * @param direction a direction to rotate the tile in
     */
    public void rotate(int direction) {
        int x = 0;
        int y = 0;

        for (CellLocation cellLocation : tile) {
            x += cellLocation.getColumn();
            y += cellLocation.getRow();
        }
        x /= tile.size();
        y /= tile.size();

        List<CellLocation> newTile = new ArrayList<>();
        for (CellLocation tilePart : tile) {
            int newX = x + ((y - tilePart.getRow()) * direction);
            int newY = y + ((x - tilePart.getColumn()) * direction);
            CellLocation replacement = new CellLocation(newY, newX);
            newTile.add(replacement);
        }

        if (inBounds(newTile)) {
            unRender();
            tile = newTile;
            render(newTile);
        }
    }

    /**
     * checks if the given locations are within the sheet
     *
     * @param locations a list of {@link CellLocation}'s  to check
     * @return true if all {@link CellLocation}'s are within the sheet, false otherwise
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
     * Creates a random new tile to drop
     */
    private void newPiece() {
        switch (randomTile.pick()) {
            case 0 -> {
                tile.add(new CellLocation(0, 0));
                tile.add(new CellLocation(0, 1));
                tile.add(new CellLocation(1, 1));
                tile.add(new CellLocation(1, 2));
                fallingType = 4;
            }
            case 1 -> {
                tile.add(new CellLocation(0, 0));
                tile.add(new CellLocation(1, 0));
                tile.add(new CellLocation(2, 0));
                tile.add(new CellLocation(2, 1));
                fallingType = 7;
            }
            case 2 -> {
                tile.add(new CellLocation(0, 1));
                tile.add(new CellLocation(1, 1));
                tile.add(new CellLocation(2, 1));
                tile.add(new CellLocation(2, 0));
                fallingType = 5;
            }
            case 3 -> {
                tile.add(new CellLocation(0, 0));
                tile.add(new CellLocation(0, 1));
                tile.add(new CellLocation(0, 2));
                tile.add(new CellLocation(1, 1));
                fallingType = 8;
            }
            case 4 -> {
                tile.add(new CellLocation(0, 0));
                tile.add(new CellLocation(0, 1));
                tile.add(new CellLocation(1, 0));
                tile.add(new CellLocation(1, 1));
                fallingType = 3;
            }
            case 5 -> {
                tile.add(new CellLocation(0, 0));
                tile.add(new CellLocation(1, 0));
                tile.add(new CellLocation(2, 0));
                tile.add(new CellLocation(3, 0));
                fallingType = 6;
            }
        }
    }
}
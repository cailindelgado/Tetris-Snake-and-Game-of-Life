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

public class Tetros implements Tick, Feature {
    private final Sheet sheet;
    private boolean started = false;

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

    private int fallingType = 1;
    private List<CellLocation> contents = new ArrayList<>();

    private boolean isStopper(CellLocation location) {
        if (location.getRow() > sheet.getRows()) {
            return true;
        }
        if (location.getColumn() >= sheet.getColumns()) {
            return true;
        }
        return !sheet.valueAt(location.getRow(), location.getColumn()).getContent().equals("");
    }

    public boolean inBounds(List<CellLocation> locations) {
        for (CellLocation location : locations.subList(3, locations.size())) {
            if (!sheet.contains(location)) {
                return false;
            }
        }
        return true;
    }

    public boolean dropTile() {
        List<CellLocation> newContents = new ArrayList<>();
        for (CellLocation tile : contents) {
            newContents.add(new CellLocation(tile.getRow() + 1, tile.getColumn()));
        }
        unrender();
        for (CellLocation newLoc : newContents) {
            if (isStopper(newLoc)) {
                ununrender(contents);
                return true;
            }
        }
        ununrender(newContents);
        this.contents = newContents;
        return false;
    }

    public void fullDrop() {
        while (!dropTile()) {

        }
    }

    public void shift(int x) {
        if (x == 2) {
            fullDrop();
        }
        List<CellLocation> newContents = new ArrayList<>();
        for (CellLocation tile : contents) {
            newContents.add(new CellLocation(tile.getRow(), tile.getColumn() + x + 1));
        }
        if (inBounds(newContents)) {
            return;
        }
        unrender();
        ununrender(newContents);
        this.contents = newContents;
    }

    public void unrender() {
        for (CellLocation cell : contents) {
            try {
                sheet.update(cell, new Nothing());
            } catch (TypeError e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void ununrender(List<CellLocation> items) {
        for (CellLocation cell : items) {
            try {
                sheet.update(cell, new Constant(fallingType));
            } catch (TypeError e) {
                throw new RuntimeException(e);
            }
        }
    }

    public final RandomTile randomTile;

    public Tetros(Sheet sheet, RandomTile randomTile) {
        this.sheet = sheet;
        this.randomTile = randomTile;
    }

    private boolean drop() {
        contents = new ArrayList<>();
        newPiece();
        for (CellLocation location : contents) {
            if (!sheet.valueAt(location).render().equals("")) {
                return true;
            }
        }
        ununrender(contents);

        return false;
    }

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

    private void flip(int direction) {
        int x = 0;
        int y = 0;
        for (CellLocation cellLocation : contents) {
            x += cellLocation.getColumn();
            y += cellLocation.getRow();
        }
        x /= contents.size(); y /= contents.size();
        List<CellLocation> newCells = new ArrayList<>();
        for (CellLocation location : contents) {
            int lx = x + ((y -location.getRow())*direction);
            int ly = y + ((x -location.getColumn())*direction);
            CellLocation replacement = new CellLocation(ly, lx);
            newCells.add(replacement);
        }
        if (!inBounds(newCells)) {
            return;
        }
        unrender();
        contents = newCells;
        ununrender(newCells);
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

    private void clear() {
        for (int row = sheet.getRows() - 1; row >= 0; row--) {
            boolean full = true;
            for (int col = 0 ; col < sheet.getColumns(); col++) {
                if (sheet.valueAt(row, col).getContent().equals("")) {
                    full = false;
                }
            }
            if (full) {
                for (int rowX = row; rowX > 0; rowX--) {
                    for (int col = 0 ; col < sheet.getColumns(); col++) {
                        try {
                            if (contents.contains(new CellLocation(rowX - 1, col))) {
                                continue;
                            }
                            sheet.update(new CellLocation(rowX, col), sheet.valueAt(new CellLocation(rowX - 1, col)));
                        } catch (TypeError e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                row = row + 1;
            }
        }
    }

    public Perform getStart() {
        return new GameStart();
    }

    public Perform getMove(int direction) {
        return new Move(direction);
    }
    public Perform getRotate(int direction) {
        return new Rotate(direction);
    }

    public class GameStart implements Perform {
        @Override
        public void perform(int row, int column, Prompt prompt) {
            started = true;
            drop();
        }
    }

    public class Move implements Perform {
        private final int direction;

        public Move(int direction) {
            this.direction = direction;
        }

        @Override
        public void perform(int row, int column, Prompt prompt) {
            if (!started) {
                return;
            }
            shift(direction);
        }
    }

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

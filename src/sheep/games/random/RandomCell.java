package sheep.games.random;

import sheep.sheets.CellLocation;

/**
 * Is an interface for random cells
 */
public interface RandomCell {

    // picks a random cell location ?
    CellLocation pick();
}

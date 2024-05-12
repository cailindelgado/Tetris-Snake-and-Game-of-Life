package sheep.games.snake;

import sheep.games.random.RandomCell;
import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;

import java.util.LinkedList;

/**
 * The Snake Game logic
 */
public class SnakeG {

    private final Sheet sheet;
    private final RandomCell randomCell;

    private int[] currentDirection = {1, 0};
    private final LinkedList<CellLocation> snakeBody = new LinkedList<>();
    private boolean ate = false;

    /**
     * Constructor class for SnakeG
     *
     * @param sheet      the sheet upon which the snake game logic will operate on
     * @param randomCell an instance of a random cell
     */
    public SnakeG(Sheet sheet, RandomCell randomCell) {
        this.sheet = sheet;
        this.randomCell = randomCell;
    }

    /**
     * Changed the current direction that the snake is moving in
     *
     * @param direction a new direction to move the snake to
     */
    public void setDirection(int[] direction) {
        currentDirection = direction;
    }

    /**
     * Creates a snake at the given location
     *
     * @param row    the row coordinate of the snake
     * @param column the column coordinate of the snake
     */
    public void startingPos(int row, int column) {
        //if no cell chosen start at 0,0
        if (!sheet.contains(new CellLocation(row, column))) {
            snakeBody.add(new CellLocation(0, 0));
        } else {
            snakeBody.add(new CellLocation(row, column));
        }
        sheet.update(snakeBody.getFirst().getRow(), snakeBody.getFirst().getColumn(), "1");
    }

    /**
     * Passively moves the snake
     *
     * @return true, if the snake can be moved in that direction, false if the new location of
     * the body part is not valid (i.e. head hits body part, or head hits wall).
     */
    public boolean moveSnake() {
        CellLocation newLoc = new CellLocation(snakeBody.getFirst().getRow() + currentDirection[0],
                snakeBody.getFirst().getColumn() + currentDirection[1]);

        //ensuring new location is in the sheet, and that it is not another body part
        if (!sheet.contains(newLoc) || sheet.valueAt(newLoc).render().equals("1")) {
            return false;
        }

        //TODO lines 72 to 89 could be put into a food method.
        //if the snake just ate something wait a tick before removing the last tile
        if (!ate) {
            sheet.update(snakeBody.getLast().getRow(), snakeBody.getLast().getColumn(), "");
            snakeBody.removeLast();
        } else {
            ate = false;
        }

        if (!sheet.valueAt(newLoc).render().isEmpty()) {
            ate = true;
            CellLocation food = randomCell.pick();

            if (!sheet.valueAt(food).render().equals("1")) {
                sheet.update(food.getRow(), food.getColumn(), "2");
            }
        }

        snakeBody.addFirst(newLoc);
        sheet.update(newLoc.getRow(), newLoc.getColumn(), "1");

        return true;
    }
}

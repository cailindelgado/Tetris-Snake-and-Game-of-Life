package sheep.games.snake;

import sheep.features.Feature;
import sheep.games.random.RandomFreeCell;
import sheep.sheets.Sheet;
import sheep.ui.Perform;
import sheep.ui.Prompt;
import sheep.ui.Tick;
import sheep.ui.UI;

public class Snake implements Tick, Feature {
    //use a list that preserves input in order to save the snake's body tiles.
    //  https://duckduckgo.com/?q=java+list+that+preserves+order+in+which+elements+are+inserted&t=braveed&ia=web

    private Sheet sheet;
    private RandomFreeCell randomFreeCell;


    private boolean start = false;
    private final Perform startGame = new Perform() {
        @Override
        public void perform(int row, int column, Prompt prompt) {
            start = true;
        }
    };

    /**
     * Constructor for the snake
     * @param sheet A sheet upon which the snake is to be played on
     * @param randomFreeCell
     */
    public Snake(Sheet sheet, RandomFreeCell randomFreeCell) {
        this.sheet = sheet;
        this.randomFreeCell = randomFreeCell;
    }

    @Override
    public void register(UI ui) {
        ui.onTick(this);
        ui.addFeature("snek", "Start Snake", startGame);
//        ui.onKey("w", "Move North", this.getMove(-1, false));
//        ui.onKey("a", "Move West", this.getMove(1, false));
//        ui.onKey("s", "Rotate South", this.getMove(-1, true));
//        ui.onKey("d", "Rotate East", this.getMove(1, true));
    }

    @Override
    public boolean onTick(Prompt prompt) {
        if (!start) {
            return false;
        }

        //will be return true when changes are done on the board.
        return false;
    }
}

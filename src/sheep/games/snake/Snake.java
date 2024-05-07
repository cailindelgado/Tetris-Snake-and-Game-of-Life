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
    //TODO ask tutors why checkstyle mad
    private final Perform Start = new Perform() {
        @Override
        public void perform(int row, int column, Prompt prompt) {
            start = true;
        }
    };

    public Snake(Sheet sheet, RandomFreeCell randomFreeCell) {
        this.sheet = sheet;
        this.randomFreeCell = randomFreeCell;
    }

    @Override
    public void register(UI ui) {
        ui.onTick(this);
        ui.addFeature("snek", "Start Snake", Start);
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

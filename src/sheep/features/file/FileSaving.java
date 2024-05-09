package sheep.features.file;

import sheep.features.Feature;
import sheep.sheets.Sheet;
import sheep.ui.Perform;
import sheep.ui.Prompt;
import sheep.ui.UI;

/**
 * Saves the current sheet state
 */
public class FileSaving implements Feature {

    private final Sheet sheet;

    private final Perform saveState = new Perform() {
        @Override
        public void perform(int row, int column, Prompt prompt) {
            //save the game state
            System.out.println("Saving current sheet state");
        }
    };

    @Override
    public void register(UI ui) {
        ui.addFeature("save-file", "Save File", saveState);
    }

    public FileSaving(Sheet sheet) {
        this.sheet = sheet;
    }
}

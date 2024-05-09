package sheep.features.file;

import sheep.features.Feature;
import sheep.sheets.Sheet;
import sheep.ui.Perform;
import sheep.ui.Prompt;
import sheep.ui.UI;

import java.util.Optional;

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
            saver(prompt.ask("File name"));
        }
    };

    @Override
    public void register(UI ui) {
        ui.addFeature("save-file", "Save File", saveState);
    }

    public FileSaving(Sheet sheet) {
        this.sheet = sheet;
    }

    private void saver(Optional<String> filename) {
        System.out.println(sheet.encode());
    }
}

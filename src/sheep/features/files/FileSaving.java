package sheep.features.files;

import sheep.features.Feature;
import sheep.sheets.Sheet;
import sheep.ui.Perform;
import sheep.ui.Prompt;
import sheep.ui.UI;

import java.io.IOException;

/**
 * Saves the current sheet state
 */
public class FileSaving implements Feature {

    private final FileHandler fileHandler;

    private final Perform saveState = new Perform() {
        @Override
        public void perform(int row, int column, Prompt prompt) {
            //save the game state
            try {
                fileHandler.perform(
                        prompt.ask("Enter file location or file: ").orElse("?<:>?"),
                        true);
            } catch (IOException e) {
                prompt.message("Error save file failed");
            }
        }
    };

    @Override
    public void register(UI ui) {
        ui.addFeature("save-file", "Save File", saveState);
    }

    /**
     * constructor class for FileSaving
     *
     * @param sheet a sheet to encode
     */
    public FileSaving(Sheet sheet) {
        this.fileHandler = new FileHandler(sheet);
    }
}
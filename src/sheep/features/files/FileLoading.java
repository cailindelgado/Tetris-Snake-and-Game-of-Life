package sheep.features.files;

import sheep.features.Feature;
import sheep.sheets.Sheet;
import sheep.ui.Perform;
import sheep.ui.Prompt;
import sheep.ui.UI;

import java.io.IOException;

/**
 * The UI for loading the current sheet state
 */
public class FileLoading implements Feature {

    private final FileHandler fileHandler;

    private final Perform loadState = new Perform() {
        @Override
        public void perform(int row, int column, Prompt prompt) {
            // load the state
            try {
                fileHandler.perform(
                        prompt.ask("Enter file to load or file location: ").orElse("?<:>?"),
                        false);
            } catch (IOException e) {
                prompt.message("Error loading file failed");
            }
        }
    };

    @Override
    public void register(UI ui) {
        ui.addFeature("load-file", "Load File", loadState);
    }

    /**
     * Constructor method for {@link FileLoading}
     *
     * @param sheet is the sheet upon which the file will be loaded to
     */
    public FileLoading(Sheet sheet) {
        this.fileHandler = new FileHandler(sheet);
    }
}
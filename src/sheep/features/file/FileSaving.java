package sheep.features.file;

import sheep.features.Feature;
import sheep.sheets.Sheet;
import sheep.ui.Perform;
import sheep.ui.Prompt;
import sheep.ui.UI;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
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
            try {
                saver(prompt.ask("File name").orElse("savedSheet"));
            } catch (IOException e) {
                prompt.message("Error save file failed");
            }
        }
    };

    @Override
    public void register(UI ui) {
        ui.addFeature("save-file", "Save File", saveState);
    }

    public FileSaving(Sheet sheet) {
        this.sheet = sheet;
    }

    /**
     * encodes and saves the current file state
     * @param filename a name for the new file
     * @throws IOException when writing to a new file fails
     */
    private void saver(String filename) throws IOException {
        //get path for file
        String currentDirectory = System.getProperty("user.dir") + "/src/sheep/features/file/";

        //create a new write which makes a new file
        FileWriter writer = new FileWriter(currentDirectory + filename, true);

        //write the encoded sheet to the file
        writer.write(sheet.encode());

        //close the encoder
        writer.close();
    }
}

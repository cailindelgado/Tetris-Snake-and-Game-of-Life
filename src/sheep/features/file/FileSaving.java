package sheep.features.file;

import sheep.features.Feature;
import sheep.sheets.Sheet;
import sheep.ui.Perform;
import sheep.ui.Prompt;
import sheep.ui.UI;

import java.io.FileWriter;
import java.io.IOException;

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
                saver(prompt.ask("Enter file path and name like so filePath/fileName.txt")
                        .orElse("savedSheet"));
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
     * @param sheet a sheet to encode
     */
    public FileSaving(Sheet sheet) {
        this.sheet = sheet;
    }

    /**
     * encodes and saves the current file state
     * @param fileLocation a name for the new file
     * @throws IOException when writing to a new file fails
     */
    private void saver(String fileLocation) throws IOException {
        //savedSheet is only put as fileLocation when user cancels the save.
        if (fileLocation.equals("savedSheet")) {
            throw new IOException();
        }

        //create a new write which makes a new file
        FileWriter writer = new FileWriter(fileLocation, true);

        //write the encoded sheet to the file
        writer.write(sheet.encode());

        //close the encoder
        writer.close();
    }
}

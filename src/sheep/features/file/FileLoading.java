package sheep.features.file;

import sheep.expression.CoreFactory;
import sheep.expression.Expression;
import sheep.expression.TypeError;
import sheep.features.Feature;
import sheep.parsing.ParseException;
import sheep.parsing.SimpleParser;
import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;
import sheep.ui.Perform;
import sheep.ui.Prompt;
import sheep.ui.UI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Loads the sheet with a pre-saved sheet state from {@link FileSaving}
 */
public class FileLoading implements Feature {

    private final Sheet sheet;
    private final SimpleParser parser = new SimpleParser(new CoreFactory());
    private boolean dimensionsUpdated = false;

    private final Perform loadState = new Perform() {
        @Override
        public void perform(int row, int column, Prompt prompt) {
            // load the state
            try {
                loader(prompt.ask("Enter a file to load: filePath\\fileName")
                        .orElse("?<:>?")); //is made of forbidden ASCII characters across
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
        this.sheet = sheet;
    }

    /**
     * loads from the saved file into the sheet.
     *
     * @param fileLocation the location of the file to read from
     * @throws IOException if there is an error while reading from the file
     */
    private void loader(String fileLocation) throws IOException {
        //create a new reader to read the given file
        BufferedReader reader = new BufferedReader(new FileReader(fileLocation));

        //clear the sheet before uploading
        sheet.clear();

        String fileLine;
        int row = 0;

        while ((fileLine = reader.readLine()) != null) {
            String[] lineBits = fileLine.split("[|]");

            //if the sheets dimensions haven't been updated yet
            if (!dimensionsUpdated) {
                dimensionsUpdated = true;
                sheet.updateDimensions(Integer.parseInt(String.valueOf(reader.lines().count())) + 1,
                        lineBits.length);
            }

            fillRow(row++, lineBits);
        }

        //close the reader to prevent any mishaps
        reader.close();
    }

    private void fillRow(int row, String[] rowCells) throws IOException {
        for (int pos = 0; pos < rowCells.length; pos++) {

            //try to parse the bit
            try {
                CellLocation loc = new CellLocation(row, pos);
                Expression result = parser.parse(rowCells[pos]);

                try {
                    sheet.update(loc, result);
                } catch (TypeError error) {
                    throw new ParseException();
                }
            } catch (ParseException e) {
                throw new IOException();
            }

        }
    }
}

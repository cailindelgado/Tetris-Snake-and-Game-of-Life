package sheep.features.files;

import sheep.expression.CoreFactory;
import sheep.expression.Expression;
import sheep.expression.TypeError;
import sheep.parsing.ParseException;
import sheep.parsing.SimpleParser;
import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;

import java.io.*;

/**
 * Saves the current state of the sheet
 */
public class FileHandler {

    private final Sheet sheet;
    private final SimpleParser parser = new SimpleParser(new CoreFactory());

    private int rows;
    private int columns;
    private Expression[][] newSheet;

    /**
     * The constructor method
     *
     * @param sheet        the sheet which will be saved
     */
    public FileHandler(Sheet sheet) {
        this.sheet = sheet;
    }

    /**
     * saves or loads a file depending on the isSaving variable
     *
     * @param fileLocation The string representation for the file to save to
     * @param isSaving true if saving the current sheet instance, false if loading from a file
     * @throws IOException if something goes wrong with the file saving or file loading
     */
    public void perform(String fileLocation, boolean isSaving) throws IOException {
        if (isSaving) {
            save(fileLocation);
        } else {
            load(fileLocation);
        }
    }

    //File Saving

    /**
     * encodes and saves the current file state
     *
     * @param fileLocation a name for the new file
     * @throws IOException when writing to a new file fails
     */
    private void save(String fileLocation) throws IOException {
        File newSave = new File(fileLocation);

        //create a new file if DNE, or replace existing one
        newSave.createNewFile();

        FileWriter writer = new FileWriter(newSave);

        //write the encoded sheet to the file
        writer.write(sheet.encode());

        //close the encoder
        writer.close();
    }

    //File Loading

    /**
     * loads from the saved file into the sheet.
     *
     * @param fileLocation the location of the file to read from
     * @throws IOException if there is an error while reading from the file
     */
    private void load(String fileLocation) throws IOException {
        setRows(fileLocation);

        populateNewSheet(fileLocation);

        updateThisSheet();
    }

    /**
     * Sets up the new sheet, so that it is the same size of what is to be loaded
     *
     * @param fileLocation the file to read from
     * @throws IOException if the reader has issues
     */
    private void setRows(String fileLocation) throws IOException {
        //ensure rows and columns are reset to default values to allow loading more than once
        rows = 1;
        columns = 0;

        BufferedReader reader = new BufferedReader(new FileReader(fileLocation));

        columns = reader.readLine().split("[|]", -1).length;

        while (reader.readLine() != null) {
            rows++;
        }
        reader.close();

        newSheet = new Expression[rows][columns];
    }

    /**
     * populates the newSheet array with the information from the given file
     */
    private void populateNewSheet(String fileLocation) throws IOException {
        //create a new reader to read the given file
        BufferedReader reader = new BufferedReader(new FileReader(fileLocation));

        for (int row = 0; row < rows; row++) {
            String[] lineBits = reader.readLine().split("[|]", -1);

            for (int col = 0; col < lineBits.length; col++) {
                //try to parse the cell info
                try {
                    Expression result = parser.parse(lineBits[col]);
                    newSheet[row][col] = result;
                } catch (ParseException e) {
                    throw new IOException();
                }
            }
        }

        //close the reader to prevent any mishaps
        reader.close();
    }

    /**
     * clears the current sheet, and updates each cell according to what is in newSheet
     *
     * @throws IOException is thrown when there is a problem with updating the sheet
     */
    private void updateThisSheet() throws IOException {
        sheet.clear();

        sheet.updateDimensions(rows, columns);

        for (int row = 0; row < newSheet.length; row++) {
            for (int column = 0; column < columns; column++) {
                if (!newSheet[row][column].render().isEmpty()) {
                    try {
                        sheet.update(new CellLocation(row, column), newSheet[row][column]);
                    } catch (TypeError error) {
                        throw new IOException();
                    }
                }
            }
        }
    }
}

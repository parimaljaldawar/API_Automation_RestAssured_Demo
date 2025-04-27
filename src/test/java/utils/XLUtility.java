package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * The {@code XLUtility} class provides utility methods for reading from and writing to Excel files
 * using Apache POI. This class supports common operations like retrieving row and cell counts, 
 * reading cell data, and writing data to cells.
 * <p>
 * Instances of this class are created by passing the path to the Excel file. The utility methods 
 * then use this path to load the workbook, perform operations, and optionally write back to the file.
 * </p>
 */
public class XLUtility {

    // Input stream for reading the Excel file.
    public FileInputStream fi;
    // Output stream for writing changes to the Excel file.
    public FileOutputStream fo;
    // Represents the entire Excel workbook.
    public XSSFWorkbook workbook;
    // Represents a sheet within the workbook.
    public XSSFSheet sheet;
    // Represents a row within a sheet.
    public XSSFRow row;
    // Represents a cell within a row.
    public XSSFCell cell;
    // Cell styling can be applied via this style object.
    public CellStyle style;
    // File path to the Excel file.
    String path;

    /**
     * Constructs an {@code XLUtility} object with the given Excel file path.
     *
     * @param path the path to the Excel file
     */
    public XLUtility(String path) {
        this.path = path;
    }

    /**
     * Retrieves the number of rows present in the specified sheet.
     *
     * @param sheetName the name of the sheet
     * @return the count of rows (zero-based index of last row)
     * @throws IOException if the file cannot be read
     */
    public int getRowCount(String sheetName) throws IOException {
        fi = new FileInputStream(path);
        workbook = new XSSFWorkbook(fi);
        sheet = workbook.getSheet(sheetName);
        // getLastRowNum returns the index of the last row (0-based)
        int rowcount = sheet.getLastRowNum();
        workbook.close();
        fi.close();
        return rowcount;
    }

    /**
     * Retrieves the number of cells in a given row of the specified sheet.
     *
     * @param sheetName the name of the sheet
     * @param rownum the row number (0-based index)
     * @return the number of cells in the row
     * @throws IOException if the file cannot be read
     */
    public int getCellCount(String sheetName, int rownum) throws IOException {
        fi = new FileInputStream(path);
        workbook = new XSSFWorkbook(fi);
        sheet = workbook.getSheet(sheetName);
        row = sheet.getRow(rownum);
        // getLastCellNum returns the number of cells in the row (1-based)
        int cellcount = row.getLastCellNum();
        workbook.close();
        fi.close();
        return cellcount;
    }

    /**
     * Retrieves the data stored in a cell as a string from the specified sheet, row, and column.
     *
     * @param sheetName the name of the sheet
     * @param rownum the row number (0-based index)
     * @param column the column number (0-based index)
     * @return the cell data as a String; returns an empty string if the cell is empty or an error occurs
     * @throws IOException if the file cannot be read
     */
    public String getCellData(String sheetName, int rownum, int column) throws IOException {
        fi = new FileInputStream(path);
        workbook = new XSSFWorkbook(fi);
        sheet = workbook.getSheet(sheetName);
        row = sheet.getRow(rownum);
        cell = row.getCell(column);

        // DataFormatter is used to format the cell value as a String regardless of its type.
        DataFormatter formatter = new DataFormatter();
        String data;
        try {
            data = formatter.formatCellValue(cell);
        } catch (Exception e) {
            data = "";
        }
        workbook.close();
        fi.close();
        return data;
    }

    /**
     * Sets the specified cell with the given data and writes the change back to the Excel file.
     * <p>
     * If the file or sheet does not exist, they will be created. If the row or cell does not exist,
     * they will be created as needed.
     * </p>
     *
     * @param sheetName the name of the sheet where data should be set
     * @param rownum the row number (0-based index) where data should be set
     * @param column the column number (0-based index) where data should be set
     * @param data the data to write into the cell
     * @throws IOException if the file cannot be written to
     */
    public void setCellData(String sheetName, int rownum, int column, String data) throws IOException {
        File xlfile = new File(path);
        // If the Excel file does not exist, create a new workbook and write an empty file.
        if (!xlfile.exists()) {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet(sheetName);
            fo = new FileOutputStream(path);
            workbook.write(fo);
            fo.close();
            workbook.close();
        }
        
        // Open the workbook from the file.
        fi = new FileInputStream(path);
        workbook = new XSSFWorkbook(fi);
        fi.close();
        
        // If the sheet does not exist, create it.
        if (workbook.getSheet(sheetName) == null) {
            sheet = workbook.createSheet(sheetName);
        } else {
            sheet = workbook.getSheet(sheetName);
        }
        
        // Retrieve the row; if it does not exist, create it.
        if (sheet.getRow(rownum) == null) {
            row = sheet.createRow(rownum);
        } else {
            row = sheet.getRow(rownum);
        }
        
        // Retrieve the cell; if it does not exist, create it.
        cell = row.getCell(column);
        if (cell == null) {
            cell = row.createCell(column);
        }
        
        // Set the cell value with the given data.
        cell.setCellValue(data);
        
        // Write the updated workbook back to the file.
        fo = new FileOutputStream(path);
        workbook.write(fo);
        fo.close();
        
        // Close the workbook.
        workbook.close();
    }
}

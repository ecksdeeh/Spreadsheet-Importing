package importer;

import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Service
public class ExcelService {

    @Autowired
    private DatabaseService dbService;

    private static final List<String> REQUIRED_COLUMNS = List.of(
        "SKU", "Brand", "Product Linq ID",
        "CLOSEOUT_ID", "CLOSEOUT_TYPE", "Active Inactive Status-Default"
    );

    public ImportResult processFile(MultipartFile file) {
        ImportResult result = new ImportResult();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
                processSheet(workbook.getSheetAt(s), result);
            }
        } catch (Exception e) {
            result.addError("File processing error: " + e.getMessage());
        }
        return result;
    }

    private void processSheet(Sheet sheet, ImportResult result) {
        int dataStartRow = -1;
        Map<String, Integer> columnIndex = new HashMap<>();

        // Search up to row 20 for the header block
        for (int i = 0; i <= Math.min(20, sheet.getLastRowNum()); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            // Build a combined column map from this row + the next row
            // to handle merged/split header rows like in item-export-test.xlsx
            Map<String, Integer> combined = new HashMap<>();
            combined.putAll(mapColumns(row));
            Row nextRow = sheet.getRow(i + 1);
            if (nextRow != null) combined.putAll(mapColumns(nextRow));

            boolean hasAll = REQUIRED_COLUMNS.stream().allMatch(combined::containsKey);
            if (hasAll) {
                columnIndex = combined;
                // Skip any metadata rows after headers (Required, Update, string, etc.)
                dataStartRow = findDataStart(sheet, i + 1);
                break;
            }
        }

        if (dataStartRow == -1) {
            System.out.println("Sheet '" + sheet.getSheetName() + "' skipped - required headers not found");
            return;
        }

        for (int i = dataStartRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            // Skip completely empty rows
            if (isEmptyRow(row)) continue;

            try {
                String sku         = getCell(row, columnIndex.get("SKU"));
                String brand       = getCell(row, columnIndex.get("Brand"));
                String linqRaw     = getCell(row, columnIndex.get("Product Linq ID"));
                String closeoutId  = getCell(row, columnIndex.get("CLOSEOUT_ID"));
                String closeoutType = getCell(row, columnIndex.get("CLOSEOUT_TYPE"));
                String avbStatus   = getCell(row, columnIndex.get("Active Inactive Status-Default"));

                // Check for missing values
                Map<String, String> fields = new LinkedHashMap<>();
                fields.put("SKU", sku);
                fields.put("Brand", brand);
                fields.put("Product Linq ID", linqRaw);
                fields.put("CLOSEOUT_ID", closeoutId);
                fields.put("CLOSEOUT_TYPE", closeoutType);
                fields.put("Active Inactive Status-Default", avbStatus);

                boolean hasMissing = false;
                for (Map.Entry<String, String> entry : fields.entrySet()) {
                    if (entry.getValue().isEmpty()) {
                        result.addError("Row " + (i + 1) + ", Column '" + entry.getKey() + "': missing value");
                        hasMissing = true;
                    }
                }
                if (hasMissing) continue;

                int linqId;
                try {
                    linqId = Integer.parseInt(linqRaw.replace(",", "").trim());
                } catch (NumberFormatException e) {
                    result.addError("Row " + (i + 1) + ", Column 'Product Linq ID': invalid number '" + linqRaw + "'");
                    continue;
                }

                dbService.upsert(sku, brand, linqId, closeoutId, closeoutType, avbStatus);
                result.incrementSuccess();

            } catch (Exception e) {
                result.addError("Row " + (i + 1) + ": unexpected error: " + e.getMessage());
            }
        }
    }

    // Skip metadata rows after headers (rows like "Required", "Update", "string", etc.)
    private int findDataStart(Sheet sheet, int afterRow) {
        for (int i = afterRow; i <= Math.min(afterRow + 5, sheet.getLastRowNum()); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            Cell first = row.getCell(0);
            if (first == null) continue;
            String val = first.toString().trim().toLowerCase();
            // Skip known metadata row values
            if (val.isEmpty() || val.equals("required") || val.equals("read only")
                    || val.equals("update") || val.equals("string") || val.equals("integer")) {
                continue;
            }
            return i;
        }
        return afterRow;
    }

    private boolean isEmptyRow(Row row) {
        for (Cell cell : row) {
            if (cell != null && !cell.toString().trim().isEmpty()) return false;
        }
        return true;
    }

    private Map<String, Integer> mapColumns(Row row) {
        Map<String, Integer> map = new HashMap<>();
        for (Cell cell : row) {
            if (cell != null) {
                String val = cell.toString().trim();
                if (!val.isEmpty()) map.put(val, cell.getColumnIndex());
            }
        }
        return map;
    }

    private String getCell(Row row, int index) {
        Cell cell = row.getCell(index);
        return cell == null ? "" : cell.toString().trim();
    }
}
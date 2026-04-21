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

    private final List<String> REQUIRED_COLUMNS = List.of(
        "SKU", "Brand", "Product Linq ID",
        "CLOSEOUT_ID", "CLOSEOUT_TYPE", "Active Inactive Status-Default"
    );

    public ImportResult processFile(MultipartFile file) {
        ImportResult result = new ImportResult();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);
            Map<String, Integer> columnIndex = mapColumns(header);

            for (String col : REQUIRED_COLUMNS) {
                if (!columnIndex.containsKey(col)) {
                    result.addError("Missing column: " + col);
                    return result;
                }
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                try {
                    String sku = getCell(row, columnIndex.get("SKU"));
                    String brand = getCell(row, columnIndex.get("Brand"));
                    String linqRaw = getCell(row, columnIndex.get("Product Linq ID"));

                    if (sku.isEmpty() || brand.isEmpty() || linqRaw.isEmpty()) {
                        result.addError("Missing data at row " + i);
                        continue;
                    }

                    int linqId = Integer.parseInt(linqRaw.replace(",", ""));
                    dbService.upsert(sku.trim(), brand.trim(), linqId);
                    result.incrementSuccess();

                } catch (Exception e) {
                    result.addError("Error at row " + i + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            result.addError("File processing error: " + e.getMessage());
        }

        return result;
    }

    private Map<String, Integer> mapColumns(Row header) {
        Map<String, Integer> map = new HashMap<>();
        for (Cell cell : header) {
            map.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
        }
        return map;
    }

    private String getCell(Row row, int index) {
        Cell cell = row.getCell(index);
        return cell == null ? "" : cell.toString().trim();
    }
}
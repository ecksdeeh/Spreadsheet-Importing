package importer;
public class ImportResult {
    private int successCount;
    private List<String> errors = new ArrayList<>();

    public void addError(String error) {
        errors.add(error);
    }

    public void incrementSuccess() {
        successCount++;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public List<String> getErrors() {
        return errors;
    }
}
@Controller
public class UploadController {

    @Autowired
    private ExcelService excelService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {

        ImportResult result = excelService.processFile(file);

        model.addAttribute("message", "Imported: " + result.getSuccessCount());
        model.addAttribute("errors", result.getErrors());

        return "index";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
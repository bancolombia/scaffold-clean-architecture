package co.com.bancolombia.api;
import co.com.bancolombia.usecase.file.FIleUseCase;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/storage")
@AllArgsConstructor
public class ApiRest {

    private final FIleUseCase useCase;

    @PostMapping(path = "/upload")
    public boolean upload(@RequestPart(value = "file") MultipartFile file) throws IOException {
        return useCase.upload(file.getBytes(),file.getContentType(),file.getOriginalFilename().replace(" ", "")
                .replace(":", "")
                .replace("\\", ""));
    }

    @GetMapping(path = "/list")
    public List<String> list() {
        return useCase.list();
    }

    @DeleteMapping(path = "/delete/{file}")
    public boolean delete(@PathVariable(value = "file") String fileUrl) {
        return useCase.delete(fileUrl);
    }
}

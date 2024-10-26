package co.com.bancolombia.api;

import co.com.bancolombia.usecase.file.FIleUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/storage")
@RequiredArgsConstructor
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

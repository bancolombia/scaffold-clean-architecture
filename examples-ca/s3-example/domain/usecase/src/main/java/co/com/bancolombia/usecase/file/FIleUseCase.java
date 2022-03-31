package co.com.bancolombia.usecase.file;

import co.com.bancolombia.model.file.gateways.FileRepository;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class FIleUseCase {

    FileRepository repository;

    public boolean upload(byte[] bytes, String contentType, String name) {
        return repository.upload(bytes,contentType,name);
    }

    public boolean delete(String fileUrl) {
        return repository.delete(fileUrl);
    }

    public List<String> list() {
        return repository.list();
    }
}

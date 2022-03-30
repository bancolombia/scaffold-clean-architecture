package co.com.bancolombia.model.file.gateways;

import java.util.List;

public interface FileRepository {

    boolean upload(byte[] bytes, String contentType, String name);

    boolean delete(String fileUrl);

    List<String> list();
}

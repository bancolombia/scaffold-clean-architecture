package co.com.bancolombia.models;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FileModel {
    private String path;
    private  String content;
}

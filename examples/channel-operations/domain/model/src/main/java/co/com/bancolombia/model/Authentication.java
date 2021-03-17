package co.com.bancolombia.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Authentication implements Serializable {

    private static final long serialVersionUID = 569381541450405501L;

    private Integer id;
    private String userName;
    private String channel;
    private String applicationId;
    private LocalDateTime authenticationTime;
    private String authenticationIp;
    private String authenticationDevice;
    private String accessToken;
    private String refreshToken;

}
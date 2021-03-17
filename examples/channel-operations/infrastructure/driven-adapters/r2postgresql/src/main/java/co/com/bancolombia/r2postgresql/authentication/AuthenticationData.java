package co.com.bancolombia.r2postgresql.authentication;

import java.time.LocalDateTime;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("management.authentication")
public class AuthenticationData implements Persistable<Integer> {

    @Id
    @Column("id")
    private Integer id;

    @Column("user_name")
    private String userName;

    @Column("channel_id")
    private String channel;

    @Column("application_id")
    private String applicationId;

    @Column("authentication_time")
    private LocalDateTime authenticationTime;

    @Column("authentication_ip")
    private String authenticationIp;

    @Column("authentication_device")
    private String authenticationDevice;

    @Column("access_token")
    private String accessToken;

    @Column("refresh_token")
    private String refreshToken;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}

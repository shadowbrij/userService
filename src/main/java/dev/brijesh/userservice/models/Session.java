package dev.brijesh.userservice.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Session extends BaseModel{
    @Column(columnDefinition = "TEXT")
    private String token;

    private Date expiryDate;
    @ManyToOne
    private User user;

    @Enumerated(EnumType.ORDINAL)
    private SessionStatus sessionStatus;

}

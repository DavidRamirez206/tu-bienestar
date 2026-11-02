package edu.co.icesi.proyectofinal.entity.keys;

import edu.co.icesi.proyectofinal.entity.Activity;
import edu.co.icesi.proyectofinal.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.Objects;

@Embeddable
@Data
public class EnrollmentId {

    @Column(name = "id_user")
    private Integer userId;

    @Column(name = "id_activity")
    private Integer activityId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof EnrollmentId that) {
            return Objects.equals(userId, that.userId) && Objects.equals(activityId, that.activityId);
        } else return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, activityId);
    }
}

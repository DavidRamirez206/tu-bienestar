package edu.co.icesi.proyectofinal.entity.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.Objects;

@Embeddable
@Data
public class AssignmentId {

    @Column(name = "id_trainer")
    private Integer trainerId;

    @Column(name = "id_user")
    private Integer userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof AssignmentId that) {
            return Objects.equals(trainerId, that.trainerId) && Objects.equals(userId, that.userId);
        } else return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainerId, userId);
    }


}

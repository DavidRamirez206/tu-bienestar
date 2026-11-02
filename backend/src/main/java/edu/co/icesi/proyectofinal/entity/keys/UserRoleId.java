package edu.co.icesi.proyectofinal.entity.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.Objects;

@Embeddable
@Data
public class UserRoleId {


    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "role_id")
    private Long roleId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof UserRoleId that) {
            return Objects.equals(userId, that.userId) && Objects.equals(roleId, that.roleId);
        } else return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId);
    }
}

package edu.co.icesi.proyectofinal.entity.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.Objects;

@Embeddable
@Data
public class RolePoliciesId {

    @Column(name = "id_role")
    private Long roleId;

    @Column(name = "id_permission")
    private Long policyId;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof RolePoliciesId that) {
            return Objects.equals(roleId, that.roleId) && Objects.equals(policyId, that.policyId);
        } else return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, policyId);
    }
}

package edu.co.icesi.proyectofinal.entity;

import edu.co.icesi.proyectofinal.entity.keys.RolePoliciesId;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "role_policies")
@Data
public class RolePolicy {

    @EmbeddedId
    private RolePoliciesId id;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @MapsId("policyId")
    @JoinColumn(name = "policy_id")
    private Policy policy;


}

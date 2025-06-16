package com.Unicor_Ads_2.Unicor_Ads_2.demo.security.persistence.entity;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
 @Table(name = "users")
public class UserEntity  extends BaseEntity {

    private String username;
    @Column(name = "name")
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Column(unique = true)
    private String dni;

    @Column(unique = true)
    private String email;

    private String password;

    private String phone;
    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Column(name = "account_No_Expired")
    private boolean accountNoExpired;

    @Column(name = "account_No_Locked")
    private boolean accountNoLocked;

    @Column(name = "credential_No_Expired")
    private boolean credentialNoExpired;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();


}
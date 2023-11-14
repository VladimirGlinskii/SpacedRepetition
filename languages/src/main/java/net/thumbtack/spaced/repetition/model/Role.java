package net.thumbtack.spaced.repetition.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.thumbtack.spaced.repetition.model.enums.RoleName;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
public class Role implements GrantedAuthority {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @Transient
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public Role(int id) {
        this.id = id;
    }

    public Role(int id, RoleName name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return getName().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return getId() == role.getId() &&
                getName() == role.getName();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}

package ru.furestry.sevlersite.entities.db;

import lombok.Data;
import org.springframework.context.support.BeanDefinitionDsl;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

@Data
@Entity
@Table(name = "Users")
public class User implements Serializable {

    private static final long serialVersionUID = 15234123l;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    private String token;

    private String tokenHash;

    @Lob
    private byte[] avatar;

    @OneToMany(mappedBy = "user")
    private Set<Comment> profileComments;

    @OneToMany(mappedBy = "author")
    private Set<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;
}

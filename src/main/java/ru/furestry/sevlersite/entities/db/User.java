package ru.furestry.sevlersite.entities.db;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

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
}

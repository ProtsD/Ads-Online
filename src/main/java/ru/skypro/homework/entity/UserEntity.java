package ru.skypro.homework.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.skypro.homework.dto.user.Role;

import javax.persistence.*;

@Entity
@Table(name = "`user`")
@Data
@Accessors(chain = true)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "phone", nullable = false, unique = true)
    private String phone;
    @Column(name = "role", nullable = false)
    private Role role;
    @Column(name = "image")
    private String image;
}

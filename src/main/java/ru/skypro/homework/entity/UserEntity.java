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
    private int id;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "firstName")
    private String firstName;
    @Column(name = "lastName")
    private String lastName;
    @Column(name = "phone")
    private String phone;
    @Column(name = "role")
    private Role role;
    @Column(name = "image")
    private String image;
}

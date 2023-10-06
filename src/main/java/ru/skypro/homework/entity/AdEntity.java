package ru.skypro.homework.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Data
@Table(name = "ads")
@Accessors(chain = true)
public class AdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk", nullable = false)
    private Integer pk;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image", nullable = false)
    private String image;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private UserEntity author;
}

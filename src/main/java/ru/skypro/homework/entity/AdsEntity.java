package ru.skypro.homework.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Entity
@Data
@Table(name = "ads")
public class AdsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk", nullable = false)
    private Integer pk;

    @Column(name = "price", nullable = false)
    @Min(0) @Max(10000000)
    private Integer price;

    @Column(name = "title", nullable = false)
    @Size(min = 4, max = 32)
    private String title;

    @Column(name = "description", nullable = false)
    @Size(min = 8, max = 64)
    private String description;

    @Column(name = "image", nullable = false)
    private String image;

    @ManyToOne
    @JoinColumn(name = "id",referencedColumnName = "author_id")
    private User author;
}

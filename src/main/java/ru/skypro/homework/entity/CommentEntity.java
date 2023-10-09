package ru.skypro.homework.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Table(name = "`comment`")
@Data
@Accessors(chain = true)
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk", nullable = false)
    private Integer pk;
    @Column(name = "created_at", nullable = false)
    private long createdAt;
    @Column(name = "text", nullable = false)
    private String text;
    @ManyToOne
    @JoinColumn(name = "ad_pk", referencedColumnName = "pk")
    private AdEntity adEntity;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity author;
}

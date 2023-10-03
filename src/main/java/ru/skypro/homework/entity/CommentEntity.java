package ru.skypro.homework.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "`comment`")
@Data
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "createdAt", nullable = false)
    private long createdAt;
    @Column(name = "text", nullable = false)
    private String text;
    @ManyToOne
    @JoinColumn(name = "ads_pk", referencedColumnName = "pk")
    private AdsEntity adsEntity;
    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User author;

    @Override
    public String toString() {
        return "CommentEntity{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", text='" + text + '\'' +
                ", author=" + author +
                '}';
    }
}

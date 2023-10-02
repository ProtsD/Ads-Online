package ru.skypro.homework.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "`Comment`")
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
    @JoinColumn(name = "pk", referencedColumnName = "ads_pk")
    private AdsEntity adsEntity;
    @ManyToOne
    @JoinColumn(name = "id",referencedColumnName = "user_id")
    private User author;

    public CommentEntity() {
    }

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

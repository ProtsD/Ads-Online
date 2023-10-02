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
    @OneToOne
    @JoinColumn(name = "pk", referencedColumnName = "ads_pk")
    private AdsEntity pk;
    @ManyToOne
    @JoinColumn(name = "authorFirstName", referencedColumnName = "user_firstname")
    private User authorFirstName;
    @ManyToOne
    @JoinColumn(name = "authorImage", referencedColumnName = "user_image")
    private User authorImage;
    @ManyToOne
    @JoinColumn(name = "author",referencedColumnName = "user_id")
    private User author;

    public CommentEntity() {
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public AdsEntity getPk() {
        return pk;
    }

    public void setPk(AdsEntity pk) {
        this.pk = pk;
    }

    public User getAuthorFirstName() {
        return authorFirstName;
    }

    public void setAuthorFirstName(User authorFirstName) {
        this.authorFirstName = authorFirstName;
    }

    public User getAuthorImage() {
        return authorImage;
    }

    public void setAuthorImage(User authorImage) {
        this.authorImage = authorImage;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "CommentEntity{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", text='" + text + '\'' +
                ", pk=" + pk +
                ", authorFirstName=" + authorFirstName +
                ", authorImage=" + authorImage +
                ", author=" + author +
                '}';
    }
}

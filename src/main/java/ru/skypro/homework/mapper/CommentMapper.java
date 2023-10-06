package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.comment.Comment;
import ru.skypro.homework.dto.comment.Comments;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.dto.user.User;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;

import java.util.Date;
import java.util.List;

@Component
public class CommentMapper {
        public CommentEntity toNewEntity(CreateOrUpdateComment createOrUpdateComment, UserEntity userEntity, AdEntity adEntity){
            Date date = new Date();
            if (userEntity==null || adEntity==null){
                return null;
            }
            else {
                return new CommentEntity()
                        .setAuthor(userEntity)
                        .setAdsEntity(adEntity)
                        .setCreatedAt(date.getTime())
                        .setText(createOrUpdateComment.getText());
            }
    }
//    public CommentEntity toEntity(){
//
//    }
    public Comment toComment(CommentEntity commentEntity) {
        return new Comment()
                .setAuthor(commentEntity.getAuthor().getId())
                .setText(commentEntity.getText())
                .setPk(commentEntity.getPk())
                .setCreatedAt(commentEntity.getCreatedAt())
                .setAuthorImage(commentEntity.getAuthor().getImage())
                .setAuthorFirstName(commentEntity.getAuthor().getFirstName());
    }

    public Comments toComments(List<Comment> commentList) {
        return new Comments()
                .setCount(commentList.size())
                .setResults(commentList);
    }
}

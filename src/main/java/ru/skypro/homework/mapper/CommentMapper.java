package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.comment.Comment;
import ru.skypro.homework.dto.comment.Comments;
import ru.skypro.homework.entity.CommentEntity;

import java.util.List;

@Component
public class CommentMapper {
    //    public CommentEntity toEntity(CreateOrUpdateComment createOrUpdateComment){
//        return new CommentEntity()
//                .setText(createOrUpdateComment.getText());
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

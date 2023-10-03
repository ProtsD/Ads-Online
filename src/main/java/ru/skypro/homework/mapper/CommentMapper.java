package ru.skypro.homework.mapper;

import ru.skypro.homework.dto.comment.Comment;
import ru.skypro.homework.dto.comment.Comments;
import ru.skypro.homework.dto.comment.CreateOrUpdateComment;
import ru.skypro.homework.entity.CommentEntity;

import java.util.List;

public class CommentMapper {
    public CommentEntity toEntity(CreateOrUpdateComment createOrUpdateComment){
        if (createOrUpdateComment==null){
            return null;
        }
        CommentEntity comment = new CommentEntity();
        comment.setText(createOrUpdateComment.getText());
        return comment;
    }
    public Comment toComment (CommentEntity commentEntity){
        if (commentEntity==null){
            return null;
        }
        Comment comment = new Comment();
        comment.setAuthor(commentEntity.getAuthor().getId());
        comment.setText(commentEntity.getText());
        comment.setPk(commentEntity.getPk().getId());
        comment.setCreatedAt(commentEntity.getCreatedAt());
        comment.setAuthorImage(commentEntity.getAuthor().getImage());
        comment.setAuthorFirstName(commentEntity.getAuthor().getFirstName());
        return comment;
    }
    public Comments toComments(List<Comment> commentList){
        if (commentList==null){
            return null;
        }
        Comments comments = new Comments();
        comments.setCount(commentList.size());
        comments.setResults(commentList);
        return comments;
    }
}

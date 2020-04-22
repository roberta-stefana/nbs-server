package main.repository;

import main.model.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepoJPA extends JpaRepository<Comments, Integer> {
    Comments save(Comments comments);
    List<Comments> findAllByIdGame(int idGame);
    List<Comments> findAllByIdGameAndQuater(int idGame, int quater);
}

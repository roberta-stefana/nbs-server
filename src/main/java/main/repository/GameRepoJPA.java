package main.repository;

import main.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepoJPA extends JpaRepository<Game, Integer> {
    Game save(Game game);
    Game findByIdGame(int idGame);
    List<Game> findAll();
    List<Game> findAllByLive(Boolean live);
}

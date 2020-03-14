package main.repository;

import main.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepoJPA extends JpaRepository<Game, Integer> {
    Game save(Game game);
}

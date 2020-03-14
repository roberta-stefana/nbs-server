package main.repository;

import main.model.LiveGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveGameRepoJPA extends JpaRepository<LiveGame, Integer> {
    LiveGame save(LiveGame liveGame);
}

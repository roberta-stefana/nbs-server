package main.repository;

import main.model.Stats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatsRepoJPA extends JpaRepository<Stats, Integer> {
    Stats save(Stats stats);
    List<Stats> findAllByIdGame(int idGame);
    List<Stats> findAllByIdPlayer(int idPlayer);
}

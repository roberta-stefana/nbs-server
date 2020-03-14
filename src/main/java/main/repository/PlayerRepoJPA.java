package main.repository;

import main.model.Player;
import main.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepoJPA extends JpaRepository<Player,Integer> {
    Player save(Player player);
    List<Player> findByTeam(Team team);
}

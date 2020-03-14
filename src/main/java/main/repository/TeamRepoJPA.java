package main.repository;

import main.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepoJPA extends JpaRepository<Team,Integer> {
    Team getOne(Integer idTeam);

    List<Team> findAll();
}

package main.repository;

import main.model.QuaterPoints;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuaterPointsRepoJPA extends JpaRepository<QuaterPoints, Integer> {
    QuaterPoints save(QuaterPoints quaterPoints);
}

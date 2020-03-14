package main.repository;

import main.model.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationUserRepoJPA extends JpaRepository<ApplicationUser,String> {
    ApplicationUser save(ApplicationUser applicationUser);
    ApplicationUser findByUsername(String username);
}

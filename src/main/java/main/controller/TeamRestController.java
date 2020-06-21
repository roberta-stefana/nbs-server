package main.controller;

import main.model.Team;
import main.service.IService;
import main.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "https://napoca-baschet-school.web.app/")
@RestController
@RequestMapping("/nbs/teams")
public class TeamRestController {
    @Autowired
    private IService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<Team> getAllTeam() {
        List<Team> teams = new ArrayList<>();
        service.findAllTeam().forEach(x -> teams.add(x));

        return teams;
    }

}

package main.controller;

import main.model.ApplicationUser;
import main.model.Player;
import main.model.Stats;
import main.model.StatsGameDTO;
import main.service.IService;
import main.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/nbs/stats")
public class StatsRestController {
    @Autowired
    private IService service;

    @RequestMapping(method = RequestMethod.POST)
    public Stats saveStats(@RequestBody Stats stats) {
        Stats newStats =service.saveStats(stats);
        return newStats;
    }

    @RequestMapping(value = "/team", method = RequestMethod.POST)
    public List<Stats> saveTeamStats(@RequestBody StatsGameDTO statsGameDTO) {
        List<Stats> newStats =service.saveTeamStats(statsGameDTO);
        return newStats;
    }



    /*
    @RequestMapping(value = "team/{teamId}", method = RequestMethod.GET)
    public List<Player> getAllTeam(@PathVariable int teamId) {
        List<Player> players = new ArrayList<>();
        service.findPlayersByTeam(teamId).forEach(x -> players.add(x));
        players.forEach(x-> System.out.println(x));
        return players;
    }*/

}

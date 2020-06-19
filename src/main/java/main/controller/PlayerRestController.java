package main.controller;

import main.model.Player;
import main.model.Stats;
import main.model.Team;
import main.service.IService;
import main.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "192.168.100.89:3000")
@RestController
@RequestMapping("/nbs/players")
public class PlayerRestController {
    @Autowired
    private IService service;

    @RequestMapping(method = RequestMethod.POST)
    public Player savePlayer(@RequestBody Player player) {
        Player newPlayer =service.savePlayer(player);
        return newPlayer;
    }

    @RequestMapping(value = "team/{teamId}", method = RequestMethod.GET)
    public List<Player> getAllTeam(@PathVariable int teamId) {
        List<Player> players = new ArrayList<>();
        service.findPlayersByTeam(teamId).forEach(x -> players.add(x));
        return players;
    }

}

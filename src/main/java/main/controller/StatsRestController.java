package main.controller;

import main.model.*;
import main.model.dto.StatsGameDTO;
import main.service.IService;
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

    @RequestMapping(value = "/game/{idGame}",method = RequestMethod.GET)
    public List<Stats> findAllTeamStatsByGame( @PathVariable int idGame) {
        List<Stats> stats = new ArrayList<>();
        service.findAllGameStats(idGame).forEach(x -> stats.add(x));
        return stats;
    }


}

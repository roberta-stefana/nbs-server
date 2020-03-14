package main.controller;

import main.model.Game;
import main.model.Player;
import main.model.QuaterPoints;
import main.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/nbs/games")
public class GameRestController {
    @Autowired
    private IService service;

    @RequestMapping(method = RequestMethod.POST)
    public Game saveGame(@RequestBody Game game) {
        Game newGame =service.saveGame(game);
        return newGame;
    }
}

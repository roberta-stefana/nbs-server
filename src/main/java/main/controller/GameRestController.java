package main.controller;

import main.model.*;
import main.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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

    @RequestMapping(method = RequestMethod.GET)
    public List<Game> getAllGame(@RequestParam(required = false) Boolean live) {
        List<Game> games= new ArrayList<>();
        if(live == null){
            service.findAllGame().forEach(x -> games.add(x));
        }else{
            service.findAllGameByLive(live).forEach(x -> games.add(x));
        }
        return games;
    }

    @RequestMapping(value = "/{idGame}", method = RequestMethod.GET)
    public Game getGameById(@PathVariable int idGame) {
        return service.findGameByIdGame(idGame);
    }

}

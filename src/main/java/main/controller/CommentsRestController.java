package main.controller;
import main.model.Comments;
import main.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "https://napoca-baschet-school.web.app/")
@RestController
@RequestMapping("/nbs/comments")
public class CommentsRestController {
    @Autowired
    private IService service;

    @RequestMapping(value = "/game/{idGame}",method = RequestMethod.GET)
    public List<Comments> getAllCommentsByGame(@PathVariable int idGame) {
        List<Comments> comments = new ArrayList<>();
        service.findAllCommentsByIdGameOrderByDate(idGame).forEach(x -> comments.add(x));

        return comments;
    }

}

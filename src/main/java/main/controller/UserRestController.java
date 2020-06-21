package main.controller;


import main.model.ApplicationUser;
import main.repository.ApplicationUserRepoJPA;
import main.service.IService;
import main.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/nbs/users")
public class UserRestController {
    @Autowired
    IService service;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserRestController(){}

    @RequestMapping(method = RequestMethod.POST)
    public ApplicationUser createUser(@RequestBody ApplicationUser applicationUser) {
        applicationUser.setPassword(bCryptPasswordEncoder.encode(applicationUser.getPassword()));
        ApplicationUser newUser =service.saveApplicationUser(applicationUser);
        return newUser;
    }


    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserById(@PathVariable String username) {
        try {
            ApplicationUser user = service.findByUsername(username);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception re) {
            return new ResponseEntity<>(re.getMessage(), HttpStatus.NOT_FOUND);
        }

    }
}

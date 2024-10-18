package user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import user.dto.UpdatePassword;
import user.dto.UserRequest;
import user.dto.UserResponse;
import user.dto.UserUpdateRequest;
import user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> addUser(@RequestBody UserRequest userRequest) {

       UserResponse userResponse= userService.registerUser(userRequest);
       return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<UserResponse> retrieveUser(@RequestParam String username) {
       UserResponse userResponse= userService.getUser(username);
       return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PutMapping
    public  ResponseEntity<UserResponse> updateUser(@RequestParam String username, @RequestBody UserUpdateRequest userRequest) {
        UserResponse userResponse=userService.updateUser(userRequest,username);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<String> updatePassword(@RequestParam String username, @RequestBody UpdatePassword updatePassword) {
     String response=   userService.changePassword(username,updatePassword);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUser(@RequestParam String username) {
      String response=  userService.deleteUser(username);
      return new ResponseEntity<>(response, HttpStatus.OK);

    }
}

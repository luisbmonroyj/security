package controllers;

import backend.models.*;
import backend.repositories.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user")

public class UserController {
    @Autowired
    private UserRepo myUserRepo;
    @Autowired
    private RoleRepo myRoleRepo;

    @GetMapping("")
    public List<User> index(){ return this.myUserRepo.findAll(); }

    @PostMapping()
    public User create(@RequestBody User newUser){
        //Check if the username does not exists 
        User test = myUserRepo.getUsername(newUser.getUsername());
        if (test!=null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");// with id "+test.get_id());
        else {
            Role undefined = myRoleRepo.getRoleByName("undefined");
            newUser.setIdRole(undefined);
            newUser.setPassword(encryptSHA256(newUser.getPassword()));
            return this.myUserRepo.save(newUser);
        }
    }
    
    @PatchMapping()
    public User update(@RequestBody User updateUser){
        User currentUser=this.myUserRepo.findById(updateUser.getId()).orElse(null);
        //first, check if the user exists
        if (currentUser!=null){
            //check if the new username is not used already
            User test = myUserRepo.getUsername(updateUser.getUsername());
            if (test!=null)
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");// with id "+test.get_id());
            else {
                currentUser.setLastname(updateUser.getLastname());
                currentUser.setName(updateUser.getName());
                currentUser.setEmail(updateUser.getEmail());
                currentUser.setAddress(updateUser.getAddress());
                currentUser.setPhone(updateUser.getPhone());
                currentUser.setPassword(encryptSHA256(updateUser.getPassword()));
                return this.myUserRepo.save(currentUser);
            }
        }
        else 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user you are trying to update does not exist");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping()
    public void delete(@RequestParam String id){
        User currentUser=this.myUserRepo.findById(id).orElse(null);
        if (currentUser!=null)
            this.myUserRepo.delete(currentUser);
        else 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user you are trying to delete does not exist");
    }

    @PutMapping()
    public User setRoleToUser(@RequestParam String id, @RequestParam String idRole){
        User currentUser = this.myUserRepo.findById(id).orElseThrow(RuntimeException::new);
        //check if the user exists
        if (currentUser==null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        else{
            Role currentRole = this.myRoleRepo.findById(idRole).orElseThrow(RuntimeException::new);
            if (currentRole==null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role does not exist");
            //if role exists, assign it to the user
            else
                currentUser.setId_rol(currentRole);
        }
        return this.myUserRepo.save(currentUser);
    }

    public String encryptSHA256(String password) {
        MessageDigest md = null;
        try { md = MessageDigest.getInstance("SHA-256"); } 
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        byte[] hash = md.digest(password.getBytes());
        StringBuffer sb = new StringBuffer();
        for (byte b : hash) 
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
    
}

package backend.controllers;

import backend.models.*;
import backend.repositories.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@CrossOrigin
@RestController
@RequestMapping("/accessToRole")
public class AccessToRoleController {
    @Autowired
    private AccessToRoleRepo myAccessToRoleRepo;
    @Autowired
    private RoleRepo myRoleRepo;
    @Autowired
    private AccessRepo myAccessRepo;

    @GetMapping("")
    public List<AccessToRole> index(){ return this.myAccessToRoleRepo.findAll(); }

    @GetMapping("accessValidation/role/{idRole}")
    public AccessToRole getAccessValidation(@PathVariable String idRole, @RequestBody Access accessData) {
        Access accessTest = myAccessRepo.getAccess(accessData.getUrl(),accessData.getMethod());
        if (accessTest!=null) {
            AccessToRole output = myAccessToRoleRepo.getRoleAccess(idRole, accessTest.getIdAccess());
            if (output != null)
                return output;
            else
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This Role has not this access granted");
        }
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The access data does not match");
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public AccessToRole create(@RequestParam String idRole, @RequestParam String idAccess){
        return this.setAccessToRole(idRole,idAccess,new AccessToRole(),"create");
    }
    
    @PutMapping("{id}/role/{idRole}/access/{idAccess}")
    public AccessToRole update(@PathVariable String id, @PathVariable String idRole, @PathVariable String idAccess){
        AccessToRole currentAtoR = this.myAccessToRoleRepo.findById(id).orElse(null);
        if(currentAtoR!=null)
            return this.setAccessToRole(idRole,id,currentAtoR,"update");
        else 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The access to role does not exist");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping()
    public void delete(@RequestParam String id){
        AccessToRole currentAtoR=this.myAccessToRoleRepo.findById(id).orElse(null);
        if (currentAtoR!=null)
            this.myAccessToRoleRepo.delete(currentAtoR);
        else 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The access to role does not exist");
    }
    public AccessToRole setAccessToRole (String idRole, String idAccess, AccessToRole output, String methodType){
        /*This method is used by the create and the update method because of the similarities of data needed*/
        try {
            //Try to create a Role with the given id
            Role aRole = this.myRoleRepo.findById(idRole).get();
            try {
                //if the role is created, try to create the access with the given id
                Access accessTest = this.myAccessRepo.findById(idAccess).get();
                //if both exist, output is the new or the existing AccessToRole
                output.setIdAccess(accessTest);
                output.setIdRole(aRole);
                return this.myAccessToRoleRepo.save(output);
            }
            catch (NoSuchElementException exception) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The access to Role cannot be " + methodType + "d, the access does not exist");
            }
        }
        catch (NoSuchElementException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The access to Role cannot be " + methodType + "d, the role does not exist");
            //methodType specifies if it is the create or the update method
        }
    }
}


package backend.repositories;

import backend.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface UserRepo extends MongoRepository<Usuario,String> {
    @Query("{'username': ?0}")
    public Usuario getUsername(String username);

    @Query("{'id': ?0}")
    public Usuario getUserByID(String id);

    @Query("{'idRole': ?0}")
    public List<Usuario> getUsersWithRole(String idRole);
}

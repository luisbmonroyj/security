package backend.repositories;

import backend.models.AccessToRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface AccessToRoleRepo extends MongoRepository <RolPermiso,String> {

        @Query("{'idAccess': ?0}")
        List<AccessToRole> getRolesConPermiso(String _id_permiso);
    
        @Query("{'_id_rol': ?0}")
        List<RolPermiso> getPermisosDelRol(String _id_rol);
        @Query("{'_id_rol': ?0,'_id_permiso': ?1}")
        RolPermiso getPermisoRol(String _id_rol, String _id_permiso);
}

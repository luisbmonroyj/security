package backend.repositories;

import backend.models.Access;

public interface AccessRepo extends MongoRepository<Access,String> {
    @Query("{'url':?0,'method':?1}")
    Access getAccess(String url, String method);
}

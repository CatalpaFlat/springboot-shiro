package cn.catalpaflat.repository;

import cn.catalpaflat.model.po.UserPO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiroPermissionRepository extends BaseRepository<UserPO,Long> {

    UserPO findAllByName(@Param("name") String name);

    @Query(nativeQuery = true,value = "SELECT r.name FROM user u JOIN role_user rn ON rn.user_id = u.id JOIN role r ON r.id = rn.role_id WHERE u.name = :name")
    List<String> queryRoleByName(@Param("name") String name);
}

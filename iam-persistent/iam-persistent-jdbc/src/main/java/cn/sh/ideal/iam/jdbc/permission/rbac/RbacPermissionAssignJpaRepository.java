package cn.sh.ideal.iam.jdbc.permission.rbac;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 宋志宗 on 2024/5/16
 */
public interface RbacPermissionAssignJpaRepository extends JpaRepository<RbacPermissionAssignDO, Long> {

}

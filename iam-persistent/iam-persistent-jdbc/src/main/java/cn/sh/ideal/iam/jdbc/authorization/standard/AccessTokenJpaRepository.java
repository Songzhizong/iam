package cn.sh.ideal.iam.jdbc.authorization.standard;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 宋志宗 on 2024/5/29
 */
public interface AccessTokenJpaRepository extends JpaRepository<AccessTokenDO, Long> {

}

package cn.sh.ideal.iam.jdbc.factor.otp;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 宋志宗 on 2024/5/29
 */
public interface UserTotpJpaRepository extends JpaRepository<UserTotpDO, Long> {
}

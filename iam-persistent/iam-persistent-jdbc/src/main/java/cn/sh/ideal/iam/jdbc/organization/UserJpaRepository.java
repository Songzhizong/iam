package cn.sh.ideal.iam.jdbc.organization;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/15
 */
public interface UserJpaRepository extends JpaRepository<UserDO, Long> {

    boolean existsByTenantIdAndAccount(long tenantId, @Nonnull String account);
}
package cn.sh.ideal.iam.jdbc.authorization.standard;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * @author 宋志宗 on 2024/5/29
 */
public interface AccessTokenJpaRepository extends JpaRepository<AccessTokenDO, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM iam_access_token WHERE id_ IN (:ids)", nativeQuery = true)
    void deleteAllByIdIn(@Nonnull @Param("ids") Collection<Long> ids);


    @Nonnull
    List<AccessTokenDO> findAllByUserIdAndClientId(@Nonnull Long userId,
                                                   @Nonnull Long clientId,
                                                   @Nonnull Pageable pageable);
}

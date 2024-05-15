package cn.sh.ideal.iam.jdbc.organization;

import cn.sh.ideal.iam.organization.domain.model.SecurityContainer;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * @author 宋志宗 on 2024/5/14
 */
public interface SecurityContainerJpaRepository extends JpaRepository<SecurityContainerDO, Long> {

    boolean existsByParentId(long parentId);

    boolean existsByParentIdAndName(long parentId, @Nonnull String name);

    @Nonnull
    List<SecurityContainerDO> findAllByParentIdIn(@Nonnull Collection<Long> parentIds);
}

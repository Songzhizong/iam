package cn.sh.ideal.iam.jdbc.organization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

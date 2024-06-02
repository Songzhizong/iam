package cn.sh.ideal.iam.jdbc.permission.front;

import cn.sh.ideal.iam.common.constant.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/16
 */
public interface AppJpaRepository extends JpaRepository<AppDO, Long> {

    boolean existsByIdGreaterThan(long idGt);

    boolean existsByTerminalAndRootPath(@Nonnull Terminal terminal, @Nonnull String rootPath);
}

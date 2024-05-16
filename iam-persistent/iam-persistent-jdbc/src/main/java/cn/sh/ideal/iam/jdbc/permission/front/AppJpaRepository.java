package cn.sh.ideal.iam.jdbc.permission.front;

import cn.sh.ideal.iam.core.constant.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface AppJpaRepository extends JpaRepository<AppDO, Long> {

    boolean existsByTerminalAndRootPath(@Nonnull Terminal terminal, @Nonnull String rootPath);
}

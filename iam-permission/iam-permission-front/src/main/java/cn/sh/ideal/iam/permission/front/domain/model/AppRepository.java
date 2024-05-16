package cn.sh.ideal.iam.permission.front.domain.model;

import cn.sh.ideal.iam.core.constant.Terminal;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface AppRepository {

    @Nonnull
    App insert(@Nonnull App app);

    @Nonnull
    App save(@Nonnull App app);

    void delete(@Nonnull App app);

    @Nonnull
    Optional<App> findById(long id);

    @Nonnull
    List<App> findAll();

    boolean existsByTerminalAndRootPath(@Nonnull Terminal terminal, @Nonnull String rootPath);
}

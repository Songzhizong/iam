package cn.sh.ideal.iam.jdbc.permission.front;

import cn.sh.ideal.iam.core.constant.Terminal;
import cn.sh.ideal.iam.permission.front.domain.model.App;
import cn.sh.ideal.iam.permission.front.domain.model.AppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Repository
@RequiredArgsConstructor
public class AppRepositoryImpl implements AppRepository {
    private final AppJpaRepository appJpaRepository;


    @Nonnull
    @Override
    public App insert(@Nonnull App app) {
        AppDO entity = (AppDO) app;
        return appJpaRepository.save(entity);
    }

    @Nonnull
    @Override
    public App save(@Nonnull App app) {
        AppDO entity = (AppDO) app;
        return appJpaRepository.save(entity);
    }

    @Override
    public void delete(@Nonnull App app) {
        AppDO entity = (AppDO) app;
        appJpaRepository.delete(entity);
    }

    @Nonnull
    @Override
    public Optional<App> findById(long id) {
        return appJpaRepository.findById(id).map(e -> e);
    }

    @Nonnull
    @Override
    public List<App> findAll() {
        return appJpaRepository.findAll()
                .stream().map(e -> (App) e).toList();
    }

    @Override
    public boolean existsByTerminalAndRootPath(@Nonnull Terminal terminal, @Nonnull String rootPath) {
        return appJpaRepository.existsByTerminalAndRootPath(terminal, rootPath);
    }
}

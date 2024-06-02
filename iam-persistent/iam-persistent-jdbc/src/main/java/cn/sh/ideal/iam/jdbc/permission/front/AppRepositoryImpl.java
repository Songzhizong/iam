package cn.sh.ideal.iam.jdbc.permission.front;

import cn.idealio.framework.concurrent.Asyncs;
import cn.sh.ideal.iam.common.constant.Terminal;
import cn.sh.ideal.iam.permission.front.domain.model.App;
import cn.sh.ideal.iam.permission.front.domain.model.AppRepository;
import cn.sh.ideal.iam.permission.front.domain.model.AppRepositoryListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/16
 */
@Repository
@RequiredArgsConstructor
public class AppRepositoryImpl implements AppRepository {
    private final AppJpaRepository appJpaRepository;


    @Nonnull
    @Override
    public App insert(@Nonnull App app) {
        AppDO entity = (AppDO) app;
        AppDO saved = appJpaRepository.saveAndFlush(entity);
        Asyncs.executeVirtual(() -> {
            for (AppRepositoryListener listener : listeners) {
                listener.onAppTableChanged();
            }
        });
        return saved;
    }

    @Nonnull
    @Override
    public App save(@Nonnull App app) {
        AppDO entity = (AppDO) app;
        AppDO saved = appJpaRepository.saveAndFlush(entity);
        Asyncs.executeVirtual(() -> {
            for (AppRepositoryListener listener : listeners) {
                listener.onAppTableChanged();
            }
        });
        return saved;
    }

    @Override
    public void delete(@Nonnull App app) {
        AppDO entity = (AppDO) app;
        appJpaRepository.delete(entity);
        Asyncs.executeVirtual(() -> {
            for (AppRepositoryListener listener : listeners) {
                listener.onAppTableChanged();
            }
        });
    }

    @Nonnull
    @Override
    public Optional<App> findById(@Nonnull Long id) {
        return appJpaRepository.findById(id).map(e -> e);
    }

    @Nonnull
    @Override
    public List<App> findAll() {
        return appJpaRepository.findAll()
                .stream().map(e -> (App) e).toList();
    }

    @Override
    public boolean exists() {
        return appJpaRepository.existsByIdGreaterThan(0);
    }

    @Override
    public boolean existsByTerminalAndRootPath(@Nonnull Terminal terminal, @Nonnull String rootPath) {
        return appJpaRepository.existsByTerminalAndRootPath(terminal, rootPath);
    }
}

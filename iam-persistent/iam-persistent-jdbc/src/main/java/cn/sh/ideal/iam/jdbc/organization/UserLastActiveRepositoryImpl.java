package cn.sh.ideal.iam.jdbc.organization;

import cn.sh.ideal.iam.infrastructure.user.UserLastActiveRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Repository
@RequiredArgsConstructor
public class UserLastActiveRepositoryImpl implements UserLastActiveRepository {
    private static final Cache<Long, Boolean> EXISTS_CACHE = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofHours(48))
            .maximumSize(1000).build();
    private final UserLastActiveJpaRepository userLastActiveJpaRepository;

    @Override
    public void updateLastActiveTime(@Nonnull Long userId) {
        long currentTimeMillis = System.currentTimeMillis();
        EXISTS_CACHE.get(userId, k -> {
            userLastActiveJpaRepository.findById(userId).orElseGet(() -> {
                UserLastActiveDO entity = new UserLastActiveDO();
                entity.setId(userId);
                entity.setLastActiveTime(currentTimeMillis);
                return userLastActiveJpaRepository.save(entity);
            });
            return true;
        });
        userLastActiveJpaRepository.updateLastActiveTimeById(userId, currentTimeMillis);
    }

    @Nullable
    @Override
    public Long getLastActiveTime(@Nonnull Long userId) {
        return userLastActiveJpaRepository.findById(userId)
                .map(UserLastActiveDO::getLastActiveTime).orElse(null);
    }

    @Nonnull
    @Override
    public Map<Long, Long> getLastActiveTime(@Nonnull Collection<Long> userIds) {
        List<UserLastActiveDO> entities = userLastActiveJpaRepository.findAllById(userIds);
        return entities.stream().collect(
                Collectors.toMap(UserLastActiveDO::getId, UserLastActiveDO::getLastActiveTime));
    }
}

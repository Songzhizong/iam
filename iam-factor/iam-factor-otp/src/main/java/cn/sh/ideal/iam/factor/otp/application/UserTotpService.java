package cn.sh.ideal.iam.factor.otp.application;

import cn.idealio.framework.cache.Cache;
import cn.idealio.framework.cache.CacheFactory;
import cn.idealio.framework.cache.serialize.String2StringSerializer;
import cn.idealio.framework.exception.BadRequestException;
import cn.idealio.framework.exception.ForbiddenException;
import cn.idealio.framework.exception.ResourceNotFoundException;
import cn.idealio.framework.lang.StringUtils;
import cn.sh.ideal.iam.factor.otp.domain.model.EntityFactory;
import cn.sh.ideal.iam.factor.otp.domain.model.UserTotp;
import cn.sh.ideal.iam.factor.otp.domain.model.UserTotpRepository;
import cn.sh.ideal.iam.factor.otp.util.TOTP;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.organization.domain.model.User;
import cn.sh.ideal.iam.organization.domain.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.time.Duration;

/**
 * @author 宋志宗 on 2024/5/29
 */
@Slf4j
@Service
public class UserTotpService {
    private final IamI18nReader i18nReader;
    private final EntityFactory entityFactory;
    private final Cache<Long, String> totpCache;
    private final UserRepository userRepository;
    private final UserTotpRepository userTotpRepository;

    public UserTotpService(@Nonnull IamI18nReader i18nReader,
                           @Nonnull CacheFactory cacheFactory,
                           @Nonnull EntityFactory entityFactory,
                           @Nonnull UserRepository userRepository,
                           @Nonnull UserTotpRepository userTotpRepository) {
        this.i18nReader = i18nReader;
        this.entityFactory = entityFactory;
        this.userRepository = userRepository;
        this.userTotpRepository = userTotpRepository;
        this.totpCache = cacheFactory.<Long, String>newBuilder(String2StringSerializer.instance())
                .expireAfterWrite(Duration.ofMinutes(5)).build("iam:totp:cache");
    }

    public void authenticate(@Nonnull Long userId, int code) {
        userTotpRepository.findByUserId(userId).ifPresentOrElse(e -> {
            String secret = e.getSecret();
            if (!TOTP.authenticate(secret, code)) {
                log.info("用户TOTP验证失败, userId: {}", userId);
                throw new ForbiddenException(i18nReader.getMessage("totp.authenticate_failed"));
            }
        }, () -> {
            log.info("用户TOTP不存在, userId: {}", userId);
            throw new ForbiddenException(i18nReader.getMessage("totp.not_exists"));
        });
    }

    @Nonnull
    public TOTP generate(@Nonnull Long userId) {
        userTotpRepository.findByUserId(userId).ifPresent(e -> {
            log.info("用户已经存在TOTP, userId: {}", userId);
            throw new BadRequestException(i18nReader.getMessage1("totp.already_exists", userId));
        });
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.info("生成用户TOTP时用户不存在, userId: {}", userId);
            return new ResourceNotFoundException(i18nReader.getMessage1("user.not_found", userId));
        });
        String accountName = user.getAccount();
        if (StringUtils.isBlank(accountName)) {
            accountName = user.getName();
        }
        String platform = user.getPlatform();
        TOTP totp = TOTP.generate(platform, accountName);
        String secret = totp.getSecret();
        totpCache.put(userId, secret);
        return totp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void confirmation(@Nonnull Long userId, int code) {
        String secret = totpCache.getIfPresent(userId);
        if (StringUtils.isBlank(secret)) {
            log.info("确认失败, 用户没有缓存TOTP数据, userId: {}", userId);
            throw new BadRequestException(i18nReader.getMessage1("totp.not_exists", userId));
        }
        if (!TOTP.authenticate(secret, code)) {
            log.info("确认失败, 用户TOTP验证失败, userId: {}", userId);
            throw new BadRequestException(i18nReader.getMessage1("totp.authenticate_failed", userId));
        }
        UserTotp userTotp = entityFactory.userTotp(userId, secret);
        userTotpRepository.insert(userTotp);
        totpCache.invalidate(userId);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void delete(@Nonnull Long userId) {
        userTotpRepository.findByUserId(userId).ifPresentOrElse(e -> {
            userTotpRepository.delete(e);
            log.info("删除用户TOTP, userId: {}", userId);
        }, () -> log.info("未能删除, 用户TOTP不存在, userId: {}", userId));
    }
}

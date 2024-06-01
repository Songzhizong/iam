package cn.sh.ideal.iam.organization.application;

import cn.sh.ideal.iam.infrastructure.encoder.password.PasswordEncoder;
import cn.sh.ideal.iam.infrastructure.user.UserDetail;
import cn.sh.ideal.iam.infrastructure.user.UserDetailService;
import cn.sh.ideal.iam.organization.domain.model.TenantRepository;
import cn.sh.ideal.iam.organization.domain.model.User;
import cn.sh.ideal.iam.organization.domain.model.UserCache;
import cn.sh.ideal.iam.organization.domain.model.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailService {
    private final UserCache userCache;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TenantRepository tenantRepository;

    @Nullable
    @Override
    public UserDetail loadUserByUsername(@Nonnull String platform,
                                         @Nonnull String username,
                                         @Nonnull String password) {
        int index = username.lastIndexOf('@');
        if (index < 0) {
            log.info("登录账号格式错误: {}", username);
            return null;
        }
        String account = username.substring(0, index);
        String abbreviation = username.substring(index + 1);
        User user = tenantRepository.findByPlatformAndAbbreviation(platform, abbreviation)
                .flatMap(tenant -> userRepository.findByTenantIdAndAccount(tenant.getId(), account))
                .or(() -> userRepository.findByPlatformAndEmail(platform, username))
                .orElse(null);
        if (user == null) {
            log.info("用户不存在: {} {}", platform, username);
            return null;
        }
        String encodedPassword = user.getPassword();
        if (!passwordEncoder.matches(password, encodedPassword)) {
            log.info("密码错误: {}", username);
            return null;
        }
        return new UserDetailImpl(user);
    }

    @Nullable
    @Override
    public UserDetail findById(long userId) {
        User user = userCache.get(userId).orElse(null);
        if (user == null) {
            log.warn("获取UserDetail失败, 用户不存在: {}", userId);
            return null;
        }
        return new UserDetailImpl(user);
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    @SuppressWarnings("ClassCanBeRecord")
    public static class UserDetailImpl implements UserDetail {
        private final User user;

        @Override
        public long getId() {
            return user.getId();
        }

        @Nonnull
        @Override
        public String getPlatform() {
            return user.getPlatform();
        }

        @Override
        public long getTenantId() {
            return user.getTenantId();
        }

        @Nonnull
        @Override
        public String getName() {
            return user.getName();
        }

        @Nullable
        @Override
        public String getAccount() {
            return user.getAccount();
        }

        @Nullable
        @Override
        public String getPhone() {
            return user.getPhone();
        }

        @Nullable
        @Override
        public String getEmail() {
            return user.getEmail();
        }

        @Override
        public boolean isBlocked() {
            return user.isBlocked();
        }

        @Override
        public boolean isAccountExpired() {
            // TODO 账号过期机制?
            return false;
        }

        @Override
        public boolean isPasswordExpired() {
            // TODO 根据配置判断密码是否过期
            return false;
        }
    }
}

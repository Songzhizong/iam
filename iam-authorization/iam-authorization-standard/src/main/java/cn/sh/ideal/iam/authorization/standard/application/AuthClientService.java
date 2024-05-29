package cn.sh.ideal.iam.authorization.standard.application;

import cn.sh.ideal.iam.authorization.standard.domain.model.AuthClient;
import cn.sh.ideal.iam.authorization.standard.domain.model.AuthClientRepository;
import cn.sh.ideal.iam.authorization.standard.domain.model.EntityFactory;
import cn.sh.ideal.iam.authorization.standard.dto.args.CreateAuthClientArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthClientService {
    private final EntityFactory entityFactory;
    private final AuthClientRepository authClientRepository;

    @Nonnull
    @Transactional(rollbackFor = Throwable.class)
    public AuthClient create(@Nonnull CreateAuthClientArgs args) {
        AuthClient authClient = entityFactory.authClient(args);
        return authClientRepository.insert(authClient);
    }
}

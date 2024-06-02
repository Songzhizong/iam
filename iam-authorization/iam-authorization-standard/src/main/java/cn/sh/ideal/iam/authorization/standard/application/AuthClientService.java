package cn.sh.ideal.iam.authorization.standard.application;

import cn.sh.ideal.iam.authorization.standard.domain.model.AuthClient;
import cn.sh.ideal.iam.authorization.standard.domain.model.AuthClientRepository;
import cn.sh.ideal.iam.authorization.standard.domain.model.StandardAuthorizationEntityFactory;
import cn.sh.ideal.iam.authorization.standard.dto.args.CreateAuthClientArgs;
import cn.sh.ideal.iam.infrastructure.configure.IamIDGenerator;
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
    private final IamIDGenerator idGenerator;
    private final AuthClientRepository authClientRepository;
    private final StandardAuthorizationEntityFactory entityFactory;

    @Nonnull
    @Transactional(rollbackFor = Throwable.class)
    public AuthClient create(@Nonnull CreateAuthClientArgs args) {
        long id = idGenerator.generate();
        AuthClient authClient = entityFactory.authClient(id, args);
        return authClientRepository.insert(authClient);
    }
}

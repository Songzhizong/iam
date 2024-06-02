package cn.sh.ideal.iam.ops.application;

import cn.idealio.framework.exception.ResourceNotFoundException;
import cn.idealio.framework.util.Asserts;
import cn.sh.ideal.iam.authorization.standard.domain.model.AuthClient;
import cn.sh.ideal.iam.authorization.standard.domain.model.AuthClientInfo;
import cn.sh.ideal.iam.authorization.standard.domain.model.AuthClientRepository;
import cn.sh.ideal.iam.authorization.standard.domain.model.StandardAuthorizationEntityFactory;
import cn.sh.ideal.iam.ops.dto.resp.PlatformDetail;
import cn.sh.ideal.iam.organization.domain.model.OrganizationEntityFactory;
import cn.sh.ideal.iam.organization.domain.model.Platform;
import cn.sh.ideal.iam.organization.domain.model.PlatformRepository;
import cn.sh.ideal.iam.organization.dto.resp.PlatformInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 宋志宗 on 2024/6/2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformOpsService {
    private final PlatformRepository platformRepository;
    private final AuthClientRepository authClientRepository;
    private final OrganizationEntityFactory organizationEntityFactory;
    private final StandardAuthorizationEntityFactory standardAuthorizationEntityFactory;

    @Nonnull
    public static String formatPlatformConfigName(@Nonnull String code) {
        return "platform_config_" + code + ".json";
    }

    @Nonnull
    public PlatformDetail export(@Nonnull String code) {
        Platform platform = platformRepository.findByCode(code).orElseThrow(() -> {
            log.info("导出平台配置失败, 平台不存在: {}", code);
            return new ResourceNotFoundException("平台不存在");
        });
        List<AuthClient> clients = authClientRepository.findAllByPlatform(code);
        PlatformInfo platformInfo = platform.toInfo();
        List<AuthClientInfo> clientInfos = clients.stream().map(AuthClient::toInfo).toList();
        return new PlatformDetail(platformInfo, clientInfos);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void reload(@Nonnull PlatformDetail detail) {
        PlatformInfo platformInfo = detail.getPlatform();
        Asserts.nonnull(platformInfo, "平台信息为空");
        String code = platformInfo.getCode();
        // 清除之前的数据
        int acc = authClientRepository.deleteAllByPlatform(code);
        platformRepository.findByCode(code).ifPresent(platformRepository::delete);
        log.info("平台[{}]删除成功, 删除授权客户端: {}条", code, acc);

        Platform platform = organizationEntityFactory.platform(platformInfo);
        List<AuthClientInfo> authClients = detail.getAuthClients();
        List<AuthClient> clients = authClients.stream()
                .map(standardAuthorizationEntityFactory::authClient).toList();

        platformRepository.insert(platform);
        authClientRepository.insert(clients);
        log.info("平台[{}]导入成功, 导入授权端: {}条", code, clients.size());
    }
}

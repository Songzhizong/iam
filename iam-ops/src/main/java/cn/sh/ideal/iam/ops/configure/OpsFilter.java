package cn.sh.ideal.iam.ops.configure;

import cn.idealio.framework.exception.ForbiddenException;
import cn.idealio.framework.spring.ServletUtils;
import cn.idealio.framework.util.net.IpMatcher;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * 运维接口过滤器, 只允许白名单中的IP访问
 *
 * @author 宋志宗 on 2024/6/2
 */
@Slf4j
@Component
public class OpsFilter implements Filter {
    private final IpMatcher whitelistMatcher;

    public OpsFilter(OpsProperties opsProperties) {
        this.whitelistMatcher = new IpMatcher(opsProperties.getIpWhitelist());
    }


    @Override
    public void doFilter(@Nonnull ServletRequest servletRequest,
                         @Nonnull ServletResponse servletResponse,
                         @Nonnull FilterChain filterChain) throws IOException, ServletException {
        if (!(servletRequest instanceof HttpServletRequest request)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String requestURI = request.getRequestURI();
        if (!requestURI.startsWith("/iam/ops")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String address = ServletUtils.getFirstRemoteAddress(request);
        if (address == null || !whitelistMatcher.matches(address)) {
            log.warn("ip地址: {} 不在白名单中, 无法访问运维接口", address);
            throw new ForbiddenException("Deny access by whitelist");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

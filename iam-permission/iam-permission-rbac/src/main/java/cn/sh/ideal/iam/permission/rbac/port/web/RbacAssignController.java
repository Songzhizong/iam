package cn.sh.ideal.iam.permission.rbac.port.web;

import cn.sh.ideal.iam.permission.rbac.application.RbacAssignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RBAC权限分配管理
 *
 * @author 宋志宗 on 2024/2/5
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/iam/rbac")
public class RbacAssignController {
    private final RbacAssignService rbacAssignService;

}

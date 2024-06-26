package cn.sh.ideal.iam.permission.front.domain.model;

import cn.sh.ideal.iam.common.constant.Terminal;
import cn.sh.ideal.iam.permission.core.PermissionModel;
import cn.sh.ideal.iam.permission.front.dto.resp.AppInfo;

import javax.annotation.Nonnull;

/**
 * 前端应用
 *
 * @author 宋志宗 on 2024/5/16
 */
public interface App {

    @Nonnull
    Long getId();

    @Nonnull
    Terminal getTerminal();

    @Nonnull
    PermissionModel getPermissionModel();

    @Nonnull
    String getRootPath();

    @Nonnull
    String getName();

    @Nonnull
    String getNote();

    boolean isApiAuthenticateEnabled();

    int getOrderNum();

    @Nonnull
    String getConfig();

    @Nonnull
    default AppInfo toInfo() {
        AppInfo appInfo = new AppInfo();
        appInfo.setId(getId());
        appInfo.setTerminal(getTerminal());
        appInfo.setRootPath(getRootPath());
        appInfo.setName(getName());
        appInfo.setNote(getNote());
        appInfo.setOrderNum(getOrderNum());
        appInfo.setConfig(getConfig());
        return appInfo;
    }
}

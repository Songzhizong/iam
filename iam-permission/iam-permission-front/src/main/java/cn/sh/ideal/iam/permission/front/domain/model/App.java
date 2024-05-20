package cn.sh.ideal.iam.permission.front.domain.model;

import cn.sh.ideal.iam.core.constant.Terminal;
import cn.sh.ideal.iam.permission.front.dto.resp.AppInfo;

import javax.annotation.Nonnull;

/**
 * 前端应用
 *
 * @author 宋志宗 on 2024/2/5
 */
public interface App {

    Long getId();

    @Nonnull
    Terminal getTerminal();

    @Nonnull
    String getRootPath();

    @Nonnull
    String getName();

    @Nonnull
    String getNote();

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

package cn.sh.ideal.iam.permission.front.domain.model;

import cn.idealio.framework.exception.BadRequestException;
import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.spring.matcher.PathMatchers;
import cn.sh.ideal.iam.permission.front.dto.resp.PermissionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 权限点
 *
 * @author 宋志宗 on 2024/2/5
 */
public interface Permission {
    Logger log = LoggerFactory.getLogger(Permission.class);

    Long getId();

    long getAppId();

    long getGroupId();

    long getItemId();

    @Nonnull
    String getName();

    @Nonnull
    Set<String> getApiPatterns();

    void setApiPatterns(@Nonnull Set<String> apiPatterns);

    @Nonnull
    Set<String> getSpecificApis();

    void setSpecificApis(@Nonnull Set<String> specificApis);

    @Nonnull
    Set<String> getAuthorities();

    @Nonnull
    Set<Long> getChildIds();

    boolean isAllInItem();

    boolean isItemSecurity();

    boolean isGroupSecurity();

    boolean isEnabled();

    int getOrderNum();

    default boolean available() {
        return isEnabled();
    }

    @Nonnull
    default Set<String> mergeApis() {
        Set<String> apiPatterns = getApiPatterns();
        Set<String> specificApis = getSpecificApis();
        int initialCapacity = apiPatterns.size() + specificApis.size();
        Set<String> apis = new LinkedHashSet<>(Math.max(initialCapacity, 16));
        apis.addAll(apiPatterns);
        apis.addAll(specificApis);
        return apis;
    }

    default void setApis(@Nonnull Set<String> apis) {
        Set<String> apiPatterns = new LinkedHashSet<>();
        Set<String> specificApis = new LinkedHashSet<>();
        for (String api : apis) {
            String[] split = StringUtils.split(api, " ");
            if (split.length > 2) {
                String message = "非法的api地址: " + api;
                log.info(message);
                throw new BadRequestException(message);
            }
            String path;
            if (split.length == 1) {
                path = split[0];
            } else {
                path = split[1];
            }
            if (PathMatchers.isPattern(path)) {
                apiPatterns.add(api);
            } else {
                specificApis.add(api);
            }
        }
        setApiPatterns(apiPatterns);
        setSpecificApis(specificApis);
    }

    @Nonnull
    default PermissionInfo toInfo() {
        PermissionInfo permissionInfo = new PermissionInfo();
        permissionInfo.setId(getId());
        permissionInfo.setAppId(getAppId());
        permissionInfo.setGroupId(getGroupId());
        permissionInfo.setItemId(getItemId());
        permissionInfo.setName(getName());
        permissionInfo.setApis(mergeApis());
        permissionInfo.setAuthorities(getAuthorities());
        permissionInfo.setChildIds(getChildIds());
        permissionInfo.setItemSecurity(isItemSecurity());
        permissionInfo.setGroupSecurity(isGroupSecurity());
        permissionInfo.setAllInItem(isAllInItem());
        permissionInfo.setEnabled(isEnabled());
        permissionInfo.setOrderNum(getOrderNum());
        return permissionInfo;
    }
}

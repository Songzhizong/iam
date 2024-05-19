package cn.sh.ideal.iam.permission.tbac.application;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author 宋志宗 on 2024/5/18
 */
public interface TbacHandler {

    /**
     * 获取用户在指定安全容器上所有可见的权限ID
     * <p>
     * 这个方法主要用于前端渲染用户有权限的页面元素
     *
     * @param userId      用户ID
     * @param containerId 容器ID
     * @return 可见权限ID列表
     * @author 宋志宗 on 2024/5/18
     */
    @Nonnull
    Set<Long> visiblePermissionIds(long userId, long containerId);

    /**
     * 获取用户拥有指定权限的容器id列表
     *
     * @param userId          用户ID
     * @param authority       权限标识
     * @param baseContainerId 基础容器ID, 不为空则获取该节点及其下的节点列表, 为空则获取所有
     * @return 有指定权限的容器ID列表
     * @author 宋志宗 on 2024/5/18
     */
    @Nonnull
    Set<Long> authorityContainerIds(long userId,
                                    @Nonnull String authority,
                                    @Nullable Long baseContainerId);

    /**
     * 过滤用户在指定安全容器上有权限的权限ID列表
     *
     * @param userId        用户ID
     * @param containerId   安全容器ID
     * @param permissionIds 需要过滤的权限ID列表, 从这个列表中过滤出用户有权限的权限ID
     * @return 用户有权限的权限ID列表
     * @author 宋志宗 on 2024/5/18
     */
    @Nonnull
    Set<Long> containerPermissionIds(long userId, long containerId, @Nonnull Set<Long> permissionIds);

    /**
     * 批量过滤用户在指定安全容器上有权限的权限ID列表
     *
     * @param userId        用户ID
     * @param containerIds  安全容器ID列表
     * @param permissionIds 需要过滤的权限ID列表, 从这个列表中过滤出用户有权限的权限ID
     * @return 用户有权限的权限ID列表
     * @author 宋志宗 on 2024/5/18
     */
    @Nonnull
    Map<Long, Set<Long>> containerPermissionIds(long userId,
                                                @Nonnull Set<Long> containerIds,
                                                @Nonnull Set<Long> permissionIds);

    /**
     * 判断用户在指定安全容器上是否拥有指定权限
     * <p>
     * 权限判断包括指定安全容器及其下所有安全容器的权限
     *
     * @param userId      用户ID
     * @param containerId 安全容器ID
     * @param authority   权限标识
     * @return 是否拥有指定权限
     * @author 宋志宗 on 2024/5/18
     */
    boolean hasAuthority(long userId, long containerId, @Nonnull String authority);

    /**
     * 判断用户在指定安全容器上是否拥有任一权限
     * <p>
     * 权限判断包括指定安全容器及其下所有安全容器的权限
     *
     * @param userId      用户ID
     * @param containerId 安全容器ID
     * @param authorities 权限标识列表
     * @return 是否拥有任一权限
     * @author 宋志宗 on 2024/5/18
     */
    boolean hasAnyAuthority(long userId, long containerId, @Nonnull Collection<String> authorities);

    /**
     * 判断用户在指定安全容器上是否拥有所有权限
     * <p>
     * 权限判断包括指定安全容器及其下所有安全容器的权限
     *
     * @param userId      用户ID
     * @param containerId 安全容器ID
     * @param authorities 权限标识列表
     * @return 是否拥有所有权限
     * @author 宋志宗 on 2024/5/18
     */
    boolean hasAllAuthority(long userId, long containerId, @Nonnull Collection<String> authorities);

    /**
     * 判断用户是否拥有API接口的访问权限
     * <p>
     * 权限判断包括指定安全容器及其下所有安全容器的权限
     *
     * @param userId      用户ID
     * @param containerId 容器ID
     * @param method      http请求方法
     * @param path        请求路径
     * @return 是否拥有接口权限
     * @author 宋志宗 on 2024/5/18
     */
    boolean hasApiPermission(long userId, long containerId,
                             @Nonnull String method, @Nonnull String path);

}
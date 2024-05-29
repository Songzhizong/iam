package cn.sh.ideal.iam.permission.tbac.domain.model;

import cn.idealio.framework.exception.BadRequestException;
import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.util.NumberSystemConverter;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.permission.tbac.dto.resp.SecurityContainerInfo;
import cn.sh.ideal.iam.permission.tbac.dto.resp.SecurityContainerTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.SequencedSet;
import java.util.stream.Collectors;

/**
 * @author 宋志宗 on 2024/5/14
 */
public interface SecurityContainer {
    Logger log = LoggerFactory.getLogger(SecurityContainer.class);

    Long getId();

    @Nullable
    Long getParentId();

    void setParentId(@Nullable Long parentId);

    @Nonnull
    String getParentRoute();

    void setParentRoute(@Nonnull String parentRoute);

    @Nonnull
    String getName();

    void setName(@Nonnull String name);

    default void changeParent(@Nullable SecurityContainer parent,
                              @Nonnull IamI18nReader i18nReader) {
        if (parent == null) {
            setParentId(null);
            setParentRoute("");
            return;
        }
        long parentId = parent.getId();
        SequencedSet<Long> parentIds = parent.parentIds();
        if (parentIds.contains(getId())) {
            log.info("不能将自身的子容器设为父容器");
            throw new BadRequestException(i18nReader.getMessage("sc.cant_child_to_parent"));

        }
        String parentRoute = parent.generateRoute();
        setParentId(parentId);
        setParentRoute(parentRoute);
    }

    @Nonnull
    default String generateRoute() {
        String parentRoute = getParentRoute();
        long id = getId();
        // 将数字转成36进制拼接, 36进制只有数字和小写字母可以防止索引冲突
        String convert = NumberSystemConverter.to36(id);
        if (StringUtils.isBlank(parentRoute)) {
            return convert + ":";
        }
        return parentRoute + convert + ":";
    }

    @Nonnull
    default SequencedSet<Long> parentIds() {
        String parentRoute = getParentRoute();
        if (StringUtils.isBlank(parentRoute)) {
            return new LinkedHashSet<>();
        }
        String[] split = StringUtils.split(parentRoute, ":");
        // 分解路由, 获取父容器id有序列表. 需要将36进制字符串重新转换为十进制数字
        return Arrays.stream(split).map(NumberSystemConverter::from36)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Nonnull
    default SecurityContainerInfo toInfo() {
        SecurityContainerInfo securityContainerInfo = new SecurityContainerInfo();
        securityContainerInfo.setId(getId());
        securityContainerInfo.setParentId(getParentId());
        securityContainerInfo.setName(getName());
        return securityContainerInfo;
    }

    @Nonnull
    default SecurityContainerTreeNode toTreeNode() {
        SecurityContainerTreeNode securityContainerTreeNode = new SecurityContainerTreeNode();
        securityContainerTreeNode.setId(getId());
        securityContainerTreeNode.setParentId(getParentId());
        securityContainerTreeNode.setName(getName());
        return securityContainerTreeNode;
    }


}

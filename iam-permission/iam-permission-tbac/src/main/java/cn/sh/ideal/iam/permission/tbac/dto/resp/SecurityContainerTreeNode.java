package cn.sh.ideal.iam.permission.tbac.dto.resp;

import cn.idealio.framework.lang.TreeNode;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * 安全容器树
 *
 * @author 宋志宗 on 2024/5/19
 */
@Getter
@Setter
public class SecurityContainerTreeNode implements TreeNode<SecurityContainerTreeNode> {

    /** 容器ID */
    private long id;

    /** 父容器ID */
    @Nullable
    private Long parentId = null;

    /** 容器名称 */
    @Nonnull
    private String name = "";

    /** 子容器树列表 */
    @Nullable
    private List<SecurityContainerTreeNode> children = null;

    @Nonnull
    @Override
    public Object nodeId() {
        return getId();
    }

    @Nullable
    @Override
    public Object parentNodeId() {
        return getParentId();
    }

    @Nullable
    @Override
    public List<SecurityContainerTreeNode> childNodes() {
        return children;
    }

    @Override
    public void childNodes(@Nullable List<SecurityContainerTreeNode> childNodes) {
        this.children = childNodes;
    }
}

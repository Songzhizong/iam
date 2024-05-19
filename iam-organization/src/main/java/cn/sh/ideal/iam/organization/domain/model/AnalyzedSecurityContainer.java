package cn.sh.ideal.iam.organization.domain.model;

import cn.idealio.framework.lang.TreeNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.SequencedSet;

/**
 * @author 宋志宗 on 2024/5/18
 */
@Getter
@RequiredArgsConstructor
public class AnalyzedSecurityContainer implements TreeNode<AnalyzedSecurityContainer> {
    @Nonnull
    private final SecurityContainer container;
    @Nonnull
    private final SequencedSet<Long> parentIds;
    @Nonnull
    private final List<SecurityContainer> parents;
    @Nullable
    private List<AnalyzedSecurityContainer> childTree = null;

    @Nonnull
    @Override
    public Object nodeId() {
        return container.getId();
    }

    @Nullable
    @Override
    public Object parentNodeId() {
        return container.getParentId();
    }

    @Nullable
    @Override
    public List<AnalyzedSecurityContainer> childNodes() {
        return childTree;
    }

    @Override
    public void childNodes(@Nullable List<AnalyzedSecurityContainer> childNodes) {
        this.childTree = childNodes;
    }
}

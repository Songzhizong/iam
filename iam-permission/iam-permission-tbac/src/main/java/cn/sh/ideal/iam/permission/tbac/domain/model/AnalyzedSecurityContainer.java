package cn.sh.ideal.iam.permission.tbac.domain.model;

import cn.idealio.framework.lang.Sets;
import cn.idealio.framework.lang.TreeNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

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
    @Nullable
    private List<AnalyzedSecurityContainer> childTree = null;

    @Nonnull
    public static List<AnalyzedSecurityContainer> filterRoots(@Nonnull List<AnalyzedSecurityContainer> containers) {
        if (containers.isEmpty()) {
            return List.of();
        }
        Set<Long> containerIds = containers.stream()
                .map(container -> container.getContainer().getId()).collect(Collectors.toSet());
        Set<Long> removeIds = new HashSet<>();
        for (AnalyzedSecurityContainer analyzedContainer : containers) {
            SequencedSet<Long> parentIds = analyzedContainer.getParentIds();
            if (parentIds.isEmpty()) {
                continue;
            }
            if (Sets.containsAny(containerIds, parentIds)) {
                removeIds.add(analyzedContainer.getContainer().getId());
            }
        }
        List<AnalyzedSecurityContainer> filtered = new ArrayList<>();
        for (AnalyzedSecurityContainer container : containers) {
            if (!removeIds.contains(container.getContainer().getId())) {
                filtered.add(container);
            }
        }
        return filtered;
    }

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

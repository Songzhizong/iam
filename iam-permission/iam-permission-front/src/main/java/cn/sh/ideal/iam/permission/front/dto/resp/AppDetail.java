package cn.sh.ideal.iam.permission.front.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 宋志宗 on 2024/5/16
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppDetail {
    @Nullable
    private AppInfo app;

    @Nonnull
    private List<PermissionInfo> permissions = new ArrayList<>();

    @Nonnull
    private List<PermissionItemInfo> permissionItems = new ArrayList<>();

    @Nonnull
    private List<PermissionGroupInfo> permissionGroups = new ArrayList<>();

}

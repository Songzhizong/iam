package cn.sh.ideal.iam.organization.domain.model;

import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.organization.dto.args.UpdatePlatformArgs;
import cn.sh.ideal.iam.organization.dto.resp.PlatformVO;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface Platform {
    @Nonnull
    String getCode();

    @Nonnull
    String getName();

    @Nonnull
    String getOpenName();

    @Nonnull
    String getNote();

    boolean isRegistrable();

    @Nonnull
    String getConfig();

    void delete();


    void update(@Nonnull UpdatePlatformArgs args,
                @Nonnull IamI18nReader i18nReader);

    @Nonnull
    default PlatformVO toVO() {
        PlatformVO platformVO = new PlatformVO();
        platformVO.setCode(getCode());
        platformVO.setName(getName());
        platformVO.setOpenName(getOpenName());
        platformVO.setNote(getNote());
        platformVO.setRegistrable(isRegistrable());
        platformVO.setConfig(getConfig());
        return platformVO;

    }
}

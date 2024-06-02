package cn.sh.ideal.iam.organization.domain.model;

import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.organization.dto.args.UpdatePlatformArgs;
import cn.sh.ideal.iam.organization.dto.resp.PlatformInfo;
import cn.sh.ideal.iam.organization.dto.resp.PlatformVO;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/16
 */
public interface Platform {

    @Nonnull
    Long getId();

    @Nonnull
    String getCode();

    @Nonnull
    String getName();

    @Nonnull
    String getOpenName();

    @Nonnull
    String getNote();

    boolean isRegistrable();

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
        return platformVO;
    }

    @Nonnull
    default PlatformInfo toInfo() {
        PlatformInfo platformInfo = new PlatformInfo();
        platformInfo.setId(getId());
        platformInfo.setCode(getCode());
        platformInfo.setName(getName());
        platformInfo.setOpenName(getOpenName());
        platformInfo.setNote(getNote());
        platformInfo.setRegistrable(isRegistrable());
        return platformInfo;
    }
}

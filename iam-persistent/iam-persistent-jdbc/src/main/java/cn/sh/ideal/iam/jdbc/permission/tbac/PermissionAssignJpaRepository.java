package cn.sh.ideal.iam.jdbc.permission.tbac;

import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface PermissionAssignJpaRepository extends JpaAttributeConverter<PermissionAssignDO, Long> {

}

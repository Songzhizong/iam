package cn.sh.ideal.iam.authorization.standard.exception;

import cn.idealio.framework.exception.VisibleException;
import cn.sh.ideal.iam.common.constant.IamGuides;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * 用户名或密码错误异常
 *
 * @author 宋志宗 on 2023/1/3
 */
public class UsernameOrPasswordIncorrectException extends VisibleException {

    public UsernameOrPasswordIncorrectException(int failureCount, @Nullable Integer failureLimit) {
        super(400, IamGuides.USERNAME_OR_PASSWORD_ERROR, "用户名或密码错误");
        if (failureLimit != null && failureLimit > 0 && failureCount > 0) {
            Data info = new Data(failureCount, failureLimit);
            setData(info);
        }
    }

    @Getter
    @Setter
    public static class Data {
        private int failureCount;
        private int failureLimit;

        public Data() {
        }

        public Data(int failureCount, int failureLimit) {
            this.failureCount = failureCount;
            this.failureLimit = failureLimit;
        }
    }
}

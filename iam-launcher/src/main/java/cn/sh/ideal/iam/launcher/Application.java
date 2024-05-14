package cn.sh.ideal.iam.launcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author 宋志宗 on 2023/12/27
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        Set<String> argSet;
        if (args != null) {
            argSet = new LinkedHashSet<>(Arrays.asList(args));
        } else {
            argSet = new HashSet<>();
        }
        if (!CommandLineHandler.exec(argSet)) {
            return;
        }
        SpringApplication.run(Application.class, args);
    }
}

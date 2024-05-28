package cn.sh.ideal.iam.launcher;

import cn.idealio.framework.lang.StringUtils;
import cn.sh.ideal.iam.core.Version;
import org.springframework.aot.AotDetector;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * @author 宋志宗 on 2023/9/7
 */
public class CommandLineHandler {

    public static boolean exec(@Nonnull Set<String> args) {
        for (String arg : args) {
            if (StringUtils.isBlank(arg)) {
                help();
                return false;
            }
            switch (arg) {
                case "--start", "-s" -> {
                    return true;
                }
                case "--help", "-h" -> {
                    help();
                    return false;
                }
                case "--all", "-a" -> {
                    info();
                    return false;
                }
                case "--version", "-v" -> {
                    version();
                    return false;
                }
            }
        }
        boolean artifacts = AotDetector.useGeneratedArtifacts();
        if (!artifacts) {
            return true;
        }
        help();
        return false;
    }

    public static void help() {
        System.out.println(" -s, --start    : start the server");
        System.out.println(" -a, --all      : print all information");
        System.out.println(" -h, --help     : display this help and exit");
        System.out.println(" -v, --version  : print version");
    }

    static void info() {
        String version = Version.getVersion();
        String buildTime = Version.getBuildTime();
        String osName = System.getProperty("os.name");
        if ("Mac OS X".equals(osName)) {
            osName = "MacOS";
        }
        String arch = System.getProperty("os.arch");
        String vmVersion = System.getProperty("java.vm.version");
        System.out.println("iam version " + version + " " + buildTime + " " + osName + "/" + arch + " vm " + vmVersion);
    }

    static void version() {
        String version = Version.getBuildVersion();
        System.out.println(version);
    }
}

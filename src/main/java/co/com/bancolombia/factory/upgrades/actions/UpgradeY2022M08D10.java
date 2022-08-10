package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

public class UpgradeY2022M08D10 implements UpgradeAction {
    public static final String JUNIT_PLATFORM = "useJUnitPlatform";
    public static final String SOURCE_COMPATIBILITY = "sourceCompatibility";

    public static final String JUNIT_PLATFORM_VALUER = "\n    test {\n        useJUnitPlatform()\n    } sourceCompatibility ";

    @Override
    @SneakyThrows
    public boolean up(ModuleBuilder builder) {
        return !UpdateUtils.contains(builder, MAIN_GRADLE, JUNIT_PLATFORM) || UpdateUtils.appendIfNotContains(
                builder, MAIN_GRADLE, SOURCE_COMPATIBILITY, JUNIT_PLATFORM_VALUER);
    }

    @Override
    public String name() {
        return "2.4.4->2.4.5";
    }

    @Override
    public String description() {
        return "Append useJUnitPlatform in main.gradle file";
    }
}

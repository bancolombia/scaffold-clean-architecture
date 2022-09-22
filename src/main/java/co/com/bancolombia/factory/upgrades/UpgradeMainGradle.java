package co.com.bancolombia.factory.upgrades;

import co.com.bancolombia.factory.ModuleBuilder;
import lombok.SneakyThrows;

import java.util.concurrent.atomic.AtomicBoolean;

import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

public abstract class UpgradeMainGradle implements UpgradeAction {

    @SneakyThrows
    public boolean up(ModuleBuilder builder, String match, String validation, String value) {
        AtomicBoolean applied = new AtomicBoolean(false);
        builder.updateFile(
                MAIN_GRADLE,
                content -> {
                    String res = UpdateUtils.appendValidate(content, match, validation, value);
                    if (!content.equals(res)) {
                        applied.set(true);
                    }
                    return res;
                });
        return applied.get();
    }
}

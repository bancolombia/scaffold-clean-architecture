package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.SneakyThrows;
import org.gradle.api.logging.Logger;

public class UpgradeY2023M02D08JavaxJakarta implements UpgradeAction {

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    Logger logger = builder.getProject().getLogger();
    File root = builder.getProject().getRootDir();
    AtomicBoolean applied = new AtomicBoolean(false);
    FileUtils.allFiles(
        root, file -> apply(builder, file, applied, logger), (dir, name) -> name.endsWith(".java"));
    return applied.get();
  }

  private void apply(ModuleBuilder builder, File file, AtomicBoolean applied, Logger logger) {
    try {
      boolean appliedItem =
          builder.updateFile(
              file.getAbsolutePath(),
              content -> {
                String res =
                    UpdateUtils.replace(content, "javax.persistence", "jakarta.persistence");
                res = UpdateUtils.replace(res, "javax.jms", "jakarta.jms");
                res = UpdateUtils.replace(res, "com.ibm.mq.jms", "com.ibm.mq.jakarta.jms");
                res =
                    UpdateUtils.replace(
                        res, "com.ibm.msg.client.jms", "com.ibm.msg.client.jakarta.jms");
                res =
                    UpdateUtils.replace(
                        res, "com.ibm.msg.client.wmq", "com.ibm.msg.client.jakarta.wmq");
                return res;
              });
      if (appliedItem) {
        logger.debug("javax to jakarta applied in: ${}", file.getName());
        applied.set(true);
      }
    } catch (IOException e) {
      logger.warn("Error applying javax to jakarta persistence", e);
    }
  }

  @Override
  public String name() {
    return "2.4.10->3.0.0";
  }

  @Override
  public String description() {
    return "Update javax.persistence to jakarta.persistence";
  }
}

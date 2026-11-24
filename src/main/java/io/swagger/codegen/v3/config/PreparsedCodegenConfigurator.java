package io.swagger.codegen.v3.config;

import io.swagger.codegen.v3.ClientOptInput;
import io.swagger.codegen.v3.ClientOpts;
import io.swagger.codegen.v3.CodegenConfig;
import io.swagger.codegen.v3.CodegenConstants;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Builds {@link ClientOptInput} from an in-memory {@link OpenAPI} model (no spec URL fetch /
 * re-parse).
 */
public class PreparsedCodegenConfigurator extends CodegenConfigurator {

  public ClientOptInput toClientOptInput(OpenAPI openAPI) {
    Validate.notEmpty(getLang(), "language must be specified");
    Objects.requireNonNull(openAPI, "openAPI");

    prepare();

    CodegenConfig config = loadCodegenConfig();
    ClientOptInput input = new ClientOptInput().opts(new ClientOpts()).openAPI(openAPI);
    config.setInputSpec("(preparsed)");
    applyConfiguratorToConfig(config);
    input.config(config);
    return input;
  }

  /**
   * Same as the private {@code CodegenConfigurator.setSystemProperties()}, through the public
   * getter. Verbose flags and dynamic properties are not supported: the domain generation never
   * sets them.
   */
  private void prepare() {
    getSystemProperties().forEach(System::setProperty);
  }

  private void applyConfiguratorToConfig(CodegenConfig config) {
    config.setOutputDir(getOutputDir());
    config.setSkipOverwrite(isSkipOverwrite());
    config.setIgnoreFilePathOverride(getIgnoreFileOverride());
    config.setRemoveOperationIdPrefix(getRemoveOperationIdPrefix());

    config.instantiationTypes().putAll(getInstantiationTypes());
    config.typeMapping().putAll(getTypeMappings());
    config.importMapping().putAll(getImportMappings());
    config.languageSpecificPrimitives().addAll(getLanguageSpecificPrimitives());
    config.reservedWordsMappings().putAll(getReservedWordsMappings());

    config.setLanguageArguments(getCodegenArguments());

    checkAndSet(config, getApiPackage(), CodegenConstants.API_PACKAGE);
    checkAndSet(config, getModelPackage(), CodegenConstants.MODEL_PACKAGE);
    checkAndSet(config, getInvokerPackage(), CodegenConstants.INVOKER_PACKAGE);
    checkAndSet(config, getGroupId(), CodegenConstants.GROUP_ID);
    checkAndSet(config, getArtifactId(), CodegenConstants.ARTIFACT_ID);
    checkAndSet(config, getArtifactVersion(), CodegenConstants.ARTIFACT_VERSION);
    checkAndSetTemplateDir(config, getTemplateDir());
    checkAndSet(config, getTemplateVersion(), CodegenConstants.TEMPLATE_VERSION);
    checkAndSet(config, getModelNamePrefix(), CodegenConstants.MODEL_NAME_PREFIX);
    checkAndSet(config, getModelNameSuffix(), CodegenConstants.MODEL_NAME_SUFFIX);
    checkAndSet(config, getGitUserId(), CodegenConstants.GIT_USER_ID);
    checkAndSet(config, getGitRepoId(), CodegenConstants.GIT_REPO_ID);
    checkAndSet(config, getGitRepoBaseURL(), CodegenConstants.GIT_REPO_BASE_URL);
    checkAndSet(config, getReleaseNote(), CodegenConstants.RELEASE_NOTE);
    checkAndSet(config, getHttpUserAgent(), CodegenConstants.HTTP_USER_AGENT);

    if (StringUtils.isNotEmpty(getLibrary())) {
      config.setLibrary(getLibrary());
    }
    config.additionalProperties().putAll(getAdditionalProperties());
  }

  private static void checkAndSet(CodegenConfig config, String value, String key) {
    if (StringUtils.isNotEmpty(value)) {
      config.additionalProperties().put(key, value);
    }
  }

  private static void checkAndSetTemplateDir(CodegenConfig config, String templateDir) {
    if (StringUtils.isNotEmpty(templateDir)) {
      config
          .additionalProperties()
          .put(
              CodegenConstants.TEMPLATE_DIR,
              java.nio.file.Paths.get(templateDir).toAbsolutePath().toString());
    }
  }
}

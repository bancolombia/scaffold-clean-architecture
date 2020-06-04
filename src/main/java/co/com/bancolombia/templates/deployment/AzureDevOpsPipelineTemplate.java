package co.com.bancolombia.templates.deployment;

public class AzureDevOpsPipelineTemplate {

    public static String BUILD_SOURCE_DIRECTORY = "$(Build.SourcesDirectory)";
    public static String BUILD_BINARIES_PATH = "$(Build.SourcesDirectory)";
    public static String BUILD_TEST_PATH = "/build/test-results/test";

    public static String generateAzureDevOpsPipelineTemplateContent() {
        return "# Gradle\n" +
                "# Build your Java project and run tests with Gradle using a Gradle wrapper script.\n" +
                "# Add steps that analyze code, save build artifacts, deploy, and more:\n" +
                "# https://docs.microsoft.com/azure/devops/pipelines/languages/java\n" +
                "\n" +
                "name: $(Build.SourceBranchName).$(date:yyyyMMdd)$(rev:.r)\n" +
                "\n" +
                "variables:\n" +
                "  - group: 'devsecops_BREFA'\n" +
                "  - name: 'artifactory.Repository'\n" +
                "    value: 'build-[ProjectName]'\n" +
                "  - name: 'proyecto'\n" +
                "    value: '[ProjectName]'\n" +
                "\n" +
                "resources:\n" +
                "- repo: self\n" +
                "\n" +
                "trigger:\n" +
                "  branches:\n" +
                "    include:\n" +
                "      - trunk\n" +
                "  paths:\n" +
                "    include:\n" +
                "      - '*'\n" +
                "\n" +
                "jobs:\n" +
                "    - job: BuildJob\n" +
                "      displayName: 'Compilacion BackEnd'\n" +
                "      pool:\n" +
                "        name: Build\n" +
                "        demands:\n" +
                "            - Agent.OS -equals linux\n" +
                "            - java\n" +
                "            \n" +
                "      steps: \n" +
                "        - task: SonarQubePrepare@4\n" +
                "          displayName: 'Preparar Analisis -> Prepare analysis on SonarQube'\n" +
                "          inputs:\n" +
                "            SonarQube: SonarQube\n" +
                "            scannerMode: Other\n" +
                "            configMode: manual\n" +
                "            cliProjectKey: '$(Build.Repository.Name)'\n" +
                "            cliProjectName: '$(Build.Repository.Name)'\n" +
                "            cliProjectVersion: '$(Build.BuildNumber)'\n" +
                "            extraProperties: |\n" +
                "              sonar.projectVersion=$(Build.BuildNumber)\n" +
                "              sonar.projectKey=$(Build.Repository.Name)\n" +
                "              sonar.branch.name=$(Build.SourceBranchName)\n" +
                "              \n" +
                "              sonar.java.binaries=[Binaries]\n" +
                "              \n" +
                "              sonar.coverage.jacoco.xmlReportPaths=$(Build.SourcesDirectory)/build/reports/jacocoMergedReport/jacocoMergedReport.xml\n" +
                "\n" +
                "              sonar.coverage.exclusions=**/*Test.java,**/*.js,**/*.html,**/*.xml,**/*.css,**/app.demo/MainAplication.java,**/config/usecase/UseCaseConfig.java,**/config/jpa/JpaConfig.java,**/config/mongo/MongoConfig.java\n" +
                "              sonar.exclusions=**/aplications/app-service/src/**\n" +
                "\n" +
                "              sonar.junit.reportsPaths=[Test]\n" +
                "\n" +
                "            \n" +
                "        - task: Gradle@1\n" +
                "          displayName: 'Gradle -> Build / Ejecutar Pruebas Unitarias'\n" +
                "          inputs:\n" +
                "            gradleWrapperFile: gradlew\n" +
                "            tasks: 'clean build jacocoMergedReport --stacktrace'\n" +
                "            publishJUnitResults: true\n" +
                "            workingDirectory: .\n" +
                "            testResultsFiles: '**/build/test-results/test/TEST-*.xml'\n" +
                "            sonarQubeRunAnalysis: true\n" +
                "            sonarQubeServiceEndpoint: SonarqubeGradle\n" +
                "            sonarQubeProjectName: '$(Build.Repository.Name)'\n" +
                "            sonarQubeProjectKey: '$(Build.Repository.Name)'\n" +
                "            sonarQubeProjectVersion: '$(Build.BuildNumber)'\n" +
                "            sonarQubeGradlePluginVersion: 2.7\n" +
                "            sonarQubeFailWhenQualityGateFails: false\n" +
                "\n" +
                "        - task: PublishCodeCoverageResults@1\n" +
                "          displayName: 'Publica -> Publicar cobertura'\n" +
                "          inputs:\n" +
                "            summaryFileLocation: '$(System.DefaultWorkingDirectory)/build/reports/jacoco/jacocoRootReport/jacocoRootReport.xml'    \n" +
                "\n" +
                "        - task: SonarSource.sonarqube.6D01813A-9589-4B15-8491-8164AEB38055.SonarQubeAnalyze@4\n" +
                "          displayName: 'Analisis -> Run Code Analysis'\n" +
                "\n" +
                "        - task: SonarQubePublish@4\n" +
                "          displayName: 'SonarQube -> Resultado Quality Gate'\n" +
                "          inputs:\n" +
                "            pollingTimeoutSec: 600\n" +
                "\n" +
                "        - task: sonar-buildbreaker@8\n" +
                "          displayName: ' SonarQube -> Validar Quality Gate'\n" +
                "          inputs:\n" +
                "            SonarQube: SonarQube\n" +
                "          enabled: true\n" +
                "           \n";
    }

}

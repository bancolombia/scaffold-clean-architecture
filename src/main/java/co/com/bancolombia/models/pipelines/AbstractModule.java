package co.com.bancolombia.models.pipelines;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public abstract class AbstractModule {

    private int code;
    private String projectName;
    private List<String> modulesPaths;

    public AbstractModule(int code) {
        this.code = code;
    }

    protected String getProjectName(String fileLine) {
        return fileLine.replace("rootProject.name = ", "")
                .replace("'", "");
    }

    protected List<String> getModulesNames(List<String> settingsGradleFileLines) {
        List<String> modulesNames = new ArrayList<>();
        settingsGradleFileLines.stream()
                .filter(x -> x.contains("include"))
                .forEach(x -> {
                    String path = x.trim();
                    path = path.replace("include \":", "");
                    path = path.replace("\"", "");
                    modulesNames.add(path);
                });
        return modulesNames;
    }

    protected String getProjectPath(List<String> settingsGradleFileLines, String moduleName) {
        Optional<String> modulePaths = settingsGradleFileLines.stream()
                .filter(x -> x.contains("project(':" + moduleName + "').projectDir"))
                .findFirst();

        if (modulePaths.isPresent()) {
            return modulePaths.get()
                    .replace("project(':" + moduleName + "').projectDir = file('.", "")
                    .replace("')", "");
        } else {
            return "";
        }
    }

    protected List<String> getSettingsAsList(String settings) {
        return Stream.of(settings.split("\n"))
                .map(elem -> new String(elem))
                .filter(elem -> !elem.isEmpty())
                .parallel()
                .collect(Collectors.toList());
    }

    public void setPropertiesToPipeline(String settings) {
        List<String> settingsList = this.getSettingsAsList(settings);
        this.setProjectName(this.getProjectName(settingsList.get(0)));
        modulesPaths = new ArrayList<>();
        for (String name : getModulesNames(settingsList)) {
            modulesPaths.add(getProjectPath(settingsList, name));
        }
    }

    public abstract String getPipelineContent(String settings);

    private void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}

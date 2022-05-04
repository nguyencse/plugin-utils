package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import helpers.FeatureHelper;
import org.jetbrains.annotations.NotNull;

public class CreateFeatureBlocAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        FeatureHelper myHelper = new FeatureHelper(project);
        myHelper.createFeature(e);
    }
}

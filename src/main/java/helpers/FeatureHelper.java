package helpers;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.FeatureCreator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FeatureHelper {
    private final Project project;
    private final String packageName;
    private String featureName;

    public FeatureHelper(Project project) {
        this.project = project;
        this.packageName = getPackageName(project);
    }

    public void createFeature(AnActionEvent e) {
        refreshProject(e);
        FeatureCreator dialog = new FeatureCreator((featureName) -> {
            this.featureName = featureName;
            if (!featureName.trim().isEmpty()) {
                generateFeature();
                Messages.showInfoMessage(project, "Feature " + featureName + " created successfully", "Feature Creator");
            }
            refreshProject(e);
        });
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void refreshProject(AnActionEvent e) {
        if (e.getProject() != null) {
            e.getProject().getBaseDir().refresh(false, true);
        }
    }

    private String getPackageName(Project project) {
        String packageName = "";
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(project.getBasePath() + "/android/app/src/main/AndroidManifest.xml");

            NodeList nodeList = doc.getElementsByTagName("manifest");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element element = (Element) node;
                packageName = element.getAttribute("package");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int lastPartIdx = packageName.lastIndexOf('.');
        return packageName.substring(lastPartIdx + 1);
    }

    private void generateFeature () {
        String featureMapString = readTemplateFile("feature_map.txt", true);
        String[] paths = featureMapString.split("\n");
        for (String fullPath : paths) {
            String[] arrPath = fullPath.split("/");
            if (arrPath.length > 0) {
                String resFileName = arrPath[arrPath.length - 1];
                String content = readTemplateFile(resFileName, false);
                writeToFile(dealTemplateContent(content), fullPath.replace(".txt", ".dart"));
            }
        }
    }

    private String dealTemplateContent(String content) {
        if (content.contains("$$$package_name")) {
            content = content.replace("$$$package_name", packageName);
        }
        if (content.contains("$$$feature_name")) {
            content = content.replace("$$$feature_name", featureName.toLowerCase());
        }
        StringBuilder formattedFeatureName = new StringBuilder(featureName.toLowerCase());
        if (content.contains("$$$FeatureName")) {
            String[] parts = formattedFeatureName.toString().split("_");
            formattedFeatureName = new StringBuilder();
            for (String part : parts) {
                if (part.length() > 1) {
                    formattedFeatureName.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
                } else if (part.length() == 1) {
                    formattedFeatureName.append(part.substring(0, 1).toUpperCase());
                }
            }
            content = content.replace("$$$FeatureName", formattedFeatureName.toString());
        }
        if (formattedFeatureName.length() > 0) {
            String featureName = formattedFeatureName.substring(0, 1).toLowerCase() + formattedFeatureName.substring(1);
            content = content.replace("$$$featureName", featureName);
        }
        return content;
    }

    private void writeToFile(String content, String filePath) {
        try {
            File file = new File(project.getBasePath() + "/" + dealTemplateContent(filePath));
            Files.createDirectories(Paths.get(file.getParentFile().toString()));

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readTemplateFile(String filePath, boolean isFeatureMap) {
        String content = "";
        try {
            String resourcePath = "bin/templates/" + (isFeatureMap ? filePath : "feature/" + filePath);
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (in != null) {
                content = new String(readStream(in));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private byte[] readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }
}
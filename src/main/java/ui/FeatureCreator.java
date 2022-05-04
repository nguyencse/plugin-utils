package ui;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FeatureCreator extends JDialog {
    private JTextField txtFeatureName;
    private JButton btnOK;
    private JPanel mainPanel;
    private JButton btnExit;

    private final DialogCallBack callBack;

    public FeatureCreator(DialogCallBack callBack) {
        this.callBack = callBack;

        setTitle("Feature Creator");
        setContentPane(mainPanel);
        setModal(true);
        getRootPane().setDefaultButton(btnOK);

        btnOK.addActionListener(e -> onOK());
        btnExit.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        mainPanel.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        if (callBack != null) {
            callBack.ok(txtFeatureName.getText().trim());
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public interface DialogCallBack {
        void ok(String moduleName);
    }
}


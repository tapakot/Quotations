package ui;

import common.ForexConstants;
import common.Settings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;

class SettingsDialog extends JDialog implements ActionListener{
    MainFrame owner;
    Box mainBox;

    JButton cancelButton;
    JButton okButton;
    JButton defAllButton;

    ArrayList<String> names;
    ArrayList<JTextField> values;
    ArrayList<JButton> defaultButtons;

    SettingsDialog(MainFrame owner){
        super(owner, "Settings", true);
        this.owner = owner;

        names = new ArrayList<>();
        values = new ArrayList<>();
        defaultButtons = new ArrayList<>();

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setMaximumSize(new Dimension(screen.width, screen.height));
        setMinimumSize(new Dimension(550, 600));
        setPreferredSize(getMinimumSize());

        mainBox = Box.createVerticalBox();

        for(Enumeration e = Settings.properties.propertyNames(); e.hasMoreElements(); ){
            Object a = e.nextElement();
            //System.out.println(i+" "+a.toString());
            String name = a.toString().trim();
            String sec = name.substring(0, name.indexOf("."));
            name = name.substring(name.indexOf(".")+1);
            if(!sec.equals("adjustable")) {
                addSetting(sec, name, Double.parseDouble(Settings.properties.getProperty(sec + "." + name)));
                mainBox.add(Box.createVerticalStrut(10));
            }
        }

        JPanel butPanel = new JPanel(new GridLayout(1, 2, 0, 10));
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        defAllButton = new JButton("Default all");
        defAllButton.addActionListener(this);
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        butPanel.add(cancelButton);
        butPanel.add(defAllButton);
        butPanel.add(okButton);
        mainBox.add(butPanel);

        add(new JScrollPane(mainBox));
    }

    private void addSetting(String section, String name, double val){
        names.add(section+"."+name);
        JPanel panel = new JPanel(new GridLayout(1, 3, 0, 10));
        panel.add(new JLabel(name));
        JTextField value = new JTextField(Double.toString(val));
        values.add(value);
        panel.add(value);
        JButton defaultButton = new JButton("default");
        defaultButton.addActionListener(this);
        defaultButtons.add(defaultButton);
        panel.add(defaultButton);
        mainBox.add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource()==cancelButton){
            //this.setVisible(false);
            this.dispose();
        } else
        if(e.getSource()==okButton){
            for(String name : names) {
                String value = values.get(names.indexOf(name)).getText();
                Settings.properties.setProperty(name, value);
            }
            //write to file
            Settings.intoFile();
            //apply
            ForexConstants.applySettings();
            this.dispose();
        } else
        if(e.getSource()==defAllButton){
            for(int i=0; i<names.size(); i++) {
                String name = names.get(i);
                String defValue = Settings.defProperties.getProperty(name);
                JTextField textField = values.get(i);
                textField.setText(defValue);
            }
        }else
        if((e.getSource().getClass().getName().equals("javax.swing.JButton"))){
            int index = -1;
            for(JButton but : defaultButtons){
                if(but.equals(e.getSource())){
                    index = defaultButtons.indexOf(but);
                }
            }
            if(index!=-1) {
                String name = names.get(index);
                String defValue = Settings.defProperties.getProperty(name);
                JTextField textField = values.get(index);
                textField.setText(defValue);
            }
        }
    }




}

package client.graphical.dialogs;

import client.DataLoader;
import client.logic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClassTimeDialog extends JDialog {

    private ClassTime time;

    private String[] fieldNames = {"Start hour", "Start minutes", "End hour", "End minutes", "Day of week"};
    private JLabel[] labels = new JLabel[this.fieldNames.length];
    private JTextField[] fields = new JTextField[this.fieldNames.length];
    private JComboBox<String> typeComboBox;
    private JButton button = new JButton();

    private int WIDTH = DataLoader.getConstraint("dialogs", "width");
    private int HEIGHT = DataLoader.getConstraint("dialogs", "height") / 2;
    private int MARGIN_SIZE = DataLoader.getConstraint("dialogs", "marginSize");
    private int ROW_HEIGHT = DataLoader.getConstraint("dialogs", "rowHeight");
    private int LABEL_WIDTH = DataLoader.getConstraint("dialogs", "labelWidth");
    private int ROW_WIDTH = DataLoader.getConstraint("dialogs", "rowWidth");
    private int BUTTON_WIDTH = DataLoader.getConstraint("dialogs", "buttonWidth") / 2;


    public ClassTimeDialog(){
        super();
        this.setModal(true);
        this.setTitle("Add Class Times");
        this.setLayout(null);
        this.setSize(WIDTH, HEIGHT);

        addLabels();
        addButton();
        revalidate();
        repaint();
        this.setVisible(true);
    }

    public int getCoor(double x){
        return (int)(MARGIN_SIZE + x * ROW_HEIGHT);
    }

    public void addLabels(){
        for (int i = 0; i < 4; i++){
            this.labels[i] = new JLabel();
            this.labels[i].setText(this.fieldNames[i]);
            this.labels[i].setBounds(MARGIN_SIZE + (i % 2) * WIDTH / 2, getCoor(1 + (int) (i / 2)), BUTTON_WIDTH, ROW_HEIGHT);
            this.add(this.labels[i]);

            this.fields[i] = new JTextField();
            this.fields[i].setBounds((1 + i % 2) * WIDTH / 2 - BUTTON_WIDTH, getCoor(1 + (int) (i / 2)), BUTTON_WIDTH, ROW_HEIGHT);
            this.add(this.fields[i]);
        }
        this.labels[4] = new JLabel(this.fieldNames[4]);
        this.labels[4].setBounds(MARGIN_SIZE, getCoor(0), ROW_WIDTH, ROW_HEIGHT);
        this.add(this.labels[4]);

        this.typeComboBox = new JComboBox<>(ClassTime.weekNames);
        this.typeComboBox.setSelectedIndex(-1);
        this.typeComboBox.setBounds(MARGIN_SIZE + BUTTON_WIDTH, getCoor(0), BUTTON_WIDTH, ROW_HEIGHT);
        this.add(this.typeComboBox);
    }

    public void addButton(){
        this.button.setText("Add time");
        this.button.setBackground(Color.GREEN);
        this.button.setBounds((WIDTH - BUTTON_WIDTH) / 2, getCoor(5), BUTTON_WIDTH, ROW_HEIGHT);
        this.button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int day = typeComboBox.getSelectedIndex();
                if (day == -1){
                    JOptionPane.showMessageDialog(ClassTimeDialog.this, "Please select a day for the class time.");
                    return;
                }
                int[] times = new int[4];
                try{
                    for (int i = 0; i < 4; i++){
                        times[i] = Integer.parseInt(fields[i].getText());
                    }
                } catch (Exception err){
                    JOptionPane.showMessageDialog(ClassTimeDialog.this, "Please enter valid numbers for the hours and minutes.");
                    return;
                }
                if ((times[0] < 7 || times[0] > 20) || (times[2] < 7 || times[2] > 20)){
                    JOptionPane.showMessageDialog(ClassTimeDialog.this, "Please enter a number between 7 and 20 as an hour.");
                    return;
                }
                if ((times[1] < 0 || times[1] > 59) || (times[3] < 0 || times[3] > 59)){
                    JOptionPane.showMessageDialog(ClassTimeDialog.this, "Please enter a number between 0 and 59 as a minute.");
                    return;
                }
                time = new ClassTime(day, times[0], times[1], times[2], times[3]);
                JOptionPane.showMessageDialog(ClassTimeDialog.this, "Time successfully added.");
                setModal(false);
                setVisible(false);
            }
        });
        this.add(this.button);
    }

    public ClassTime getTime() {
        return time;
    }
}

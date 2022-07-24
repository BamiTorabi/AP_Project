package process;

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

    private int WIDTH = 450;
    private int HEIGHT = 300;
    private int MARGIN_SIZE = 15;
    private int LABEL_WIDTH = 100;
    private int LABEL_HEIGHT = 40;

    public ClassTimeDialog(){
        super();
        this.setModal(true);
        this.setTitle("Add Class Times");
        this.setLayout(null);
        this.setSize(WIDTH, HEIGHT);

        addLabels();
        addButton();
        this.setVisible(true);
    }

    public int getCoor(double x){
        return (int)(MARGIN_SIZE + x * LABEL_HEIGHT);
    }

    public void addLabels(){
        for (int i = 0; i < 4; i++){
            this.labels[i] = new JLabel();
            this.labels[i].setText(this.fieldNames[i]);
            this.labels[i].setBounds(MARGIN_SIZE + (i % 2) * 225, getCoor(1 + (int) (i / 2)), LABEL_WIDTH, LABEL_HEIGHT);
            this.add(this.labels[i]);

            this.fields[i] = new JTextField();
            this.fields[i].setBounds(100 + MARGIN_SIZE + (i % 2) * 225, getCoor(1 + (int) (i / 2)), LABEL_WIDTH, LABEL_HEIGHT);
            this.add(this.fields[i]);
        }
        this.labels[4] = new JLabel(this.fieldNames[4]);
        this.labels[4].setBounds(MARGIN_SIZE, getCoor(0), LABEL_WIDTH, LABEL_HEIGHT);
        this.add(this.labels[4]);

        this.typeComboBox = new JComboBox<>(ClassTime.weekNames);
        this.typeComboBox.setSelectedIndex(-1);
        this.typeComboBox.setBounds(MARGIN_SIZE + 100, getCoor(0), 150, LABEL_HEIGHT);
        this.add(this.typeComboBox);
    }

    public void addButton(){
        this.button.setText("Add time");
        this.button.setBackground(Color.GREEN);
        this.button.setBounds((WIDTH - 150) / 2, getCoor(5), 150, LABEL_HEIGHT);
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

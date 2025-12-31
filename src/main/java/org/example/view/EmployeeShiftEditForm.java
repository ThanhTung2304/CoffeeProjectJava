package org.example.view;

import org.example.controller.EmployeeShiftController;
import org.example.controller.EmployeeController;
import org.example.controller.ShiftController;
import org.example.entity.Employee;
import org.example.entity.Shift;
import org.example.entity.WorkSchedule;

import javax.swing.*;
import java.awt.*;

public class EmployeeShiftEditForm extends JDialog {

    private JComboBox<Employee> cbEmployee;
    private JComboBox<Shift> cbShift;
    private JTextField txtDate;

    private final EmployeeShiftController controller = new EmployeeShiftController();
    private final WorkSchedule ws;
    private final Runnable callback;

    public EmployeeShiftEditForm(WorkSchedule ws, Runnable callback) {
        this.ws = ws;
        this.callback = callback;

        setTitle("Sửa ca làm");
        setSize(360, 230);
        setLocationRelativeTo(null);
        setModal(true);

        initUI();
        loadEmployees();
        loadShifts();
        fillData();
    }

    private void initUI() {

        setLayout(new GridLayout(4, 2, 10, 10));

        cbEmployee = new JComboBox<>();
        cbShift = new JComboBox<>();
        txtDate = new JTextField();

        JButton btnSave = new JButton("Cập nhật");
        JButton btnCancel = new JButton("Hủy");

        add(new JLabel("Nhân viên"));
        add(cbEmployee);

        add(new JLabel("Ca làm"));
        add(cbShift);

        add(new JLabel("Ngày làm"));
        add(txtDate);

        add(btnSave);
        add(btnCancel);

        btnSave.addActionListener(e -> update());
        btnCancel.addActionListener(e -> dispose());
    }

    private void loadEmployees() {
        new EmployeeController().getAll().forEach(cbEmployee::addItem);

        cbEmployee.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null)
                    setText(((Employee) value).getName());
                return this;
            }
        });
    }

    private void loadShifts() {
        new ShiftController().getAll().forEach(cbShift::addItem);

        cbShift.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    Shift s = (Shift) value;
                    setText(s.getName() + " (" + s.getStartTime() + "-" + s.getEndTime() + ")");
                }
                return this;
            }
        });
    }

    private void fillData() {

        // set employee
        for (int i = 0; i < cbEmployee.getItemCount(); i++) {
            if (cbEmployee.getItemAt(i).getId() == ws.getEmployeeId()) {
                cbEmployee.setSelectedIndex(i);
                break;
            }
        }
        cbEmployee.setEnabled(false);

        // set shift
        for (int i = 0; i < cbShift.getItemCount(); i++) {
            if (cbShift.getItemAt(i).getId() == ws.getShiftId()) {
                cbShift.setSelectedIndex(i);
                break;
            }
        }

        txtDate.setText(ws.getWorkDate().toString());
        txtDate.setEnabled(false);
    }

    private void update() {

        try {
            Shift newShift = (Shift) cbShift.getSelectedItem();

            controller.update(
                    ws.getEmployeeId(),
                    ws.getShiftId(),
                    newShift.getId(),
                    ws.getWorkDate().toString()
            );

            JOptionPane.showMessageDialog(this, "Cập nhật thành công");
            callback.run();
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

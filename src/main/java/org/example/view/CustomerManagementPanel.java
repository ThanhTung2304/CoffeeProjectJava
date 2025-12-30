package org.example.view;

import org.example.controller.CustomerController;
import org.example.entity.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import org.example.view.MainFrame;




public class CustomerManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;

    private final CustomerController controller = new CustomerController();

    public CustomerManagementPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(16,16,16,16));

        // ===== TITLE =====
        JLabel title = new JLabel("QUẢN LÝ KHÁCH HÀNG");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(0,0,16,0));

        // ===== SEARCH =====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);

        txtSearch = new JTextField(22);
        JButton btnSearch = new JButton("🔍 Tìm");

        searchPanel.add(new JLabel("Tìm (Tên / SĐT):"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // ===== BUTTONS =====
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,12,10));
        actionPanel.setOpaque(false);

        JButton btnAdd = createButton("Thêm", new Color(46,204,113));
        JButton btnEdit = createButton("Sửa", new Color(241,196,15));
        JButton btnDelete = createButton("Xóa", new Color(231,76,60));
        JButton btnRefresh = createButton("Refresh", new Color(149,165,166));

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);

        // ===== TABLE =====
        String[] columns = {
                "ID","Tên","SĐT","Email","Điểm","Ngày tạo"
        };

        model = new DefaultTableModel(columns,0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        JScrollPane scroll = new JScrollPane(table);

        // ===== LAYOUT =====
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(searchPanel, BorderLayout.CENTER);
        header.add(actionPanel, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // ===== LOAD DATA =====
        loadData("");

        // ===== EVENTS =====
        btnSearch.addActionListener(e ->
                loadData(txtSearch.getText().trim()));

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadData("");
        });

        btnAdd.addActionListener(e -> showAddDialog());

        btnEdit.addActionListener(e -> showEditDialog());

        btnDelete.addActionListener(e -> deleteCustomer());

        /*table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row == -1) return;

                    int customerId = (int) model.getValueAt(row, 0);
                    String name = model.getValueAt(row, 1).toString();

                    Customer c = new Customer();
                    c.setId(customerId);
                    c.setName(name);

                    MainFrame mainFrame =
                            (MainFrame) SwingUtilities.getWindowAncestor(CustomerManagementPanel.this);
                    if (!"ADMIN".equals(mainFrame.getRole())) {
                        JOptionPane.showMessageDialog(
                                CustomerManagementPanel.this,
                                "Bạn không có quyền xem lịch sử đơn hàng!",
                                "Từ chối truy cập",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }
                    mainFrame.openOrderHistory(c);

                }
            }
        });*/

    }


    // ================= LOAD DATA =================
    private void loadData(String keyword) {
        model.setRowCount(0);
        List<Customer> list = controller.getAll();

        for (Customer c : list) {
            if (!keyword.isEmpty()) {
                String key = keyword.toLowerCase();
                if (!c.getName().toLowerCase().contains(key)
                        && !c.getPhone().contains(key)) {
                    continue;
                }
            }

            model.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getPhone(),
                    c.getEmail(),
                    c.getTotalPoint(),
                    c.getCreatedTime()
            });
        }
    }

    // ================= ADD =================
    private void showAddDialog() {
        JTextField name = new JTextField();
        JTextField phone = new JTextField();
        JTextField email = new JTextField();

        Object[] form = {
                "Tên:", name,
                "SĐT:", phone,
                "Email:", email
        };

        int rs = JOptionPane.showConfirmDialog(
                this, form, "Thêm khách hàng",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (rs == JOptionPane.OK_OPTION) {
            try {
                controller.add(
                        name.getText().trim(),
                        phone.getText().trim(),
                        email.getText().trim()
                );
                loadData("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    // ================= EDIT =================
    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn khách hàng cần sửa");
            return;
        }

        int id = (int) model.getValueAt(row,0);

        JTextField name = new JTextField(model.getValueAt(row,1).toString());
        JTextField phone = new JTextField(model.getValueAt(row,2).toString());
        JTextField email = new JTextField(model.getValueAt(row,3).toString());
        JTextField point = new JTextField(model.getValueAt(row,4).toString());

        Object[] form = {
                "Tên:", name,
                "SĐT:", phone,
                "Email:", email,
                "Điểm:", point
        };

        int rs = JOptionPane.showConfirmDialog(
                this, form, "Sửa khách hàng",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (rs == JOptionPane.OK_OPTION) {
            Customer c = new Customer();
            c.setId(id);
            c.setName(name.getText().trim());
            c.setPhone(phone.getText().trim());
            c.setEmail(email.getText().trim());
            c.setTotalPoint(Integer.parseInt(point.getText().trim()));

            controller.update(c);
            loadData("");
        }
    }

    // ================= DELETE =================
    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn khách hàng cần xóa");
            return;
        }

        int id = (int) model.getValueAt(row,0);

        int rs = JOptionPane.showConfirmDialog(
                this,
                "Bạn chắc chắn muốn xóa khách hàng này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (rs == JOptionPane.YES_OPTION) {
            controller.delete(id);
            loadData("");
        }
    }

    // ================= BUTTON STYLE =================
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(120,36));
        btn.setFocusPainted(false);
        return btn;
    }
}

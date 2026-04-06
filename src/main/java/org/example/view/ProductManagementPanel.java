//package org.example.view;
//
//import org.example.entity.Product;
//import org.example.repository.ProductRepository;
//import org.example.repository.impl.ProductRepositoryImpl;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.util.List;
//
//public class ProductManagementPanel extends JPanel {
//
//    private JTable table;
//    private DefaultTableModel tableModel;
//
//    private final ProductRepository productRepository = new ProductRepositoryImpl();
//
//    private JTextField txtSearch;
//    private JComboBox<String> cbStatus;
//
//    public ProductManagementPanel() {
//        setLayout(new BorderLayout());
//        setOpaque(false);
//        setBorder(new EmptyBorder(16, 16, 16, 16));
//
//        // ===== TITLE =====
//        JLabel title = new JLabel("QUẢN LÝ SẢN PHẨM", SwingConstants.LEFT);
//        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
//        title.setBorder(new EmptyBorder(0, 0, 16, 0));
//
//        // ===== TOP PANEL (SEARCH + FILTER) =====
//        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
//        topPanel.setOpaque(false);
//
//        txtSearch = new JTextField(22);
//        JButton btnSearch = new JButton("🔍 Tìm");
//
//        cbStatus = new JComboBox<>(new String[]{
//                "Tất cả", "Đang bán", "Ngừng bán"
//        });
//
//        topPanel.add(new JLabel("Tìm:"));
//        topPanel.add(txtSearch);
//        topPanel.add(btnSearch);
//        topPanel.add(Box.createHorizontalStrut(20));
//        topPanel.add(new JLabel("Trạng thái:"));
//        topPanel.add(cbStatus);
//
//        // ===== ACTION BUTTONS =====
//        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
//        actionPanel.setOpaque(false);
//
//        JButton btnAdd = createButton("Thêm", new Color(46, 204, 113));
//        JButton btnEdit = createButton("Sửa", new Color(241, 196, 15));
//        JButton btnDelete = createButton("Xóa", new Color(231, 76, 60));
//        JButton btnRefresh = createButton("Refresh", new Color(149, 165, 166));
//
//        actionPanel.add(btnAdd);
//        actionPanel.add(btnEdit);
//        actionPanel.add(btnDelete);
//        actionPanel.add(btnRefresh);
//
//        // ===== TABLE =====
//        String[] columns = {
//                "STT",
//                "Tên sản phẩm",
//                "Giá bán",
//                "Trạng thái",
//                "Ngày tạo",
//                "Cập nhật"
//        };
//
//        tableModel = new DefaultTableModel(columns, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//
//        table = new JTable(tableModel);
//        table.setRowHeight(28);
//        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
//
//        JScrollPane scrollPane = new JScrollPane(table);
//
//        // ===== LAYOUT =====
//        JPanel headerPanel = new JPanel(new BorderLayout());
//        headerPanel.setOpaque(false);
//        headerPanel.add(title, BorderLayout.NORTH);
//        headerPanel.add(topPanel, BorderLayout.CENTER);
//        headerPanel.add(actionPanel, BorderLayout.SOUTH);
//
//        add(headerPanel, BorderLayout.NORTH);
//        add(scrollPane, BorderLayout.CENTER);
//
//        // ===== LOAD DATA =====
//        loadDataFromDB();
//
//        // ===== EVENTS =====
//        btnRefresh.addActionListener(e -> loadDataFromDB());
//        btnSearch.addActionListener(e -> loadDataFromDB());
//
//        btnAdd.addActionListener(e ->
//                JOptionPane.showMessageDialog(this, "Mở form THÊM sản phẩm"));
//
//        btnEdit.addActionListener(e ->
//                JOptionPane.showMessageDialog(this, "Mở form SỬA sản phẩm"));
//
//        btnDelete.addActionListener(e ->
//                JOptionPane.showMessageDialog(this, "XÓA sản phẩm"));
//    }
//
//    // ===== LOAD DATA FROM DATABASE =====
//    private void loadDataFromDB() {
//        tableModel.setRowCount(0);
//
//        String keyword = txtSearch.getText().trim().toLowerCase();
//        String statusFilter = cbStatus.getSelectedItem().toString();
//
//        List<Product> products = productRepository.findAll();
//
//        int stt = 1;
//        for (Product p : products) {
//
//            // filter name
//            if (!keyword.isEmpty()
//                    && !p.getName().toLowerCase().contains(keyword)) {
//                continue;
//            }
//
//            // filter status
//            String statusText = p.isActive() ? "Đang bán" : "Ngừng bán";
//            if (!statusFilter.equals("Tất cả")
//                    && !statusFilter.equals(statusText)) {
//                continue;
//            }
//
//            tableModel.addRow(new Object[]{
//                    stt++,
//                    p.getName(),
//                    p.getPrice(),
//                    statusText,
//                    p.getCreatedTime(),
//                    p.getUpdatedTime()
//            });
//        }
//    }
//
//    // ===== BUTTON STYLE =====
//    private JButton createButton(String text, Color color) {
//        JButton btn = new JButton(text);
//        btn.setForeground(Color.WHITE);
//        btn.setBackground(color);
//        btn.setFocusPainted(false);
//        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
//        btn.setPreferredSize(new Dimension(120, 36));
//        return btn;
//    }
//}
//
package org.example.view;

import org.example.controller.ProductController;
import org.example.entity.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    // THÊM CONTROLLER
    private final ProductController productController = new ProductController();

    private JTextField txtSearch;
    private JComboBox<String> cbStatus;

    public ProductManagementPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // ===== TITLE =====
        JLabel title = new JLabel("QUẢN LÝ SẢN PHẨM", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(0, 0, 16, 0));

        // ===== TOP PANEL (SEARCH + FILTER) =====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        topPanel.setOpaque(false);

        txtSearch = new JTextField(22);
        JButton btnSearch = new JButton("🔍 Tìm");

        cbStatus = new JComboBox<>(new String[]{
                "Tất cả", "Đang bán", "Ngừng bán"
        });

        topPanel.add(new JLabel("Tìm:"));
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("Trạng thái:"));
        topPanel.add(cbStatus);

        // ===== ACTION BUTTONS =====
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        actionPanel.setOpaque(false);

        JButton btnAdd = createButton("Thêm", new Color(46, 204, 113));
        JButton btnEdit = createButton("Sửa", new Color(241, 196, 15));
        JButton btnDelete = createButton("Xóa", new Color(231, 76, 60));
        JButton btnRefresh = createButton("Refresh", new Color(149, 165, 166));

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);

        // ===== TABLE =====
        String[] columns = {
                "ID", "Tên sản phẩm", "Giá bán", "Trạng thái", "Ngày tạo", "Cập nhật"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(table);

        // ===== LAYOUT =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(topPanel, BorderLayout.CENTER);
        headerPanel.add(actionPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // ===== LOAD DATA =====
        loadDataFromDB();

        // ===== XỬ LÝ SỰ KIỆN =====
        btnRefresh.addActionListener(e -> loadDataFromDB());
        btnSearch.addActionListener(e -> loadDataFromDB());
        cbStatus.addActionListener(e -> loadDataFromDB());

        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteSelectedProduct());
    }

    // ===== LOAD DATA FROM DATABASE =====
    private void loadDataFromDB() {
        tableModel.setRowCount(0);

        String keyword = txtSearch.getText().trim().toLowerCase();
        String statusFilter = cbStatus.getSelectedItem().toString();

        List<Product> products = productController.getAllProduct();

        for (Product p : products) {

            // filter name
            if (!keyword.isEmpty()
                    && !p.getName().toLowerCase().contains(keyword)) {
                continue;
            }
            // filter status
            String statusText = p.isActive() ? "Đang bán" : "Ngừng bán";
            if (!statusFilter.equals("Tất cả")
                    && !statusFilter.equals(statusText)) {
                continue;
            }

            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getPrice(),
                    statusText,
                    p.getCreatedTime(),
                    p.getUpdatedTime()
            });
        }
    }

    // ===== MỞ DIALOG THÊM SẢN PHẨM =====
    private void openAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Thêm sản phẩm mới", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Panel chứa form
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblName = new JLabel("Tên sản phẩm:");
        JTextField txtName = new JTextField();

        JLabel lblPrice = new JLabel("Giá bán:");
        JTextField txtPrice = new JTextField();

        JLabel lblStatus = new JLabel("Trạng thái:");
        JCheckBox chkActive = new JCheckBox("Đang bán");
        chkActive.setSelected(true);

        formPanel.add(lblName);
        formPanel.add(txtName);
        formPanel.add(lblPrice);
        formPanel.add(txtPrice);
        formPanel.add(lblStatus);
        formPanel.add(chkActive);

        // Panel chứa nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");

        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(231, 76, 60));
        btnCancel.setForeground(Color.WHITE);

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Xử lý nút Lưu
        btnSave.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().trim());
                boolean isActive = chkActive.isSelected();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Tên sản phẩm không được để trống!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (price <= 0) {
                    JOptionPane.showMessageDialog(dialog,
                            "Giá phải lớn hơn 0!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Gọi controller để thêm sản phẩm
                productController.createProduct(name, price, isActive);

                JOptionPane.showMessageDialog(dialog,
                        "Thêm sản phẩm thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                loadDataFromDB(); // Refresh bảng

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Giá phải là số!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Lỗi: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Xử lý nút Hủy
        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    // ===== MỞ DIALOG SỬA SẢN PHẨM =====
    private void openEditDialog() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn sản phẩm cần sửa!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy ID từ cột đầu tiên
        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        double currentPrice = (double) tableModel.getValueAt(selectedRow, 2);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 3);
        boolean isActive = currentStatus.equals("Đang bán");

        // Tạo dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Sửa sản phẩm", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Panel chứa form
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblName = new JLabel("Tên sản phẩm:");
        JTextField txtName = new JTextField(currentName);

        JLabel lblPrice = new JLabel("Giá bán:");
        JTextField txtPrice = new JTextField(String.valueOf(currentPrice));

        JLabel lblStatus = new JLabel("Trạng thái:");
        JCheckBox chkActive = new JCheckBox("Đang bán");
        chkActive.setSelected(isActive);

        formPanel.add(lblName);
        formPanel.add(txtName);
        formPanel.add(lblPrice);
        formPanel.add(txtPrice);
        formPanel.add(lblStatus);
        formPanel.add(chkActive);

        // Panel chứa nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSave = new JButton("Cập nhật");
        JButton btnCancel = new JButton("Hủy");

        btnSave.setBackground(new Color(241, 196, 15));
        btnSave.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(231, 76, 60));
        btnCancel.setForeground(Color.WHITE);

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Xử lý nút Cập nhật
        btnSave.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().trim());
                boolean active = chkActive.isSelected();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Tên sản phẩm không được để trống!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (price <= 0) {
                    JOptionPane.showMessageDialog(dialog,
                            "Giá phải lớn hơn 0!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Tạo product với thông tin mới
                Product product = new Product();
                product.setId(productId);
                product.setName(name);
                product.setPrice(price);
                product.setActive(active);

                // Gọi controller để cập nhật
                productController.updateProduct(product);

                JOptionPane.showMessageDialog(dialog,
                        "Cập nhật sản phẩm thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                loadDataFromDB(); // Refresh bảng

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Giá phải là số!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Lỗi: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Xử lý nút Hủy
        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    // ===== XÓA SẢN PHẨM =====
    private void deleteSelectedProduct() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn sản phẩm cần xóa!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy thông tin sản phẩm
        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        String productName = (String) tableModel.getValueAt(selectedRow, 1);

        // Xác nhận xóa
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa sản phẩm:\n" + productName + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Tạo product với ID để xóa
                Product product = new Product();
                product.setId(productId);

                // Gọi controller để xóa
                productController.deleteProduct(product);

                JOptionPane.showMessageDialog(this,
                        "Xóa sản phẩm thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

                loadDataFromDB(); // Refresh bảng

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi xóa: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ===== BUTTON STYLE =====
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(120, 36));
        return btn;
    }
}
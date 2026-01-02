package org.example.view;

import org.example.controller.ProductController;
import org.example.controller.RecipeController;
import org.example.entity.Product;
import org.example.entity.Recipe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RecipeManagementPanel extends JPanel {

    private JComboBox<Product> cboProduct;
    private JTable table;
    private DefaultTableModel tableModel;

    private final ProductController productController = new ProductController();
    private final RecipeController recipeController = new RecipeController();

    public RecipeManagementPanel() {

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16));

        /* ================= TOP ================= */
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));

        JLabel lblTitle = new JLabel("CÔNG THỨC PHA CHẾ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        top.add(lblTitle);
        top.add(Box.createHorizontalStrut(30));

        top.add(new JLabel("Sản phẩm:"));

        cboProduct = new JComboBox<>();
        cboProduct.setPreferredSize(new Dimension(260, 30));
        top.add(cboProduct);

        JButton btnLoad = new JButton("Xem công thức");
        top.add(btnLoad);

        add(top, BorderLayout.NORTH);

        /* ================= TABLE ================= */
        String[] cols = {"ID", "Nguyên liệu", "Số lượng", "Đơn vị"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);
        add(new JScrollPane(table), BorderLayout.CENTER);

        /* ================= BUTTONS ================= */
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Làm mới");

        bottom.add(btnAdd);
        bottom.add(btnEdit);
        bottom.add(btnDelete);
        bottom.add(btnRefresh);

        add(bottom, BorderLayout.SOUTH);

        /* ================= LOAD DATA ================= */
        loadProducts();

        /* ================= EVENTS ================= */
        btnLoad.addActionListener(e -> loadRecipe());
        btnRefresh.addActionListener(e -> loadRecipe());
        btnAdd.addActionListener(e -> addRecipe());
        btnEdit.addActionListener(e -> editRecipe());
        btnDelete.addActionListener(e -> deleteRecipe());
    }

    /* ================= LOAD PRODUCTS ================= */
    private void loadProducts() {
        cboProduct.removeAllItems();

        List<Product> products = productController.getAllProduct();
        for (Product p : products) {
            cboProduct.addItem(p);
        }
    }

    /* ================= LOAD RECIPE ================= */
    private void loadRecipe() {

        Product p = (Product) cboProduct.getSelectedItem();
        if (p == null) return;

        tableModel.setRowCount(0);

        List<Recipe> list =
                recipeController.getRecipeByProduct(p.getId());

        for (Recipe r : list) {
            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getIngredientName(),
                    r.getAmount(),
                    r.getUnit()
            });
        }
    }

    /* ================= ADD ================= */
    private void addRecipe() {

        Product p = (Product) cboProduct.getSelectedItem();
        if (p == null) return;

        JTextField txtName = new JTextField();
        JTextField txtAmount = new JTextField();
        JTextField txtUnit = new JTextField();

        Object[] form = {
                "Nguyên liệu", txtName,
                "Số lượng", txtAmount,
                "Đơn vị", txtUnit
        };

        int rs = JOptionPane.showConfirmDialog(
                this, form,
                "Thêm nguyên liệu",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (rs == JOptionPane.OK_OPTION) {
            recipeController.createRecipe(
                    p.getId(),
                    txtName.getText(),
                    Double.parseDouble(txtAmount.getText()),
                    txtUnit.getText()
            );
            loadRecipe();
        }
    }

    /* ================= EDIT ================= */
    private void editRecipe() {

        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        double amount = (double) tableModel.getValueAt(row, 2);
        String unit = (String) tableModel.getValueAt(row, 3);

        JTextField txtName = new JTextField(name);
        JTextField txtAmount = new JTextField(String.valueOf(amount));
        JTextField txtUnit = new JTextField(unit);

        Object[] form = {
                "Nguyên liệu", txtName,
                "Số lượng", txtAmount,
                "Đơn vị", txtUnit
        };

        int rs = JOptionPane.showConfirmDialog(
                this, form,
                "Sửa nguyên liệu",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (rs == JOptionPane.OK_OPTION) {
            Recipe r = new Recipe();
            r.setId(id);
            r.setIngredientName(txtName.getText());
            r.setAmount(Double.parseDouble(txtAmount.getText()));
            r.setUnit(txtUnit.getText());

            recipeController.updateRecipe(r);
            loadRecipe();
        }
    }

    /* ================= DELETE ================= */
    private void deleteRecipe() {

        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xóa nguyên liệu này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            recipeController.deleteRecipe(id);
            loadRecipe();
        }
    }
}

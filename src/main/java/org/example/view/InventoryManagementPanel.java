package org.example.view;

import org.example.controller.InventoryController;
import org.example.entity.Inventory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
// THÊM 2 DÒNG IMPORT NÀY ĐỂ KHÔNG BỊ LỖI ĐỎ
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class InventoryManagementPanel extends JPanel {

    private final InventoryController controller = new InventoryController();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private JTable tblInventory;
    private JTable tblHistory;
    private DefaultTableModel inventoryModel;
    private DefaultTableModel historyModel;

    private JTextField txtQuantity;
    private JTextField txtNote;

    public InventoryManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
        loadInventory();

        // --- ĐOẠN CODE THÊM VÀO ĐÂY ---
        this.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                // Tự động load lại dữ liệu mỗi khi tab này được hiển thị
                loadInventory();
                if (historyModel != null) {
                    historyModel.setRowCount(0);
                }
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {}

            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });
        // --- KẾT THÚC ĐOẠN THÊM ---
    }

    private void initUI() {
        // Giữ nguyên toàn bộ nội dung hàm initUI của bạn...
        inventoryModel = new DefaultTableModel(new String[]{"Mã SP", "Tên SP", "Số lượng tồn"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblInventory = new JTable(inventoryModel);
        tblInventory.setRowHeight(28);

        tblInventory.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadHistory();
            }
        });

        historyModel = new DefaultTableModel(new String[]{"Thời gian", "Hành động", "Thay đổi", "Ghi chú"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHistory = new JTable(historyModel);
        tblHistory.setRowHeight(25);

        JScrollPane scrollInv = new JScrollPane(tblInventory);
        scrollInv.setBorder(BorderFactory.createTitledBorder("TỒN KHO HIỆN TẠI"));

        JScrollPane scrollHistory = new JScrollPane(tblHistory);
        scrollHistory.setBorder(BorderFactory.createTitledBorder("LỊCH SỬ GIAO DỊCH"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollInv, scrollHistory);
        splitPane.setDividerLocation(300);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        actionPanel.setBorder(BorderFactory.createEtchedBorder());

        txtQuantity = new JTextField(6);
        txtNote = new JTextField(15);

        JButton btnImport = new JButton("Nhập kho");
        JButton btnExport = new JButton("Xuất kho");
        JButton btnDeleteHistory = new JButton("Xóa lịch sử");

        btnImport.setBackground(new Color(46, 204, 113)); btnImport.setForeground(Color.WHITE);
        btnExport.setBackground(new Color(231, 76, 60)); btnExport.setForeground(Color.WHITE);
        btnDeleteHistory.setBackground(new Color(149, 165, 166)); btnDeleteHistory.setForeground(Color.WHITE);

        actionPanel.add(new JLabel("Số lượng:"));
        actionPanel.add(txtQuantity);
        actionPanel.add(new JLabel("Ghi chú:"));
        actionPanel.add(txtNote);
        actionPanel.add(btnImport);
        actionPanel.add(btnExport);
        actionPanel.add(btnDeleteHistory);

        add(splitPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        btnImport.addActionListener(e -> handleAction(true));
        btnExport.addActionListener(e -> handleAction(false));
        btnDeleteHistory.addActionListener(e -> handleDeleteHistory());
    }

    private void loadInventory() {
        inventoryModel.setRowCount(0);
        List<Inventory> list = controller.getAllInventory();
        for (Inventory inv : list) {
            inventoryModel.addRow(new Object[]{inv.getProductId(), inv.getProductName(), inv.getQuantity()});
        }
    }

    private void loadHistory() {
        historyModel.setRowCount(0);
        int row = tblInventory.getSelectedRow();
        if (row == -1) return;

        int productId = (int) inventoryModel.getValueAt(row, 0);
        List<Inventory> list = controller.getHistoryByProduct(productId);
        for (Inventory h : list) {
            historyModel.addRow(new Object[]{
                    h.getCreatedTime().format(formatter),
                    h.getAction(),
                    (h.getQuantityChange() > 0 ? "+" : "") + h.getQuantityChange(),
                    h.getNote()
            });
        }
    }

    private void handleAction(boolean isImport) {
        int row = tblInventory.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn sản phẩm!");
            return;
        }
        try {
            int pid = (int) inventoryModel.getValueAt(row, 0);
            int qty = Integer.parseInt(txtQuantity.getText().trim());
            String note = txtNote.getText();

            if (isImport) controller.importStock(pid, qty, note);
            else controller.exportStock(pid, qty, note);

            loadInventory();
            loadHistory();
            txtQuantity.setText(""); txtNote.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void handleDeleteHistory() {
        int row = tblInventory.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Hãy chọn sản phẩm ở bảng trên!");
            return;
        }
        int pid = (int) inventoryModel.getValueAt(row, 0);
        String name = (String) inventoryModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this, "Xóa toàn bộ lịch sử của " + name + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteHistory(pid);
            loadHistory();
            JOptionPane.showMessageDialog(this, "Đã xóa lịch sử!");
        }
    }
}
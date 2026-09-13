package org.example.view;

import org.example.controller.InventoryController;
import org.example.entity.Inventory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InventoryManagementPanel extends JPanel {

    private static final Color BG           = new Color(0xF5F7FA);
    private static final Color HEADER_BG    = new Color(0x1E293B);
    private static final Color BTN_GREEN  = new Color(0x22C55E);
    private static final Color BTN_RED    = new Color(0xEF4444);
    private static final Color BTN_SLATE  = new Color(0x64748B);
    private static final Color BTN_BLUE   = new Color(0x3B82F6);

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD, 13);

    private final InventoryController controller = new InventoryController();
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private JTable tblInventory;
    private JTable tblHistory;
    private DefaultTableModel inventoryModel;
    private DefaultTableModel historyModel;

    public InventoryManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);

        initUI();
        try {
            loadInventory();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu tồn kho: " + ex.getMessage());
        }

        this.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                try {
                    loadInventory();
                    if (historyModel != null) historyModel.setRowCount(0);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(InventoryManagementPanel.this, "Lỗi khi tải dữ liệu tồn kho: " + ex.getMessage());
                }
            }
            @Override public void ancestorRemoved(AncestorEvent event) {}
            @Override public void ancestorMoved(AncestorEvent event) {}
        });
    }

    private void initUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("\uD83D\uDCE6");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Quản Lý Tồn Kho");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Theo dõi nhập, xuất và lịch sử biến động kho hàng");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(0x94A3B8));

        titleBlock.add(title);
        titleBlock.add(sub);

        left.add(icon);
        left.add(titleBlock);
        header.add(left, BorderLayout.WEST);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controlPanel.setOpaque(false);
        controlPanel.setBorder(new EmptyBorder(16, 20, 10, 20));

        JButton btnImport = createButton("＋ Nhập kho", BTN_GREEN);
        JButton btnExport = createButton("－ Xuất kho", BTN_RED);
        JButton btnDeleteHistory = createButton("✕ Xóa lịch sử", BTN_SLATE);
        JButton btnRefresh = createButton("↻ Làm mới", BTN_BLUE);

        controlPanel.add(btnImport);
        controlPanel.add(btnExport);
        controlPanel.add(btnDeleteHistory);
        controlPanel.add(btnRefresh);

        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.setBackground(BG);
        northContainer.add(header, BorderLayout.NORTH);
        northContainer.add(controlPanel, BorderLayout.CENTER);

        add(northContainer, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        inventoryModel = new DefaultTableModel(
                new String[]{"Mã SP", "Tên SP", "Số lượng tồn"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblInventory = new JTable(inventoryModel);
        tblInventory.setRowHeight(32);
        tblInventory.setFont(FONT_BODY);

        tblInventory.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadHistory();
        });

        JScrollPane scrollInv = new JScrollPane(tblInventory);
        scrollInv.setBorder(BorderFactory.createTitledBorder("Danh sách tồn kho"));
        scrollInv.getViewport().setBackground(Color.WHITE);

        historyModel = new DefaultTableModel(
                new String[]{"Thời gian", "Hành động", "Thay đổi", "Ghi chú"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHistory = new JTable(historyModel);
        tblHistory.setRowHeight(28);
        tblHistory.setFont(FONT_BODY);

        JScrollPane scrollHistory = new JScrollPane(tblHistory);
        scrollHistory.setBorder(BorderFactory.createTitledBorder("Lịch sử kho"));
        scrollHistory.getViewport().setBackground(Color.WHITE);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, scrollInv, scrollHistory
        );
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);

        centerPanel.add(splitPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        btnImport.addActionListener(e -> showStockDialog(true));
        btnExport.addActionListener(e -> showStockDialog(false));
        btnDeleteHistory.addActionListener(e -> handleDeleteHistory());
        btnRefresh.addActionListener(e -> {
            try {
                loadInventory();
                historyModel.setRowCount(0);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi làm mới dữ liệu: " + ex.getMessage());
            }
        });
    }

    /* ===== DIALOG NHẬP/XUẤT KHO ===== */
    private void showStockDialog(boolean isImport) {
        int row = tblInventory.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!");
            return;
        }

        int productId = (int) inventoryModel.getValueAt(row, 0);
        String productName = (String) inventoryModel.getValueAt(row, 1);
        int currentQty = (int) inventoryModel.getValueAt(row, 2);

        String title = isImport ? "Nhập kho" : "Xuất kho";
        String headerText = isImport
                ? "Nhập hàng cho: " + productName
                : "Xuất hàng cho: " + productName;

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblHeader = new JLabel(headerText);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblHeader, gbc);

        if (!isImport) {
            gbc.gridy++;
            JLabel lblCurrent = new JLabel("Tồn kho hiện tại: " + currentQty);
            lblCurrent.setFont(FONT_BODY);
            panel.add(lblCurrent, gbc);
        }

        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Số lượng:"), gbc);
        JSpinner spnQuantity = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        spnQuantity.setFont(FONT_BODY);
        spnQuantity.setPreferredSize(new Dimension(120, 30));
        gbc.gridx = 1;
        panel.add(spnQuantity, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Ghi chú:"), gbc);
        JTextField txtNote = new JTextField(20);
        txtNote.setFont(FONT_BODY);
        gbc.gridx = 1;
        panel.add(txtNote, gbc);

        int result = JOptionPane.showConfirmDialog(
                this, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                int qty = (int) spnQuantity.getValue();
                String note = txtNote.getText().trim();

                if (isImport) {
                    controller.importStock(productId, qty, note);
                } else {
                    controller.exportStock(productId, qty, note);
                }

                loadInventory();
                selectProductById(productId);
                loadHistory();
                JOptionPane.showMessageDialog(this,
                        (isImport ? "Nhập" : "Xuất") + " kho thành công!");
            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (msg == null || msg.isEmpty()) msg = "Lỗi không xác định";
                JOptionPane.showMessageDialog(this, msg);
            }
        }
    }

    /* ===== LOAD DATA ===== */
    private void loadInventory() {
        try {
            inventoryModel.setRowCount(0);
            List<Inventory> list = controller.getAllInventory();
            for (Inventory inv : list) {
                inventoryModel.addRow(new Object[]{
                        inv.getProductId(), inv.getProductName(), inv.getQuantity()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách tồn kho: " + ex.getMessage());
        }
    }

    private void selectProductById(int productId) {
        for (int i = 0; i < inventoryModel.getRowCount(); i++) {
            if ((int) inventoryModel.getValueAt(i, 0) == productId) {
                tblInventory.setRowSelectionInterval(i, i);
                tblInventory.scrollRectToVisible(tblInventory.getCellRect(i, 0, true));
                return;
            }
        }
    }

    private void loadHistory() {
        try {
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
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải lịch sử tồn kho: " + ex.getMessage());
        }
    }

    private void handleDeleteHistory() {
        int row = tblInventory.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Hãy chọn sản phẩm!");
            return;
        }
        int pid = (int) inventoryModel.getValueAt(row, 0);
        String name = (String) inventoryModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this, "Xóa toàn bộ lịch sử của " + name + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteHistory(pid);
            loadHistory();
            JOptionPane.showMessageDialog(this, "Đã xóa lịch sử!");
        }
    }

    private JButton createButton(String text, Color base) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed()
                        ? base.darker()
                        : getModel().isRollover() ? base.brighter() : base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 36));
        return btn;
    }
}

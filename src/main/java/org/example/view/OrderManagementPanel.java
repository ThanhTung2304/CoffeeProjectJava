package org.example.view;

import org.example.controller.OrderController;
import org.example.controller.ProductController;
import org.example.entity.Order;
import org.example.entity.OrderDetail;
import org.example.entity.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderManagementPanel extends JPanel {

    // ===== STYLE =====
    private static final Color BG = new Color(0xF5F7FA);
    private static final Color HEADER_BG = new Color(0x1E293B);
    private static final Color ROW_ODD = Color.WHITE;
    private static final Color ROW_EVEN = new Color(0xF8FAFC);
    private static final Color ROW_SELECTED = new Color(0xDBEAFE);

    private static final Color BTN_GREEN = new Color(0x22C55E);
    private static final Color BTN_RED = new Color(0xEF4444);
    private static final Color BTN_BLUE = new Color(0x3B82F6);
    private static final Color BTN_SLATE = new Color(0x64748B);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    private final OrderController orderController = new OrderController();
    private final ProductController productController = new ProductController();

    private JTable orderTable;
    private DefaultTableModel orderTableModel;

    private static final NumberFormat VND =
            NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    public OrderManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);

        loadOrders();
    }

    // ===== HEADER =====
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel("🧾 Quản Lý Đơn Hàng");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        return header;
    }

    // ===== CENTER =====
    private JPanel buildCenter() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(16, 20, 20, 20));
        panel.setBackground(BG);

        panel.add(buildActions(), BorderLayout.NORTH);
        panel.add(buildTable(), BorderLayout.CENTER);

        return panel;
    }

    // ===== ACTIONS =====
    private JPanel buildActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);

        JButton btnNew = createButton("+ Tạo đơn", BTN_GREEN);
        JButton btnView = createButton("📋 Hóa đơn", BTN_BLUE);
        JButton btnDone = createButton("✔ Hoàn thành", BTN_GREEN);
        JButton btnCancel = createButton("✖ Hủy", BTN_RED);
        JButton btnRefresh = createButton("↻", BTN_SLATE);

        panel.add(btnNew);
        panel.add(btnView);
        panel.add(btnDone);
        panel.add(btnCancel);
        panel.add(btnRefresh);

        btnRefresh.addActionListener(e -> loadOrders());
        btnNew.addActionListener(e -> openCreateOrderDialog());
        btnView.addActionListener(e -> openInvoiceDialog());
        btnDone.addActionListener(e -> changeStatus("COMPLETED"));
        btnCancel.addActionListener(e -> changeStatus("CANCELLED"));

        return panel;
    }

    // ===== TABLE =====
    private JScrollPane buildTable() {

        String[] cols = {"ID", "Mã đơn", "Tổng tiền", "Trạng thái", "Ghi chú", "Ngày tạo"};

        orderTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        orderTable = new JTable(orderTableModel);
        orderTable.setRowHeight(38);
        orderTable.setFont(FONT_BODY);
        orderTable.setSelectionBackground(ROW_SELECTED);
        orderTable.setShowVerticalLines(false);
        orderTable.setAutoCreateRowSorter(true);

        // ===== RENDER =====
        orderTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {

                if (col == 3 && value != null) {
                    JLabel lb = new JLabel(value.toString(), CENTER);
                    lb.setOpaque(true);

                    String v = value.toString();
                    if (v.equals("Hoàn thành")) lb.setBackground(new Color(0xDCFCE7));
                    else if (v.equals("Chờ xử lý")) lb.setBackground(new Color(0xFEF9C3));
                    else if (v.equals("Đã hủy")) lb.setBackground(new Color(0xFEE2E2));

                    return lb;
                }

                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);

                if (isSelected) setBackground(ROW_SELECTED);
                else setBackground(row % 2 == 0 ? ROW_ODD : ROW_EVEN);

                return this;
            }
        });

        return new JScrollPane(orderTable);
    }

    // ===== LOAD =====
    private void loadOrders() {
        orderTableModel.setRowCount(0);

        List<Order> list = orderController.getAllOrders();
        for (Order o : list) {
            orderTableModel.addRow(new Object[]{
                    o.getId(),
                    o.getOrderCode(),
                    VND.format(o.getTotalAmount()) + " ₫",
                    translateStatus(o.getStatus()),
                    o.getNote(),
                    o.getCreatedTime()
            });
        }
    }

    // ===== STATUS =====
    private void changeStatus(String status) {
        int row = orderTable.getSelectedRow();
        if (row == -1) return;

        int modelRow = orderTable.convertRowIndexToModel(row);
        int id = (int) orderTableModel.getValueAt(modelRow, 0);

        if ("COMPLETED".equals(status))
            orderController.completeOrder(id);
        else
            orderController.cancelOrder(id);

        loadOrders();
    }

    private String translateStatus(String s) {
        if ("PENDING".equals(s)) return "Chờ xử lý";
        if ("COMPLETED".equals(s)) return "Hoàn thành";
        if ("CANCELLED".equals(s)) return "Đã hủy";
        return s;
    }

    // ===== BUTTON =====
    private JButton createButton(String text, Color base) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(getModel().isPressed() ? base.darker() : base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        return btn;
    }

    // ===== DIALOG GIỮ NGUYÊN =====
    private void openCreateOrderDialog() {
        JOptionPane.showMessageDialog(this, "Giữ nguyên dialog cũ của bạn");
    }

    private void openInvoiceDialog() {
        JOptionPane.showMessageDialog(this, "Giữ nguyên phần hóa đơn");
    }
}
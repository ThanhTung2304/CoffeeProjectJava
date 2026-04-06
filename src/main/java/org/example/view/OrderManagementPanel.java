package org.example.view;

import org.example.controller.OrderController;
import org.example.controller.ProductController;
import org.example.entity.Order;
import org.example.entity.OrderDetail;
import org.example.entity.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderManagementPanel extends JPanel {

    private final OrderController orderController = new OrderController();
    private final ProductController productController = new ProductController();

    private JTable orderTable;
    private DefaultTableModel orderTableModel;

    private static final NumberFormat VND =
            NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    public OrderManagementPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // ===== TITLE =====
        JLabel title = new JLabel("QUẢN LÝ ĐƠN HÀNG", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(0, 0, 12, 0));

        // ===== ACTION BUTTONS =====
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actionPanel.setOpaque(false);

        JButton btnNewOrder  = createButton("+ Tạo đơn",  new Color(46, 204, 113));
        JButton btnViewInv   = createButton("📋 Hóa đơn", new Color(52, 152, 219));
        JButton btnComplete  = createButton("✔ Hoàn thành", new Color(39, 174, 96));
        JButton btnCancel    = createButton("✖ Hủy đơn",  new Color(231, 76, 60));
        JButton btnRefresh   = createButton("↺ Refresh",  new Color(149, 165, 166));

        actionPanel.add(btnNewOrder);
        actionPanel.add(btnViewInv);
        actionPanel.add(btnComplete);
        actionPanel.add(btnCancel);
        actionPanel.add(btnRefresh);

        // ===== ORDER TABLE =====
        String[] cols = {"ID", "Mã đơn", "Tổng tiền", "Trạng thái", "Ghi chú", "Ngày tạo"};
        orderTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        orderTable = new JTable(orderTableModel);
        orderTable.setRowHeight(28);
        orderTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(orderTable);

        // ===== LAYOUT =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(actionPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // ===== EVENTS =====
        btnRefresh.addActionListener(e -> loadOrders());
        btnNewOrder.addActionListener(e -> openCreateOrderDialog());
        btnViewInv.addActionListener(e -> openInvoiceDialog());
        btnComplete.addActionListener(e -> changeStatus("COMPLETED"));
        btnCancel.addActionListener(e -> changeStatus("CANCELLED"));

        loadOrders();
    }

    // ===== LOAD DANH SÁCH ĐƠN =====
    private void loadOrders() {
        orderTableModel.setRowCount(0);
        List<Order> orders = orderController.getAllOrders();
        for (Order o : orders) {
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

    // ===== DIALOG TẠO ĐƠN HÀNG =====
    private void openCreateOrderDialog() {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Tạo đơn hàng mới", true);
        dialog.setSize(720, 540);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(8, 8));

        // --- Chọn sản phẩm (trái) ---
        List<Product> products = productController.getAllProduct()
                .stream().filter(Product::isActive).toList();

        String[] prodCols = {"ID", "Tên sản phẩm", "Giá"};
        DefaultTableModel prodModel = new DefaultTableModel(prodCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Product p : products) {
            prodModel.addRow(new Object[]{
                    p.getId(), p.getName(), VND.format(p.getPrice()) + " ₫"
            });
        }
        JTable prodTable = new JTable(prodModel);
        prodTable.setRowHeight(26);
        prodTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane prodScroll = new JScrollPane(prodTable);
        prodScroll.setPreferredSize(new Dimension(280, 300));

        JLabel lblProd = new JLabel("Chọn sản phẩm:");
        lblProd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JPanel leftPanel = new JPanel(new BorderLayout(4, 4));
        leftPanel.setBorder(new EmptyBorder(12, 12, 4, 6));
        leftPanel.add(lblProd, BorderLayout.NORTH);
        leftPanel.add(prodScroll, BorderLayout.CENTER);

        // Số lượng + nút thêm
        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        JLabel lblQty = new JLabel("Số lượng:");
        JSpinner spnQty = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        JButton btnAddItem = createButton("Thêm vào đơn", new Color(52, 152, 219));
        qtyPanel.add(lblQty);
        qtyPanel.add(spnQty);
        qtyPanel.add(btnAddItem);
        leftPanel.add(qtyPanel, BorderLayout.SOUTH);

        // --- Chi tiết đơn hàng (phải) ---
        String[] detCols = {"Sản phẩm", "Đơn giá", "SL", "Thành tiền", "Xóa"};
        DefaultTableModel detModel = new DefaultTableModel(detCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 4; }
        };
        JTable detTable = new JTable(detModel);
        detTable.setRowHeight(26);
        JScrollPane detScroll = new JScrollPane(detTable);
        detScroll.setPreferredSize(new Dimension(340, 300));

        JLabel lblDet = new JLabel("Chi tiết đơn hàng:");
        lblDet.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel lblTotal = new JLabel("Tổng cộng: 0 ₫");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setForeground(new Color(192, 57, 43));

        JPanel rightPanel = new JPanel(new BorderLayout(4, 4));
        rightPanel.setBorder(new EmptyBorder(12, 6, 4, 12));
        rightPanel.add(lblDet, BorderLayout.NORTH);
        rightPanel.add(detScroll, BorderLayout.CENTER);
        rightPanel.add(lblTotal, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(310);
        split.setBorder(null);

        // --- Ghi chú + nút lưu ---
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 0));
        bottomPanel.setBorder(new EmptyBorder(4, 12, 12, 12));
        JTextField txtNote = new JTextField();
        JButton btnSave = createButton("Tạo đơn & In hóa đơn", new Color(46, 204, 113));
        JButton btnClose = createButton("Đóng", new Color(149, 165, 166));
        bottomPanel.add(new JLabel("Ghi chú:"), BorderLayout.WEST);
        bottomPanel.add(txtNote, BorderLayout.CENTER);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.add(btnSave);
        btnRow.add(btnClose);
        bottomPanel.add(btnRow, BorderLayout.EAST);

        dialog.add(split, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        // --- Danh sách items đang chọn ---
        List<OrderDetail> cartItems = new ArrayList<>();

        // Hàm cập nhật tổng tiền
        Runnable refreshTotal = () -> {
            double sum = cartItems.stream().mapToDouble(OrderDetail::getSubtotal).sum();
            lblTotal.setText("Tổng cộng: " + VND.format(sum) + " ₫");
        };

        // Thêm item vào đơn
        btnAddItem.addActionListener(e -> {
            int row = prodTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn sản phẩm!");
                return;
            }
            int prodId   = (int) prodModel.getValueAt(row, 0);
            String pName = (String) prodModel.getValueAt(row, 1);
            Product prod = productController.findProductById(prodId);
            int qty      = (int) spnQty.getValue();

            // Nếu sản phẩm đã có trong giỏ thì cộng thêm số lượng
            OrderDetail existing = cartItems.stream()
                    .filter(d -> d.getProductId() == prodId)
                    .findFirst().orElse(null);

            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + qty);
                // Cập nhật lại dòng trong bảng
                for (int i = 0; i < detModel.getRowCount(); i++) {
                    if (detModel.getValueAt(i, 0).equals(pName)) {
                        detModel.setValueAt(existing.getQuantity(), i, 2);
                        detModel.setValueAt(VND.format(existing.getSubtotal()) + " ₫", i, 3);
                    }
                }
            } else {
                OrderDetail detail = new OrderDetail(
                        prodId, pName, prod.getPrice(), qty);
                cartItems.add(detail);
                detModel.addRow(new Object[]{
                        pName,
                        VND.format(prod.getPrice()) + " ₫",
                        qty,
                        VND.format(detail.getSubtotal()) + " ₫",
                        "Xóa"
                });
            }
            refreshTotal.run();
        });

        // Xóa item khỏi đơn bằng cách click cột "Xóa"
        detTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = detTable.columnAtPoint(e.getPoint());
                int row = detTable.rowAtPoint(e.getPoint());
                if (col == 4 && row >= 0) {
                    cartItems.remove(row);
                    detModel.removeRow(row);
                    refreshTotal.run();
                }
            }
        });

        // Tạo đơn & in hóa đơn
        btnSave.addActionListener(e -> {
            if (cartItems.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Chưa có sản phẩm trong đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Order order = orderController.createOrder(
                        cartItems, txtNote.getText().trim());
                dialog.dispose();
                loadOrders();
                showInvoice(order);  // In hóa đơn ngay sau khi tạo
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnClose.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    // ===== XEM HÓA ĐƠN TỪ DANH SÁCH ĐƠN =====
    private void openInvoiceDialog() {
        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng!");
            return;
        }
        int orderId = (int) orderTableModel.getValueAt(row, 0);
        Order order = orderController.getOrderWithDetails(orderId);
        if (order != null) showInvoice(order);
    }

    // ===== HIỂN THỊ HÓA ĐƠN CHI TIẾT =====
    private void showInvoice(Order order) {
        JDialog inv = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Hóa đơn - " + order.getOrderCode(), true);
        inv.setSize(480, 520);
        inv.setLocationRelativeTo(this);
        inv.setLayout(new BorderLayout(8, 8));

        // Nội dung hóa đơn dạng text
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════╗\n");
        sb.append("║    HIGHTLANDS COFFEE - HÓA ĐƠN       ║\n");
        sb.append("╚══════════════════════════════════════╝\n\n");
        sb.append("  Mã đơn  : ").append(order.getOrderCode()).append("\n");
        sb.append("  Ngày    : ").append(order.getCreatedTime()).append("\n");
        sb.append("  TT      : ").append(translateStatus(order.getStatus())).append("\n");
        if (order.getNote() != null && !order.getNote().isBlank())
            sb.append("  Ghi chú : ").append(order.getNote()).append("\n");
        sb.append("\n");
        sb.append("──────────────────────────────────────────\n");
        sb.append(String.format("  %-22s %5s %12s\n", "Sản phẩm", "SL", "Thành tiền"));
        sb.append("──────────────────────────────────────────\n");

        for (OrderDetail d : order.getDetails()) {
            sb.append(String.format("  %-22s %5d %12s ₫\n",
                    d.getProductName(),
                    d.getQuantity(),
                    VND.format(d.getSubtotal())));
        }

        sb.append("──────────────────────────────────────────\n");
        sb.append(String.format("  %-28s %12s ₫\n",
                "TỔNG CỘNG", VND.format(order.getTotalAmount())));
        sb.append("\n        Cảm ơn quý khách! Hẹn gặp lại.\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setMargin(new Insets(12, 12, 12, 12));

        JScrollPane scroll = new JScrollPane(area);

        // Nút in & đóng
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        JButton btnPrint = createButton("🖨 In hóa đơn", new Color(52, 152, 219));
        JButton btnClose = createButton("Đóng", new Color(149, 165, 166));
        btnPanel.add(btnPrint);
        btnPanel.add(btnClose);

        btnPrint.addActionListener(e -> {
            try {
                area.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(inv, "Lỗi in: " + ex.getMessage());
            }
        });
        btnClose.addActionListener(e -> inv.dispose());

        inv.add(scroll, BorderLayout.CENTER);
        inv.add(btnPanel, BorderLayout.SOUTH);
        inv.setVisible(true);
    }

    // ===== ĐỔI TRẠNG THÁI ĐƠN =====
    private void changeStatus(String newStatus) {
        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng!");
            return;
        }
        int orderId = (int) orderTableModel.getValueAt(row, 0);
        String code = (String) orderTableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận đổi trạng thái đơn " + code + " sang "
                        + translateStatus(newStatus) + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if ("COMPLETED".equals(newStatus))
                    orderController.completeOrder(orderId);
                else
                    orderController.cancelOrder(orderId);
                loadOrders();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }
    }

    // ===== HELPER =====
    private String translateStatus(String status) {
        return switch (status) {
            case "PENDING"   -> "Chờ xử lý";
            case "COMPLETED" -> "Hoàn thành";
            case "CANCELLED" -> "Đã hủy";
            default -> status;
        };
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(150, 36));
        return btn;
    }
}
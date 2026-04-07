package org.example.view;

import org.example.controller.OrderController;
import org.example.controller.ProductController;
import org.example.controller.TableController;
import org.example.controller.VoucherController;
import org.example.entity.Order;
import org.example.entity.OrderDetail;
import org.example.entity.Product;
import org.example.entity.TableSeat;
import org.example.entity.Voucher;
import org.example.session.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductManagementPanel extends JPanel {

    private JTable productTable;
    private DefaultTableModel productTableModel;

    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel lblTotal;

    private final ProductController productController = new ProductController();
    private final OrderController   orderController   = new OrderController();
    private final TableController   tableController   = new TableController();
    private final VoucherController voucherController = new VoucherController();

    private JTextField txtSearch;
    private JComboBox<String> cbStatus;

    public ProductManagementPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        boolean isAdmin = UserSession.getInstance().isAdmin();

        // ===== TITLE =====
        JLabel title = new JLabel("QUẢN LÝ SẢN PHẨM", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(0, 0, 12, 0));

        // ===== SEARCH =====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        topPanel.setOpaque(false);
        txtSearch = new JTextField(22);
        JButton btnSearch = new JButton("🔍 Tìm");
        cbStatus = new JComboBox<>(new String[]{"Tất cả", "Đang bán", "Ngừng bán"});
        topPanel.add(new JLabel("Tìm:"));
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("Trạng thái:"));
        topPanel.add(cbStatus);

        // ===== ACTION BUTTONS =====
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        actionPanel.setOpaque(false);
        JButton btnAdd     = createButton("Thêm",       new Color(46, 204, 113));
        JButton btnEdit    = createButton("Sửa",        new Color(241, 196, 15));
        JButton btnDelete  = createButton("Xóa",        new Color(231, 76, 60));
        JButton btnRefresh = createButton("Refresh",    new Color(149, 165, 166));
        JButton btnAddCart = createButton("+ Giỏ hàng", new Color(52, 152, 219));
        btnAdd.setVisible(isAdmin);
        btnEdit.setVisible(isAdmin);
        btnDelete.setVisible(isAdmin);
        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);
        actionPanel.add(btnAddCart);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(title,       BorderLayout.NORTH);
        headerPanel.add(topPanel,    BorderLayout.CENTER);
        headerPanel.add(actionPanel, BorderLayout.SOUTH);

        // ===== PRODUCT TABLE =====
        productTableModel = new DefaultTableModel(
                new String[]{"ID", "Tên sản phẩm", "Giá bán", "Trạng thái", "Ngày tạo", "Cập nhật"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = new JTable(productTableModel);
        productTable.setRowHeight(28);
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane productScroll = new JScrollPane(productTable);
        productScroll.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm"));

        // ===== CART TABLE =====
        cartTableModel = new DefaultTableModel(
                new String[]{"Tên sản phẩm", "Đơn giá", "SL", "Thành tiền"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 2; }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(28);
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        cartTableModel.addTableModelListener(e -> {
            if (e.getColumn() == 2) {
                int row = e.getFirstRow();
                try {
                    int qty = Integer.parseInt(cartTableModel.getValueAt(row, 2).toString());
                    double unitPrice = (double) cartTableModel.getValueAt(row, 1);
                    if (qty <= 0) cartTableModel.removeRow(row);
                    else cartTableModel.setValueAt(unitPrice * qty, row, 3);
                    updateTotal();
                } catch (NumberFormatException ignored) {}
            }
        });
        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setBorder(BorderFactory.createTitledBorder("Giỏ hàng"));

        // ===== BOTTOM GIỎ HÀNG =====
        lblTotal = new JLabel("Tổng tiền: 0 đ");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotal.setForeground(new Color(192, 57, 43));
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnRemoveCart = createFullWidthButton("Xóa dòng đã chọn", new Color(192, 57, 43));
        JButton btnClearCart  = createFullWidthButton("Xóa tất cả",       new Color(127, 140, 141));
        JButton btnCheckout   = createFullWidthButton("🛒  Thanh toán",    new Color(39, 174, 96));

        JPanel cartBottomPanel = new JPanel();
        cartBottomPanel.setLayout(new BoxLayout(cartBottomPanel, BoxLayout.Y_AXIS));
        cartBottomPanel.setOpaque(false);
        cartBottomPanel.setBorder(new EmptyBorder(8, 6, 6, 6));
        cartBottomPanel.add(lblTotal);
        cartBottomPanel.add(Box.createVerticalStrut(8));
        cartBottomPanel.add(btnRemoveCart);
        cartBottomPanel.add(Box.createVerticalStrut(4));
        cartBottomPanel.add(btnClearCart);
        cartBottomPanel.add(Box.createVerticalStrut(4));
        cartBottomPanel.add(btnCheckout);

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setOpaque(false);
        cartPanel.add(cartScroll,      BorderLayout.CENTER);
        cartPanel.add(cartBottomPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, productScroll, cartPanel);
        splitPane.setDividerLocation(750);
        splitPane.setResizeWeight(0.65);
        splitPane.setOpaque(false);

        add(headerPanel, BorderLayout.NORTH);
        add(splitPane,   BorderLayout.CENTER);

        loadDataFromDB();

        // ===== EVENTS =====
        btnRefresh.addActionListener(e -> loadDataFromDB());
        btnSearch.addActionListener(e -> loadDataFromDB());
        cbStatus.addActionListener(e -> loadDataFromDB());
        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteSelectedProduct());
        btnAddCart.addActionListener(e -> addToCart());
        productTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) addToCart();
            }
        });
        btnRemoveCart.addActionListener(e -> removeFromCart());
        btnClearCart.addActionListener(e -> { cartTableModel.setRowCount(0); updateTotal(); });
        btnCheckout.addActionListener(e -> openCheckoutDialog());
    }

    // ===== THÊM VÀO GIỎ =====
    private void addToCart() {
        int row = productTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return; }
        if (!"Đang bán".equals(productTableModel.getValueAt(row, 3))) {
            JOptionPane.showMessageDialog(this, "Sản phẩm đã ngừng bán!", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return;
        }
        String name  = (String) productTableModel.getValueAt(row, 1);
        double price = (double) productTableModel.getValueAt(row, 2);
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            if (cartTableModel.getValueAt(i, 0).equals(name)) {
                int qty = (int) cartTableModel.getValueAt(i, 2);
                cartTableModel.setValueAt(qty + 1, i, 2);
                cartTableModel.setValueAt(price * (qty + 1), i, 3);
                updateTotal(); return;
            }
        }
        cartTableModel.addRow(new Object[]{name, price, 1, price});
        updateTotal();
    }

    private void removeFromCart() {
        int row = cartTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm trong giỏ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return; }
        cartTableModel.removeRow(row); updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) total += (double) cartTableModel.getValueAt(i, 3);
        lblTotal.setText(String.format("Tổng tiền: %,.0f đ", total));
    }

    // ===== DIALOG THANH TOÁN =====
    private void openCheckoutDialog() {
        if (cartTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thanh Toán", true);
        dialog.setSize(600, 720);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // ===== BẢNG TÓM TẮT =====
        DefaultTableModel summaryModel = new DefaultTableModel(
                new String[]{"Sản phẩm", "Đơn giá", "SL", "Thành tiền"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        double subtotal = 0;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            summaryModel.addRow(new Object[]{
                    cartTableModel.getValueAt(i, 0), cartTableModel.getValueAt(i, 1),
                    cartTableModel.getValueAt(i, 2), cartTableModel.getValueAt(i, 3)
            });
            subtotal += (double) cartTableModel.getValueAt(i, 3);
        }
        final double[] subtotalRef = {subtotal}; // dùng trong lambda

        JTable summaryTable = new JTable(summaryModel);
        summaryTable.setRowHeight(26);
        summaryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane summaryScroll = new JScrollPane(summaryTable);
        summaryScroll.setPreferredSize(new Dimension(560, 130));
        summaryScroll.setBorder(BorderFactory.createTitledBorder("Chi tiết đơn hàng"));

        // ===== THÔNG TIN ĐƠN =====
        List<TableSeat> tables = tableController.getAllTables();
        String[] tableNames = tables.stream()
                .map(t -> t.getName() + " (Bàn " + t.getTableNumber() + ")")
                .toArray(String[]::new);
        JComboBox<String> cbTable   = new JComboBox<>(tableNames);
        JComboBox<String> cbPayment = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản"});
        JTextField txtNote = new JTextField();

        JPanel orderInfoPanel = new JPanel(new GridLayout(3, 2, 10, 8));
        orderInfoPanel.setOpaque(false);
        orderInfoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin đơn hàng"));
        orderInfoPanel.add(new JLabel("  Chọn bàn:"));       orderInfoPanel.add(cbTable);
        orderInfoPanel.add(new JLabel("  Phương thức TT:")); orderInfoPanel.add(cbPayment);
        orderInfoPanel.add(new JLabel("  Ghi chú:"));        orderInfoPanel.add(txtNote);

        // ===== PANEL NGÂN HÀNG =====
        JComboBox<String> cbBank      = new JComboBox<>(new String[]{"Vietcombank","Techcombank","BIDV","Agribank","MB Bank","VPBank","ACB","Sacombank","TPBank"});
        JTextField txtAccountNumber   = new JTextField();
        JTextField txtAccountName     = new JTextField();
        JTextField txtTransferContent = new JTextField("Thanh toan don hang");

        JPanel bankPanel = new JPanel(new GridLayout(4, 2, 10, 8));
        bankPanel.setOpaque(false);
        bankPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 1),
                "Thông tin chuyển khoản", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12), new Color(52, 152, 219)));
        bankPanel.add(new JLabel("  Ngân hàng:"));     bankPanel.add(cbBank);
        bankPanel.add(new JLabel("  Số tài khoản:"));  bankPanel.add(txtAccountNumber);
        bankPanel.add(new JLabel("  Tên tài khoản:")); bankPanel.add(txtAccountName);
        bankPanel.add(new JLabel("  Nội dung CK:"));   bankPanel.add(txtTransferContent);
        bankPanel.setVisible(false);

        cbPayment.addActionListener(e -> {
            bankPanel.setVisible("Chuyển khoản".equals(cbPayment.getSelectedItem()));
            dialog.revalidate(); dialog.repaint();
        });

        // ===== PANEL VOUCHER =====
        JTextField txtVoucherCode = new JTextField();
        txtVoucherCode.setPreferredSize(new Dimension(180, 30));
        JButton btnApplyVoucher   = createButton("Áp dụng", new Color(52, 152, 219));
        btnApplyVoucher.setPreferredSize(new Dimension(100, 30));
        JLabel lblVoucherInfo     = new JLabel("Chưa áp dụng voucher");
        lblVoucherInfo.setForeground(Color.GRAY);
        lblVoucherInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        JPanel voucherInputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        voucherInputRow.setOpaque(false);
        voucherInputRow.add(new JLabel("  Mã voucher:"));
        voucherInputRow.add(txtVoucherCode);
        voucherInputRow.add(btnApplyVoucher);

        JPanel voucherPanel = new JPanel(new BorderLayout(0, 4));
        voucherPanel.setOpaque(false);
        voucherPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(39, 174, 96), 1),
                "Voucher giảm giá", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12), new Color(39, 174, 96)));
        voucherPanel.add(voucherInputRow, BorderLayout.NORTH);
        voucherPanel.add(lblVoucherInfo,  BorderLayout.CENTER);

        // ===== TỔNG TIỀN =====
        JLabel lblSubtotal    = new JLabel(String.format("  Tạm tính:       %,.0f đ", subtotal));
        JLabel lblDiscount    = new JLabel("  Giảm giá:       0 đ");
        JLabel lblFinalTotal  = new JLabel(String.format("  Tổng thanh toán: %,.0f đ", subtotal));

        lblSubtotal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDiscount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDiscount.setForeground(new Color(39, 174, 96));
        lblFinalTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFinalTotal.setForeground(new Color(192, 57, 43));

        JPanel totalPanel = new JPanel(new GridLayout(3, 1, 0, 4));
        totalPanel.setOpaque(false);
        totalPanel.setBorder(new EmptyBorder(6, 4, 6, 4));
        totalPanel.add(lblSubtotal);
        totalPanel.add(lblDiscount);
        totalPanel.add(lblFinalTotal);

        // Voucher được chọn (lưu tham chiếu để dùng khi xác nhận)
        final Voucher[] appliedVoucher = {null};
        final double[]  discountAmount = {0};
        final double[]  finalTotal     = {subtotal};

        // Xử lý áp dụng voucher
        btnApplyVoucher.addActionListener(e -> {
            String code = txtVoucherCode.getText().trim();
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập mã voucher!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Voucher v = voucherController.getByCode(code);
                if (v == null) {
                    lblVoucherInfo.setText("❌ Mã voucher không tồn tại!");
                    lblVoucherInfo.setForeground(new Color(192, 57, 43));
                    appliedVoucher[0] = null;
                    discountAmount[0] = 0;
                    finalTotal[0] = subtotalRef[0];
                } else if (!"ACTIVE".equals(v.getStatus())) {
                    lblVoucherInfo.setText("❌ Voucher đã hết hạn hoặc đã sử dụng!");
                    lblVoucherInfo.setForeground(new Color(192, 57, 43));
                    appliedVoucher[0] = null;
                    discountAmount[0] = 0;
                    finalTotal[0] = subtotalRef[0];
                } else if (v.getEndDate() != null && v.getEndDate().isBefore(LocalDate.now())) {
                    lblVoucherInfo.setText("❌ Voucher đã hết hạn ngày " + v.getEndDate() + "!");
                    lblVoucherInfo.setForeground(new Color(192, 57, 43));
                    appliedVoucher[0] = null;
                    discountAmount[0] = 0;
                    finalTotal[0] = subtotalRef[0];
                } else if (v.getUsageLimit() != null && v.getUsedCount() != null
                        && v.getUsedCount() >= v.getUsageLimit()) {
                    lblVoucherInfo.setText("❌ Voucher đã đạt giới hạn sử dụng!");
                    lblVoucherInfo.setForeground(new Color(192, 57, 43));
                    appliedVoucher[0] = null;
                    discountAmount[0] = 0;
                    finalTotal[0] = subtotalRef[0];
                } else {
                    // Tính giảm giá
                    if ("PERCENT".equals(v.getDiscountType())) {
                        discountAmount[0] = subtotalRef[0] * v.getDiscountValue() / 100.0;
                        lblVoucherInfo.setText(String.format("✅ Giảm %.0f%% → -  %,.0f đ", v.getDiscountValue(), discountAmount[0]));
                    } else {
                        discountAmount[0] = Math.min(v.getDiscountValue(), subtotalRef[0]);
                        lblVoucherInfo.setText(String.format("✅ Giảm trực tiếp: - %,.0f đ", discountAmount[0]));
                    }
                    finalTotal[0] = Math.max(0, subtotalRef[0] - discountAmount[0]);
                    appliedVoucher[0] = v;
                    lblVoucherInfo.setForeground(new Color(39, 174, 96));
                }

                // Cập nhật label tổng
                lblDiscount.setText(String.format("  Giảm giá:       - %,.0f đ", discountAmount[0]));
                lblFinalTotal.setText(String.format("  Tổng thanh toán: %,.0f đ", finalTotal[0]));

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ===== NÚT =====
        JButton btnConfirm = createButton("✅ Xác nhận thanh toán", new Color(39, 174, 96));
        JButton btnCancel  = createButton("Hủy", new Color(192, 57, 43));
        btnConfirm.setPreferredSize(new Dimension(200, 38));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        btnPanel.setOpaque(false);
        btnPanel.add(btnConfirm); btnPanel.add(btnCancel);

        // ===== LAYOUT DIALOG =====
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(10, 14, 4, 14));
        centerPanel.add(summaryScroll);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(orderInfoPanel);
        centerPanel.add(Box.createVerticalStrut(6));
        centerPanel.add(bankPanel);
        centerPanel.add(Box.createVerticalStrut(6));
        centerPanel.add(voucherPanel);   // <-- VOUCHER
        centerPanel.add(Box.createVerticalStrut(6));
        centerPanel.add(totalPanel);     // <-- TỔNG TIỀN CHI TIẾT

        JScrollPane dialogScroll = new JScrollPane(centerPanel);
        dialogScroll.setBorder(null);
        dialogScroll.getVerticalScrollBar().setUnitIncrement(12);

        dialog.add(dialogScroll, BorderLayout.CENTER);
        dialog.add(btnPanel,     BorderLayout.SOUTH);

        // ===== XÁC NHẬN THANH TOÁN =====
        btnConfirm.addActionListener(e -> {
            try {
                if ("Chuyển khoản".equals(cbPayment.getSelectedItem())) {
                    if (txtAccountNumber.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Vui lòng nhập số tài khoản!", "Lỗi", JOptionPane.ERROR_MESSAGE); return;
                    }
                    if (txtAccountName.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên tài khoản!", "Lỗi", JOptionPane.ERROR_MESSAGE); return;
                    }
                }

                int tableIndex       = cbTable.getSelectedIndex();
                String paymentMethod = (String) cbPayment.getSelectedItem();
                String selectedTable = tableIndex >= 0 ? tables.get(tableIndex).getName() : "Không xác định";

                StringBuilder fullNote = new StringBuilder();
                fullNote.append("Bàn: ").append(selectedTable);
                fullNote.append(" | TT: ").append(paymentMethod);
                if ("Chuyển khoản".equals(paymentMethod)) {
                    fullNote.append(" | NH: ").append(cbBank.getSelectedItem());
                    fullNote.append(" | STK: ").append(txtAccountNumber.getText().trim());
                    fullNote.append(" | TK: ").append(txtAccountName.getText().trim());
                }
                if (appliedVoucher[0] != null) {
                    fullNote.append(" | Voucher: ").append(appliedVoucher[0].getCode());
                    fullNote.append(" (-").append(String.format("%,.0f", discountAmount[0])).append(" đ)");
                }
                String note = txtNote.getText().trim();
                if (!note.isEmpty()) fullNote.append(" | ").append(note);

                // Tạo OrderDetail
                List<OrderDetail> details = new ArrayList<>();
                List<Product> products = productController.getAllProduct();
                for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                    String productName = (String) cartTableModel.getValueAt(i, 0);
                    double unitPrice   = (double) cartTableModel.getValueAt(i, 1);
                    int    quantity    = (int)    cartTableModel.getValueAt(i, 2);
                    int productId = products.stream()
                            .filter(p -> p.getName().equals(productName))
                            .mapToInt(Product::getId).findFirst().orElse(0);
                    details.add(new OrderDetail(productId, productName, unitPrice, quantity));
                }

                Order order = orderController.createOrder(details, fullNote.toString());
                orderController.completeOrder(order.getId());

                // Thông báo
                StringBuilder msg = new StringBuilder();
                msg.append("✅ Thanh toán thành công!\n");
                msg.append("Mã đơn: ").append(order.getOrderCode()).append("\n");
                msg.append("Bàn: ").append(selectedTable).append("\n");
                msg.append(String.format("Tạm tính: %,.0f đ\n", subtotalRef[0]));
                if (appliedVoucher[0] != null) {
                    msg.append(String.format("Voucher [%s]: - %,.0f đ\n", appliedVoucher[0].getCode(), discountAmount[0]));
                }
                msg.append(String.format("Tổng thanh toán: %,.0f đ\n", finalTotal[0]));
                msg.append("Phương thức: ").append(paymentMethod);
                if ("Chuyển khoản".equals(paymentMethod)) {
                    msg.append("\nNgân hàng: ").append(cbBank.getSelectedItem());
                    msg.append("\nSố TK: ").append(txtAccountNumber.getText().trim());
                    msg.append("\nTên TK: ").append(txtAccountName.getText().trim());
                }

                JOptionPane.showMessageDialog(dialog, msg.toString(), "Thanh toán thành công", JOptionPane.INFORMATION_MESSAGE);
                cartTableModel.setRowCount(0);
                updateTotal();
                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    // ===== LOAD DATA =====
    private void loadDataFromDB() {
        productTableModel.setRowCount(0);
        String keyword      = txtSearch.getText().trim().toLowerCase();
        String statusFilter = cbStatus.getSelectedItem().toString();
        for (Product p : productController.getAllProduct()) {
            if (!keyword.isEmpty() && !p.getName().toLowerCase().contains(keyword)) continue;
            String statusText = p.isActive() ? "Đang bán" : "Ngừng bán";
            if (!statusFilter.equals("Tất cả") && !statusFilter.equals(statusText)) continue;
            productTableModel.addRow(new Object[]{p.getId(), p.getName(), p.getPrice(), statusText, p.getCreatedTime(), p.getUpdatedTime()});
        }
    }

    // ===== DIALOG THÊM =====
    private void openAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm sản phẩm mới", true);
        dialog.setSize(400, 240); dialog.setLocationRelativeTo(this); dialog.setLayout(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 10, 20));
        JTextField txtName = new JTextField(), txtPrice = new JTextField();
        JCheckBox chkActive = new JCheckBox("Đang bán"); chkActive.setSelected(true);
        form.add(new JLabel("Tên sản phẩm:")); form.add(txtName);
        form.add(new JLabel("Giá bán:"));      form.add(txtPrice);
        form.add(new JLabel("Trạng thái:"));   form.add(chkActive);
        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton btnSave = createButton("Lưu", new Color(46, 204, 113));
        JButton btnCan  = createButton("Hủy", new Color(231, 76, 60));
        btnP.add(btnSave); btnP.add(btnCan);
        dialog.add(form, BorderLayout.CENTER); dialog.add(btnP, BorderLayout.SOUTH);
        btnSave.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().trim());
                if (name.isEmpty()) { JOptionPane.showMessageDialog(dialog, "Tên không được trống!", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
                if (price <= 0)     { JOptionPane.showMessageDialog(dialog, "Giá phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
                productController.createProduct(name, price, chkActive.isSelected());
                JOptionPane.showMessageDialog(dialog, "Thêm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose(); loadDataFromDB();
            } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(dialog, "Giá phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE); }
        });
        btnCan.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    // ===== DIALOG SỬA =====
    private void openEditDialog() {
        int row = productTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return; }
        int pid = (int) productTableModel.getValueAt(row, 0);
        String name = (String) productTableModel.getValueAt(row, 1);
        double price = (double) productTableModel.getValueAt(row, 2);
        boolean active = "Đang bán".equals(productTableModel.getValueAt(row, 3));
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa sản phẩm", true);
        dialog.setSize(400, 240); dialog.setLocationRelativeTo(this); dialog.setLayout(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 10, 20));
        JTextField txtName = new JTextField(name), txtPrice = new JTextField(String.valueOf(price));
        JCheckBox chkActive = new JCheckBox("Đang bán"); chkActive.setSelected(active);
        form.add(new JLabel("Tên sản phẩm:")); form.add(txtName);
        form.add(new JLabel("Giá bán:"));      form.add(txtPrice);
        form.add(new JLabel("Trạng thái:"));   form.add(chkActive);
        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton btnSave = createButton("Cập nhật", new Color(241, 196, 15));
        JButton btnCan  = createButton("Hủy",      new Color(231, 76, 60));
        btnP.add(btnSave); btnP.add(btnCan);
        dialog.add(form, BorderLayout.CENTER); dialog.add(btnP, BorderLayout.SOUTH);
        btnSave.addActionListener(e -> {
            try {
                String newName = txtName.getText().trim();
                double newPrice = Double.parseDouble(txtPrice.getText().trim());
                if (newName.isEmpty()) { JOptionPane.showMessageDialog(dialog, "Tên không được trống!", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
                if (newPrice <= 0)     { JOptionPane.showMessageDialog(dialog, "Giá phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
                Product p = new Product();
                p.setId(pid); p.setName(newName); p.setPrice(newPrice); p.setActive(chkActive.isSelected());
                productController.updateProduct(p);
                JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose(); loadDataFromDB();
            } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(dialog, "Giá phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE); }
        });
        btnCan.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    // ===== XÓA SẢN PHẨM =====
    private void deleteSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return; }
        int pid = (int) productTableModel.getValueAt(row, 0);
        String pName = (String) productTableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa sản phẩm:\n" + pName + "?", "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Product p = new Product(); p.setId(pid);
                productController.deleteProduct(p);
                JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDataFromDB();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); }
        }
    }

    // ===== NÚT FULL WIDTH =====
    private JButton createFullWidthButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    // ===== NÚT THƯỜNG =====
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
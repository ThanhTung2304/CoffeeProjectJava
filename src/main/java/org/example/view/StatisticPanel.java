package org.example.view;

import org.example.controller.StatisticController;
import org.example.event.DataChangeEventBus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatisticPanel extends JPanel
        implements DataChangeEventBus.DataChangeListener {

    private JLabel lblCustomer;
    private JLabel lblEmployee;
    private JLabel lblRevenue;
    private JLabel lblReservation;
    private JLabel lblStock;
    private JLabel lblExported;

    private final StatisticController reportController = new StatisticController();

    public StatisticPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 255));

        initUI();
        loadData(); // load lần đầu

        // ✅ ĐÚNG method
        DataChangeEventBus.onRegister(this);
    }

    /* ================= UI ================= */
    private void initUI() {

        JLabel title = new JLabel("THỐNG KÊ TỔNG QUAN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(60, 60, 90));
        add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setOpaque(false);

        lblCustomer = createValueLabel();
        lblEmployee = createValueLabel();
        lblRevenue = createValueLabel();
        lblReservation = createValueLabel();
        lblStock = createValueLabel();
        lblExported = createValueLabel();

        grid.add(createCard("Khách hàng", lblCustomer, new Color(52, 152, 219)));
        grid.add(createCard("Nhân viên", lblEmployee, new Color(46, 204, 113)));
        grid.add(createCard("Doanh thu tháng", lblRevenue, new Color(241, 196, 15)));
        grid.add(createCard("Đặt bàn tháng", lblReservation, new Color(155, 89, 182)));
        grid.add(createCard("Tồn kho", lblStock, new Color(230, 126, 34)));
        grid.add(createCard("Đã xuất kho", lblExported, new Color(231, 76, 60)));

        add(grid, BorderLayout.CENTER);
    }

    /* ================= LOAD DATA ================= */
    private void loadData() {
        try {
            lblCustomer.setText(String.valueOf(
                    reportController.getCustomerCount()
            ));

            lblEmployee.setText(String.valueOf(
                    reportController.getEmployeeCount()
            ));

            lblRevenue.setText(String.format("%,.0f VNĐ",
                    reportController.getMonthlyRevenue()
            ));

            lblReservation.setText(String.valueOf(
                    reportController.getReservationCount()
            ));


            lblStock.setText(String.valueOf(
                    reportController.getTotalInventory()
            ));

            lblExported.setText(String.valueOf(
                    reportController.getExportedTotal()
            ));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không thể tải dữ liệu thống kê",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /* ================= PUBLIC API ================= */
    // 👉 Cho MainFrame gọi khi switch màn hình
    public void reload() {
        SwingUtilities.invokeLater(this::loadData);
    }

    /* ================= EVENT ================= */
    @Override
    public void onDataChanged() {
        reload();
    }

    /* ================= UI HELPER ================= */
    private JPanel createCard(String title, JLabel value, Color color) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(color);
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
    }

    private JLabel createValueLabel() {
        JLabel lbl = new JLabel("0");
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        return lbl;
    }
}

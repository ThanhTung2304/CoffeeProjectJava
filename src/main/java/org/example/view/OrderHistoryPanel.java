package org.example.view;

import org.example.entity.Customer;
import org.example.entity.Order;
import org.example.service.CustomerService;
import org.example.service.OrderService;
import org.example.service.impl.CustomerServiceImpl;
import org.example.service.impl.OrderServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderHistoryPanel extends JPanel {

    private JComboBox<Customer> cbCustomer;
    private JTable table;
    private DefaultTableModel model;

    private CustomerService customerService = new CustomerServiceImpl();
    private OrderService orderService = new OrderServiceImpl();
    private JLabel lblCustomerName;


    public OrderHistoryPanel() {
        setLayout(new BorderLayout(10,10));

        lblCustomerName = new JLabel("LỊCH SỬ ĐƠN HÀNG");
        lblCustomerName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCustomerName.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        cbCustomer = new JComboBox<>();
        JButton btnView = new JButton("Xem lịch sử");

        JPanel top = new JPanel();
        top.add(new JLabel("Khách hàng:"));
        top.add(cbCustomer);
        top.add(btnView);

        model = new DefaultTableModel(
                new String[]{"Tổng tiền", "Ngày tạo"}, 0
        );
        table = new JTable(model);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadCustomers();
        btnView.addActionListener(e -> loadOrders());
    }
    public void showHistoryByCustomer(Customer customer) {
        // ✅ HIỂN THỊ TÊN KHÁCH
        lblCustomerName.setText(
                "LỊCH SỬ ĐƠN HÀNG – " + customer.getName()
        );
        model.setRowCount(0);
        List<Order> list = orderService.getHistoryByCustomer(customer.getId());

        for (Order o : list) {
            model.addRow(new Object[]{
                    o.getTotalAmount(),
                    o.getCreatedTime()
            });
        }
    }

    private void loadCustomers() {
        for (Customer c : customerService.findAll()) {
            cbCustomer.addItem(c);
        }
    }

    private void loadOrders() {
        model.setRowCount(0);
        Customer c = (Customer) cbCustomer.getSelectedItem();
        List<Order> list = orderService.getHistoryByCustomer(c.getId());

        for (Order o : list) {
            model.addRow(new Object[]{
                    o.getTotalAmount(),
                    o.getCreatedTime()
            });
        }
    }
}

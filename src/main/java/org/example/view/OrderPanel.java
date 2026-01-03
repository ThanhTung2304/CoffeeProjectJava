package org.example.view;

import org.example.entity.Customer;
import org.example.service.CustomerService;
import org.example.service.OrderService;
import org.example.service.impl.CustomerServiceImpl;
import org.example.service.impl.OrderServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OrderPanel extends JPanel {

    private JComboBox<Customer> cbCustomer;
    private JTextField txtTotal;
    private JButton btnPay;

    private OrderService orderService = new OrderServiceImpl();
    private CustomerService customerService = new CustomerServiceImpl();

    public OrderPanel() {
        setLayout(new GridLayout(4, 2, 10, 10));

        cbCustomer = new JComboBox<>();
        txtTotal = new JTextField();
        btnPay = new JButton("Thanh toán");

        loadCustomers();

        add(new JLabel("Khách hàng:"));
        add(cbCustomer);

        add(new JLabel("Tổng tiền:"));
        add(txtTotal);

        add(new JLabel(""));
        add(btnPay);

        btnPay.addActionListener(e -> pay());
    }

    private void loadCustomers() {
        List<Customer> list = customerService.findAll();
        for (Customer c : list) {
            cbCustomer.addItem(c);
        }
    }

    private void pay() {
        Customer c = (Customer) cbCustomer.getSelectedItem();
        double total = Double.parseDouble(txtTotal.getText());

        orderService.checkout(c.getId(), total);

        JOptionPane.showMessageDialog(this,
                "Thanh toán thành công!\n+" + (int) (total / 10000) + " điểm");
    }
}

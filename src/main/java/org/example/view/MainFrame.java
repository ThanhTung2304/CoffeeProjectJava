package org.example.view;

import javax.swing.*;

public class MainFrame {
    public static void main(String[] args){
        //Set look and feel
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){
            e.printStackTrace();
        }

        //chạy Loginform
    }
}

package com.sqkds.trbrgen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.*;

public class Generator {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.print("How many people will take part in this tournament? ");
        int participants = Integer.parseInt(s.next()); // Get an int from stdin.
        int r1 = 1; // This implies one person in the first full round.
        while (r1 * 2 <= participants)
            r1 *= 2; // Multiply r1 by 2 indefinitely until it gets to the power of two which is less than or equal to the number of participants.
        int byes = 2 * r1 - participants; // The quantity of people who will not participate in the first round.
        if (byes == participants) byes = 0;
        // We are recycling r1. Now, it will represent the number of participants in the first round,
        // considering byes as matches against no-shows, or "".
        if (r1 != participants) r1 *= 2;
        int[] players = new int[r1];
        for (int i = 0, j = 0, b = byes; i < r1; i++) {
            if (i % 2 == 0) { // If we're on the top of a match, add the next person.
                players[i] = j;
                j++;
            } else
                if (b != 0) { // If there are still byes to cover, cover one more.
                    players[i] = -1;
                    b--;
                } else { // If there are no more byes, add the next person.
                    players[i] = j;
                    j++;
                }
        }
        JFrame f = new JFrame("Generated Bracket");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(null);
        int rounds = Integer.numberOfTrailingZeros(players.length);
        BufferedImage bimg = new BufferedImage(250 * rounds - 50, players.length * 30, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = bimg.createGraphics();
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2.0f));
        JTextField[] fields = new JTextField[(players.length - byes) * 2 - 2];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = new JTextField();
            int roundNo = rounds - 32 + Integer.numberOfLeadingZeros(i / 2 + 1);
            fields[i].setSize(200, 25);
            fields[i].setLocation(250 * roundNo,
                    ((1 << (32 - Integer.numberOfLeadingZeros(i + 2))) - 2 - i) * 30 + (30 << roundNo)
                            + (roundNo != 0 ? ((30 << roundNo + 1) - 60)
                                    * (((1 << (32 - Integer.numberOfLeadingZeros(i + 2))) - 3 - i) / 2) : 0) - 60);
            panel.add(fields[i]);
            if (fields.length - i > participants) fields[i].setEnabled(false);
            g.drawLine(fields[i].getX() + 200, fields[i].getY() + 13, fields[i].getX() + 210,
                    ((i % 2 == 1) ? (fields[i].getY() + 27) : (fields[i].getY() - 3)));
            g.drawLine(fields[i].getX() + 210, ((i % 2 == 1) ? (fields[i].getY() + 27) : (fields[i].getY() - 3)),
                    250 * (roundNo + 1), ((1 << (32 - Integer.numberOfLeadingZeros((i / 2 - 1) + 2))) - 2 - (i / 2 - 1))
                            * 30 + (30 << (roundNo + 1)) + ((roundNo + 1) != 0 ? ((30 << (roundNo + 1) + 1) - 60)
                                    * (((1 << (32 - Integer.numberOfLeadingZeros((i / 2 - 1) + 2))) - 3 - (i / 2 - 1)) / 2) : 0)
                                            - 47);
        }
        Arrays.stream(fields).forEach((JTextField field) -> {
            field.addActionListener((ActionEvent e) -> {
                if (!field.getText().isBlank()) field.setEnabled(false);
                int i = 0;
                while (fields[i] != field) i++;
                if (fields[i % 2 == 0 ? i + 1 : i - 1].getText().isBlank())
                    field.setBackground(Color.ORANGE);
                else {
                    fields[i % 2 == 0 ? i + 1 : i - 1].setBackground(Color.BLUE);
                    field.setBackground(Color.BLUE);
                }
            });
        });
        Arrays.stream(fields).forEach((JTextField field) -> {
            field.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!field.getText().isBlank() & field.getBackground() != Color.ORANGE) {
                        int i = 0;
                        while (fields[i] != field) i++;
                        if (i <= 1 || fields[i / 2 - 1].getText().isBlank()) {
                            fields[i % 2 == 0 ? i + 1 : i - 1].setBackground(Color.RED);
                            field.setBackground(Color.GREEN);
                            if (i > 1) {
                                fields[i / 2 - 1].setText(field.getText());
                                if (fields[(i / 2 - 1) % 2 == 0 ? i / 2 : i / 2 - 2].getText().isBlank())
                                    fields[i / 2 - 1].setBackground(Color.ORANGE);
                                else {
                                    fields[(i / 2 - 1) % 2 == 0 ? i / 2 : i / 2 - 2].setBackground(Color.BLUE);
                                    fields[i / 2 - 1].setBackground(Color.BLUE);
                                }
                            }
                        }
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {}

                @Override
                public void mouseEntered(MouseEvent e) {}

                @Override
                public void mouseReleased(MouseEvent e) {}

                @Override
                public void mousePressed(MouseEvent e) {}
            });
        });
        JLabel img = new JLabel(new ImageIcon(bimg));
        img.setLocation(0, 0);
        img.setSize(bimg.getWidth(), bimg.getHeight());
        img.setOpaque(false);
        panel.add(img);
        JScrollPane sp = new JScrollPane();
        f.setContentPane(sp);
        sp.setSize(f.getSize());
        sp.setViewportView(panel);
        panel.setPreferredSize(new Dimension(250 * rounds - 50, players.length * 30 - 5));
        sp.revalidate();
        f.pack();
        f.setVisible(true);
    }
}
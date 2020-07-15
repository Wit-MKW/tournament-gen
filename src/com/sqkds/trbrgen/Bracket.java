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
import javax.swing.*;

public class Bracket {

    public static void main(String[] args) {
        int participants = Integer.parseInt(args[0]);
        int r1 = 1; // This implies one person in the first full round.
        while (r1 * 2 <= participants) {
            r1 *= 2; // Multiply r1 by 2 indefinitely until it gets to the power of two which is less than or equal to the number of participants.
        }
        int byes = 2 * r1 - participants; // The quantity of people who will not participate in the first round.
        if (byes == participants) {
            byes = 0;
        }
        // The code has been modified since the following comment was written:
        // We are recycling r1. Now, it will represent the number of participants in the first round,
        // considering byes as matches against nobody.
        int r0 = r1 != participants ? r1 * 2 : r1;
        int[] players = new int[r0];
        switch (args[1]) {
            case "1":
                for (int i = 0, j = 0, b = byes; i < r0; i++) {
                    if (i % 2 == 0) { // If we're on the top of a match, add the next person.
                        players[i] = j;
                        j++;
                    } else if (b != 0) { // If there are still byes to cover, cover one more.
                        players[i] = -1;
                        b--;
                    } else { // If there are no more byes, add the next person.
                        players[i] = j;
                        j++;
                    }
                }   break;
            case "0":
                players[0] = 0;
                for (int i = 1; i < r0; i++) {
                    int j = r0;
                    while (players[i - (r0 / j)] >= j / 2) {
                        j /= 2;
                    }
                    players[i] = j - players[i - (r0 / j)] - 1;
                }
                for (int i = 1; i <= byes; i++) {
                    for (int j = 0; j < players.length; j++) {
                        if (players[j] == r0 - i) {
                            players[j] = -1;
                            break;
                        }
                    }
                }   break;
            default:
                for (int i = 0, j = 0, b = byes; i < r0; i++) {
                    if (i % 4 != 1 && (i % 2 == 0 || 4 * b < r0 - i)) { // If we're on the top of a match, add the next person.
                        players[i] = j;
                        j++;
                    } else if (b != 0) { // If there are still byes to cover, cover one more.
                        players[i] = -1;
                        b--;
                    } else { // If there are no more byes, add the next person.
                        players[i] = j;
                        j++;
                    }
                }   break;
        }
        JFrame f = new JFrame("Generated Bracket");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(null);
        int rounds = Integer.numberOfTrailingZeros(players.length); // The players array's length will ALWAYS be a power of 2.
        BufferedImage bimg = new BufferedImage(250 * rounds - 50, players.length * 30, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = bimg.createGraphics();
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2.0f));
        JTextField[] fields = new JTextField[(players.length - byes) * 2 - 2];
        for (int i = 0, j = 0; j < fields.length;) {
            int roundNo = rounds - 32 + Integer.numberOfLeadingZeros(j / 2 + 1);
            if (roundNo != 0 || (i % 2 == 1 || players[i + 1] != -1)) {
                fields[j] = new JTextField();
                fields[j].setSize(200, 25);
                fields[j].setDisabledTextColor(Color.BLACK);
                if (roundNo != 0) {
                    fields[j].setLocation(250 * roundNo,
                            ((1 << (32 - Integer.numberOfLeadingZeros(j + 2))) - 2 - j) * 30 + (30 << roundNo)
                            + (roundNo != 0 ? ((30 << roundNo + 1) - 60)
                            * (((1 << (32 - Integer.numberOfLeadingZeros(j + 2))) - 3 - j) / 2) : 0) - 60);
                    panel.add(fields[j]);
                    // Draw a line from this competitor that meets the equivalent line from their opponent.
                    g.drawLine(fields[j].getX() + 200, fields[j].getY() + 13, fields[j].getX() + 210,
                            ((j % 2 == 1) ? (fields[j].getY() + 27) : (fields[j].getY() - 3)));
                    // Draw a line from the aforementioned meeting point to the slot which will be taken by either competitor.
                    g.drawLine(fields[j].getX() + 210, ((j % 2 == 1) ? (fields[j].getY() + 27) : (fields[j].getY() - 3)),
                            250 * (roundNo + 1), ((1 << (32 - Integer.numberOfLeadingZeros((j / 2 - 1) + 2))) - 2 - (j / 2 - 1))
                            * 30 + (30 << (roundNo + 1)) + ((roundNo + 1) != 0 ? ((30 << (roundNo + 1) + 1) - 60)
                            * (((1 << (32 - Integer.numberOfLeadingZeros((j / 2 - 1) + 2))) - 3 - (j / 2 - 1)) / 2) : 0)
                            - 47);
                    if (r0 != r1 && roundNo == 1)
                        if (players[(j - r1 + 2) * 2 + 1] != -1) fields[j].setEnabled(false);
                        else fields[j].setText("Seed #" + (players[(j - r1 + 2) * 2] + 1));
                    else fields[j].setEnabled(false);
                } else {
                    fields[j].setLocation(0, 30 * (players.length - i - 1));
                    panel.add(fields[j]);
                    // Draw a line from this competitor that meets the equivalent line from their opponent.
                    g.drawLine(200, fields[j].getY() + 13, 210,
                            ((i % 2 == 1) ? (fields[j].getY() + 27) : (fields[j].getY() - 3)));
                    // Draw a line from the aforementioned meeting point to the slot which will be taken by either competitor.
                    g.drawLine(fields[j].getX() + 210, ((i % 2 == 1) ? (fields[j].getY() + 27) : (fields[j].getY() - 3)),
                            250 * (roundNo + 1), ((1 << (32 - Integer.numberOfLeadingZeros(((2 * r0 - fields[j].getY() / 30 - 3)
                            / 2 - 1) + 2))) - 2 - ((2 * r0 - fields[j].getY() / 30 - 3) / 2 - 1)) * 30 + (30 << (roundNo + 1))
                            + ((roundNo + 1) != 0 ? ((30 << (roundNo + 1) + 1) - 60) * (((1 << (32
                            - Integer.numberOfLeadingZeros(((2 * r0 - fields[j].getY() / 30 - 3) / 2 - 1) + 2))) - 3 - ((2 * r0
                            - fields[j].getY() / 30 - 3) / 2 - 1)) / 2) : 0) - 47);
                    fields[j].setText("Seed #" + (players[i] + 1));
                    i++;
                }
                j++;
            } else i += 2;
        }
        Arrays.stream(fields).forEach((JTextField field) -> {
            field.addActionListener((ActionEvent e) -> {
                if (!field.getText().isEmpty())
                    field.setEnabled(false);
                int i = 0;
                while (fields[i] != field) i++;
                if (fields[i % 2 == 0 ? i + 1 : i - 1].getText().isEmpty() || fields[i % 2 == 0 ? i + 1 : i - 1].isEnabled())
                    field.setBackground(Color.ORANGE);
                else {
                    fields[i % 2 == 0 ? i + 1 : i - 1].setBackground(new Color(0.25f, 0.6f, 1.0f));
                    field.setBackground(new Color(0.25f, 0.6f, 1.0f));
                }
            });
        });
        Arrays.stream(fields).forEach((JTextField field) -> {
            field.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int i = 0;
                    while (fields[i] != field) i++;
                    if (field.getBackground() != Color.ORANGE
                            && fields[i % 2 == 0 ? i + 1 : i - 1].getBackground() != Color.ORANGE) {
                        if (i + 2 < r0) {
                            if (i <= 1 || fields[i / 2 - 1].getText().isEmpty()) {
                                fields[i % 2 == 0 ? i + 1 : i - 1].setBackground(new Color(1.0f, 0.25f, 0.25f));
                                field.setBackground(new Color(0.25f, 1.0f, 0.25f));
                                if (i > 1) {
                                    fields[i / 2 - 1].setText(field.getText());
                                    if (fields[(i / 2 - 1) % 2 == 0 ? i / 2 : i / 2 - 2].getText().isEmpty()) {
                                        fields[i / 2 - 1].setBackground(Color.ORANGE);
                                    } else {
                                        fields[(i / 2 - 1) % 2 == 0 ? i / 2 : i / 2 - 2].setBackground(new Color(0.25f, 0.6f, 1.0f));
                                        fields[i / 2 - 1].setBackground(new Color(0.25f, 0.6f, 1.0f));
                                    }
                                }
                            }
                        } else {
                            int j = (2 * r0) - 3 - (field.getY() / 30);
                            if (fields[j / 2 - 1].getText().isEmpty()) {
                                fields[i % 2 == 0 ? i + 1 : i - 1].setBackground(new Color(1.0f, 0.25f, 0.25f));
                                field.setBackground(new Color(0.25f, 1.0f, 0.25f));
                                fields[j / 2 - 1].setText(field.getText());
                                if (fields[(j / 2 - 1) % 2 == 0 ? j / 2 : j / 2 - 2].getText().isEmpty()) {
                                    fields[j / 2 - 1].setBackground(Color.ORANGE);
                                } else {
                                    fields[(j / 2 - 1) % 2 == 0 ? j / 2 : j / 2 - 2].setBackground(new Color(0.25f, 0.6f, 1.0f));
                                        fields[j / 2 - 1].setBackground(new Color(0.25f, 0.6f, 1.0f));
                                }
                            }
                        }
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }
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

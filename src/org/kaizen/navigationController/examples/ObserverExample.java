/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kaizen.navigationController.examples;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stackoverflow;

import org.kaizen.navigationController.NavigationControllerPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import org.kaizen.navigationController.NavigatableView;

public class ObserverExample {

    public static void main(String[] args) {
        UserService userService = new DefaultUserService();
        new ObserverExample(userService);
    }

    public ObserverExample(UserService userService) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.add(new RootNavigationPane(userService));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    public class RootNavigationPane extends NavigationControllerPane {

        enum View {
            WELCOME, LOGIN, MAIN;
        }
        private UserService userService;

        public RootNavigationPane(UserService userService) {
            setLayout(new BorderLayout());
            this.userService = userService;
            push(View.WELCOME);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, 400);
        }

        public UserService getUserService() {
            return userService;
        }

        protected void push(View view) {
            switch (view) {
                case WELCOME:
                    push(makeWelcomeView());
                    break;
                case LOGIN:
                    push(makeLoginView());
                    break;
            }
        }

        protected void popCurrentView() {
            pop();
        }

        // These factory methods could be handled by a factory
        // implementation, but I think I'm getting beyond the scope
        // of what I'm trying to do here
        protected WelcomePane makeWelcomeView() {
            WelcomePane welcomePane = new WelcomePane(new WelcomePane.Observer() {
                @Override
                public void login(WelcomePane source) {
                    push(View.LOGIN);
                }

                @Override
                public void register(WelcomePane source) {
                }
            });
            return welcomePane;
        }

        protected LoginPane makeLoginView() {
            LoginPane loginPane = new LoginPane(getUserService(), new LoginPane.Observer() {
                @Override
                public void didLogin(LoginPane source, User user) {
                    replaceWith(makeMainView(user));
                }

                @Override
                public void didCancel(LoginPane source) {
                    popCurrentView();
                }
            });
            return loginPane;
        }

        protected MainPane makeMainView(User user) {
            MainPane mainPane = new MainPane(user, new MainPane.Observer() {
                @Override
                public void logout(MainPane source) {
                    popToRoot();
                }
            });
            return mainPane;
        }
        
    }

    public abstract class NavigationPane extends JPanel implements NavigatableView {

        public NavigationPane() {
        }

        @Override
        public void willPresent() {
            System.out.println("willPresent " + getClass().getName());
        }

        @Override
        public void didPresent() {
            System.out.println("didPresent " + getClass().getName());
        }

        @Override
        public void willDismiss() {
            System.out.println("willDismiss " + getClass().getName());
        }

        @Override
        public void didDismiss() {
            System.out.println("didDismiss " + getClass().getName());
        }

    }

    public class WelcomePane extends NavigationPane {

        public interface Observer {

            public void login(WelcomePane source);

            public void register(WelcomePane source);
        }

        public WelcomePane(Observer observer) {
            setLayout(new GridBagLayout());
            JLabel label = new JLabel("<html><p style='text-align:center;'>Welcome to the wonderful mess</p></html>");
            label.setFont(label.getFont().deriveFont(Font.BOLD, 32));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = gbc.REMAINDER;
            gbc.gridx = 0;
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.weighty = 1;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.BOTH;

            add(label, gbc);

            JButton loginButton = new JButton("Login");
            JButton loginRegister = new JButton("Register");

            JPanel actionsPane = new JPanel();
            actionsPane.add(loginButton);
            actionsPane.add(loginRegister);

            gbc.weighty = 0;
            add(actionsPane, gbc);

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    observer.login(WelcomePane.this);
                }
            });
        }

    }

    public class LoginPane extends NavigationPane {

        public interface Observer {

            public void didLogin(LoginPane source, User user);

            public void didCancel(LoginPane source);
        }

        private Observer observer;

        private JTextField userNameField;
        private JPasswordField passwordField;

        public LoginPane(UserService userServicem, Observer observer) {
            this.observer = observer;
            setLayout(new GridBagLayout());
            userNameField = new JTextField(10);
            passwordField = new JPasswordField(10);

            JPanel credentialPanel = new JPanel(new GridBagLayout());
            JPanel actionsPane = new JPanel(new GridLayout(1, 2, 4, 4));

            JButton loginButton = new JButton("Login");
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Do some log
                    String userName = userNameField.getText();
                    char[] password = passwordField.getPassword();
                    userServicem.authenticateUser(userName, password, new UserService.Observer() {
                        @Override
                        public void authenticiationWasSuccessful(UserService source, User user) {
                            observer.didLogin(LoginPane.this, user);
                        }

                        @Override
                        public void authenticiationDidFail(UserService source) {
                            JOptionPane.showMessageDialog(LoginPane.this, "You are not worthy", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                }
            });

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    observer.didCancel(LoginPane.this);
                }
            });

            GridBagConstraints gbc = new GridBagConstraints();

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.LINE_END;
            gbc.insets = new Insets(4, 4, 4, 4);

            credentialPanel.add(new JLabel("User name"), gbc);
            gbc.gridy++;
            credentialPanel.add(new JLabel("Password"), gbc);

            gbc.gridx++;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            credentialPanel.add(userNameField, gbc);
            gbc.gridy++;
            credentialPanel.add(passwordField, gbc);

            actionsPane.add(cancelButton);
            actionsPane.add(loginButton);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = gbc.HORIZONTAL;

            JLabel titleLable = new JLabel("Prove your worthiness");
            titleLable.setFont(titleLable.getFont().deriveFont(Font.BOLD, 24));

            add(titleLable, gbc);
            add(new JSeparator(), gbc);
            add(credentialPanel, gbc);
            add(actionsPane, gbc);
        }
    }

    public class MainPane extends NavigationPane {

        public interface Observer {

            public void logout(MainPane source);
        }

        public MainPane(User user, Observer observer) {
            setLayout(new GridBagLayout());
            JLabel label = new JLabel("<html><p style='text-align:center;'>Welcome " + user.getName() + "</p></html>", JLabel.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD, 32));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = gbc.REMAINDER;
            gbc.gridx = 0;
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.weighty = 1;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.BOTH;

            add(label, gbc);

            JButton logoutButton = new JButton("Logout");

            gbc.weighty = 0;
            add(logoutButton, gbc);

            logoutButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    observer.logout(MainPane.this);
                }
            });
        }

    }

}

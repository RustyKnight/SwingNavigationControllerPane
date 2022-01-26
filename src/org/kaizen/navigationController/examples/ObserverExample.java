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

import org.kaizen.navigationController.ViewNavigationPane;
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
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import org.kaizen.navigationController.AbstractViewNavigationController;
import org.kaizen.navigationController.DefaultViewNavigationModel;
import org.kaizen.navigationController.NavigatableView;
import org.kaizen.navigationController.ViewNavigationController;
import org.kaizen.navigationController.ViewNavigationModel;
import org.kaizen.navigationController.examples.ObserverExample.ExampleNavigationController.View;
import static org.kaizen.navigationController.examples.ObserverExample.ExampleNavigationController.View.LOGIN;
import static org.kaizen.navigationController.examples.ObserverExample.ExampleNavigationController.View.REGISTER;
import static org.kaizen.navigationController.examples.ObserverExample.ExampleNavigationController.View.WELCOME;

public class ObserverExample {

    public static void main(String[] args) {
        UserService userService = new DefaultUserService();
        new ObserverExample(userService);
    }

    public ObserverExample(UserService userService) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ViewNavigationModel<ExampleNavigationController.View> model = new DefaultViewNavigationModel<>();
                ViewNavigationPane navigationPane = new ViewNavigationPane() {
                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(400, 400);
                    }
                };
                ViewNavigationController<ExampleNavigationController.View> controller = new ExampleNavigationController(model, navigationPane, userService);

                JFrame frame = new JFrame();
                frame.add(navigationPane);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class ExampleNavigationController extends AbstractViewNavigationController<ExampleNavigationController.View> {

        enum View {
            WELCOME, LOGIN, MAIN, REGISTER;
        }

        private UserService userService;
        private Map<Object, Object> context;

        public ExampleNavigationController(ViewNavigationModel<View> model, ViewNavigationPane navigationView, UserService userService) {
            super(model, navigationView);
            this.userService = userService;
            context = new HashMap<>();
            push(View.WELCOME);
        }

        protected UserService getUserService() {
            return userService;
        }

        @Override
        protected JComponent componentForView(View view) {
            switch (view) {
                case WELCOME:
                    return makeWelcomeView();
                case LOGIN:
                    return makeLoginView();
                case REGISTER:
                    return makeRegistrationView();
                case MAIN:
                    User user = (User) context.get("user");
                    if (user == null) {
                        throw new RuntimeException("User can not be null when presenting main view");
                    }
                    return makeMainView(user);
            }
            return null;
        }

        // These factory methods could be handled by a factory
        // implementation, but I think I'm getting beyond the scope
        // of what I'm trying to do here
        protected WelcomePane makeWelcomeView() {
            WelcomePane welcomePane = new WelcomePane(new WelcomePane.Observer() {
                @Override
                public void login(WelcomePane source) {
                    getModel().push(View.LOGIN);
                }

                @Override
                public void register(WelcomePane source) {
                    getModel().push(View.REGISTER);
                }
            });
            return welcomePane;
        }

        protected LoginPane makeLoginView() {
            LoginPane loginPane = new LoginPane(getUserService(), new LoginPane.Observer() {
                @Override
                public void didLogin(LoginPane source, User user) {
                    context.put("user", user);
                    getModel().replaceWith(View.MAIN);
                }

                @Override
                public void didCancel(LoginPane source) {
                    getModel().pop();
                }
            });
            return loginPane;
        }

        protected MainPane makeMainView(User user) {
            MainPane mainPane = new MainPane(user, new MainPane.Observer() {
                @Override
                public void logout(MainPane source) {
                    context.remove("user");
                    getModel().popToRoot();
                }
            });
            return mainPane;
        }

        protected RegisterPane makeRegistrationView() {
            RegisterPane registerPane = new RegisterPane(getUserService(), new RegisterPane.Observer() {
                @Override
                public void didRegsiterUser(RegisterPane source, User user) {
                    getModel().replaceWith(LOGIN);
                }

                @Override
                public void didCancelRegistration(RegisterPane source) {
                    getModel().pop();
                }
            });
            return registerPane;
        }
    }

    public class RootNavigationPane extends ViewNavigationPane {

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

        // How do we deal with "context"?
        protected JComponent makeViewComponentFor(View view) {
            switch (view) {
                case WELCOME:
                    return makeWelcomeView();
                case LOGIN:
                    return makeLoginView();
                case REGISTER:
                    return makeRegistrationView();
            }
            return null;
        }

        protected void push(View view) {
            push(makeViewComponentFor(view));
        }

        protected void popCurrentView() {
            pop();
        }

        protected void replaceWith(View view) {
            replaceWith(makeViewComponentFor(view));
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
                    push(View.REGISTER);
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

        protected RegisterPane makeRegistrationView() {
            RegisterPane registerPane = new RegisterPane(getUserService(), new RegisterPane.Observer() {
                @Override
                public void didRegsiterUser(RegisterPane source, User user) {
                    replaceWith(View.LOGIN);
                }

                @Override
                public void didCancelRegistration(RegisterPane source) {
                    pop();
                }
            });
            return registerPane;
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
            JButton registerButton = new JButton("Register");

            JPanel actionsPane = new JPanel();
            actionsPane.add(loginButton);
            actionsPane.add(registerButton);

            gbc.weighty = 0;
            add(actionsPane, gbc);

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    observer.login(WelcomePane.this);
                }
            });

            registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    observer.register(WelcomePane.this);
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
                    userServicem.authenticateUser(userName, password, new UserService.AuthenticationObserver() {
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

    public class RegisterPane extends NavigationPane {

        public interface Observer {

            public void didRegsiterUser(RegisterPane source, User user);

            public void didCancelRegistration(RegisterPane source);
        }

        public RegisterPane(UserService userService, Observer observer) {
            setLayout(new GridBagLayout());
            JTextField userNameField = new JTextField(10);
            JPasswordField passwordField = new JPasswordField(10);

            JPanel credentialPanel = new JPanel(new GridBagLayout());
            JPanel actionsPane = new JPanel(new GridLayout(1, 2, 4, 4));

            JButton loginButton = new JButton("Register");
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Do some log
                    String userName = userNameField.getText();
                    char[] password = passwordField.getPassword();

                    userService.registerUser(userName, password, new UserService.RegistrationObserver() {
                        @Override
                        public void didRegisterUser(UserService source, User user) {
                            observer.didRegsiterUser(RegisterPane.this, user);
                        }

                        @Override
                        public void reegistrationDidFail(UserService source) {
                            JOptionPane.showMessageDialog(RegisterPane.this, "You are not worthy", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                }
            });

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    observer.didCancelRegistration(RegisterPane.this);
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

            JLabel titleLable = new JLabel("Give user all your details");
            titleLable.setFont(titleLable.getFont().deriveFont(Font.BOLD, 24));

            add(titleLable, gbc);
            add(new JSeparator(), gbc);
            add(credentialPanel, gbc);
            add(actionsPane, gbc);
        }
    }

}

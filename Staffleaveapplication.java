import java.sql.*;
import java.util.Scanner;

class User {
    protected int id;
    protected String username;
    protected String password;

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public void viewStatus(Connection connection) {
        // Default implementation for users
    }
}

class Teacher extends User {
    private String substitutePhone;

    public Teacher(int id, String username, String password, String substitutePhone) {
        super(id, username, password);
        this.substitutePhone = substitutePhone;
    }

    @Override
    public void viewStatus(Connection connection) {
        try {
            String viewStatusQuery = "SELECT id, leave_dates, reason, status FROM leave_applications "
                    + "WHERE teacher_id = ?";
            PreparedStatement viewStatusStatement = connection.prepareStatement(viewStatusQuery);
            viewStatusStatement.setInt(1, id);
            ResultSet viewStatusResult = viewStatusStatement.executeQuery();

            System.out.println("\nYour Leave Applications:");
            while (viewStatusResult.next()) {
                int leaveId = viewStatusResult.getInt("id");
                String leaveDates = viewStatusResult.getString("leave_dates");
                String reason = viewStatusResult.getString("reason");
                String status = viewStatusResult.getString("status");

                System.out.println("Leave ID: " + leaveId);
                System.out.println("Leave Dates: " + leaveDates);
                System.out.println("Reason: " + reason);
                System.out.println("Status: " + status);
                System.out.println("-------------------------");
            }

            viewStatusResult.close();
            viewStatusStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class HOD extends User {
    public HOD(int id, String username, String password) {
        super(id, username, password);
    }

    @Override
    public void viewStatus(Connection connection) {
        try {
            String pendingLeaveQuery = "SELECT l.id, t.username, l.leave_dates, l.reason "
                    + "FROM leave_applications l "
                    + "JOIN teachers t ON l.teacher_id = t.id "
                    + "WHERE l.status = 'pending'";
            ResultSet pendingLeaveResult = connection.createStatement().executeQuery(pendingLeaveQuery);

            System.out.println("\nPending Leave Applications:");
            while (pendingLeaveResult.next()) {
                int leaveId = pendingLeaveResult.getInt("id");
                String teacher = pendingLeaveResult.getString("username");
                String leaveDates = pendingLeaveResult.getString("leave_dates");
                String reason = pendingLeaveResult.getString("reason");

                System.out.println("Leave ID: " + leaveId);
                System.out.println("Teacher: " + teacher);
                System.out.println("Leave Dates: " + leaveDates);
                System.out.println("Reason: " + reason);
                System.out.println("-------------------------");
            }

            pendingLeaveResult.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

public class StaffLeaveApplicationproject {
    // MySQL database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/staff_leave";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Aditi@123";

    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement statement = connection.createStatement();
            String leaveDates = " ";
            String reason = " ";

            String createTeachersTableQuery = "CREATE TABLE IF NOT EXISTS teachers ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(100) NOT NULL,"
                    + "password VARCHAR(100) NOT NULL,"
                    + "substitute_phone VARCHAR(20) NOT NULL"
                    + ")";
            statement.executeUpdate(createTeachersTableQuery);

            String createLeaveApplicationsTableQuery = "CREATE TABLE IF NOT EXISTS leave_applications ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "teacher_id INT NOT NULL,"
                    + "leave_dates VARCHAR(100) NOT NULL,"
                    + "reason VARCHAR(255) NOT NULL,"
                    + "status VARCHAR(20) NOT NULL,"
                    + "FOREIGN KEY (teacher_id) REFERENCES teachers(id)"
                    + ")";
            statement.executeUpdate(createLeaveApplicationsTableQuery);

            Scanner scanner = new Scanner(System.in);

            User currentUser = null; // To store the currently logged-in user

            // Teacher login
            System.out.print("Enter your username: ");
            String teacherUsername = scanner.nextLine();

            System.out.print("Enter your password: ");
            String teacherPassword = scanner.nextLine();

            String teacherLoginQuery = "SELECT id, substitute_phone FROM teachers WHERE username = ? AND password = ?";
            PreparedStatement teacherLoginStatement = connection.prepareStatement(teacherLoginQuery);
            teacherLoginStatement.setString(1, teacherUsername);
            teacherLoginStatement.setString(2, teacherPassword);
            ResultSet teacherLoginResult = teacherLoginStatement.executeQuery();

            if (teacherLoginResult.next()) {
                int teacherId = teacherLoginResult.getInt("id");
                String substitutePhone = teacherLoginResult.getString("substitute_phone");

                currentUser = new Teacher(teacherId, teacherUsername, teacherPassword, substitutePhone);

                while (true) {
                    // Display teacher menu
                    System.out.println("\nTeacher Menu:");
                    System.out.println("1. Apply for leave");
                    System.out.println("2. View status of leave applications");
                    System.out.println("3. Exit");
                    System.out.print("Enter your choice (1/2/3): ");
                    int choice = Integer.parseInt(scanner.nextLine());

                    switch (choice) {
                        case 1:
                            // Apply for leave
                            System.out.print("Enter leave dates (e.g., 2023-08-01 to 2023-08-03): ");
                            leaveDates = scanner.nextLine();

                            System.out.print("Enter reason for leave: ");
                            reason = scanner.nextLine();

                            String status = "pending";

                            String insertLeaveApplicationQuery = "INSERT INTO leave_applications (teacher_id, leave_dates, reason, status) "
                                    + "VALUES (?, ?, ?, ?)";
                            PreparedStatement insertLeaveStatement = connection.prepareStatement(insertLeaveApplicationQuery);
                            insertLeaveStatement.setInt(1, teacherId);
                            insertLeaveStatement.setString(2, leaveDates);
                            insertLeaveStatement.setString(3, reason);
                            insertLeaveStatement.setString(4, status);
                            insertLeaveStatement.executeUpdate();

                            System.out.println("Leave application submitted successfully.");
                            break;

                        case 2:
                            // View status of leave applications
                            currentUser.viewStatus(connection);
                            break;

                        case 3:
                            // Exit the teacher menu
                            System.out.println("Exiting the teacher menu.");
                            break;

                        default:
                            System.out.println("Invalid choice. Please try again.");
                            break;
                    }

                    if (choice == 3) {
                        break; // Exit the teacher menu
                    }
                }
            } else {
                System.out.println("Invalid login credentials for teacher.");
            }

            // Handle HOD login similarly as Teacher login
			// ... (Existing code up to where Teacher login ends)

            // Head of Department (HOD) login
            System.out.print("\nEnter HOD username: ");
            String hodUsername = scanner.nextLine();

            System.out.print("Enter HOD password: ");
            String hodPassword = scanner.nextLine();

            String hodLoginQuery = "SELECT id FROM hod WHERE username = ? AND password = ?";
            PreparedStatement hodLoginStatement = connection.prepareStatement(hodLoginQuery);
            hodLoginStatement.setString(1, hodUsername);
            hodLoginStatement.setString(2, hodPassword);
            ResultSet hodLoginResult = hodLoginStatement.executeQuery();

            if (hodLoginResult.next()) {
                int hodId = hodLoginResult.getInt("id");

                currentUser = new HOD(hodId, hodUsername, hodPassword);

                while (true) {
                    // Display HOD menu
                    System.out.println("\nHOD Menu:");
                    System.out.println("1. View pending leave applications");
                    System.out.println("2. Approve/Reject leave application");
                    System.out.println("3. Exit");
                    System.out.print("Enter your choice (1/2/3): ");
                    int choice = Integer.parseInt(scanner.nextLine());

                    switch (choice) {
                        case 1:
                            // View pending leave applications
                            currentUser.viewStatus(connection);
                            break;

                        case 2:
                            // Approve/Reject leave application
                            System.out.print("Enter the ID of the leave application to approve/reject (0 to exit): ");
                            int leaveIdToProcess = Integer.parseInt(scanner.nextLine());

                            if (leaveIdToProcess != 0) {
                                System.out.print("Enter 'approve' or 'reject': ");
                                String approvalStatus = scanner.nextLine().toLowerCase();

                                String updateLeaveStatusQuery = "UPDATE leave_applications SET status = ? WHERE id = ?";
                                PreparedStatement updateLeaveStatusStatement = connection.prepareStatement(updateLeaveStatusQuery);
                                updateLeaveStatusStatement.setString(1, approvalStatus);
                                updateLeaveStatusStatement.setInt(2, leaveIdToProcess);
                                updateLeaveStatusStatement.executeUpdate();

                                if (approvalStatus.equalsIgnoreCase("approve")) {
                                    // Step 12: Send SMS to the corresponding substitute teacher (simulate with output)
                                    String substitutePhoneNumber = "substitute_teacher_phone_number"; // Replace with the actual phone number
                                    String substituteTeacher = "substitute_teacher_name"; // Replace with the actual substitute teacher's name

                                    // Compose SMS message
                                    String smsMessage = "Leave approved. You are required to substitute for " + substituteTeacher
                                            + " on " + leaveDates + ". Reason: " + reason;

                                    // Simulate sending SMS (display "SMS sent" as output)
                                    System.out.println("SMS sent to substitute teacher.");
                                }

                                // Step 13: Send email to the teacher who applied for leave (simulate with output)
                                String teacherEmail = "teacher_email"; // Replace with the actual email address
                                String emailSubject = "Leave Application Status";
                                String emailBody = "Your leave application with ID " + leaveIdToProcess + " has been " + approvalStatus + ".";
                                // Simulate sending email (display "Email sent" as output)
                                System.out.println("Email sent to: " + teacherEmail);
                                System.out.println("Subject: " + emailSubject);
                                System.out.println("Body: " + emailBody);
                            } else {
                                System.out.println("No leave application processed.");
                            }
                            break;

                        case 3:
                            // Exit the HOD menu
                            System.out.println("Exiting the HOD menu.");
                            break;

                        default:
                            System.out.println("Invalid choice. Please try again.");
                            break;
                    }

                    if (choice == 3) {
                        break; // Exit the HOD menu
                    }
                }
            } else {
                System.out.println("Invalid login credentials for HOD.");
            }

// ... (Remaining code)


            scanner.close();
            teacherLoginResult.close();
            teacherLoginStatement.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

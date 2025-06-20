import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


// Student class (Serializable)
class Student implements Serializable {
    private String systemId;
    private String name;
    private int age;
    private String course = "";
    private int[] marks = new int[5];
    private double percentage;
    private String grade;

    public Student(String systemId, String name, int age) {
        this.systemId = systemId;
        this.name = name;
        this.age = age;
    }

    public String getSystemId() {
        return systemId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCourse() {
        return course;
    }

    public void setMarks(int[] marks) {
        this.marks = marks;
        int total = 0;
        for (int m : marks)
            total += m;
        this.percentage = total / 5.0;
        if (percentage >= 90)
            grade = "A";
        else if (percentage >= 75)
            grade = "B";
        else if (percentage >= 60)
            grade = "C";
        else if (percentage >= 40)
            grade = "D";
        else
            grade = "F";
    }

    public double getPercentage() {
        return percentage;
    }

    public String getGrade() {
        return grade;
    }

    public String toString() {
        return "System ID: " + systemId + "\nName: " + name + "\nAge: " + age +
                (course.isEmpty() ? "" : "\nCourse: " + course) +
                (grade == null ? "" : "\nPercentage: " + percentage + " %" + "\nGrade: " + grade);
    }
}

// Utility class for reading and writing students
class StudentData {
    private static final String FILE = "students.dat";

    public static List<Student> loadStudents() {
        List<Student> list = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            while (true) {
                list.add((Student) ois.readObject());
            }
        } catch (EOFException ignored) {
        } catch (Exception e) {
            System.out.println("Load error: " + e);
        }
        return list;
    }

    public static void saveStudents(List<Student> students) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            for (Student s : students)
                oos.writeObject(s);
        } catch (Exception e) {
            System.out.println("Save error: " + e);
        }
    }

    public static Student getStudentById(String id) {
        for (Student s : loadStudents()) {
            if (s.getSystemId().equals(id))
                return s;
        }
        return null;
    }

    public static void updateStudent(Student updated) {
        List<Student> list = loadStudents();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getSystemId().equals(updated.getSystemId())) {
                list.set(i, updated);
                break;
            }
        }
        saveStudents(list);
    }
}

// Main System
public class StudentManagementSystem extends JFrame {
    public StudentManagementSystem() {
        setTitle("Student Management System");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 10, 10));

        JButton btnRegister = new JButton("Registration");
        JButton btnCourse = new JButton("Courses");
        JButton btnMarks = new JButton("Examination Marks");
        JButton btnCert = new JButton("Certifications");

        add(btnRegister);
        add(btnCourse);
        add(btnMarks);
        add(btnCert);

        btnRegister.addActionListener(e -> {
            new RegistrationWindow(this);
            setVisible(false);
        });
        btnCourse.addActionListener(e -> {
            new CourseWindow(this);
            setVisible(false);
        });
        btnMarks.addActionListener(e -> {
            new MarksWindow(this);
            setVisible(false);
        });
        btnCert.addActionListener(e -> {
            new CertificationWindow(this);
            setVisible(false);
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new StudentManagementSystem();
    }
}

// Registration Window
class RegistrationWindow extends JFrame {
    public RegistrationWindow(JFrame mainWindow) {
        setTitle("Register Student");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2));

        JTextField txtId = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtAge = new JTextField();
        JButton btnRegister = new JButton("Register");

        add(new JLabel("System ID:"));
        add(txtId);
        add(new JLabel("Name:"));
        add(txtName);
        add(new JLabel("Age:"));
        add(txtAge);
        add(new JLabel());
        add(btnRegister);

        btnRegister.addActionListener(e -> {
            try {
                String id = txtId.getText();
                String name = txtName.getText();
                int age = Integer.parseInt(txtAge.getText());
                Student s = new Student(id, name, age);
                List<Student> list = StudentData.loadStudents();
                list.add(s);
                StudentData.saveStudents(list);
                JOptionPane.showMessageDialog(this, "Student Registered.");
                dispose();
                mainWindow.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        setVisible(true);
    }
}

// Course Window
class CourseWindow extends JFrame {
    public CourseWindow(JFrame mainWindow) {
        setTitle("Assign Course");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2));

        JTextField txtId = new JTextField();
        JTextField txtCourse = new JTextField();
        JButton btnAssign = new JButton("Save");

        add(new JLabel("System ID:"));
        add(txtId);
        add(new JLabel("Course:"));
        add(txtCourse);
        add(new JLabel());
        add(btnAssign);

        btnAssign.addActionListener(e -> {
            String id = txtId.getText();
            String course = txtCourse.getText();
            Student s = StudentData.getStudentById(id);
            if (s == null) {
                JOptionPane.showMessageDialog(this, "Student not found.");
            } else {
                s.setCourse(course);
                StudentData.updateStudent(s);
                JOptionPane.showMessageDialog(this, "Course assigned.");
                dispose();
                mainWindow.setVisible(true);
            }
        });

        setVisible(true);
    }
}

// Marks Window
class MarksWindow extends JFrame {
    public MarksWindow(JFrame mainWindow) {
        setTitle("Enter Marks");
        setSize(300, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 2));

        JTextField txtId = new JTextField();
        JTextField[] txtMarks = new JTextField[5];
        for (int i = 0; i < 5; i++)
            txtMarks[i] = new JTextField();
        JButton btnSubmit = new JButton("Submit");

        add(new JLabel("System ID:"));
        add(txtId);
        for (int i = 0; i < 5; i++) {
            add(new JLabel("Subject " + (i + 1) + ":"));
            add(txtMarks[i]);
        }
        add(new JLabel());
        add(btnSubmit);

        btnSubmit.addActionListener(e -> {
            Student s = StudentData.getStudentById(txtId.getText());
            if (s == null) {
                JOptionPane.showMessageDialog(this, "Student not found.");
            } else {
                try {
                    int[] marks = new int[5];
                    for (int i = 0; i < 5; i++)
                        marks[i] = Integer.parseInt(txtMarks[i].getText());
                    s.setMarks(marks);
                    StudentData.updateStudent(s);
                    JOptionPane.showMessageDialog(this, "Percentage: " + s.getPercentage() + " %" + ", Grade: " + s.getGrade());
                    dispose();
                    mainWindow.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input.");
                }
            }
        });

        setVisible(true);
    }
}

// Certification Window
class CertificationWindow extends JFrame {
    public CertificationWindow(JFrame mainWindow) {
        setTitle("Student Report");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField txtId = new JTextField(15);
        JButton btnFetch = new JButton("Fetch");
        JTextArea txtOutput = new JTextArea(8, 30);
        txtOutput.setEditable(false);

        inputPanel.add(new JLabel("System ID:"));
        inputPanel.add(txtId);
        inputPanel.add(btnFetch);
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(txtOutput), BorderLayout.CENTER);

        btnFetch.addActionListener(e -> {
            Student s = StudentData.getStudentById(txtId.getText());
            if (s == null)
                txtOutput.setText("Student not found.");
            else
                txtOutput.setText(s.toString());
        });

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            dispose();
            mainWindow.setVisible(true);
        });
        add(btnBack, BorderLayout.SOUTH);

        setVisible(true);
    }
}
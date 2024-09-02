CREATE DATABASE staff_leave;

USE staff_leave;
drop database staff_leave;
INSERT INTO teachers (id,username,password,substitute_phone )
VALUES
    ('101', 'Krishna','krishna123',9384948485),
    ('102', 'Rahul','rahul123',8638758798),
    ('103', 'Shalini','shalini123',7554398952),
    ('104', 'Bindhu','bindhu123',2679989644),
    ('105', 'Anu','anu123',9754335789);
CREATE TABLE IF NOT EXISTS leave_applications (
  id INT AUTO_INCREMENT PRIMARY KEY,
  teacher_id INT NOT NULL,
  leave_dates VARCHAR(100) NOT NULL,
  reason VARCHAR(255) NOT NULL,
  status VARCHAR(20) NOT NULL,
  FOREIGN KEY (teacher_id) REFERENCES teachers(id)
);

CREATE TABLE IF NOT EXISTS hod (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL
);
INSERT INTO hod (id,username,password )
VALUES
    ('100', 'hod','hod123');
    
    CREATE TABLE IF NOT EXISTS teachers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    substitute_phone VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS leave_applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id INT NOT NULL,
    leave_dates VARCHAR(100) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id)
);

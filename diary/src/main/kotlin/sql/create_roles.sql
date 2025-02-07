CREATE ROLE student WITH
    LOGIN
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    NOREPLICATION
    PASSWORD 'student'
    CONNECTION LIMIT -1;

GRANT SELECT ON Class,
    User,
    UserRole,
    Student,
    Subject,
    Schedule,
    Assessment,
    Library,
    StudentBook
    TO student;

GRANT INSERT, DELETE ON StudentBook
    TO student;

CREATE ROLE teacher WITH
    LOGIN
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    NOREPLICATION
    PASSWORD 'teacher'
    CONNECTION LIMIT -1;

GRANT SELECT ON Class,
    User,
    UserRole,
    Teacher,
    Student,
    Subject,
    Schedule,
    Assessment,
    TeacherSubject
    TO teacher;

GRANT INSERT, DELETE, UPDATE ON Assessment
    TO teacher;

create role administrator WITH
    LOGIN
    SUPERUSER
    CONNECTION LIMIT -1;


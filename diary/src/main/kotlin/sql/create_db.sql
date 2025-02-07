CREATE TABLE IF NOT EXISTS diary.Class(
                                          ClassId SERIAL,
                                          ClassLetter VARCHAR(1),
    ClassNumber ClassNumberType
    );

CREATE TABLE IF NOT EXISTS diary.User(
     UserId SERIAL,
     FirstName VARCHAR(100),
    LastName VARCHAR(100),
    Patronymic VARCHAR(100),
    Birthday Date,
    Password VARCHAR(100),
    Identifier VARCHAR(100),
    Phone VARCHAR(100),
    Gender GenderType
    );

CREATE TABLE IF NOT EXISTS diary.Role(
                                         RoleId SERIAL,
                                         NameRole NameRole
);

CREATE TABLE IF NOT EXISTS diary.UserRole(
                                             UserId Int,
                                             RoleId Int
);

CREATE TABLE IF NOT EXISTS diary.Student(
                                            StudentId Int UNIQUE,
                                            DateStartStuding Date,
                                            ClassId Int,
                                            GroupStudent GroupStudentType
);

CREATE TABLE IF NOT EXISTS diary.Teacher(
                                            TeacherId Int UNIQUE,
                                            DateStartTeaching Date,
                                            Cabinet Int
);

CREATE TABLE IF NOT EXISTS diary.Subject(
                                            SubjectId SERIAL,
                                            Name VARCHAR(100),
    CountTeachers CountGroupType
    );

CREATE TABLE IF NOT EXISTS diary.Schedule(
                                             DayOfWeek DayOfWeekType,
                                             LessonNumber Int,
                                             GroupStudent GroupScheduleType,
                                             ClassId Int,
                                             SubjectId Int,
                                             TeacherId Int
);

CREATE TABLE IF NOT EXISTS diary.Assessment(
                                               StudentId Int,
                                               SubjectId Int,
                                               TeacherId Int,
                                               Assessment StudentAssessmentType,
                                               Date Date
);

CREATE TABLE IF NOT EXISTS diary.Library(
                                            BookId SERIAL,
                                            Title VARCHAR(100),
    Author VARCHAR(100),
    DatePublication Date,
    Publisher VARCHAR(100),
    Count Int
    );

CREATE TABLE IF NOT EXISTS diary.StudentBook(
                                                BookId Int,
                                                StudentID Int
);

CREATE TABLE IF NOT EXISTS diary.TeacherSubject(
                                                   TeacherID Int,
                                                   SubjectId Int
);

ALTER table diary.Class
    ADD CONSTRAINT pk_class_id primary key(ClassId);

ALTER table diary.User
    ADD CONSTRAINT pk_user_id primary key(UserId);

ALTER table diary.Role
    ADD CONSTRAINT pk_role_id primary key(RoleId);

ALTER table diary.Subject
    ADD CONSTRAINT pk_subject_id primary key(SubjectId);

ALTER table diary.Library
    ADD CONSTRAINT pk_book_id primary key(BookId);

ALTER table diary.User
    ADD CONSTRAINT check_phone_number CHECK (Phone ~ '^\+\d{1,3}\d{10}$'),
    ADD CONSTRAINT check_birthday_before_current CHECK (Birthday <= CURRENT_DATE),
ALTER COLUMN FirstName SET NOT NULL,
    ALTER COLUMN Birthday SET NOT NULL,
    ALTER COLUMN Password SET NOT NULL,
    ALTER COLUMN Identifier SET NOT NULL,
    ALTER COLUMN Phone SET NOT NULL;

ALTER table diary.Role
    ALTER COLUMN NameRole SET NOT NULL;

ALTER table diary.Student
    ADD CONSTRAINT fk_classId foreign key(ClassId) references diary.Class(ClassId) ON DELETE CASCADE,
    ADD CONSTRAINT fk_studentId foreign key(StudentId) references diary.User(UserId) ON DELETE CASCADE,
    ADD CONSTRAINT check_date_before_current CHECK (DateStartStuding <= CURRENT_DATE),
    ALTER COLUMN DateStartStuding SET NOT NULL,
    ALTER COLUMN GroupStudent SET NOT NULL;

ALTER table diary.Teacher
    ADD CONSTRAINT fk_teacherId foreign key(TeacherId) references diary.User(UserId) ON DELETE CASCADE,
    ADD CONSTRAINT check_date_before_current CHECK (DateStartTeaching <= CURRENT_DATE),
    ADD CONSTRAINT unique_cabinet UNIQUE (Cabinet),
    ALTER COLUMN DateStartTeaching SET NOT NULL,
    ALTER COLUMN Cabinet SET NOT NULL;

ALTER table diary.Subject
    ALTER COLUMN Name SET NOT NULL,
ALTER COLUMN CountTeachers SET NOT NULL;

ALTER table diary.Class
    ALTER COLUMN ClassNumber SET NOT NULL,
ALTER COLUMN ClassLetter SET NOT NULL;

ALTER table diary.Schedule
    ADD CONSTRAINT fk_classId foreign key(ClassId) references diary.Class(ClassId) ON DELETE CASCADE,
    ADD CONSTRAINT fk_subjectId foreign key(SubjectId) references diary.Subject(SubjectId) ON DELETE CASCADE,
    ADD CONSTRAINT fk_teacherId foreign key(TeacherId) references diary.Teacher(TeacherId) ON DELETE CASCADE,
    ALTER COLUMN LessonNumber SET NOT NULL,
    ALTER COLUMN ClassId SET NOT NULL,
    ALTER COLUMN SubjectId SET NOT NULL,
    ALTER COLUMN TeacherId SET NOT NULL,
    ALTER COLUMN DayOfWeek SET NOT NULL;

ALTER table diary.Assessment
    ADD CONSTRAINT fk_studentId foreign key(StudentId) references diary.Student(StudentId) ON DELETE CASCADE,
    ADD CONSTRAINT fk_subjectId foreign key(SubjectId) references diary.Subject(SubjectId) ON DELETE CASCADE,
    ADD CONSTRAINT check_date_before_current CHECK (Date <= CURRENT_DATE),
    ALTER COLUMN StudentId SET NOT NULL,
    ALTER COLUMN SubjectId SET NOT NULL,
    ALTER COLUMN Assessment SET NOT NULL,
    ALTER COLUMN Date SET NOT NULL;

ALTER table diary.Library
    ADD CONSTRAINT check_date_before_current CHECK (DatePublication <= CURRENT_DATE),
    ADD CONSTRAINT check_positive_number CHECK (Count >= 0),
ALTER COLUMN BookId SET NOT NULL,
    ALTER COLUMN Title SET NOT NULL,
    ALTER COLUMN Author SET NOT NULL,
    ALTER COLUMN DatePublication SET NOT NULL,
    ALTER COLUMN Publisher SET NOT NULL,
    ALTER COLUMN Count SET NOT NULL;

ALTER table diary.StudentBook
    ADD CONSTRAINT fk_bookId foreign key(BookId) references diary.Library(BookId) ON DELETE CASCADE,
    ADD CONSTRAINT fk_studentId foreign key(StudentId) references diary.Student(StudentId) ON DELETE CASCADE,
    ALTER COLUMN StudentId SET NOT NULL,
    ALTER COLUMN BookId SET NOT NULL;

ALTER table diary.TeacherSubject
    ADD CONSTRAINT fk_TeacherId foreign key(TeacherId) references diary.Teacher(TeacherId) ON DELETE CASCADE,
    ADD CONSTRAINT fk_SubjectId foreign key(SubjectId) references diary.Subject(SubjectId) ON DELETE CASCADE,
    ALTER COLUMN TeacherId SET NOT NULL,
    ALTER COLUMN SubjectId SET NOT NULL;

ALTER table diary.UserRole
    ADD CONSTRAINT fk_userId foreign key(UserId) references diary.User(UserId) ON DELETE CASCADE,
    ADD CONSTRAINT fk_RoleId foreign key(RoleId) references diary.Role(RoleId) ON DELETE CASCADE,
    ALTER COLUMN UserId SET NOT NULL,
    ALTER COLUMN RoleId SET NOT NULL;

INSERT INTO diary.Role
VALUES (1, 'TEACHER')

INSERT INTO diary.Role
VALUES (2, 'STUDENT')

INSERT INTO diary.Role
VALUES (3, 'ADMIN')
DROP TABLE tblUsers;
DROP TABLE tblReports;

CREATE TABLE tblUsers (
  Username CHAR(20) UNIQUE PRIMARY KEY,
  DateJoined TIMESTAMP NOT NULL,
  Rating INTEGER,
  PasswordHash INTEGER NOT NULL
);

CREATE TABLE tblReports (
  Offender CHAR(20) REFERENCES tblUsers(Username),
  Reporter CHAR(20) REFERENCES tblUsers(Username),
  DateReported TIMESTAMP NOT NULL
);

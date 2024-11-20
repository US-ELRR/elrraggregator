-- Navigate to ELRR schema
SET search_path = elrr;



-- Truncate tables
TRUNCATE TABLE configuration cascade;
TRUNCATE TABLE competency cascade;
TRUNCATE TABLE course cascade;
TRUNCATE TABLE employment cascade;
TRUNCATE TABLE person cascade;
TRUNCATE TABLE contactinformation cascade;
TRUNCATE TABLE learnerprofile cascade;
TRUNCATE TABLE organization cascade;
TRUNCATE TABLE role cascade;
TRUNCATE TABLE rolerelations cascade;
TRUNCATE TABLE elrrauditlog cascade; 
COMMIT;



-- Insert data into tables  
INSERT INTO elrr.configuration
  (configurationname, configurationvalue, frequency, starttime, primarycontact, primaryemail, primaryorgname, primaryphone, secondarycontact, secondaryemail, secondaryorgname, secondaryphone, recordstatus, updatedby, inserteddate, lastmodified)
VALUES
  ('Deloitte LRS','https://deloitte-prototype-noisy.lrs.io/xapi','2 Weeks','0:00 Sunday EST', 
   'John Johnson','SysAdmin@USAF.mil','USAF','1-800-330-1212','David Lyod','david.lyod@gmail.com','USAF','1-800-212-3456','ACTIVE',NULL,'2021-06-29',NULL),
  ('ADL Authoritative LRS','https://yet-lrs-v3.usalearning.net/xapi','3 Weeks','2:00 Saturday EST',
   'Smith Smithson', 'SysSupport@USN.mil','Navy','1-800-321-0212',NULL,NULL,NULL,NULL,'ACTIVE',NULL,'2021-06-29',NULL),
  ('Rustici LRS','https://rustici-dev.lrs.io/xapi','2 Weeks','0:00 Sunday EST',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ACTIVE',NULL,'2021-06-29',NULL); 
COMMIT;



INSERT INTO elrr.competency
  (competencydefinitionidentifier, competencydefinitionidentifierurl, competencytaxonomyid, competencydefinitionvalidstartdate, competencydefinitionvalideenddate, competencydefinitionparentidentifier, competencydefinitionparenturl, competencydescriptionparentcode, 
   competencydefinitioncode, competencydefinitiontype, competencydefinitiontypeurl, competencydefinitionstatement, competencyframeworktitle, competencyframeworkversion, competencyframeworkidentifier, competencyframeworkdescription, competencyframeworksubject, 
   competencyframeworkvalidstartdate, competencyframeworkvalidenddate, recordstatus, updatedby, inserteddate, lastmodified)
VALUES
  (NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 
   'Skill and Roles: Business Skills and Acumen', NULL, NULL, NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
   'Contract Principles: General Contracting Concepts', NULL, NULL, NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'SERVICE3', NULL, NULL, NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL);
COMMIT;
 


INSERT INTO elrr.course
  (coursetitle, coursesubjectmatter, coursesubjectabbreviation, courseidentifier, courselevel, coursenumber, courseinstructionmethod, coursestartdate, courseenddate, courseenrollmentdate, courseacademicgrade, courseprovidername, departmentname, coursegradescalecode, coursemetadatarepository, courselrsendpoint, coursedescription, recordstatus, updatedby, inserteddate, lastmodified)
VALUES
  ('Fundamentals of Systems Acquisition Management', NULL, NULL, 'ACQ 101', NULL, '101', 'Web', '2020-12-21', '2021-01-15', '2020-12-01', NULL, 'DAU', 'Defense Acquisition University', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  ('Mentoring the Acquisition Workforce', NULL, NULL, 'CLC 067', NULL, '67', 'Web', '2021-01-15', '2021-01-25', '2021-01-03', NULL, 'DAU', 'Defense Acquisition University', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  ('Facilities Capital Cost of Money', NULL, NULL, 'CLC 103', NULL, '103', 'Web', '2021-01-15', '2021-01-25', '2021-01-03', NULL, 'DAU', 'Defense Acquisition University', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  ('Analyzing Profit or Fee', NULL, NULL, 'CLC 104', NULL, '104', 'Web', '2020-12-21', '2021-01-15', '2020-12-01', NULL, 'DAU', 'Defense Acquisition University', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  ('Shaping Smart Business Arrangements', NULL, NULL, 'CON 100', NULL, '100', 'Web', '2021-01-15', '2021-01-25', '2021-01-03', NULL, 'DAU', 'Defense Acquisition University', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  ('Contracting Fundamentals', NULL, NULL, 'CON 091', NULL, '091', 'Web', '2020-12-21', '2021-01-15', '2020-12-01', NULL, 'DAU', 'Defense Acquisition University', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  ('Core Concepts for Requirements Management', NULL, NULL, 'RQM 110', NULL, '110', 'Web', '2021-03-03', '2021-03-20', '2021-02-27', NULL, 'DAU', 'Defense Acquisition University', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  ('Advanced Concepts and Skills for Requirements Management', NULL, NULL, 'RQM 310', NULL, '310', 'Web', '2021-03-03', '2021-03-20', '2021-02-27', NULL, 'DAU', 'Defense Acquisition University', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  ('Requirements Executive Overview Workshop', NULL, NULL, 'RQM 403', NULL, '403', 'Web', '2021-03-03', '2021-03-20', '2021-02-27', NULL, 'DAU', 'Defense Acquisition University', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  ('Department of Defense (DoD) Cyber Awareness Challenge 2021 (1 hr) ', NULL, NULL, 'DOD-US1364-21', NULL, '21', 'Web', '2021-03-03', '2021-03-20', '2021-02-27', NULL, 'JKO', 'Joint Knowledge Online', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  ('HH-60 AIRCRAFT MAINTENANCE OFFICER/SUPERVISOR FAMILIARIZATION', NULL, NULL, 'J4OMP21A3  A30A', NULL, '30', 'Web', '2021-03-03', '2021-03-20', '2021-02-27', NULL, 'AETC', 'Air Education and Training Command', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  ('GIAC Security Essentials Certification', NULL, NULL, 'https://w3id.org/xapi/credential/GIAC Security Essentials Certification %28GSEC%29', NULL, '35', 'Web', '2021-03-03', '2021-03-20', '2021-02-27', NULL, 'AETC', 'Air Education and Training Command', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL);
COMMIT;



INSERT INTO elrr.employment
  (employername, employerdepartment, hiredate, employmentstartdate, employmentenddate, joblevel, occupation, employed, primarycareercategory, recordstatus, updatedby, inserteddate, lastmodified)
VALUES
  ('NAVY', '', '2012-06-30', '2012-07-07', NULL, 'Petty Officer Third Class', 'Mass Communications Specialist', 'Y', NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  ('NAVY', '', '2012-06-30', '2012-07-07', NULL, 'Petty Officer First Class', 'Navy Counselor', 'Y', NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  ('NAVY', '', '2012-06-30', '2012-07-07', NULL, 'Petty Officer Second Class', 'Legalmen', 'Y', NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  ('NAVY', '', '2018-07-07', '2018-07-27', NULL, 'Senior Chief Petty Officer', 'Personnel Specialist', 'Y', NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  ('USAF', 'DoD Air Force Acquisitions', '2012-06-30', '2012-07-05', NULL, '', 'Manager of Team Acquisitions', 'Y', NULL,'ACTIVE', NULL, '2021-06-29', NULL),
  ('USAF', 'DoD Air Force Acquisitions', '2012-06-30', '2010-07-07', NULL, '', '', 'Y', NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  ('USAF', 'DoD Air Force Acquisitions', '2019-05-07', '2019-05-25', NULL, '', '', 'Y', NULL, 'ACTIVE', NULL, '2021-06-29', NULL);
COMMIT;



INSERT INTO elrr.person
  (name, firstname, middlename, lastname, nameprefix, titleaffixcode, namesuffix, qualificationaffixcode, maidenname, preferredname, humanresourceidentifier, personnelidentificationsystem, birthdate, sex, primarylanguage, militaryveteranindicator, recordstatus, updatedby, inserteddate, lastmodified)
VALUES
  ('Alexandrina Annabelle Arredondo-Arteaga', 'Alexandrina', 'Annabelle', 'Arredondo-Arteaga', 'Ms.', NULL, NULL, NULL, 'Atkinson-Abbas', NULL, '3599900000', NULL, '2000-12-31', 'F', 'Spanish', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('Brinleigh Belarmino Blanchard', 'Brinleigh', 'Belarmino', 'Blanchard', 'Adm.', NULL, NULL, NULL, 'Bragg', NULL, '3599900010', NULL, '1999-10-03', 'M', 'English', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('Christopher Cooper Cunningham', 'Christopher', 'Cooper', 'Cunningham', 'Mr.', NULL, NULL, NULL, NULL, NULL, '3518193230', NULL, '1995-01-02', 'M', 'English', 'N', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('Dominick Delarosa', 'Dominick', '', 'Delarosa', 'Mr.', NULL, NULL, NULL, '', NULL, '3518193250', NULL, '1994-02-05', 'M', 'Spanish', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('Alice Beth Smith', 'Alice', 'Beth', 'Smith', 'Ms.', NULL, NULL, NULL, 'Smith', NULL, '0987654321', NULL, '1985-07-04', 'F', 'English', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('Bill Christian Phillips', 'Bill', 'Christian', 'Phillips', 'Mr.', NULL, NULL, NULL, 'Phillips', NULL, '0123456789', NULL, '1976-03-19', 'M', 'English', 'N', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('Liz May Glass', 'Liz', 'May', 'Glass', 'Mrs.', NULL, NULL, NULL, 'Richards', NULL, '0123456700', NULL, '1966-05-18', 'F', 'English', 'N', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('Michael Aaron Christopher', 'Michael', 'Aaron', 'Christopher', 'Mr.', NULL, NULL, NULL, NULL, NULL, '3599900000', NULL, '2000-12-31', 'M', 'Spanish', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('Jessica Bradley Matthew', 'Jessica', 'Bradley', 'Matthew', 'Ms.', NULL, NULL, NULL, 'Johnson', NULL, '3599900000', NULL, '2000-12-30', 'F', 'Spanish', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('Ashley Carolyn Jennifer', 'Ashley', 'Carolyn', 'Jennifer', 'Ms.', NULL, NULL, NULL, 'Morton', NULL, '3599900000', NULL, '2000-12-18', 'F', 'Spanish', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('Joshua David Armand', 'Joshua', 'David', 'Armand', 'Mr.', NULL, NULL, NULL, '', NULL, '3599900000', NULL, '2000-11-25', 'M', 'Spanish', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('Daniel Frank David', 'Daniel', 'Frank', 'David', 'Mr.', NULL, NULL, NULL, '', NULL, '3599900000', NULL, '2000-12-31', 'M', 'Spanish', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('James Gregory Robert', 'James', 'Gregory', 'Robert', 'Mr.', NULL, NULL, NULL, NULL, NULL, '3599900000', NULL, '2000-08-21', 'M', 'Spanish', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('John Henry Joseph', 'John', 'Henry', 'Joseph', 'Mr.', NULL, NULL, NULL, NULL, NULL, '3599900000', NULL, '2000-12-31', 'M', 'Spanish', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL);
COMMIT;



INSERT INTO elrr.contactinformation
  (personid, contactinformation, telephonenumber, isprimaryindicator, telephonetype, electronicmailaddress, electronicmailaddresstype, emergencycontact, recordstatus, updatedby, inserteddate, lastmodified)
VALUES
  (1, 'Email', '1-935-456-4578', 'Y', 'Private', 'alex91@gmail.com', 'Personal', 'Email', 'ACTIVE', NULL, '2021-06-29', NULL),
  (2, 'Email', '1-935-456-4578', 'Y', 'Private', 'Alexandrina@gmail.com', 'Personal', 'Email', 'ACTIVE', NULL, '2021-06-29', NULL),
  (5, 'Email', '1-935-456-4578', 'Y', 'Private', 'alice.smith@us.af.mil', 'Personal', 'Email', 'ACTIVE', NULL, '2021-06-29', NULL),
  (7, 'Email', '+1 403-443-5541', 'Y', 'Business', 'glassliz@gmail.com', 'Business', 'Email', 'ACTIVE', NULL, '2021-06-29', NULL);
COMMIT;



INSERT INTO elrr.learnerprofile
(personid, learneraddressid, contactinformationid, employmentid, positionid, citizenshipid, studentid, courseid, courseaccreditationid, competencyid, credentialid, organizationid, organizationaddressid, accreditationid, activitystatus, recordstatus, updatedby, inserteddate, lastmodified)
VALUES
  (1, NULL, NULL, 1, NULL, NULL, NULL, 1, NULL, 1, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-04-29', NULL),
  (1, NULL, NULL, 1, NULL, NULL, NULL, 2, NULL, 2, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-04-29', NULL),
  (1, NULL, NULL, 1, NULL, NULL, NULL, 3, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-04-29', NULL),
  (2, NULL, 101, 1, NULL, NULL, NULL, 3, NULL, 1, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-04-29', NULL),
  (7, NULL, NULL, 1, NULL, NULL, NULL, 1, NULL, 1, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-04-29', NULL),
  (7, NULL, NULL, 1, NULL, NULL, NULL, 2, NULL, 2, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-04-29', NULL),
  (7, NULL, NULL, 1, NULL, NULL, NULL, 3, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-04-29', NULL),
  (7, NULL, NULL, 1, NULL, NULL, NULL, 9, NULL, 1, NULL, NULL, NULL, NULL, 'Completed','ACTIVE', NULL, '2021-07-15', NULL),
  (6, NULL, NULL, 1, NULL, NULL, NULL, 1, NULL, 1, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (6, NULL, NULL, 1, NULL, NULL, NULL, 2, NULL, 2, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (5, NULL, 104, 1, NULL, NULL, NULL, 1, NULL, 1, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (5, NULL, 104, 1, NULL, NULL, NULL, 2, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (5, NULL, 104, 1, NULL, NULL, NULL, 5, NULL, 2, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (5, NULL, 104, 1, NULL, NULL, NULL, 10, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (7, NULL, NULL, 1, NULL, NULL, NULL, 10, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (6, NULL, NULL, 1, NULL, NULL, NULL, 10, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (7, NULL, NULL, 1, NULL, NULL, NULL, 11, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (8, NULL, NULL, 1, NULL, NULL, NULL, 11, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (9, NULL, NULL, 1, NULL, NULL, NULL, 11, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (10, NULL, NULL, 1, NULL, NULL, NULL, 11, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (11, NULL, NULL, 1, NULL, NULL, NULL, 11, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (12, NULL, NULL, 1, NULL, NULL, NULL, 11, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (13, NULL, NULL, 1, NULL, NULL, NULL, 11, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (14, NULL, NULL, 1, NULL, NULL, NULL, 11, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL);
COMMIT;


INSERT INTO elrr.organization
(organizationname, organizationidentifier, organizationidentificationcode, organizationidentificationsystem, industrytypeidentifier, organizationfein, organizationdescription, parentorganization, recordstatus, updatedby, inserteddate, lastmodified)
VALUES
  ('NAVY', 'D0DAF', 'G0V4', NULL, NULL, '1234573', 'NAVY', 'NAVY', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('AETC1', 'D0DAF', 'G0V4', NULL, NULL, '1234572', 'DoD AIR FORCE', 'AIR FORCE', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('AETC2', 'D0DAF', 'G0V4', NULL, NULL, '1234571', 'DoD AIR FORCE', 'AIR FORCE', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('AETC3', 'D0DAF', 'G0V4', NULL, NULL, '1234570', 'DoD AIR FORCE', 'AIR FORCE', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('AETC4', 'D0DAF', 'G0V4', NULL, NULL, '1234569', 'DoD AIR FORCE', 'AIR FORCE', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('AETC5', 'D0DAF', 'G0V4', NULL, NULL, '1234568', 'DoD AIR FORCE', 'AIR FORCE', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('AETC', 'D0DAF', 'G0V4', NULL, NULL, '1234567', 'DoD AIR FORCE', 'AIR FORCE', 'ACTIVE', NULL,  '2021-06-28', NULL),
  ('AETC', 'D0DAF', 'G0V4', NULL, NULL, '1234565', 'DoD AIR FORCE', 'AIR FORCE', 'ACTIVE', NULL,  '2021-06-28', NULL),
  ('AETC', 'D0DAF', 'G0V4', NULL, NULL, '1234563', 'DoD AIR FORCE', 'AIR FORCE', 'ACTIVE', NULL,  '2021-06-28', NULL);
COMMIT;



INSERT INTO elrr.role (rolename, recordstatus, updatedby, inserteddate, lastmodified)
VALUES
  ('TRAINING_MANAGER', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('CAREER_MANAGER', 'ACTIVE', NULL, '2021-06-28', NULL),
  ('LEARNER', 'ACTIVE', NULL, NULL, '2021-06-28');
COMMIT;



INSERT INTO elrr.rolerelations 
  (parentroleid, parentpersonid, childroleid, childpersonid, recordstatus, updatedby,    inserteddate, lastmodified)
VALUES
  (1, 7, 3, 2, 'ACTIVE', NULL, '2021-06-28', NULL),
  (1, 7, 3, 1, 'ACTIVE', NULL, '2021-06-28', NULL),
  (1, 7, 3, 3, 'ACTIVE', NULL, '2021-06-28', NULL),
  (1, 7, 3, 5, 'ACTIVE', NULL, '2021-06-28', NULL),
  (2, 6, 3, 5, 'ACTIVE', NULL, '2021-06-28', NULL),
  (2, 6, 3, 1, 'ACTIVE', NULL, '2021-06-28', NULL);
COMMIT;
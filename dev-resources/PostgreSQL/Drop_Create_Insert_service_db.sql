-- Create schema
CREATE schema IF NOT EXISTS elrr;

-- Navigate to elrr schema
SET search_path = elrr;

-- Drop elrr tables
DROP TABLE IF EXISTS elrr.accreditation cascade;
DROP TABLE IF EXISTS elrr.configuration cascade;
DROP TABLE IF EXISTS elrr.competency cascade;
DROP TABLE IF EXISTS elrr.course cascade; 
DROP TABLE IF EXISTS elrr.courseaccreditation cascade; 
DROP TABLE IF EXISTS elrr.rolerelations cascade;
DROP TABLE IF EXISTS elrr.role cascade;
DROP TABLE IF EXISTS elrr.organization cascade;
DROP TABLE IF EXISTS elrr.learnerprofile cascade;
DROP TABLE IF EXISTS elrr.contactinformation cascade;
DROP TABLE IF EXISTS elrr.person cascade;
DROP TABLE IF EXISTS elrr.employment cascade;  
DROP TABLE IF EXISTS elrr.elrrauditlog cascade; 



-- Create elrr tables 
CREATE TABLE IF NOT EXISTS elrr.accreditation (
    accreditationid int8 NOT NULL,
    inserteddate timestamp(6) NULL,
    lastmodified timestamp(6) NULL,
    updatedby varchar(255) NULL,
    organizationaccredits varchar(255) NULL,
    CONSTRAINT accreditation_pkey PRIMARY KEY (accreditationid)
);

CREATE SEQUENCE IF NOT EXISTS elrr.accreditation_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    NO CYCLE;
   
ALTER SEQUENCE elrr.accreditation_seq OWNED BY elrr.accreditation.accreditationid;
    

    
CREATE TABLE IF NOT EXISTS elrr.configuration (
    configurationid int8 NOT NULL,
    configurationname varchar(255) NOT NULL,
    configurationvalue varchar(255) NULL,
    frequency varchar(255) NULL,
    starttime varchar(255) NULL,
    primarycontact varchar(255) NULL,
    primaryemail varchar(254) NULL,
    primaryorgname varchar(100) NULL,
    primaryphone varchar(255) NULL,
    secondarycontact varchar(255) NULL,
    secondaryemail varchar(254) NULL,
    secondaryorgname varchar(100) NULL,
    secondaryphone varchar(255) NULL,
    recordstatus varchar(10) NULL,
    updatedby varchar(20) NULL,
    inserteddate timestamp NULL,
    lastmodified timestamp NULL,
    CONSTRAINT configuration_pk PRIMARY KEY (configurationid)
);

CREATE SEQUENCE IF NOT EXISTS elrr.configuration_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    NO CYCLE;

ALTER SEQUENCE elrr.configuration_seq OWNED BY elrr.configuration.configurationid;


CREATE TABLE IF NOT EXISTS elrr.competency (
    competencyid int8 NOT NULL,
    competencydefinitionidentifier varchar(100) NULL,
    competencydefinitionidentifierurl text NULL,
    competencytaxonomyid varchar(100) NULL,
    competencydefinitionvalidstartdate date NULL,
    competencydefinitionvalideenddate date NULL,
    competencydefinitionparentidentifier varchar(100) NULL,
    competencydefinitionparenturl text NULL,
    competencydescriptionparentcode varchar(100) NULL,
    competencydefinitioncode varchar(100) NULL,
    competencydefinitiontype varchar(100) NULL,
    competencydefinitiontypeurl text NULL,
    competencydefinitionstatement text NULL,
    competencyframeworktitle varchar(100) NOT NULL,
    competencyframeworkversion varchar(100) NULL,
    competencyframeworkidentifier varchar(100) NULL,
    competencyframeworkdescription text NULL,
    competencyframeworksubject varchar(100) NULL,
    competencyframeworkvalidstartdate date NULL,
    competencyframeworkvalidenddate date NULL,
    recordstatus varchar(10) NULL,
    updatedby varchar(20) NULL,
    inserteddate timestamp NULL,
    lastmodified timestamp NULL,
    CONSTRAINT competency_pk15 PRIMARY KEY (competencyid)
);

CREATE SEQUENCE IF NOT EXISTS elrr.competency_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    NO CYCLE;
    
ALTER SEQUENCE elrr.competency_seq OWNED BY elrr.competency.competencyid;
 


CREATE TABLE IF NOT EXISTS elrr.course (
    courseid int8 NOT NULL,
    coursetitle varchar(300) NOT NULL,
    coursesubjectmatter varchar(100) NULL,
    coursesubjectabbreviation varchar(20) NULL,
    courseidentifier varchar(150) NOT NULL,
    courselevel varchar(50) NULL,
    coursenumber varchar(50) NULL,
    courseinstructionmethod varchar(50) NULL,
    coursestartdate date NULL,
    courseenddate date NULL,
    courseenrollmentdate date NULL,
    courseacademicgrade varchar(50) NULL,
    courseprovidername varchar(100) NULL,
    departmentname varchar(100) NULL,
    coursegradescalecode varchar(50) NULL,
    coursemetadatarepository varchar(50) NULL,
    courselrsendpoint varchar(50) NULL,
    coursedescription text NULL,
    recordstatus varchar(10) NULL,
    updatedby varchar(20) NULL,
    inserteddate timestamp NULL,
    lastmodified timestamp NULL,
    CONSTRAINT course_pk10 PRIMARY KEY (courseid)
);


CREATE SEQUENCE IF NOT EXISTS elrr.course_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    NO CYCLE;
    
ALTER SEQUENCE elrr.course_seq OWNED BY elrr.course.courseid;



CREATE TABLE IF NOT EXISTS elrr.courseaccreditation (
    courseaccreditationid int8 NOT NULL,
    inserteddate timestamp(6) NULL,
    lastmodified timestamp(6) NULL,
    updatedby varchar(255) NULL,
    CONSTRAINT courseaccreditation_pkey PRIMARY KEY (courseaccreditationid)
);

CREATE SEQUENCE IF NOT EXISTS elrr.courseaccreditation_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    NO CYCLE;

ALTER SEQUENCE elrr.courseaccreditation_seq OWNED BY elrr.courseaccreditation.courseaccreditationid;

    
    
CREATE TABLE IF NOT EXISTS elrr.elrrauditlog (
    elrrauditlogid int8 NOT NULL,
    syncid int8 NOT NULL,
    recordstatus varchar(10) NULL, 
    updatedby varchar(20) NULL,
    inserteddate timestamp NULL,
    lastmodified timestamp NULL,
    CONSTRAINT elrrauditlog_pk PRIMARY KEY (elrrauditlogid)
);

CREATE SEQUENCE IF NOT EXISTS elrr.elrrauditlog_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    NO CYCLE;

ALTER SEQUENCE elrr.elrrauditlog_seq OWNED BY elrr.elrrauditlog.elrrauditlogid;



CREATE TABLE IF NOT EXISTS elrr.employment (
    employmentid int8 NOT NULL,
    employername varchar(100) NOT NULL,
    employerdepartment varchar(100) NULL,
    hiredate date NULL,
    employmentstartdate date NULL,
    employmentenddate date NULL,
    joblevel varchar(100) NULL,
    occupation varchar(100) NULL,
    employed bpchar(1) NULL,
    primarycareercategory varchar(50) NULL,
    recordstatus varchar(10) NULL,
    updatedby varchar(20) NULL,
    inserteddate timestamp NULL,
    lastmodified timestamp NULL,
    CONSTRAINT employment_pk8 PRIMARY KEY (employmentid)
);

CREATE SEQUENCE elrr.employment_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    NO CYCLE;

ALTER SEQUENCE elrr.employment_seq OWNED BY elrr.employment.employmentid;
    
    
    
CREATE TABLE IF NOT EXISTS elrr.person (
    personid int8 NOT NULL,
    name varchar(250) NOT NULL,
    firstname varchar(50) NOT NULL,
    middlename varchar(50) NULL,
    lastname varchar(50) NOT NULL,
    nameprefix varchar(50) NULL,
    titleaffixcode varchar(50) NULL,
    namesuffix varchar(50) NULL,
    qualificationaffixcode varchar(50) NULL,
    maidenname varchar(50) NULL,
    preferredname varchar(50) NULL,
    humanresourceidentifier text NULL,
    personnelidentificationsystem text NULL,
    birthdate date NULL,
    sex bpchar(1) NULL,
    primarylanguage varchar(50) NULL,
    militaryveteranindicator bpchar(1) NULL,
    recordstatus varchar(10) NULL,
    updatedby varchar(20) NULL,
    inserteddate timestamp NULL,
    lastmodified timestamp NULL,
    CONSTRAINT person_pk4 PRIMARY KEY (personid),
    CONSTRAINT ref_person FOREIGN KEY (personid) REFERENCES elrr.person(personid)
);

CREATE SEQUENCE IF NOT EXISTS elrr.person_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    NO CYCLE;

ALTER SEQUENCE elrr.person_seq OWNED BY elrr.person.personid;
        


CREATE TABLE IF NOT EXISTS elrr.contactinformation (
    contactinformationid int8 NOT NULL,
    personid int4 NOT NULL,
    contactinformation varchar(20) NOT NULL,
    telephonenumber varchar(20) NULL,
    isprimaryindicator bpchar(1) NULL,
    telephonetype varchar(20) NULL,
    electronicmailaddress varchar(254) NULL,
    electronicmailaddresstype varchar(20) NULL,
    emergencycontact varchar(250) NULL,
    recordstatus varchar(10) NULL,
    updatedby varchar(20) NULL,
    inserteddate timestamp NULL,
    lastmodified timestamp NULL,
    CONSTRAINT contactinformation_pk5 PRIMARY KEY (contactinformationid),
    CONSTRAINT refperson20 FOREIGN KEY (personid) REFERENCES elrr.person(personid)
);

CREATE SEQUENCE IF NOT EXISTS elrr.contactinformation_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    NO CYCLE;
    
ALTER SEQUENCE elrr.contactinformation_seq OWNED BY elrr.contactinformation.contactinformationid;



-- Re-visit relationships
CREATE TABLE IF NOT EXISTS elrr.learnerprofile (
    learnerprofileid int8 NOT NULL,
    personid int8 NOT NULL,
    learneraddressid int8 NULL,
    contactinformationid int8 NULL,
    employmentid int8 NOT NULL,
    positionid int8 NULL,
    citizenshipid int8 NULL,
    studentid int8 NULL,
    courseid int8 NULL,
    courseaccreditationid int8 NULL,
    competencyid int8 NULL,
    credentialid int8 NULL,
    organizationid int8 NULL,
    organizationaddressid int8 NULL,
    accreditationid int8 NULL,
    activitystatus varchar(10) NOT NULL,
    recordstatus varchar(10) NULL,
    updatedby varchar(20) NULL,
    inserteddate timestamp NULL,
    lastmodified timestamp NULL,
    CONSTRAINT learnerprofile_pk PRIMARY KEY (learnerprofileid),
    CONSTRAINT refemployment25 FOREIGN KEY (employmentid) REFERENCES elrr.employment(employmentid)
);

CREATE SEQUENCE IF NOT EXISTS elrr.learnerprofile_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    NO CYCLE;

ALTER SEQUENCE elrr.learnerprofile_seq OWNED BY elrr.learnerprofile.learnerprofileid;



CREATE TABLE IF NOT EXISTS elrr.organization (
    organizationid int8 NOT NULL,
    organizationname varchar(100) NOT NULL,
    organizationidentifier varchar(100) NULL,
    organizationidentificationcode varchar(100) NULL,
    organizationidentificationsystem varchar(100) NULL,
    industrytypeidentifier varchar(100) NULL,
    organizationfein varchar(100) NULL,
    organizationdescription text NULL,
    parentorganization varchar(100) NULL,
    recordstatus varchar(10) NULL,
    updatedby varchar(20) NULL,
    inserteddate timestamp NULL,
    lastmodified timestamp NULL,
    CONSTRAINT organization_pk9 PRIMARY KEY (organizationid)
);

CREATE SEQUENCE IF NOT EXISTS elrr.organization_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    NO CYCLE;
    
ALTER SEQUENCE elrr.organization_seq OWNED BY elrr.organization.organizationid;    


CREATE TABLE IF NOT EXISTS elrr.role (
    roleid int4 NOT NULL,
    rolename varchar(20) NULL,
    recordstatus varchar(10) NULL,
    updatedby varchar(20) NULL,
    inserteddate date NULL,
    lastmodified date NULL,
    CONSTRAINT role_pk16 PRIMARY KEY (roleid)
);

CREATE SEQUENCE IF NOT EXISTS elrr.role_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    NO CYCLE;
    
ALTER SEQUENCE elrr.role_seq OWNED BY elrr.role.roleid;    



--Re-visit role/person relationships => view
CREATE TABLE IF NOT EXISTS elrr.ROLERELATIONS (
    rolerelationsid int4 NOT NULL,
    parentroleid int4 NOT NULL,
    parentpersonid int4 NOT NULL,
    childroleid int4 NOT NULL,
    childpersonid int4 NOT NULL,
    recordstatus varchar(10) NULL,
    updatedby varchar(20) NULL,
    inserteddate date NULL,
    lastmodified date NULL,
    CONSTRAINT rolerelations_pk PRIMARY KEY (rolerelationsid)
);

CREATE SEQUENCE IF NOT EXISTS elrr.rolerelations_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    NO CYCLE;
    
ALTER SEQUENCE elrr.rolerelations_seq OWNED BY elrr.rolerelations.rolerelationsid;


-- Truncate tables
TRUNCATE TABLE elrr.configuration cascade;
TRUNCATE TABLE elrr.competency cascade;
TRUNCATE TABLE elrr.course cascade;
TRUNCATE TABLE elrr.employment cascade;
TRUNCATE TABLE elrr.person cascade;
TRUNCATE TABLE elrr.contactinformation cascade;
TRUNCATE TABLE elrr.learnerprofile cascade;
TRUNCATE TABLE elrr.organization cascade;
TRUNCATE TABLE elrr.role cascade;
TRUNCATE TABLE elrr.rolerelations cascade;
TRUNCATE TABLE elrr.elrrauditlog cascade; 

-- Insert data into tables  
INSERT INTO elrr.configuration
  (configurationid, configurationname, configurationvalue, frequency, starttime, primarycontact, primaryemail, primaryorgname, primaryphone, secondarycontact, 
  secondaryemail, secondaryorgname, secondaryphone, recordstatus, updatedby, inserteddate, lastmodified)
VALUES
  (1,'Deloitte LRS','https://deloitte-prototype-noisy.lrs.io/xapi','2 Weeks','0:00 Sunday EST', 
   'John Johnson','SysAdmin@USAF.mil','USAF','1-800-330-1212','David Lyod','david.lyod@gmail.com','USAF','1-800-212-3456','ACTIVE',NULL,'2021-06-29',NULL),
  (2,'ADL Authoritative LRS','https://yet-lrs-v3.usalearning.net/xapi','3 Weeks','2:00 Saturday EST',
   'Smith Smithson', 'SysSupport@USN.mil','Navy','1-800-321-0212',NULL,NULL,NULL,NULL,'ACTIVE',NULL,'2021-06-29',NULL),
  (3,'Rustici LRS','https://rustici-dev.lrs.io/xapi','2 Weeks','0:00 Sunday EST',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ACTIVE',NULL,'2021-06-29',NULL); 

  SELECT setval('"elrr"."configuration_seq"'::regclass, (SELECT MAX("configurationid") FROM "elrr"."configuration"));



INSERT INTO elrr.competency
  (competencyid, competencydefinitionidentifier, competencydefinitionidentifierurl, competencytaxonomyid, competencydefinitionvalidstartdate, competencydefinitionvalideenddate, 
  competencydefinitionparentidentifier, competencydefinitionparenturl, competencydescriptionparentcode, 
   competencydefinitioncode, competencydefinitiontype, competencydefinitiontypeurl, competencydefinitionstatement, competencyframeworktitle, 
   competencyframeworkversion, competencyframeworkidentifier, competencyframeworkdescription, competencyframeworksubject, competencyframeworkvalidstartdate, 
   competencyframeworkvalidenddate, recordstatus, updatedby, inserteddate, lastmodified)
VALUES
  (100, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 
   'Skill and Roles: Business Skills and Acumen', NULL, NULL, NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (101, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
   'Contract Principles: General Contracting Concepts', NULL, NULL, NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (102, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'SERVICE3', NULL, NULL, NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL);
 
SELECT setval('"elrr"."competency_seq"'::regclass, (SELECT MAX("competencyid") FROM "elrr"."competency"));



INSERT INTO elrr.course
  (courseid, coursetitle, coursesubjectmatter, coursesubjectabbreviation, courseidentifier, courselevel, coursenumber, courseinstructionmethod, coursestartdate, courseenddate, courseenrollmentdate, courseacademicgrade, courseprovidername, departmentname, 
  coursegradescalecode, coursemetadatarepository, courselrsendpoint, coursedescription, recordstatus, updatedby, inserteddate, lastmodified)
VALUES
  (100, 'Fundamentals of Systems Acquisition Management', NULL, NULL, 'ACQ 101', NULL, '101', 'Web', '2020-12-21', '2021-01-15', '2020-12-01', NULL, 'DAU', 'Defense Acquisition University', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (101, 'Mentoring the Acquisition Workforce', NULL, NULL, 'CLC 067', NULL, '67', 'Web', '2021-01-15', '2021-01-25', '2021-01-03', NULL, 'DAU', 'Defense Acquisition University', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (102, 'Facilities Capital Cost of Money', NULL, NULL, 'CLC 103', NULL, '103', 'Web', '2021-01-15', '2021-01-25', '2021-01-03', NULL, 'DAU', 'Defense Acquisition University', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (103, 'Analyzing Profit or Fee', NULL, NULL, 'CLC 104', NULL, '104', 'Web', '2020-12-21', '2021-01-15', '2020-12-01', NULL, 'DAU', 'Defense Acquisition University', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (104, 'Shaping Smart Business Arrangements', NULL, NULL, 'CON 100', NULL, '100', 'Web', '2021-01-15', '2021-01-25', '2021-01-03', NULL, 'DAU', 'Defense Acquisition University', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (105, 'Contracting Fundamentals', NULL, NULL, 'CON 091', NULL, '091', 'Web', '2020-12-21', '2021-01-15', '2020-12-01', NULL, 'DAU', 'Defense Acquisition University', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (106, 'Core Concepts for Requirements Management', NULL, NULL, 'RQM 110', NULL, '110', 'Web', '2021-03-03', '2021-03-20', '2021-02-27', NULL, 'DAU', 'Defense Acquisition University', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (107, 'Advanced Concepts and Skills for Requirements Management', NULL, NULL, 'RQM 310', NULL, '310', 'Web', '2021-03-03', '2021-03-20', '2021-02-27', NULL, 'DAU', 'Defense Acquisition University', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (108, 'Requirements Executive Overview Workshop', NULL, NULL, 'RQM 403', NULL, '403', 'Web', '2021-03-03', '2021-03-20', '2021-02-27', NULL, 'DAU', 'Defense Acquisition University', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (109, 'Department of Defense (DoD) Cyber Awareness Challenge 2021 (1 hr) ', NULL, NULL, 'DOD-US1364-21', NULL, '21', 'Web', '2021-03-03', '2021-03-20', '2021-02-27', NULL, 'JKO', 'Joint Knowledge Online', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (110, 'HH-60 AIRCRAFT MAINTENANCE OFFICER/SUPERVISOR FAMILIARIZATION', NULL, NULL, 'J4OMP21A3  A30A', NULL, '30', 'Web', '2021-03-03', '2021-03-20', '2021-02-27', NULL, 'AETC', 'Air Education and Training Command', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (111, 'GIAC Security Essentials Certification', NULL, NULL, 'GIAC Security Essentials Certification (GSEC)', NULL, '35', 'Web', '2021-03-03', '2021-03-20', '2021-02-27', NULL, 'AETC', 'Air Education and Training Command', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (112, 'course name', NULL, NULL, '5', NULL, '36', 'Web', '2021-03-03', '2021-03-20', '2021-02-27', NULL, 'AETC', 'Air Education and Training Command', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (113, 'GIAC Security Essentials2 Certification', NULL, NULL, 'GIAC Security Essentials2 Certification (GSEC)', NULL, '37', 'Web', '2021-03-03', '2021-03-20', '2021-02-27', NULL, 'AETC', 'Air Education and Training Command', NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2021-06-29', NULL);


SELECT setval('"elrr"."course_seq"'::regclass, (SELECT MAX("courseid") FROM "elrr"."course"));



INSERT INTO elrr.employment
  (employmentid, employername, employerdepartment, hiredate, employmentstartdate, employmentenddate, joblevel, occupation, employed, primarycareercategory, recordstatus, updatedby, inserteddate, lastmodified)
VALUES
  (100, 'NAVY', '', '2012-06-30', '2012-07-07', NULL, 'Petty Officer Third Class', 'Mass Communications Specialist', 'Y', NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (101, 'NAVY', '', '2012-06-30', '2012-07-07', NULL, 'Petty Officer First Class', 'Navy Counselor', 'Y',
   NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (102, 'NAVY', '', '2012-06-30', '2012-07-07', NULL, 'Petty Officer Second Class', 'Legalmen', 'Y', NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (103, 'NAVY', '', '2018-07-07', '2018-07-27', NULL, 'Senior Chief Petty Officer', 'Personnel Specialist', 'Y', NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (104, 'USAF', 'DoD Air Force Acquisitions', '2012-06-30', '2012-07-05', NULL, '', 'Manager of Team Acquisitions', 'Y', NULL,'ACTIVE', NULL, '2021-06-29', NULL),
  (105, 'USAF', 'DoD Air Force Acquisitions', '2012-06-30', '2010-07-07', NULL, '', '', 'Y', NULL, 'ACTIVE', NULL, '2021-06-29', NULL),
  (106, 'USAF', 'DoD Air Force Acquisitions', '2019-05-07', '2019-05-25', NULL, '', '', 'Y', NULL, 'ACTIVE', NULL, '2021-06-29', NULL);

SELECT setval('"elrr"."employment_seq"'::regclass, (SELECT MAX("employmentid") FROM "elrr"."employment"));



INSERT INTO elrr.person
  (personid, name, firstname, middlename, lastname, nameprefix, titleaffixcode, namesuffix, qualificationaffixcode, maidenname, preferredname, humanresourceidentifier, personnelidentificationsystem, birthdate, sex, primarylanguage, militaryveteranindicator, recordstatus, updatedby, inserteddate, lastmodified)
VALUES
  (100, 'Alexandrina Annabelle Arredondo-Arteaga', 'Alexandrina', 'Annabelle', 'Arredondo-Arteaga', 'Ms.', NULL, NULL, NULL, 'Atkinson-Abbas', NULL, '3599900000', NULL, '2000-12-31', 'F', 'Spanish', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  (101, 'Brinleigh Belarmino Blanchard', 'Brinleigh', 'Belarmino', 'Blanchard', 'Adm.', NULL, NULL, NULL, 'Bragg', NULL, '3599900010', NULL, '1999-10-03', 'M', 'English', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  (102, 'Christopher Cooper Cunningham', 'Christopher', 'Cooper', 'Cunningham', 'Mr.', NULL, NULL, NULL, NULL, NULL, '3518193230', NULL, '1995-01-02', 'M', 'English', 'N', 'ACTIVE', NULL, '2021-06-28', NULL),
  (103, 'Dominick Delarosa', 'Dominick', '', 'Delarosa', 'Mr.', NULL, NULL, NULL, '', NULL, '3518193250', NULL, '1994-02-05', 'M', 'Spanish', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  (104, 'Alice Beth Smith', 'Alice', 'Beth', 'Smith', 'Ms.', NULL, NULL, NULL, 'Smith', NULL, '0987654321', NULL, '1985-07-04', 'F', 'English', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  (105, 'Bill Christian Phillips', 'Bill', 'Christian', 'Phillips', 'Mr.', NULL, NULL, NULL, 'Phillips', NULL, '0123456789', NULL, '1976-03-19', 'M', 'English', 'N', 'ACTIVE', NULL, '2021-06-28', NULL),
  (106, 'Liz May Glass', 'Liz', 'May', 'Glass', 'Mrs.', NULL, NULL, NULL, 'Richards', NULL, '0123456700', NULL, '1966-05-18', 'F', 'English', 'N', 'ACTIVE', NULL, '2021-06-28', NULL),
  (107, 'Michael Aaron Christopher', 'Michael', 'Aaron', 'Christopher', 'Mr.', NULL, NULL, NULL, NULL, NULL, '3599900000', NULL, '2000-12-31', 'M', 'Spanish', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  (108, 'Jessica Bradley Matthew', 'Jessica', 'Bradley', 'Matthew', 'Ms.', NULL, NULL, NULL, 'Johnson', NULL, '3599900000', NULL, '2000-12-30', 'F', 'Spanish', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  (109, 'Ashley Carolyn Jennifer', 'Ashley', 'Carolyn', 'Jennifer', 'Ms.', NULL, NULL, NULL, 'Morton', NULL, '3599900000', NULL, '2000-12-18', 'F', 'Spanish', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  (110, 'Joshua David Armand', 'Joshua', 'David', 'Armand', 'Mr.', NULL, NULL, NULL, '', NULL, '3599900000', NULL, '2000-11-25', 'M', 'Spanish', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  (111, 'Daniel Frank David', 'Daniel', 'Frank', 'David', 'Mr.', NULL, NULL, NULL, '', NULL, '3599900000', NULL, '2000-12-31', 'M', 'Spanish', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  (112, 'James Gregory Robert', 'James', 'Gregory', 'Robert', 'Mr.', NULL, NULL, NULL, NULL, NULL, '3599900000', NULL, '2000-08-21', 'M', 'Spanish', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL),
  (113, 'John Henry Joseph', 'John', 'Henry', 'Joseph', 'Mr.', NULL, NULL, NULL, NULL, NULL, '3599900000', NULL, '2000-12-31', 'M', 'Spanish', 'Y', 'ACTIVE', NULL, '2021-06-28', NULL);

  SELECT setval('"elrr"."person_seq"'::regclass, (SELECT MAX("personid") FROM "elrr"."person"));



INSERT INTO elrr.contactinformation
  (contactinformationid, personid, contactinformation, telephonenumber, isprimaryindicator, telephonetype, electronicmailaddress, electronicmailaddresstype, emergencycontact, recordstatus, updatedby, inserteddate, lastmodified)
VALUES
  (100, 100, 'Email', '1-935-456-4578', 'Y', 'Private', 'alex91@gmail.com', 'Personal', 'Email', 'ACTIVE', NULL, '2021-06-29', NULL),
  (101, 101, 'Email', '1-935-456-4578', 'Y', 'Private', 'Alexandrina@gmail.com', 'Personal', 'Email', 'ACTIVE', NULL, '2021-06-29', NULL),
  (104, 104, 'Email', '1-935-456-4578', 'Y', 'Private', 'alice.smith@us.af.mil', 'Personal', 'Email', 'ACTIVE', NULL, '2021-06-29', NULL),
  (106, 106, 'Email', '+1 403-443-5541', 'Y', 'Business', 'glassliz@gmail.com', 'Business', 'Email', 'ACTIVE', NULL, '2021-06-29', NULL);

SELECT setval('"elrr"."contactinformation_seq"'::regclass, (SELECT MAX("contactinformationid") FROM "elrr"."contactinformation"));



INSERT INTO elrr.learnerprofile
(learnerprofileid, personid, learneraddressid, contactinformationid, employmentid, positionid, citizenshipid, studentid, courseid, courseaccreditationid, competencyid, credentialid, organizationid, organizationaddressid, accreditationid, activitystatus, recordstatus, updatedby, inserteddate, lastmodified)
VALUES
  (1, 100, NULL, NULL, 100, NULL, NULL, NULL, 100, NULL, 100, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-04-29', NULL),
  (2, 100, NULL, NULL, 100, NULL, NULL, NULL, 101, NULL, 101, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-04-29', NULL),
  (3, 100, NULL, NULL, 100, NULL, NULL, NULL, 102, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-04-29', NULL),
  (4, 101, NULL, 101, 100, NULL, NULL, NULL, 102, NULL, 100, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-04-29', NULL),
  (5, 106, NULL, NULL, 100, NULL, NULL, NULL, 100, NULL, 100, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-04-29', NULL),
  (6, 106, NULL, NULL, 100, NULL, NULL, NULL, 101, NULL, 101, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-04-29', NULL),
  (7, 106, NULL, NULL, 100, NULL, NULL, NULL, 102, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-04-29', NULL),
  (8, 106, NULL, NULL, 100, NULL, NULL, NULL, 108, NULL, 100, NULL, NULL, NULL, NULL, 'Completed','ACTIVE', NULL, '2021-07-15', NULL),
  (9, 105, NULL, NULL, 100, NULL, NULL, NULL, 100, NULL, 100, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (10, 105, NULL, NULL, 100, NULL, NULL, NULL, 101, NULL, 101, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (11, 104, NULL, 104, 100, NULL, NULL, NULL, 100, NULL, 100, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (12, 104, NULL, 104, 100, NULL, NULL, NULL, 101, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (13, 104, NULL, 104, 100, NULL, NULL, NULL, 104, NULL, 101, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (14, 104, NULL, 104, 100, NULL, NULL, NULL, 109, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (15, 106, NULL, NULL, 100, NULL, NULL, NULL, 109, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (16, 105, NULL, NULL, 100, NULL, NULL, NULL, 109, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (17, 106, NULL, NULL, 100, NULL, NULL, NULL, 110, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (18, 107, NULL, NULL, 100, NULL, NULL, NULL, 110, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (19, 108, NULL, NULL, 100, NULL, NULL, NULL, 110, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (20, 109, NULL, NULL, 100, NULL, NULL, NULL, 110, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (21, 110, NULL, NULL, 100, NULL, NULL, NULL, 110, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (22, 111, NULL, NULL, 100, NULL, NULL, NULL, 110, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (23, 112, NULL, NULL, 100, NULL, NULL, NULL, 110, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL),
  (24, 113, NULL, NULL, 100, NULL, NULL, NULL, 110, NULL, NULL, NULL, 100, NULL, 1, 'Completed', 'ACTIVE', NULL, '2021-06-28', NULL);

SELECT setval('"elrr"."learnerprofile_seq"'::regclass, (SELECT MAX("learnerprofileid") FROM "elrr"."learnerprofile"));



INSERT INTO elrr.organization
(organizationid, organizationname, organizationidentifier, organizationidentificationcode, organizationidentificationsystem, 
industrytypeidentifier, organizationfein, organizationdescription, parentorganization, recordstatus, updatedby, inserteddate, lastmodified)
VALUES
  (100, 'NAVY', 'D0DAF', 'G0V4', NULL, NULL, '1234573', 'NAVY', 'NAVY', 'ACTIVE', NULL, '2021-06-28', NULL),
  (101, 'AETC1', 'D0DAF', 'G0V4', NULL, NULL, '1234572', 'DoD AIR FORCE', 'AIR FORCE', 'ACTIVE', NULL, '2021-06-28', NULL),
  (102, 'AETC2', 'D0DAF', 'G0V4', NULL, NULL, '1234571', 'DoD AIR FORCE', 'AIR FORCE', 'ACTIVE', NULL, '2021-06-28', NULL),
  (103, 'AETC3', 'D0DAF', 'G0V4', NULL, NULL, '1234570', 'DoD AIR FORCE', 'AIR FORCE', 'ACTIVE', NULL, '2021-06-28', NULL),
  (104, 'AETC4', 'D0DAF', 'G0V4', NULL, NULL, '1234569', 'DoD AIR FORCE', 'AIR FORCE', 'ACTIVE', NULL, '2021-06-28', NULL),
  (105, 'AETC5', 'D0DAF', 'G0V4', NULL, NULL, '1234568', 'DoD AIR FORCE', 'AIR FORCE', 'ACTIVE', NULL, '2021-06-28', NULL),
  (106, 'AETC', 'D0DAF', 'G0V4', NULL, NULL, '1234567', 'DoD AIR FORCE', 'AIR FORCE', 'ACTIVE', NULL,  '2021-06-28', NULL),
  (107, 'AETC', 'D0DAF', 'G0V4', NULL, NULL, '1234565', 'DoD AIR FORCE', 'AIR FORCE', 'ACTIVE', NULL,  '2021-06-28', NULL),
  (108, 'AETC', 'D0DAF', 'G0V4', NULL, NULL, '1234563', 'DoD AIR FORCE', 'AIR FORCE', 'ACTIVE', NULL,  '2021-06-28', NULL);

  SELECT setval('"elrr"."organization_seq"'::regclass, (SELECT MAX("organizationid") FROM "elrr"."organization"));

  

INSERT INTO elrr.ROLE (roleid, rolename, recordstatus, updatedby, inserteddate, lastmodified)
VALUES
  (1, 'TRAINING_MANAGER', 'ACTIVE', NULL, '2021-06-28', NULL),
  (2, 'CAREER_MANAGER', 'ACTIVE', NULL, '2021-06-28', NULL),
  (3, 'LEARNER', 'ACTIVE', NULL, NULL, '2021-06-28');

SELECT setval('"elrr"."role_seq"'::regclass, (SELECT MAX("roleid") FROM "elrr"."role"));



INSERT INTO elrr.ROLERELATIONS 
  (rolerelationsid, parentroleid, parentpersonid, childroleid, childpersonid, recordstatus, updatedby,    inserteddate, lastmodified)
VALUES
  (1, 1, 106, 3, 101, 'ACTIVE', NULL, '2021-06-28', NULL),
  (2, 1, 106, 3, 100, 'ACTIVE', NULL, '2021-06-28', NULL),
  (3, 1, 106, 3, 102, 'ACTIVE', NULL, '2021-06-28', NULL),
  (4, 1, 106, 3, 104, 'ACTIVE', NULL, '2021-06-28', NULL),
  (5, 2, 105, 3, 104, 'ACTIVE', NULL, '2021-06-28', NULL),
  (6, 2, 105, 3, 100, 'ACTIVE', NULL, '2021-06-28', NULL);
  
  SELECT setval('"elrr"."rolerelations_seq"'::regclass, (SELECT MAX("rolerelationsid") FROM "elrr"."rolerelations"));
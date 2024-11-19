CREATE DATABASE [IF NOT EXISTS] service_db;

CREATE SCHEMA [IF NOT EXISTS] elrr;

-- Navigate to elrr schema 
SET search_path TO elrr;

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
    INCREMENT BY 50
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE;
    
ALTER SEQUENCE elrr.accreditation_seq OWNED BY elrr.accreditation.accrediationid
    

    
CREATE TABLE IF NOT EXISTS elrr.CONFIGURATION (
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


CREATE TABLE IF NOT EXISTS elrr.COMPETENCY (
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
    INCREMENT BY 50
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE;
    
ALTER SEQUENCE elrr.competency_seq OWNED BY elrr.competency.competencyid
 


CREATE TABLE IF NOT EXISTS elrr.COURSE (
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
    INCREMENT BY 50
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
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
    INCREMENT BY 50
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE;

ALTER SEQUENCE elrr.courseaccreditation_seq OWNED BY elrr.courseaccreditation.courseaccreditationid;

    
    
CREATE TABLE IF NOT EXISTS elrr.ELRRAUDITLOG (
	elrrauditlogid int8 NOT NULL,
	payload jsonb NULL,
	syncid int8 NOT NULL,
	recordstatus varchar(10) NULL, 
	updatedby varchar(20) NULL,
	inserteddate timestamp NULL,
	lastmodified timestamp NULL,
	CONSTRAINT elrrauditlog_pk PRIMARY KEY (elrrauditlogid)
);

CREATE SEQUENCE IF NOT EXISTS elrr.elrrauditlog_seq
   START WITH 1
   INCREMENT BY 50
   NO MINVALUE
   NO MAXVALUE
   CACHE 1;

ALTER SEQUENCE elrr.elrrauditlog_seq OWNED BY elrr.elrrauditlog.elrrauditlogid;



CREATE TABLE IF NOT EXISTS elrr.EMPLOYMENT (
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
    INCREMENT BY 50
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE;

ALTER SEQUENCE elrr.employment_seq OWNED BY elrr.employment.employmentid;
    
    
    
CREATE TABLE IF NOT EXISTS elrr.PERSON (
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
    INCREMENT BY 50
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE;

ALTER SEQUENCE elrr.person_seq OWNED BY elrr.person.personid;
        


CREATE TABLE IF NOT EXISTS elrr.CONTACTINFORMATION (
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
    INCREMENT BY 50
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE;
    
ALTER SEQUENCE elrr.contactinformation_seq OWNED BY elrr.contactinformation.contactinformationid;



-- Re-visit relationships
CREATE TABLE IF NOT EXISTS elrr.LEARNERPROFILE (
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
    INCREMENT BY 50
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE;

ALTER SEQUENCE elrr.learnerprofile_seq OWNED BY elrr.learnerprofile.learnerprofileidid;



CREATE TABLE IF NOT EXISTS elrr.ORGANIZATION (
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
    INCREMENT BY 50
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE;
    
ALTER SEQUENCE elrr.organization_seq OWNED BY elrr.organization.organizationid;    


CREATE TABLE IF NOT EXISTS elrr.ROLE (
	roleid int4 NOT NULL,
	rolename varchar(20) NULL,
	recordstatus varchar(10) NULL,
	updatedby varchar(20) NULL,
	inserteddate date NULL,
	lastmodified date NULL,
	CONSTRAINT role_pk16 PRIMARY KEY (roleid)
);



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
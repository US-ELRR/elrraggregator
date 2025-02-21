-- Create schema
CREATE schema IF NOT EXISTS elrr;

-- Navigate to elrr schema
SET search_path = elrr;

-- Drop elrr tables
DROP TABLE IF EXISTS elrr.elrrauditlog cascade; 



-- Create elrr tables 
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

-- Truncate tables
TRUNCATE TABLE elrr.elrrauditlog cascade; 

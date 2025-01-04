--create the db
--create the table
--optional insert data to tables

CREATE table restaurant (
	id int NOT NULL auto_increment,
    name varchar(150),
    phonenumber varchar(10),
    emailaddress varchar(100),
    streetaddress varchar(50),
    streetaddresstwo varchar(20),
    city varchar(45),
    state varchar(27),
    zipcode varchar(10),
    PRIMARY KEY (id)
);
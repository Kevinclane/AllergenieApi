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

CREATE TABLE menu(
    id int NOT NULL AUTO_INCREMENT,
    name varchar(50),
    isactive tinyint,
    PRIMARY KEY(id)
);

CREATE TABLE restaurantmenucrosswalk(
id int NOT NULL AUTO_INCREMENT,
restaurantid int NOT NULL,
menuid int NOT NULL,
PRIMARY KEY (id),
FOREIGN KEY (restaurantid) references restaurant(id),
FOREIGN KEY (menuid) references menu(id)
);
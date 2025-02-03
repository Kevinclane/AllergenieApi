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

CREATE TABLE menuitem(
    id int NOT NULL AUTO_INCREMENT,
    menuid int NOT NULL,
    menuitemgroupid int NOT NULL,
    name varchar(50),
    description varchar(500),
    extradetails varchar(200),
    price decimal(10,2),
    position int,
    PRIMARY KEY(id),
    FOREIGN KEY (menuid) references menu(id)
    FOREIGN KEY (menuitemgroupid) references menuitemgroup(id)
);

CREATE TABLE allergen(
    id int NOT NULL AUTO_INCREMENT,
    name varchar(50),
    PRIMARY KEY(id)
);

CREATE TABLE menuitemallergen(
    id int NOT NULL AUTO_INCREMENT,
    menuitemid int NOT NULL,
    allergenid int NOT NULL,
    maycontain tinyint,
    refined tinyint,
    PRIMARY KEY(id),
    FOREIGN KEY (menuitemid) references menuitem(id),
    FOREIGN KEY (allergenid) references allergen(id)
);

CREATE TABLE menuitemgroup(
    id int NOT NULL AUTO_INCREMENT,
    menuid int NOT NULL,
    name varchar(50),
    position int,
    PRIMARY KEY(id),
    FOREIGN KEY (menuid) references menu(id)
)

INSERT INTO allergens(name)
VALUES("Milk"),
("Eggs"),
("Fish"),
("Shellfish"),
("Tree Nuts"),
("Peanuts"),
("Wheat"),
("Soybeans"),
("Sesame")
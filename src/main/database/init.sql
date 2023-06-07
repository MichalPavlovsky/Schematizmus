CREATE TABLE `FUNKCIA` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `NAZOV` VARCHAR(45) NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `OSOBA` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `NAZOV` VARCHAR(45) NULL,
  `FK_FUNKCIA` INT,
  PRIMARY KEY (`ID`),
  CONSTRAINT `FK_OSOBA_FUNKCIA`
    FOREIGN KEY (`FK_FUNKCIA`)
    REFERENCES `FUNKCIA` (`ID`)
);

CREATE TABLE `EPARCHIA` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `PROTOPRESBYTERAT` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `NAZOV` VARCHAR(45) NULL,
  `FK_EPARCHIA` INT,
  PRIMARY KEY (`ID`),
  CONSTRAINT `FK_PROTOPRESBYTERAT_EPARCHIA`
    FOREIGN KEY (`FK_EPARCHIA`)
    REFERENCES `EPARCHIA` (`ID`)
);

CREATE TABLE `FARNOST` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `NAZOV` VARCHAR(45) NULL,
  `FK_PROTOPRESBYTERAT` INT,
  `FK_OSOBA_SPRAVCA` INT,
  PRIMARY KEY (`ID`),
  CONSTRAINT `FK_FARNOST_PROTOPRESBYTERAT`
    FOREIGN KEY (`FK_PROTOPRESBYTERAT`)
    REFERENCES `PROTOPRESBYTERAT` (`ID`),
  CONSTRAINT `FK_FARNOST_OSOBA_SPRAVCA`
    FOREIGN KEY (`FK_OSOBA_SPRAVCA`)
    REFERENCES `OSOBA` (`ID`)
);

CREATE TABLE `KAPLAN` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `FK_OSOBA` INT,
  `FK_FARNOST` INT,
  PRIMARY KEY (`ID`),
  CONSTRAINT `FK_KAPLAN_OSOBA`
    FOREIGN KEY (`FK_OSOBA`)
    REFERENCES `OSOBA` (`ID`),
  CONSTRAINT `FK_KAPLAN_FARNOST`
    FOREIGN KEY (`FK_FARNOST`)
    REFERENCES `FARNOST` (`ID`)
);

CREATE TABLE `VYPOMOCNYDUCHOVNY` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `FK_OSOBA` INT,
  `FK_FARNOST` INT,
  PRIMARY KEY (`ID`),
  CONSTRAINT `FK_VYPOMOCNYDUCHOVNY_OSOBA`
    FOREIGN KEY (`FK_OSOBA`)
    REFERENCES `OSOBA` (`ID`),
  CONSTRAINT `FK_VYPOMOCNYDUCHOVNY_FARNOST`
    FOREIGN KEY (`FK_FARNOST`)
    REFERENCES `FARNOST` (`ID`)
);

CREATE TABLE `FILIALKA` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `NAZOV` VARCHAR(45) NULL,
  `FK_FARNOST` INT,
  PRIMARY KEY (`ID`),
  CONSTRAINT `FK_FILIALKA_FARNOST`
    FOREIGN KEY (`FK_FARNOST`)
    REFERENCES `FARNOST` (`ID`)
);

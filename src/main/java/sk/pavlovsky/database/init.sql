CREATE TABLE `schematizmus`.`funkcia` (
  `id` INT NOT NULL,
  `nazov` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));

  CREATE TABLE `schematizmus`.`osoba` (
  `id` INT NOT NULL,
  `nazov` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `funkcia`
    FOREIGN KEY (`id`)
    REFERENCES `schematizmus`.`funkcia` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

	CREATE TABLE `schematizmus`.`eparchia` (
  `id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `eparcha`
    FOREIGN KEY (`id`)
    REFERENCES `schematizmus`.`osoba` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE `schematizmus`.`protopresbyterat` (
  `id` INT NOT NULL,
  `Nazov` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `Eparchia`
    FOREIGN KEY (`id`)
    REFERENCES `schematizmus`.`eparchia` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `Protopresbyter`
    FOREIGN KEY (`id`)
    REFERENCES `schematizmus`.`osoba` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE `schematizmus`.`farnost` (
  `id` INT NOT NULL,
  `Nazov` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `Protopresbyterat`
    FOREIGN KEY (`id`)
    REFERENCES `schematizmus`.`protopresbyterat` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `Spravca`
    FOREIGN KEY (`id`)
    REFERENCES `schematizmus`.`osoba` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE `schematizmus`.`kaplan` (
  `id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `Osoba`
    FOREIGN KEY (`id`)
    REFERENCES `schematizmus`.`osoba` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `Farnost`
    FOREIGN KEY (`id`)
    REFERENCES `schematizmus`.`farnost` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE `schematizmus`.`vypomocny duchovny` (
  `id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `Meno`
    FOREIGN KEY (`id`)
    REFERENCES `schematizmus`.`osoba` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `Parish`
    FOREIGN KEY (`id`)
    REFERENCES `schematizmus`.`farnost` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE `schematizmus`.`filialka` (
  `id` INT NOT NULL,
  `Nazov` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `MParish`
    FOREIGN KEY (`id`)
    REFERENCES `schematizmus`.`farnost` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
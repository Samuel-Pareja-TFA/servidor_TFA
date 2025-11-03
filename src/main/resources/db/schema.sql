-- MySQL Workbench Forward Engineering para apitwitter

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema apitwitter
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `apitwitter`;
CREATE SCHEMA IF NOT EXISTS `apitwitter` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `apitwitter`;

-- -----------------------------------------------------
-- Table `apitwitter`.`roles`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `apitwitter`.`roles`;
CREATE TABLE IF NOT EXISTS `apitwitter`.`roles` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name` (`name` ASC) VISIBLE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `apitwitter`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `apitwitter`.`users`;
CREATE TABLE IF NOT EXISTS `apitwitter`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(20) NOT NULL,
  `email` VARCHAR(90) NOT NULL,
  `password` CHAR(60) NOT NULL,
  `description` LONGTEXT NULL DEFAULT NULL,
  `create_date` DATE NOT NULL,
  `role_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `username` (`username` ASC) VISIBLE,
  UNIQUE INDEX `email` (`email` ASC) VISIBLE,
  INDEX `role_id` (`role_id` ASC) VISIBLE,
  CONSTRAINT `users_ibfk_1`
    FOREIGN KEY (`role_id`)
    REFERENCES `apitwitter`.`roles` (`id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `apitwitter`.`publications`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `apitwitter`.`publications`;
CREATE TABLE IF NOT EXISTS `apitwitter`.`publications` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `text` LONGTEXT NOT NULL,
  `create_date` DATETIME NOT NULL,
  `update_date` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `user_id` (`user_id` ASC) VISIBLE,
  CONSTRAINT `publications_ibfk_1`
    FOREIGN KEY (`user_id`)
    REFERENCES `apitwitter`.`users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `apitwitter`.`users_follow_users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `apitwitter`.`users_follow_users`;
CREATE TABLE IF NOT EXISTS `apitwitter`.`users_follow_users` (
  `user_who_follows_id` INT NOT NULL,
  `user_to_follow_id` INT NOT NULL,
  PRIMARY KEY (`user_who_follows_id`, `user_to_follow_id`),
  INDEX `user_to_follow_id` (`user_to_follow_id` ASC) VISIBLE,
  CONSTRAINT `users_follow_users_ibfk_1`
    FOREIGN KEY (`user_who_follows_id`)
    REFERENCES `apitwitter`.`users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `users_follow_users_ibfk_2`
    FOREIGN KEY (`user_to_follow_id`)
    REFERENCES `apitwitter`.`users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

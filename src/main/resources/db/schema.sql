-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema apitwitter
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema apitwitter
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `apitwitter` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `apitwitter` ;

-- -----------------------------------------------------
-- Table `apitwitter`.`roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `apitwitter`.`roles` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name` (`name` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `apitwitter`.`users`
-- -----------------------------------------------------
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
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `apitwitter`.`publications`
-- -----------------------------------------------------
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
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 29
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `apitwitter`.`comments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `apitwitter`.`comments` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `publication_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `text` LONGTEXT NOT NULL,
  `create_date` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_pub` (`publication_id` ASC) VISIBLE,
  INDEX `idx_user` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_comment_publication`
    FOREIGN KEY (`publication_id`)
    REFERENCES `apitwitter`.`publications` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `apitwitter`.`users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `apitwitter`.`publication_likes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `apitwitter`.`publication_likes` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `publication_id` INT NOT NULL,
  `create_date` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_publication_likes_user_publication` (`user_id` ASC, `publication_id` ASC) VISIBLE,
  INDEX `idx_publication_likes_publication_id` (`publication_id` ASC) VISIBLE,
  CONSTRAINT `fk_publication_likes_publication`
    FOREIGN KEY (`publication_id`)
    REFERENCES `apitwitter`.`publications` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_publication_likes_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `apitwitter`.`users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 8
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `apitwitter`.`users_follow_users`
-- -----------------------------------------------------
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
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

CREATE DATABASE IF NOT EXISTS `cam-history` CHARSET utf8 COLLATE utf8_slovenian_ci;

CREATE TABLE `cam-history`.`task_event` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `process_definition_key` VARCHAR(255) NOT NULL,
  `process_definition_name` VARCHAR(255) NOT NULL,
  `process_instance_id` VARCHAR(255) NOT NULL,
  `execution_id` VARCHAR(255) NOT NULL,
  `task_instance_id` VARCHAR(255) NOT NULL,
  `task_id` VARCHAR(255) NOT NULL,
  `task_name` VARCHAR(255) NOT NULL,
  `last_assignee` VARCHAR(255) NULL DEFAULT NULL,
  `candidate_users` TEXT NULL DEFAULT NULL,
  `customer_id` BIGINT NULL,
  `product_id` BIGINT NULL,
  `customer_general_data` JSON NULL,
  `product_general_data` JSON NULL,
  `start_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`));
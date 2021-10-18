CREATE DATABASE IF NOT EXISTS `cam-history` CHARSET utf8 COLLATE utf8_slovenian_ci;

CREATE TABLE `cam-history`.`task_event` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `process_definition_key` VARCHAR(255) NOT NULL,
  `process_definition_name` VARCHAR(255) NOT NULL,
  `process_instance_id` VARCHAR(255) NOT NULL,
  `super_process_instance_id` VARCHAR(255) NULL NOT NULL,
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
  PRIMARY KEY (`id`)
);

CREATE TABLE `cam-history`.`historic_variable_store` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `process_instance_id` varchar(255) COLLATE utf8_slovenian_ci NOT NULL,
  `customer_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_historic_variable_store_process_inst_id` (`process_instance_id`)
);
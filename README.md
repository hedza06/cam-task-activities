## Camunda History Stream For Human Task Activities
This project illustrates usage of Camunda "alternative" data store for managing Human Tasks
outside of Camunda Core tables. The functionality doesn't remove the core features for saving data to
historical tables just extends history events and store data additionally into custom tables. Those data
can be used for reporting or showing task activities on custom dashboards etc.

Project use Spring Boot and embedded Camunda Engine and MySQL database.

### Database table
For creating a database, you can use the query bellow or `schema.sql` file in root of the project.

```
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
```

### Test process
In `resources -> bpmn` you can find the test process which can be used for testing the functionality. 
The process can be started using Camunda Web App. After process start, the token will wait in Human Task.
At that time, you can check the table `task_event` - task will be registered with all additional data in it.

After completion of task, row in `task_event` table will be updated.

### Running the project
- Execute `schema.sql` file
- Open `pom.xml` file and check the version of MySQL Driver (the 5.X.X is default version)
- Open `application.yml` and change driver-class-name (uncomment line)

### Contribution/Suggestions
If someone is interested for contribution or have some suggestions please contact me on e-mail `hedzaprog@gmail.com`.

### Author
Heril Muratović  
Software Engineer  
<br>
**Mobile**: +38269657962  
**E-mail**: hedzaprog@gmail.com  
**Skype**: hedza06  
**Twitter**: hedzakirk  
**LinkedIn**: https://www.linkedin.com/in/heril-muratovi%C4%87-021097132/  
**StackOverflow**: https://stackoverflow.com/users/4078505/heril-muratovic

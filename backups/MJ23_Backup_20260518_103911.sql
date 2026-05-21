-- MJ23 Playgrind Gym database backup
-- Generated at 2026-05-18T10:39:11.286489700
SET FOREIGN_KEY_CHECKS=0;

-- Table: users
DELETE FROM `users`;
INSERT INTO `users` (`user_id`,`username`,`password`,`full_name`,`email`,`phone`,`role`,`status`,`is_active`,`last_login`,`created_at`,`updated_at`,`recovery_question`,`recovery_answer_hash`) VALUES (1,'admin','admin123','Admin User','admin@mj23gym.com','09170000001','admin','active',TRUE,'2026-05-18 10:35:19.0','2026-05-15 17:02:36.0','2026-05-18 10:35:19.0','What city is MJ23 Playgrind Gym located in?','$2a$10$EAUlYXQMTOHv1wSycYKeGec1K4rYnXmdPXFuiuifxMyU.0XR2GLju');
INSERT INTO `users` (`user_id`,`username`,`password`,`full_name`,`email`,`phone`,`role`,`status`,`is_active`,`last_login`,`created_at`,`updated_at`,`recovery_question`,`recovery_answer_hash`) VALUES (5,'staff01','$2a$10$p9To6AReUSGUB0vVZmQzMuC/cNB0cLID04JsLJriODhnBpKs0aVO6','Bradley Venus','bradleyvenus8@gmail.com','09488907697','staff','active',TRUE,'2026-05-18 10:34:43.0','2026-05-18 10:33:45.0','2026-05-18 10:34:43.0',NULL,NULL);

-- Table: plans
DELETE FROM `plans`;
INSERT INTO `plans` (`plan_id`,`plan_name`,`description`,`duration`,`price`,`benefits`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (1,'Per Session','Single gym session access','Per Session',100.00,'One walk-in training session.',TRUE,NULL,'2026-05-17 17:08:34.0','2026-05-17 17:08:34.0');
INSERT INTO `plans` (`plan_id`,`plan_name`,`description`,`duration`,`price`,`benefits`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (2,'Monthly','One-month gym membership','Monthly',788.00,'Unlimited access for one month.',TRUE,NULL,'2026-05-17 17:08:34.0','2026-05-17 17:08:34.0');
INSERT INTO `plans` (`plan_id`,`plan_name`,`description`,`duration`,`price`,`benefits`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (3,'Quarterly','Three-month gym membership','Quarterly',1988.00,'Unlimited access for three months.',TRUE,NULL,'2026-05-17 17:08:34.0','2026-05-17 17:08:34.0');
INSERT INTO `plans` (`plan_id`,`plan_name`,`description`,`duration`,`price`,`benefits`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (4,'Semi Annual','Six-month gym membership','Semi Annual',3288.00,'Unlimited access for six months.',TRUE,NULL,'2026-05-17 17:09:39.0','2026-05-17 17:09:39.0');
INSERT INTO `plans` (`plan_id`,`plan_name`,`description`,`duration`,`price`,`benefits`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (5,'Annual','Twelve-month gym membership','Annual',4988.00,'Unlimited access for one year.',TRUE,NULL,'2026-05-17 17:09:39.0','2026-05-17 17:09:39.0');

-- Table: members
DELETE FROM `members`;
INSERT INTO `members` (`member_id`,`unique_member_code`,`first_name`,`last_name`,`contact_number`,`email`,`address`,`date_of_birth`,`emergency_contact`,`emergency_phone`,`gender`,`membership_start_date`,`membership_end_date`,`membership_type`,`status`,`created_by`,`created_at`,`updated_at`) VALUES (1,'M-001','Juan','dela Cruz','09171234567','juan@email.com','Taguig City',NULL,'','','M','2025-06-01','2025-07-01','Monthly','Active',NULL,'2026-05-15 17:02:36.0','2026-05-18 10:30:39.0');
INSERT INTO `members` (`member_id`,`unique_member_code`,`first_name`,`last_name`,`contact_number`,`email`,`address`,`date_of_birth`,`emergency_contact`,`emergency_phone`,`gender`,`membership_start_date`,`membership_end_date`,`membership_type`,`status`,`created_by`,`created_at`,`updated_at`) VALUES (3,'M-003','Pedro','Reyes','09193456789','pedro@email.com','Taguig City',NULL,NULL,NULL,'M','2025-05-01','2025-06-01','Monthly','Expired',NULL,'2026-05-15 17:02:36.0','2026-05-15 17:02:36.0');
INSERT INTO `members` (`member_id`,`unique_member_code`,`first_name`,`last_name`,`contact_number`,`email`,`address`,`date_of_birth`,`emergency_contact`,`emergency_phone`,`gender`,`membership_start_date`,`membership_end_date`,`membership_type`,`status`,`created_by`,`created_at`,`updated_at`) VALUES (4,'M-002','Maria','Santos','09182345678','maria@email.com','Taguig City',NULL,NULL,NULL,'F','2025-06-01','2025-07-01','Monthly','Active',NULL,'2026-05-17 17:09:39.0','2026-05-17 17:09:39.0');

-- Table: attendance
DELETE FROM `attendance`;

-- Table: inventory
DELETE FROM `inventory`;
INSERT INTO `inventory` (`item_id`,`item_code`,`item_name`,`category`,`description`,`quantity`,`current_stock`,`minimum_stock`,`reorder_level`,`unit_price`,`selling_price`,`unit_of_measure`,`supplier`,`last_restock`,`expiration_date`,`status`,`photo_url`,`notes`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (1,'INV-001','Whey Protein (1kg)','Supplements',NULL,12,12,10,10,1000.00,1200.00,'pcs',NULL,NULL,NULL,'In Stock',NULL,NULL,TRUE,NULL,'2026-05-15 17:02:36.0','2026-05-15 17:02:36.0');
INSERT INTO `inventory` (`item_id`,`item_code`,`item_name`,`category`,`description`,`quantity`,`current_stock`,`minimum_stock`,`reorder_level`,`unit_price`,`selling_price`,`unit_of_measure`,`supplier`,`last_restock`,`expiration_date`,`status`,`photo_url`,`notes`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (2,'INV-002','Nature Spring Water','Drinks',NULL,48,48,10,10,18.00,25.00,'pcs',NULL,NULL,NULL,'In Stock',NULL,NULL,TRUE,NULL,'2026-05-15 17:02:36.0','2026-05-15 17:02:36.0');
INSERT INTO `inventory` (`item_id`,`item_code`,`item_name`,`category`,`description`,`quantity`,`current_stock`,`minimum_stock`,`reorder_level`,`unit_price`,`selling_price`,`unit_of_measure`,`supplier`,`last_restock`,`expiration_date`,`status`,`photo_url`,`notes`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (3,'INV-003','Gym Gloves (M)','Accessories',NULL,0,0,10,10,280.00,350.00,'pcs',NULL,NULL,NULL,'Out of Stock',NULL,NULL,TRUE,NULL,'2026-05-15 17:02:36.0','2026-05-15 17:02:36.0');

-- Table: equipment
DELETE FROM `equipment`;
INSERT INTO `equipment` (`equipment_id`,`equipment_code`,`equipment_name`,`category`,`brand_model`,`purchase_date`,`purchase_cost`,`cost`,`quantity`,`condition`,`location`,`last_maintenance`,`next_maintenance`,`maintenance_notes`,`photo_url`,`notes`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (1,'EQ-001','Treadmill','Cardio',NULL,NULL,NULL,NULL,2,'Good',NULL,NULL,'2025-07-15',NULL,NULL,NULL,TRUE,NULL,'2026-05-15 17:02:36.0','2026-05-15 17:02:36.0');
INSERT INTO `equipment` (`equipment_id`,`equipment_code`,`equipment_name`,`category`,`brand_model`,`purchase_date`,`purchase_cost`,`cost`,`quantity`,`condition`,`location`,`last_maintenance`,`next_maintenance`,`maintenance_notes`,`photo_url`,`notes`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (2,'EQ-002','Cable Machine','Strength',NULL,NULL,NULL,NULL,1,'Maintenance',NULL,NULL,'2025-06-30',NULL,NULL,NULL,TRUE,NULL,'2026-05-15 17:02:36.0','2026-05-15 17:02:36.0');
INSERT INTO `equipment` (`equipment_id`,`equipment_code`,`equipment_name`,`category`,`brand_model`,`purchase_date`,`purchase_cost`,`cost`,`quantity`,`condition`,`location`,`last_maintenance`,`next_maintenance`,`maintenance_notes`,`photo_url`,`notes`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (3,'EQ-003','Barbell Set','Strength',NULL,NULL,NULL,NULL,4,'Good',NULL,NULL,'2025-08-01',NULL,NULL,NULL,TRUE,NULL,'2026-05-15 17:02:36.0','2026-05-15 17:02:36.0');

-- Table: maintenance_logs
DELETE FROM `maintenance_logs`;

-- Table: billing
DELETE FROM `billing`;

-- Table: payment_records
DELETE FROM `payment_records`;

-- Table: pos_transactions
DELETE FROM `pos_transactions`;

-- Table: pos_transaction_items
DELETE FROM `pos_transaction_items`;

-- Table: reports
DELETE FROM `reports`;
INSERT INTO `reports` (`report_id`,`report_name`,`report_type`,`report_period_start`,`report_period_end`,`file_path`,`notes`,`generated_by`,`generated_at`) VALUES (1,'Sales Report - 2026-05-01 to 2026-05-17','Financial','2026-05-01','2026-05-17',NULL,'Report Type: Sales Report
Period: 2026-05-01 to 2026-05-17

Sales Summary
- POS Revenue: PHP 0.00
- POS Transactions: 0
- Total Revenue: PHP 0.00
',1,'2026-05-17 17:13:38.0');
INSERT INTO `reports` (`report_id`,`report_name`,`report_type`,`report_period_start`,`report_period_end`,`file_path`,`notes`,`generated_by`,`generated_at`) VALUES (2,'Sales Report - 2026-05-01 to 2026-05-17','Financial','2026-05-01','2026-05-17','C:\\Users\\itomi\\Downloads\\MJ23_PlaygrindGym-main\\MJ23_PlaygrindGym-main\\reports\\Sales_Report_-_2026-05-01_to_2026-05-17_20260517_172450.pdf','Report Type: Sales Report
Period: 2026-05-01 to 2026-05-17

Sales Summary
- POS Revenue: PHP 0.00
- POS Transactions: 0
- Total Revenue: PHP 0.00
',1,'2026-05-17 17:24:50.0');
INSERT INTO `reports` (`report_id`,`report_name`,`report_type`,`report_period_start`,`report_period_end`,`file_path`,`notes`,`generated_by`,`generated_at`) VALUES (3,'Sales Report - 2026-05-01 to 2026-05-17','Financial','2026-05-01','2026-05-17','C:\\Users\\itomi\\Downloads\\MJ23_PlaygrindGym-main\\MJ23_PlaygrindGym-main\\reports\\Sales_Report_-_2026-05-01_to_2026-05-17_20260517_172524.pdf','Report Type: Sales Report
Period: 2026-05-01 to 2026-05-17

Sales Summary
- POS Revenue: PHP 0.00
- POS Transactions: 0
- Total Revenue: PHP 0.00
',1,'2026-05-17 17:25:24.0');
INSERT INTO `reports` (`report_id`,`report_name`,`report_type`,`report_period_start`,`report_period_end`,`file_path`,`notes`,`generated_by`,`generated_at`) VALUES (4,'Inventory Report - 2026-05-01 to 2026-05-18','Inventory','2026-05-01','2026-05-18','C:\\Users\\itomi\\Downloads\\MJ23_PlaygrindGym-main\\MJ23_PlaygrindGym-main\\reports\\Inventory_Report_-_2026-05-01_to_2026-05-18_20260518_103229.pdf','Report Type: Inventory Report
Period: 2026-05-01 to 2026-05-18

Inventory Summary
- Active Inventory Items: 3
- Low Stock Items: 1
- Active Equipment: 3
- Equipment Needing Attention: 3
',1,'2026-05-18 10:32:29.0');

-- Table: system_tools
DELETE FROM `system_tools`;
INSERT INTO `system_tools` (`tool_id`,`tool_name`,`module_name`,`description`,`status`,`version`,`updated_by`,`created_at`,`updated_at`) VALUES (1,'Backup','Maintenance','Create secured database backup copies.','Active','1.0',NULL,'2026-05-17 17:10:16.0','2026-05-17 17:10:16.0');
INSERT INTO `system_tools` (`tool_id`,`tool_name`,`module_name`,`description`,`status`,`version`,`updated_by`,`created_at`,`updated_at`) VALUES (2,'Restore','Maintenance','Recover data from saved backup files.','Active','1.0',NULL,'2026-05-17 17:10:16.0','2026-05-17 17:10:16.0');
INSERT INTO `system_tools` (`tool_id`,`tool_name`,`module_name`,`description`,`status`,`version`,`updated_by`,`created_at`,`updated_at`) VALUES (3,'Member Archive','Member Management','Archive inactive members without deleting records.','Active','1.0',NULL,'2026-05-17 17:10:16.0','2026-05-17 17:10:16.0');
INSERT INTO `system_tools` (`tool_id`,`tool_name`,`module_name`,`description`,`status`,`version`,`updated_by`,`created_at`,`updated_at`) VALUES (4,'Inventory Monitor','Inventory Management','Highlight low-stock items for restocking.','Active','1.0',NULL,'2026-05-17 17:10:16.0','2026-05-17 17:10:16.0');
INSERT INTO `system_tools` (`tool_id`,`tool_name`,`module_name`,`description`,`status`,`version`,`updated_by`,`created_at`,`updated_at`) VALUES (5,'POS Checkout','Point of Sale','Process item purchases separately from membership billing.','Active','1.0',NULL,'2026-05-17 17:10:16.0','2026-05-17 17:10:16.0');
INSERT INTO `system_tools` (`tool_id`,`tool_name`,`module_name`,`description`,`status`,`version`,`updated_by`,`created_at`,`updated_at`) VALUES (6,'Report Generator','Report','Generate and view stored system reports.','Active','1.0',NULL,'2026-05-17 17:10:16.0','2026-05-17 17:10:16.0');

SET FOREIGN_KEY_CHECKS=1;

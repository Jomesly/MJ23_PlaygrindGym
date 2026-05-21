-- MJ23 Playgrind Gym database backup
-- Generated at 2026-05-16T14:50:09.835567500
SET FOREIGN_KEY_CHECKS=0;

-- Table: users
DELETE FROM `users`;
INSERT INTO `users` (`user_id`,`username`,`password`,`full_name`,`email`,`phone`,`role`,`status`,`is_active`,`last_login`,`created_at`,`updated_at`) VALUES (1,'admin','$2a$10$CaeA/Hb69C6o65LZWo5FguQ6xzWQTMlZdzS93VVEOEqLY59gnAvEC','Admin User','admin@mj23gym.com','09170000001','admin','active',TRUE,'2026-05-16 22:49:16.0','2026-05-14 01:04:11.0','2026-05-16 22:49:41.0');
INSERT INTO `users` (`user_id`,`username`,`password`,`full_name`,`email`,`phone`,`role`,`status`,`is_active`,`last_login`,`created_at`,`updated_at`) VALUES (3,'brad','$2a$10$LyQInMR21WW2Iynda9orBeFK1WWkWIsB9/5B.e2UiCv64BUjeluuu','Bradley Venus','bradleyvenus@gmail.com','09488907697','staff','active',TRUE,'2026-05-16 20:32:02.0','2026-05-16 03:05:40.0','2026-05-16 20:32:02.0');

-- Table: plans
DELETE FROM `plans`;
INSERT INTO `plans` (`plan_id`,`plan_name`,`description`,`duration`,`price`,`benefits`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (1,'Per Session','Single gym session access','Per Session',100.00,'One walk-in training session.',TRUE,NULL,'2026-05-16 20:51:38.0','2026-05-16 20:51:38.0');
INSERT INTO `plans` (`plan_id`,`plan_name`,`description`,`duration`,`price`,`benefits`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (2,'Monthly','One-month gym membership','Monthly',788.00,'Unlimited access for one month.',TRUE,NULL,'2026-05-16 20:51:38.0','2026-05-16 20:51:38.0');
INSERT INTO `plans` (`plan_id`,`plan_name`,`description`,`duration`,`price`,`benefits`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (3,'Quarterly','Three-month gym membership','Quarterly',1988.00,'Unlimited access for three months.',TRUE,NULL,'2026-05-16 20:51:38.0','2026-05-16 20:51:38.0');
INSERT INTO `plans` (`plan_id`,`plan_name`,`description`,`duration`,`price`,`benefits`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (4,'Semi Annual','Six-month gym membership','Semi Annual',3288.00,'Unlimited access for six months.',TRUE,NULL,'2026-05-16 20:51:38.0','2026-05-16 20:51:38.0');
INSERT INTO `plans` (`plan_id`,`plan_name`,`description`,`duration`,`price`,`benefits`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (5,'Annual','Twelve-month gym membership','Annual',4988.00,'Unlimited access for one year.',TRUE,NULL,'2026-05-16 20:51:38.0','2026-05-16 20:51:38.0');

-- Table: members
DELETE FROM `members`;
INSERT INTO `members` (`member_id`,`unique_member_code`,`first_name`,`last_name`,`contact_number`,`email`,`address`,`date_of_birth`,`emergency_contact`,`emergency_phone`,`gender`,`membership_start_date`,`membership_end_date`,`membership_type`,`status`,`created_by`,`created_at`,`updated_at`) VALUES (1,'M-001','Juan','dela Cruz','09171234567','juan@email.com','Taguig City',NULL,NULL,NULL,'M','2025-06-01','2025-07-01','Monthly','Active',NULL,'2026-05-14 01:04:11.0','2026-05-14 01:04:11.0');
INSERT INTO `members` (`member_id`,`unique_member_code`,`first_name`,`last_name`,`contact_number`,`email`,`address`,`date_of_birth`,`emergency_contact`,`emergency_phone`,`gender`,`membership_start_date`,`membership_end_date`,`membership_type`,`status`,`created_by`,`created_at`,`updated_at`) VALUES (2,'M-002','Maria','Santos','09182345678','maria@email.com','Taguig City',NULL,NULL,NULL,'F','2025-06-01','2025-07-01','Monthly','Active',NULL,'2026-05-14 01:04:11.0','2026-05-14 01:04:11.0');
INSERT INTO `members` (`member_id`,`unique_member_code`,`first_name`,`last_name`,`contact_number`,`email`,`address`,`date_of_birth`,`emergency_contact`,`emergency_phone`,`gender`,`membership_start_date`,`membership_end_date`,`membership_type`,`status`,`created_by`,`created_at`,`updated_at`) VALUES (3,'M-003','Pedro','Reyes','09193456789','pedro@email.com','Taguig City',NULL,NULL,NULL,'M','2025-05-01','2025-06-01','Monthly','Expired',NULL,'2026-05-14 01:04:11.0','2026-05-14 01:04:11.0');
INSERT INTO `members` (`member_id`,`unique_member_code`,`first_name`,`last_name`,`contact_number`,`email`,`address`,`date_of_birth`,`emergency_contact`,`emergency_phone`,`gender`,`membership_start_date`,`membership_end_date`,`membership_type`,`status`,`created_by`,`created_at`,`updated_at`) VALUES (5,'M-004','rafel','bisnar','-994378292','bianar@gmail.,com','mARIKINA','2005-05-06','','','M','2026-05-14','2026-06-14','Monthly','Active',1,'2026-05-14 20:42:12.0','2026-05-16 20:12:52.0');

-- Table: attendance
DELETE FROM `attendance`;

-- Table: inventory
DELETE FROM `inventory`;
INSERT INTO `inventory` (`item_id`,`item_code`,`item_name`,`category`,`description`,`quantity`,`current_stock`,`minimum_stock`,`reorder_level`,`unit_price`,`selling_price`,`unit_of_measure`,`supplier`,`last_restock`,`expiration_date`,`status`,`photo_url`,`notes`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (1,'INV-001','Whey Protein (1kg)','Supplements',NULL,12,12,10,10,1000.00,1200.00,'pcs',NULL,NULL,NULL,'In Stock',NULL,NULL,TRUE,NULL,'2026-05-14 01:04:11.0','2026-05-14 01:04:11.0');
INSERT INTO `inventory` (`item_id`,`item_code`,`item_name`,`category`,`description`,`quantity`,`current_stock`,`minimum_stock`,`reorder_level`,`unit_price`,`selling_price`,`unit_of_measure`,`supplier`,`last_restock`,`expiration_date`,`status`,`photo_url`,`notes`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (2,'INV-002','Nature Spring Water','Drinks',NULL,48,48,10,10,18.00,25.00,'pcs',NULL,NULL,NULL,'In Stock',NULL,NULL,TRUE,NULL,'2026-05-14 01:04:11.0','2026-05-14 01:04:11.0');
INSERT INTO `inventory` (`item_id`,`item_code`,`item_name`,`category`,`description`,`quantity`,`current_stock`,`minimum_stock`,`reorder_level`,`unit_price`,`selling_price`,`unit_of_measure`,`supplier`,`last_restock`,`expiration_date`,`status`,`photo_url`,`notes`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (3,'INV-003','Gym Gloves (M)','Accessories',NULL,0,0,10,10,280.00,350.00,'pcs',NULL,NULL,NULL,'Out of Stock',NULL,NULL,TRUE,NULL,'2026-05-14 01:04:11.0','2026-05-14 01:04:11.0');
INSERT INTO `inventory` (`item_id`,`item_code`,`item_name`,`category`,`description`,`quantity`,`current_stock`,`minimum_stock`,`reorder_level`,`unit_price`,`selling_price`,`unit_of_measure`,`supplier`,`last_restock`,`expiration_date`,`status`,`photo_url`,`notes`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (4,'INV-004','Water distilled','Drinks','',10,10,5,5,10.00,10.00,'pcs','Wilkins',NULL,NULL,'In Stock',NULL,'Wilkind distilled',TRUE,1,'2026-05-14 22:07:01.0','2026-05-14 22:07:01.0');

-- Table: equipment
DELETE FROM `equipment`;
INSERT INTO `equipment` (`equipment_id`,`equipment_code`,`equipment_name`,`category`,`brand_model`,`purchase_date`,`purchase_cost`,`cost`,`quantity`,`condition`,`location`,`last_maintenance`,`next_maintenance`,`maintenance_notes`,`photo_url`,`notes`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (1,'EQ-001','Treadmill','Cardio',NULL,NULL,NULL,NULL,2,'Good',NULL,NULL,'2025-07-15',NULL,NULL,NULL,TRUE,NULL,'2026-05-14 01:04:11.0','2026-05-14 01:04:11.0');
INSERT INTO `equipment` (`equipment_id`,`equipment_code`,`equipment_name`,`category`,`brand_model`,`purchase_date`,`purchase_cost`,`cost`,`quantity`,`condition`,`location`,`last_maintenance`,`next_maintenance`,`maintenance_notes`,`photo_url`,`notes`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (2,'EQ-002','Cable Machine','Strength',NULL,NULL,NULL,NULL,1,'Maintenance',NULL,NULL,'2025-06-30',NULL,NULL,NULL,TRUE,NULL,'2026-05-14 01:04:11.0','2026-05-14 01:04:11.0');
INSERT INTO `equipment` (`equipment_id`,`equipment_code`,`equipment_name`,`category`,`brand_model`,`purchase_date`,`purchase_cost`,`cost`,`quantity`,`condition`,`location`,`last_maintenance`,`next_maintenance`,`maintenance_notes`,`photo_url`,`notes`,`is_active`,`created_by`,`created_at`,`updated_at`) VALUES (3,'EQ-003','Barbell Set','Strength',NULL,NULL,NULL,NULL,4,'Good',NULL,NULL,'2025-08-01',NULL,NULL,NULL,TRUE,NULL,'2026-05-14 01:04:11.0','2026-05-14 01:04:11.0');

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

-- Table: system_tools
DELETE FROM `system_tools`;
INSERT INTO `system_tools` (`tool_id`,`tool_name`,`module_name`,`description`,`status`,`version`,`updated_by`,`created_at`,`updated_at`) VALUES (1,'Backup','Maintenance','Create secured database backup copies.','Active','1.0',NULL,'2026-05-16 22:15:02.0','2026-05-16 22:15:02.0');
INSERT INTO `system_tools` (`tool_id`,`tool_name`,`module_name`,`description`,`status`,`version`,`updated_by`,`created_at`,`updated_at`) VALUES (2,'Restore','Maintenance','Recover data from saved backup files.','Active','1.0',NULL,'2026-05-16 22:15:02.0','2026-05-16 22:15:02.0');
INSERT INTO `system_tools` (`tool_id`,`tool_name`,`module_name`,`description`,`status`,`version`,`updated_by`,`created_at`,`updated_at`) VALUES (3,'Member Archive','Member Management','Archive inactive members without deleting records.','Active','1.0',NULL,'2026-05-16 22:15:02.0','2026-05-16 22:15:02.0');
INSERT INTO `system_tools` (`tool_id`,`tool_name`,`module_name`,`description`,`status`,`version`,`updated_by`,`created_at`,`updated_at`) VALUES (4,'Inventory Monitor','Inventory Management','Highlight low-stock items for restocking.','Active','1.0',NULL,'2026-05-16 22:15:02.0','2026-05-16 22:15:02.0');
INSERT INTO `system_tools` (`tool_id`,`tool_name`,`module_name`,`description`,`status`,`version`,`updated_by`,`created_at`,`updated_at`) VALUES (5,'POS Checkout','Point of Sale','Process item purchases separately from membership billing.','Active','1.0',NULL,'2026-05-16 22:15:02.0','2026-05-16 22:15:02.0');
INSERT INTO `system_tools` (`tool_id`,`tool_name`,`module_name`,`description`,`status`,`version`,`updated_by`,`created_at`,`updated_at`) VALUES (6,'Report Generator','Report','Generate and view stored system reports.','Active','1.0',NULL,'2026-05-16 22:15:02.0','2026-05-16 22:15:02.0');

SET FOREIGN_KEY_CHECKS=1;

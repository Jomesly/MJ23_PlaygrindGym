-- MJ23 Playgrind Gym Management System
-- MySQL 8.0 Database Schema

-- Create Database
CREATE DATABASE IF NOT EXISTS mj23gym;
USE mj23gym;

-- ============================================
-- USERS TABLE (Login & Authentication)
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    role ENUM('admin', 'staff', 'trainer') DEFAULT 'staff',
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- MEMBERS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    member_code VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    gender ENUM('M', 'F', 'Other') DEFAULT 'M',
    date_of_birth DATE,
    membership_type ENUM('Daily', 'Monthly', 'Quarterly', 'Yearly', 'Per Session') DEFAULT 'Monthly',
    membership_start_date DATE NOT NULL,
    membership_end_date DATE,
    status ENUM('Active', 'Expired', 'Suspended', 'Cancelled') DEFAULT 'Active',
    emergency_contact VARCHAR(100),
    emergency_phone VARCHAR(20),
    address TEXT,
    city VARCHAR(50),
    postal_code VARCHAR(20),
    photo_url VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_member_code (member_code),
    INDEX idx_status (status),
    INDEX idx_membership_end_date (membership_end_date),
    FULLTEXT INDEX idx_fulltext (first_name, last_name, email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- EQUIPMENT TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS equipment (
    equipment_id INT AUTO_INCREMENT PRIMARY KEY,
    equipment_code VARCHAR(20) UNIQUE NOT NULL,
    equipment_name VARCHAR(150) NOT NULL,
    category ENUM('Cardio', 'Strength', 'Bodyweight', 'Flexibility', 'Other') DEFAULT 'Other',
    quantity INT DEFAULT 1,
    condition ENUM('Good', 'Fair', 'Maintenance', 'Broken') DEFAULT 'Good',
    purchase_date DATE,
    last_maintenance DATE,
    next_maintenance DATE,
    maintenance_notes TEXT,
    supplier VARCHAR(150),
    cost DECIMAL(10, 2),
    photo_url VARCHAR(255),
    location VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_equipment_code (equipment_code),
    INDEX idx_category (category),
    INDEX idx_condition (condition),
    INDEX idx_next_maintenance (next_maintenance)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- INVENTORY TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS inventory (
    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    item_code VARCHAR(20) UNIQUE NOT NULL,
    item_name VARCHAR(150) NOT NULL,
    category ENUM('Supplements', 'Drinks', 'Accessories', 'Equipment', 'Snacks', 'Other') DEFAULT 'Other',
    quantity INT DEFAULT 0,
    unit_price DECIMAL(10, 2) NOT NULL,
    reorder_level INT DEFAULT 10,
    supplier VARCHAR(150),
    status ENUM('In Stock', 'Low Stock', 'Out of Stock') DEFAULT 'In Stock',
    photo_url VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_item_code (item_code),
    INDEX idx_status (status),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- PAYMENTS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('Cash', 'GCash', 'Bank Transfer', 'Credit Card', 'Other') DEFAULT 'Cash',
    transaction_ref VARCHAR(100),
    description VARCHAR(255),
    status ENUM('Completed', 'Pending', 'Failed') DEFAULT 'Completed',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE,
    INDEX idx_member_id (member_id),
    INDEX idx_payment_date (payment_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- POS TRANSACTIONS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS pos_transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    member_id INT,
    total_amount DECIMAL(10, 2) NOT NULL,
    discount DECIMAL(10, 2) DEFAULT 0,
    final_amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('Cash', 'GCash', 'Bank Transfer', 'Credit Card', 'Other') DEFAULT 'Cash',
    payment_ref VARCHAR(100),
    status ENUM('Completed', 'Pending', 'Cancelled') DEFAULT 'Completed',
    cashier_id INT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE SET NULL,
    FOREIGN KEY (cashier_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_member_id (member_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- POS TRANSACTION ITEMS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS pos_transaction_items (
    transaction_item_id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id INT NOT NULL,
    inventory_id INT,
    item_name VARCHAR(150) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (transaction_id) REFERENCES pos_transactions(transaction_id) ON DELETE CASCADE,
    FOREIGN KEY (inventory_id) REFERENCES inventory(inventory_id) ON DELETE SET NULL,
    INDEX idx_transaction_id (transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- MAINTENANCE LOG TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS maintenance_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    equipment_id INT NOT NULL,
    maintenance_date DATE NOT NULL,
    maintenance_type ENUM('Preventive', 'Corrective', 'Inspection') DEFAULT 'Preventive',
    performed_by INT,
    cost DECIMAL(10, 2),
    description TEXT,
    status ENUM('Scheduled', 'In Progress', 'Completed', 'Cancelled') DEFAULT 'Scheduled',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id) ON DELETE CASCADE,
    FOREIGN KEY (performed_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_equipment_id (equipment_id),
    INDEX idx_maintenance_date (maintenance_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- ACTIVITY LOG TABLE (for audit trail)
-- ============================================
CREATE TABLE IF NOT EXISTS activity_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action VARCHAR(255) NOT NULL,
    entity_type VARCHAR(100),
    entity_id INT,
    old_value TEXT,
    new_value TEXT,
    status ENUM('Success', 'Failure') DEFAULT 'Success',
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_entity (entity_type, entity_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- INSERT SAMPLE DATA
-- ============================================

-- Admin User
INSERT INTO users (username, password, first_name, last_name, email, phone, role, status) 
VALUES ('admin', 'admin123', 'Admin', 'User', 'admin@mj23gym.com', '09170000001', 'admin', 'active');

-- Sample Members
INSERT INTO members (member_code, first_name, last_name, email, phone, gender, membership_type, membership_start_date, membership_end_date, status) 
VALUES 
('#M-001', 'Juan', 'dela Cruz', 'juan@email.com', '09171234567', 'M', 'Monthly', '2025-06-01', '2025-07-01', 'Active'),
('#M-002', 'Maria', 'Santos', 'maria@email.com', '09182345678', 'F', 'Monthly', '2025-06-01', '2025-07-01', 'Active'),
('#M-003', 'Pedro', 'Reyes', 'pedro@email.com', '09193456789', 'M', 'Monthly', '2025-05-01', '2025-06-01', 'Expired');

-- Sample Equipment
INSERT INTO equipment (equipment_code, equipment_name, category, quantity, condition, next_maintenance) 
VALUES 
('#EQ-001', 'Treadmill', 'Cardio', 2, 'Good', '2025-07-15'),
('#EQ-002', 'Cable Machine', 'Strength', 1, 'Maintenance', '2025-06-30'),
('#EQ-003', 'Barbell Set', 'Strength', 4, 'Good', '2025-08-01');

-- Sample Inventory
INSERT INTO inventory (item_code, item_name, category, quantity, unit_price, status) 
VALUES 
('#INV-001', 'Whey Protein (1kg)', 'Supplements', 12, 1200, 'In Stock'),
('#INV-002', 'Nature Spring Water', 'Drinks', 48, 25, 'In Stock'),
('#INV-003', 'Gym Gloves (M)', 'Accessories', 0, 350, 'Out of Stock');

-- ============================================
-- CREATE VIEWS FOR REPORTING
-- ============================================

-- Active Members View
CREATE OR REPLACE VIEW active_members AS
SELECT * FROM members WHERE status = 'Active' AND membership_end_date >= CURDATE();

-- Expired Memberships View
CREATE OR REPLACE VIEW expired_memberships AS
SELECT * FROM members WHERE status = 'Expired' OR membership_end_date < CURDATE();

-- Low Stock Inventory View
CREATE OR REPLACE VIEW low_stock_inventory AS
SELECT * FROM inventory WHERE quantity <= reorder_level;

-- Equipment Maintenance Due View
CREATE OR REPLACE VIEW equipment_maintenance_due AS
SELECT * FROM equipment WHERE next_maintenance <= DATE_ADD(CURDATE(), INTERVAL 30 DAY) AND condition != 'Broken';

-- Monthly Revenue View
CREATE OR REPLACE VIEW monthly_revenue AS
SELECT 
    DATE_FORMAT(payment_date, '%Y-%m') as month,
    COUNT(*) as transaction_count,
    SUM(amount) as total_revenue
FROM payments
WHERE status = 'Completed'
GROUP BY DATE_FORMAT(payment_date, '%Y-%m')
ORDER BY month DESC;

-- Member Status Summary View
CREATE OR REPLACE VIEW member_status_summary AS
SELECT 
    status,
    COUNT(*) as count
FROM members
GROUP BY status;

-- ============================================
-- SET CHARACTER SET
-- ============================================
ALTER DATABASE mj23gym CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- PRINT SUCCESS MESSAGE
-- ============================================
SELECT 'MJ23 Playgrind Gym Database initialized successfully!' as status;

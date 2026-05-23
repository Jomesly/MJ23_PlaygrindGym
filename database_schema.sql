-- ============================================================
-- MJ23 Playgrind Gym Management System
-- MySQL 8.0 Database Schema  (FIXED)
-- ============================================================

CREATE DATABASE IF NOT EXISTS mj23gym
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE mj23gym;
SET time_zone = '+08:00';

-- ============================================================
-- USERS  (Login & Authentication)
-- ============================================================
-- FIX: STRING → VARCHAR(N); added missing email/phone columns
--      that the INSERT sample data expects
CREATE TABLE IF NOT EXISTS users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(100) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,          -- store hashed value
    full_name   VARCHAR(200) NOT NULL,
    email       VARCHAR(150),
    phone       VARCHAR(20),
    role        ENUM('admin', 'staff', 'trainer') DEFAULT 'staff',
    status      ENUM('active', 'inactive')       DEFAULT 'active',
    is_active   BOOLEAN DEFAULT TRUE,
    last_login  TIMESTAMP NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_username (username),
    INDEX idx_role     (role),
    INDEX idx_status   (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- PLANS
-- ============================================================
-- FIX: created_by inline FOREIGN KEY → proper FK constraint
CREATE TABLE IF NOT EXISTS plans (
    plan_id     INT AUTO_INCREMENT PRIMARY KEY,
    plan_name   VARCHAR(100) NOT NULL,
    description TEXT,
    duration    ENUM('Daily','Per Session','Monthly','Quarterly','Semi Annual','Yearly','Annual') DEFAULT 'Monthly',
    price       DECIMAL(10,2) NOT NULL,
    benefits    TEXT,
    is_active   BOOLEAN DEFAULT TRUE,
    created_by  INT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_plan_name (plan_name),
    INDEX idx_duration  (duration)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- MEMBERS
-- ============================================================
-- FIX: STRING → VARCHAR; column name unique_member_code kept consistent;
--      INDEX now references unique_member_code (not the old alias member_code);
--      created_by inline FK → proper FK constraint
CREATE TABLE IF NOT EXISTS members (
    member_id              INT AUTO_INCREMENT PRIMARY KEY,
    unique_member_code     VARCHAR(20) UNIQUE NOT NULL,
    first_name             VARCHAR(100) NOT NULL,
    last_name              VARCHAR(100) NOT NULL,
    contact_number         VARCHAR(20)  NOT NULL,
    email                  VARCHAR(150) NOT NULL,
    address                VARCHAR(255) NOT NULL,
    date_of_birth          DATE,
    emergency_contact      VARCHAR(100),
    emergency_phone        VARCHAR(20),
    gender                 ENUM('M','F','Other') DEFAULT 'M',
    membership_start_date  DATE NOT NULL,
    membership_end_date    DATE,
    membership_type        ENUM('Daily','Per Session','Monthly','Quarterly','Semi Annual','Yearly','Annual') DEFAULT 'Monthly',
    sessions_paid          INT NOT NULL DEFAULT 0,
    sessions_remaining     INT NOT NULL DEFAULT 0,
    status                 ENUM('Active','Expired','Suspended','Cancelled') DEFAULT 'Active',
    created_by             INT,
    created_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_member_code        (unique_member_code),
    INDEX idx_status             (status),
    INDEX idx_membership_end     (membership_end_date),
    FULLTEXT INDEX idx_fulltext  (first_name, last_name, email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- ATTENDANCE
-- ============================================================
-- FIX: table name typo 'attendace' → 'attendance';
--      member_id AUTO_INCREMENT removed (only PK can be AUTO_INCREMENT);
--      STRING → VARCHAR; trailing comma before ')' removed;
--      proper FK constraint for member_id
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id   INT AUTO_INCREMENT PRIMARY KEY,
    member_id       INT NOT NULL,
    time_in         DATETIME,
    time_out        DATETIME,
    attendance_date DATE,
    session_type    ENUM('Daily','Per Session','Monthly','Quarterly','Semi Annual','Yearly','Annual') DEFAULT 'Monthly',
    notes           VARCHAR(255),

    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE,
    INDEX idx_member_id      (member_id),
    INDEX idx_attendance_date (attendance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- INVENTORY
-- ============================================================
-- FIX: duplicate created_at / updated_at columns removed;
--      created_by inline FK → proper FK constraint
CREATE TABLE IF NOT EXISTS inventory (
    item_id         INT AUTO_INCREMENT PRIMARY KEY,
    item_code       VARCHAR(20)  UNIQUE NOT NULL,
    item_name       VARCHAR(150) NOT NULL,
    category        ENUM('Supplements','Drinks','Accessories','Equipment','Snacks','Other') DEFAULT 'Other',
    description     TEXT,
    quantity        INT DEFAULT 0,
    current_stock   INT DEFAULT 0,
    minimum_stock   INT DEFAULT 10,
    reorder_level   INT DEFAULT 10,
    unit_price      DECIMAL(10,2) NOT NULL,
    selling_price   DECIMAL(10,2) NOT NULL,
    unit_of_measure ENUM('pcs','kg','liters','packs','boxes','other') DEFAULT 'pcs',
    supplier        VARCHAR(150),
    last_restock    DATE,
    expiration_date DATE,
    status          ENUM('In Stock','Low Stock','Out of Stock') DEFAULT 'In Stock',
    photo_url       VARCHAR(255),
    notes           TEXT,
    is_active       BOOLEAN DEFAULT TRUE,
    created_by      INT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_item_code (item_code),
    INDEX idx_status    (status),
    INDEX idx_category  (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS inventory_batches (
    batch_id        INT AUTO_INCREMENT PRIMARY KEY,
    item_id         INT NOT NULL,
    batch_code      VARCHAR(30),
    quantity        INT NOT NULL DEFAULT 0,
    expiration_date DATE,
    received_date   DATE NOT NULL DEFAULT (CURDATE()),
    notes           TEXT,
    created_by      INT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id)    REFERENCES inventory(item_id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(user_id)    ON DELETE SET NULL,
    INDEX idx_item_expiry (item_id, expiration_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- EQUIPMENT
-- ============================================================
-- FIX: created_by inline FK → proper FK constraint
CREATE TABLE IF NOT EXISTS equipment (
    equipment_id       INT AUTO_INCREMENT PRIMARY KEY,
    equipment_code     VARCHAR(20)  UNIQUE NOT NULL,
    equipment_name     VARCHAR(150) NOT NULL,
    category           ENUM('Cardio','Strength','Bodyweight','Flexibility','Other') DEFAULT 'Other',
    brand_model        VARCHAR(150),
    purchase_date      DATE,
    purchase_cost      DECIMAL(10,2),
    cost               DECIMAL(10,2),
    quantity           INT DEFAULT 1,
    `condition`        ENUM('Good','Fair','Maintenance','Broken') DEFAULT 'Good',
    location           VARCHAR(100),
    last_maintenance   DATE,
    next_maintenance   DATE,
    maintenance_notes  TEXT,
    photo_url          VARCHAR(255),
    notes              TEXT,
    is_active          BOOLEAN DEFAULT TRUE,
    created_by         INT,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_equipment_code  (equipment_code),
    INDEX idx_category        (category),
    INDEX idx_condition       (`condition`),
    INDEX idx_next_maintenance (next_maintenance)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- BILLING
-- ============================================================
-- FIX: member_id / plan_id / created_by inline FK → proper FK constraints
CREATE TABLE IF NOT EXISTS billing (
    billing_id      INT AUTO_INCREMENT PRIMARY KEY,
    member_id       INT NOT NULL,
    plan_id         INT,
    billing_date    DATE NOT NULL,
    due_date        DATE,
    amount_due      DECIMAL(10,2) NOT NULL,
    amount_paid     DECIMAL(10,2) DEFAULT 0,
    payment_status  ENUM('Paid','Unpaid','Overdue') DEFAULT 'Unpaid',
    status          ENUM('Active','Cancelled','Completed') DEFAULT 'Active',
    created_by      INT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (member_id)  REFERENCES members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id)    REFERENCES plans(plan_id)     ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(user_id)     ON DELETE SET NULL,
    INDEX idx_member_id      (member_id),
    INDEX idx_plan_id        (plan_id),
    INDEX idx_billing_date   (billing_date),
    INDEX idx_payment_status (payment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- MAINTENANCE LOGS
-- ============================================================
-- FIX: equipment_id / recorded_by inline FK → proper FK constraints;
--      status STRING → VARCHAR(50)
CREATE TABLE IF NOT EXISTS maintenance_logs (
    maintenance_id      INT AUTO_INCREMENT PRIMARY KEY,
    equipment_id        INT NOT NULL,
    maintenance_date    DATE NOT NULL,
    maintenance_type    VARCHAR(100) NOT NULL,
    description         TEXT,
    cost                DECIMAL(10,2),
    performed_by        VARCHAR(150),
    status              VARCHAR(50) DEFAULT 'Completed',
    next_scheduled_date DATE,
    notes               TEXT,
    recorded_by         INT,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by)  REFERENCES users(user_id)          ON DELETE SET NULL,
    INDEX idx_equipment_id   (equipment_id),
    INDEX idx_maintenance_date (maintenance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- PAYMENT RECORDS
-- ============================================================
-- FIX: processed_by inline FK → proper FK constraint
CREATE TABLE IF NOT EXISTS payment_records (
    payment_id      INT AUTO_INCREMENT PRIMARY KEY,
    member_id       INT NOT NULL,
    billing_id      INT,
    payment_method  ENUM('Cash','GCash','Bank Transfer','Other') DEFAULT 'Cash',
    payment_type    ENUM('Membership','Personal Training','Merchandise','Other') DEFAULT 'Membership',
    plan_type_snapshot VARCHAR(50) NULL,
    payment_date    DATE NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    transaction_ref VARCHAR(100),
    status          ENUM('Completed','Pending','Failed') DEFAULT 'Completed',
    notes           TEXT,
    processed_by    INT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (member_id)   REFERENCES members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (billing_id)  REFERENCES billing(billing_id) ON DELETE SET NULL,
    FOREIGN KEY (processed_by) REFERENCES users(user_id)    ON DELETE SET NULL,
    INDEX idx_member_id    (member_id),
    INDEX idx_payment_date (payment_date),
    INDEX idx_status       (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- POS TRANSACTIONS  (header record per sale)
-- ============================================================
-- FIX: item_id / member_id / processed_by inline FK → proper FK constraints;
--      missing COLLATE added; quantity_sold changed to INT (items are whole units)
CREATE TABLE IF NOT EXISTS pos_transactions (
    transaction_id  INT AUTO_INCREMENT PRIMARY KEY,
    member_id       INT,                         -- nullable: walk-in guest
    payment_method  ENUM('Cash','GCash','Bank Transfer','Other') DEFAULT 'Cash',
    reference_number VARCHAR(100),
    total_amount    DECIMAL(10,2) NOT NULL,
    amount_paid     DECIMAL(10,2) NOT NULL DEFAULT 0,
    change_amount   DECIMAL(10,2) NOT NULL DEFAULT 0,
    sale_date       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_by    INT,
    notes           TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (member_id)    REFERENCES members(member_id) ON DELETE SET NULL,
    FOREIGN KEY (processed_by) REFERENCES users(user_id)     ON DELETE SET NULL,
    INDEX idx_member_id (member_id),
    INDEX idx_sale_date (sale_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- POS TRANSACTION ITEMS  (line items per transaction)
-- ============================================================
-- FIX: FK references inventory(item_id) not inventory(inventory_id)
CREATE TABLE IF NOT EXISTS pos_transaction_items (
    transaction_item_id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id      INT NOT NULL,
    item_id             INT,                     -- FK to inventory.item_id
    item_name           VARCHAR(150) NOT NULL,   -- snapshot at time of sale
    quantity            INT NOT NULL,
    unit_price          DECIMAL(10,2) NOT NULL,
    subtotal            DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (transaction_id) REFERENCES pos_transactions(transaction_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id)        REFERENCES inventory(item_id)               ON DELETE SET NULL,
    INDEX idx_transaction_id (transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- REPORTS
-- ============================================================
-- FIX: generated_by inline FK → proper FK constraint
CREATE TABLE IF NOT EXISTS reports (
    report_id           INT AUTO_INCREMENT PRIMARY KEY,
    report_name         VARCHAR(150) NOT NULL,
    report_type         ENUM('Membership','Attendance','Inventory','Financial','Equipment','Other') DEFAULT 'Other',
    report_period_start DATE,
    report_period_end   DATE,
    file_path           VARCHAR(255),
    notes               TEXT,
    generated_by        INT,
    generated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (generated_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_report_type  (report_type),
    INDEX idx_generated_at (generated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- SYSTEM BACKUPS
-- ============================================================
CREATE TABLE IF NOT EXISTS system_backups (
    backup_id    INT AUTO_INCREMENT PRIMARY KEY,
    backup_name  VARCHAR(150) NOT NULL,
    file_path    VARCHAR(255) NOT NULL,
    status       ENUM('Created','Restored','Failed') DEFAULT 'Created',
    notes        TEXT,
    generated_by INT,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (generated_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_backup_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- SYSTEM TOOLS
-- ============================================================
CREATE TABLE IF NOT EXISTS system_tools (
    tool_id     INT AUTO_INCREMENT PRIMARY KEY,
    tool_name   VARCHAR(120) NOT NULL,
    module_name VARCHAR(120) NOT NULL,
    description TEXT,
    status      ENUM('Active','Archived') DEFAULT 'Active',
    version     VARCHAR(30) DEFAULT '1.0',
    updated_by  INT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (updated_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_tool_status (status),
    INDEX idx_tool_module (module_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- AUDIT LOGS
-- ============================================================
-- FIX: old_values DATETIME → TEXT (stores JSON snapshot);
--      new_value INT FOREIGN KEY → TEXT;
--      added missing entity_type / entity_id columns for the index;
--      user_id inline FK → proper FK constraint
CREATE TABLE IF NOT EXISTS audit_logs (
    log_id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT,
    entity_type VARCHAR(100),                   -- e.g. 'members', 'payment_records'
    entity_id   INT,                            -- PK of the affected row
    action      VARCHAR(255) NOT NULL,          -- e.g. 'INSERT', 'UPDATE', 'DELETE'
    old_values  TEXT,                           -- JSON snapshot of old data
    new_values  TEXT,                           -- JSON snapshot of new data
    status      ENUM('Success','Failure') DEFAULT 'Success',
    ip_address  VARCHAR(45),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_user_id    (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_entity     (entity_type, entity_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- DATABASE-LEVEL AUDIT TRIGGERS
-- ============================================================
-- These protect critical membership and financial tables even when changes
-- are made outside the Java application.
DROP TRIGGER IF EXISTS trg_members_update;
DROP TRIGGER IF EXISTS trg_members_delete;
DROP TRIGGER IF EXISTS trg_payment_records_delete;
DROP TRIGGER IF EXISTS trg_billing_update;

DELIMITER $$

CREATE TRIGGER trg_members_update
BEFORE UPDATE ON members
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (entity_type, entity_id, action, old_values, new_values, status, created_at)
    VALUES (
        'members',
        OLD.member_id,
        'UPDATE',
        JSON_OBJECT(
            'membership_type', OLD.membership_type,
            'status', OLD.status,
            'membership_end_date', OLD.membership_end_date
        ),
        JSON_OBJECT(
            'membership_type', NEW.membership_type,
            'status', NEW.status,
            'membership_end_date', NEW.membership_end_date
        ),
        'Success',
        NOW()
    );
END$$

CREATE TRIGGER trg_members_delete
BEFORE DELETE ON members
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (entity_type, entity_id, action, old_values, status, created_at)
    VALUES (
        'members',
        OLD.member_id,
        'DELETE',
        JSON_OBJECT(
            'unique_member_code', OLD.unique_member_code,
            'first_name', OLD.first_name,
            'last_name', OLD.last_name,
            'membership_type', OLD.membership_type,
            'status', OLD.status
        ),
        'Success',
        NOW()
    );
END$$

CREATE TRIGGER trg_payment_records_delete
BEFORE DELETE ON payment_records
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (entity_type, entity_id, action, old_values, status, created_at)
    VALUES (
        'payment_records',
        OLD.payment_id,
        'DELETE',
        JSON_OBJECT(
            'member_id', OLD.member_id,
            'amount', OLD.amount,
            'payment_date', OLD.payment_date,
            'payment_type', OLD.payment_type,
            'status', OLD.status
        ),
        'Success',
        NOW()
    );
END$$

CREATE TRIGGER trg_billing_update
BEFORE UPDATE ON billing
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (entity_type, entity_id, action, old_values, new_values, status, created_at)
    VALUES (
        'billing',
        OLD.billing_id,
        'UPDATE',
        JSON_OBJECT(
            'payment_status', OLD.payment_status,
            'status', OLD.status,
            'amount_paid', OLD.amount_paid
        ),
        JSON_OBJECT(
            'payment_status', NEW.payment_status,
            'status', NEW.status,
            'amount_paid', NEW.amount_paid
        ),
        'Success',
        NOW()
    );
END$$

DELIMITER ;

REVOKE DELETE ON mj23gym.audit_logs FROM 'root'@'localhost';


-- ============================================================
-- SYSTEM SETTINGS
-- ============================================================
-- FIX: INDEX referenced 'setting_key' → changed to match column 'setting_name';
--      updated_by inline FK → proper FK constraint
CREATE TABLE IF NOT EXISTS system_settings (
    setting_id    INT AUTO_INCREMENT PRIMARY KEY,
    setting_name  VARCHAR(100) UNIQUE NOT NULL,
    setting_value VARCHAR(255) NOT NULL,
    description   TEXT,
    updated_by    INT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (updated_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_setting_name (setting_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- EXISTING DATABASE MIGRATIONS
-- ============================================================
-- CREATE TABLE IF NOT EXISTS does not change old tables.
-- These ALTER statements keep an already-created database compatible
-- with the current app and sample data.
ALTER TABLE plans
    MODIFY duration ENUM('Daily','Per Session','Monthly','Quarterly','Semi Annual','Yearly','Annual') DEFAULT 'Monthly';

ALTER TABLE members
    MODIFY membership_type ENUM('Daily','Per Session','Monthly','Quarterly','Semi Annual','Yearly','Annual') DEFAULT 'Monthly';

-- ============================================================
-- SAMPLE DATA
-- ============================================================
-- FIX: users INSERT now matches actual column names (full_name, not first_name/last_name)
INSERT INTO users (username, password, full_name, email, phone, role, status, is_active)
VALUES ('admin', '$2a$10$8JZRvu95Q0GUO.cdxzaZtePG29Tql.9kV42onm5qxM.ROvnC06DCC', 'Admin User', 'admin@mj23gym.com', '09170000001', 'admin', 'active', TRUE)
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    full_name = VALUES(full_name),
    email = VALUES(email),
    phone = VALUES(phone),
    role = VALUES(role),
    status = VALUES(status),
    is_active = VALUES(is_active);

INSERT INTO plans (plan_name, description, duration, price, benefits, is_active)
SELECT 'Per Session', 'Single gym session access', 'Per Session', 100.00, 'One walk-in training session.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM plans WHERE plan_name = 'Per Session');

INSERT INTO plans (plan_name, description, duration, price, benefits, is_active)
SELECT 'Monthly', 'One-month gym membership', 'Monthly', 788.00, 'Unlimited access for one month.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM plans WHERE plan_name = 'Monthly');

INSERT INTO plans (plan_name, description, duration, price, benefits, is_active)
SELECT 'Quarterly', 'Three-month gym membership', 'Quarterly', 1988.00, 'Unlimited access for three months.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM plans WHERE plan_name = 'Quarterly');

INSERT INTO plans (plan_name, description, duration, price, benefits, is_active)
SELECT 'Semi Annual', 'Six-month gym membership', 'Semi Annual', 3288.00, 'Unlimited access for six months.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM plans WHERE plan_name = 'Semi Annual');

INSERT INTO plans (plan_name, description, duration, price, benefits, is_active)
SELECT 'Annual', 'Twelve-month gym membership', 'Annual', 4988.00, 'Unlimited access for one year.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM plans WHERE plan_name = 'Annual');

-- FIX: members INSERT now uses unique_member_code (not member_code)
INSERT INTO members (unique_member_code, first_name, last_name, email, contact_number, address, gender, membership_type, membership_start_date, membership_end_date, status)
VALUES
    ('M-001', 'Juan',  'dela Cruz', 'juan@email.com',  '09171234567', 'Taguig City', 'M', 'Monthly', '2025-06-01', '2025-07-01', 'Active'),
    ('M-002', 'Maria', 'Santos',    'maria@email.com', '09182345678', 'Taguig City', 'F', 'Monthly', '2025-06-01', '2025-07-01', 'Active'),
    ('M-003', 'Pedro', 'Reyes',     'pedro@email.com', '09193456789', 'Taguig City', 'M', 'Monthly', '2025-05-01', '2025-06-01', 'Expired')
ON DUPLICATE KEY UPDATE
    first_name = VALUES(first_name),
    last_name = VALUES(last_name),
    email = VALUES(email),
    contact_number = VALUES(contact_number),
    address = VALUES(address),
    gender = VALUES(gender),
    membership_type = VALUES(membership_type),
    membership_start_date = VALUES(membership_start_date),
    membership_end_date = VALUES(membership_end_date),
    status = VALUES(status);

INSERT INTO equipment (equipment_code, equipment_name, category, quantity, `condition`, next_maintenance)
VALUES
    ('EQ-001', 'Treadmill',    'Cardio',   2, 'Good',        '2025-07-15'),
    ('EQ-002', 'Cable Machine','Strength', 1, 'Maintenance', '2025-06-30'),
    ('EQ-003', 'Barbell Set',  'Strength', 4, 'Good',        '2025-08-01')
ON DUPLICATE KEY UPDATE
    equipment_name = VALUES(equipment_name),
    category = VALUES(category),
    quantity = VALUES(quantity),
    `condition` = VALUES(`condition`),
    next_maintenance = VALUES(next_maintenance);

INSERT INTO inventory (item_code, item_name, category, quantity, current_stock, unit_price, selling_price, status)
VALUES
    ('INV-001', 'Whey Protein (1kg)', 'Supplements', 12, 12, 1000.00, 1200.00, 'In Stock'),
    ('INV-002', 'Nature Spring Water','Drinks',       48, 48,   18.00,   25.00, 'In Stock'),
    ('INV-003', 'Gym Gloves (M)',     'Accessories',   0,  0,  280.00,  350.00, 'Out of Stock')
ON DUPLICATE KEY UPDATE
    item_name = VALUES(item_name),
    category = VALUES(category),
    quantity = VALUES(quantity),
    current_stock = VALUES(current_stock),
    unit_price = VALUES(unit_price),
    selling_price = VALUES(selling_price),
    status = VALUES(status);


-- ============================================================
-- VIEWS
-- ============================================================
CREATE OR REPLACE VIEW active_members AS
SELECT * FROM members
WHERE status = 'Active' AND membership_end_date >= CURDATE();

CREATE OR REPLACE VIEW expired_memberships AS
SELECT * FROM members
WHERE status = 'Expired' OR membership_end_date < CURDATE();

CREATE OR REPLACE VIEW low_stock_inventory AS
SELECT * FROM inventory
WHERE current_stock <= reorder_level AND is_active = TRUE;

CREATE OR REPLACE VIEW equipment_maintenance_due AS
SELECT * FROM equipment
WHERE next_maintenance <= DATE_ADD(CURDATE(), INTERVAL 30 DAY)
  AND `condition` != 'Broken'
  AND is_active = TRUE;

-- FIX: was referencing 'payments' (wrong table name) → now 'payment_records'
CREATE OR REPLACE VIEW monthly_revenue AS
SELECT
    DATE_FORMAT(payment_date, '%Y-%m') AS month,
    COUNT(*)                           AS transaction_count,
    SUM(amount)                        AS total_revenue
FROM payment_records
WHERE status = 'Completed'
GROUP BY DATE_FORMAT(payment_date, '%Y-%m')
ORDER BY month DESC;

CREATE OR REPLACE VIEW member_status_summary AS
SELECT status, COUNT(*) AS count
FROM members
GROUP BY status;


-- ============================================================
ALTER DATABASE mj23gym CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

SELECT 'MJ23 Playgrind Gym database initialized successfully!' AS status;





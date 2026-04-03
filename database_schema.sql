USE railway;

-- 确保数据库使用支持中文的编码
SET NAMES utf8mb4;

-- 1. 用户账号表 (user_account)
CREATE TABLE IF NOT EXISTS `user_account` (
  `user_id` INT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(50) UNIQUE NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `email` VARCHAR(100),
  `account_status` ENUM('Active', 'Suspended') DEFAULT 'Active',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 用户权限表 (user_profile)
CREATE TABLE IF NOT EXISTS `user_profile` (
  `profile_id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT,
  `role_name` ENUM('User Admin', 'Fund Raiser', 'Donee', 'Platform Management') NOT NULL,
  `permissions` TEXT,
  FOREIGN KEY (`user_id`) REFERENCES `user_account`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 活动类别表 (fsa_category)
CREATE TABLE IF NOT EXISTS `fsa_category` (
  `category_id` INT PRIMARY KEY AUTO_INCREMENT,
  `category_name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `status` ENUM('Active', 'Inactive') DEFAULT 'Active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 活动主表 (fsa_activity)
CREATE TABLE IF NOT EXISTS `fsa_activity` (
  `activity_id` INT PRIMARY KEY AUTO_INCREMENT,
  `category_id` INT,
  `creator_id` INT,
  `title` VARCHAR(200) NOT NULL,
  `description` TEXT,
  `goal_amount` DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
  `current_amount` DECIMAL(15, 2) DEFAULT 0.00,
  `start_date` DATE,
  `end_date` DATE,
  `status` ENUM('Pending', 'Active', 'Completed', 'Deleted') DEFAULT 'Pending',
  FOREIGN KEY (`category_id`) REFERENCES `fsa_category`(`category_id`) ON DELETE SET NULL,
  FOREIGN KEY (`creator_id`) REFERENCES `user_account`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 活动统计表 (fsa_stats)
CREATE TABLE IF NOT EXISTS `fsa_stats` (
  `stat_id` INT PRIMARY KEY AUTO_INCREMENT,
  `activity_id` INT,
  `view_count` INT DEFAULT 0,
  `save_count` INT DEFAULT 0,
  FOREIGN KEY (`activity_id`) REFERENCES `fsa_activity`(`activity_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. 收藏夹 (favorite_list)
CREATE TABLE IF NOT EXISTS `favorite_list` (
  `favorite_id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT,
  `activity_id` INT,
  `saved_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `user_account`(`user_id`) ON DELETE CASCADE,
  FOREIGN KEY (`activity_id`) REFERENCES `fsa_activity`(`activity_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. 捐赠与进度记录表 (donation_history)
CREATE TABLE IF NOT EXISTS `donation_history` (
  `donation_id` INT PRIMARY KEY AUTO_INCREMENT,
  `activity_id` INT,
  `donee_id` INT,
  `amount` DECIMAL(15, 2) NOT NULL,
  `donation_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `transaction_status` VARCHAR(50),
  FOREIGN KEY (`activity_id`) REFERENCES `fsa_activity`(`activity_id`) ON DELETE CASCADE,
  FOREIGN KEY (`donee_id`) REFERENCES `user_account`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. 系统报表表 (system_report)
CREATE TABLE IF NOT EXISTS `system_report` (
  `report_id` INT PRIMARY KEY AUTO_INCREMENT,
  `report_type` ENUM('Daily', 'Weekly', 'Monthly') NOT NULL,
  `generated_by` INT,
  `summary_data` JSON,
  `created_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`generated_by`) REFERENCES `user_account`(`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

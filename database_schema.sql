
USE railway;
-- 确保数据库使用支持中文的编码
SET NAMES utf8mb4;

-- 1. 用户账号表 (User Account)
CREATE TABLE IF NOT EXISTS `User_Account` (
  `UserID` INT PRIMARY KEY AUTO_INCREMENT,
  `Username` VARCHAR(50) UNIQUE NOT NULL,
  `Password` VARCHAR(255) NOT NULL,
  `Email` VARCHAR(100),
  `Account_Status` ENUM('Active', 'Suspended') DEFAULT 'Active',
  `Created_At` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 用户权限表 (User Profile)
CREATE TABLE IF NOT EXISTS `User_Profile` (
  `ProfileID` INT PRIMARY KEY AUTO_INCREMENT,
  `UserID` INT,
  `Role_Name` ENUM('User Admin', 'Fund Raiser', 'Donee', 'Platform Management') NOT NULL,
  `Permissions` TEXT,
  FOREIGN KEY (`UserID`) REFERENCES `User_Account`(`UserID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 活动类别表 (FSA Category)
CREATE TABLE IF NOT EXISTS `FSA_Category` (
  `CategoryID` INT PRIMARY KEY AUTO_INCREMENT,
  `Category_Name` VARCHAR(100) NOT NULL,
  `Description` TEXT,
  `Status` ENUM('Active', 'Inactive') DEFAULT 'Active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 活动主表 (FSA Activity)
CREATE TABLE IF NOT EXISTS `FSA_Activity` (
  `ActivityID` INT PRIMARY KEY AUTO_INCREMENT,
  `CategoryID` INT,
  `CreatorID` INT,
  `Title` VARCHAR(200) NOT NULL,
  `Description` TEXT,
  `Goal_Amount` DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
  `Current_Amount` DECIMAL(15, 2) DEFAULT 0.00,
  `Start_Date` DATE,
  `End_Date` DATE,
  `Status` ENUM('Pending', 'Active', 'Completed', 'Deleted') DEFAULT 'Pending',
  FOREIGN KEY (`CategoryID`) REFERENCES `FSA_Category`(`CategoryID`) ON DELETE SET NULL,
  FOREIGN KEY (`CreatorID`) REFERENCES `User_Account`(`UserID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 活动统计表 (FSA Stats)
CREATE TABLE IF NOT EXISTS `FSA_Stats` (
  `StatID` INT PRIMARY KEY AUTO_INCREMENT,
  `ActivityID` INT,
  `View_Count` INT DEFAULT 0,
  `Save_Count` INT DEFAULT 0,
  FOREIGN KEY (`ActivityID`) REFERENCES `FSA_Activity`(`ActivityID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. 收藏夹 (Favorite List)
CREATE TABLE IF NOT EXISTS `Favorite_List` (
  `FavoriteID` INT PRIMARY KEY AUTO_INCREMENT,
  `UserID` INT,
  `ActivityID` INT,
  `Saved_Date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`UserID`) REFERENCES `User_Account`(`UserID`) ON DELETE CASCADE,
  FOREIGN KEY (`ActivityID`) REFERENCES `FSA_Activity`(`ActivityID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. 捐赠与进度记录表 (Donation History)
CREATE TABLE IF NOT EXISTS `Donation_History` (
  `DonationID` INT PRIMARY KEY AUTO_INCREMENT,
  `ActivityID` INT,
  `DoneeID` INT,
  `Amount` DECIMAL(15, 2) NOT NULL,
  `Donation_Date` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `Transaction_Status` VARCHAR(50),
  FOREIGN KEY (`ActivityID`) REFERENCES `FSA_Activity`(`ActivityID`) ON DELETE CASCADE,
  FOREIGN KEY (`DoneeID`) REFERENCES `User_Account`(`UserID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. 系统报表表 (System Report)
CREATE TABLE IF NOT EXISTS `System_Report` (
  `ReportID` INT PRIMARY KEY AUTO_INCREMENT,
  `Report_Type` ENUM('Daily', 'Weekly', 'Monthly') NOT NULL,
  `Generated_By` INT,
  `Summary_Data` JSON,
  `Created_Date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`Generated_By`) REFERENCES `User_Account`(`UserID`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
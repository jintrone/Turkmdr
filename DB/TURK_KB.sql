# ************************************************************
# Sequel Pro SQL dump
# Version 4004
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: localhost (MySQL 5.1.47)
# Database: turk-kb
# Generation Time: 2013-02-06 12:11:23 -0500
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table AUTO_PK_SUPPORT
# ------------------------------------------------------------

DROP TABLE IF EXISTS `AUTO_PK_SUPPORT`;

CREATE TABLE `AUTO_PK_SUPPORT` (
  `TABLE_NAME` char(100) NOT NULL,
  `NEXT_ID` bigint(20) NOT NULL,
  UNIQUE KEY `TABLE_NAME` (`TABLE_NAME`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table AwsCredentials
# ------------------------------------------------------------

DROP TABLE IF EXISTS `AwsCredentials`;

CREATE TABLE `AwsCredentials` (
  `awsId` varchar(2048) NOT NULL,
  `awsSecret` varchar(2048) NOT NULL,
  `id` int(11) NOT NULL,
  `username` varchar(2048) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table Batch
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Batch`;

CREATE TABLE `Batch` (
  `AwsId` varchar(255) DEFAULT NULL,
  `AwsSecret` varchar(255) DEFAULT NULL,
  `Created` datetime DEFAULT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `experimentId` bigint(20) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `isReal` bit(1) DEFAULT NULL,
  `parameters` longtext,
  `restartRate` bigint(20) DEFAULT NULL,
  `autoApprove` bit(1) DEFAULT NULL,
  `contactEmail` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `experimentId` (`experimentId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table BatchStatus
# ------------------------------------------------------------

DROP TABLE IF EXISTS `BatchStatus`;

CREATE TABLE `BatchStatus` (
  `batchId` bigint(20) DEFAULT NULL,
  `creation` datetime DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `jsonStatus` longtext,
  PRIMARY KEY (`id`),
  KEY `batchId` (`batchId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table Experiment
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Experiment`;

CREATE TABLE `Experiment` (
  `id` bigint(20) NOT NULL,
  `Created` datetime DEFAULT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `classname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table Hits
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Hits`;

CREATE TABLE `Hits` (
  `batchId` bigint(20) DEFAULT NULL,
  `completed` int(11) DEFAULT NULL,
  `id` varchar(256) NOT NULL,
  `previous` varchar(256) DEFAULT NULL,
  `requested` int(11) DEFAULT NULL,
  `status` varchar(128) DEFAULT NULL,
  `creation` datetime DEFAULT NULL,
  `lifetime` bigint(20) DEFAULT NULL,
  `url` varchar(1024) DEFAULT NULL,
  `amtStatus` varchar(128) DEFAULT NULL,
  `screen` longtext,
  `autoApprove` bit(1) DEFAULT NULL,
  `processed` bit(1) DEFAULT NULL,
  `hitTypeId` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table TurkerLog
# ------------------------------------------------------------

DROP TABLE IF EXISTS `TurkerLog`;

CREATE TABLE `TurkerLog` (
  `date` datetime DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `batchId` bigint(20) DEFAULT NULL,
  `assignmentId` varchar(256) DEFAULT NULL,
  `data` longtext,
  `hit` varchar(256) DEFAULT NULL,
  `queryparams` varchar(1024) DEFAULT NULL,
  `type` varchar(128) DEFAULT NULL,
  `workerId` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `batchId` (`batchId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table Users
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Users`;

CREATE TABLE `Users` (
  `id` bigint(20) NOT NULL,
  `password` varchar(128) DEFAULT NULL,
  `username` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

# ************************************************************
# Sequel Pro SQL dump
# Version 4004
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: localhost (MySQL 5.1.47)
# Database: amtsolutions
# Generation Time: 2013-02-06 12:12:58 -0500
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



# Dump of table Question
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Question`;

CREATE TABLE `Question` (
  `batchid` bigint(20) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `text` varchar(2048) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `batchid` (`batchid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table Solution
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Solution`;

CREATE TABLE `Solution` (
  `assignmentId` varchar(256) DEFAULT NULL,
  `creation` datetime DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `questionId` bigint(20) DEFAULT NULL,
  `round` int(11) DEFAULT NULL,
  `text` longtext,
  `workerId` varchar(256) DEFAULT NULL,
  `valid` varchar(32) DEFAULT NULL,
  `meta` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `questionId` (`questionId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table SolutionMap
# ------------------------------------------------------------

DROP TABLE IF EXISTS `SolutionMap`;

CREATE TABLE `SolutionMap` (
  `from` bigint(20) NOT NULL,
  `to` bigint(20) NOT NULL,
  PRIMARY KEY (`from`,`to`),
  KEY `to` (`to`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table SolutionRank
# ------------------------------------------------------------

DROP TABLE IF EXISTS `SolutionRank`;

CREATE TABLE `SolutionRank` (
  `date` datetime DEFAULT NULL,
  `hitId` varchar(1024) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `rank` varchar(1024) DEFAULT NULL,
  `round` int(11) DEFAULT NULL,
  `solutionid` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `solutionid` (`solutionid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

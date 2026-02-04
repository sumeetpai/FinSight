-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: finsight_db
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `portfolio`
--

DROP TABLE IF EXISTS `portfolio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `portfolio` (
  `portfolio_id` int NOT NULL AUTO_INCREMENT,
  `shares` int DEFAULT NULL,
  `user_user_id` int DEFAULT NULL,
  `yield` int DEFAULT NULL,
  `cost_basis` bigint DEFAULT NULL,
  `current_price` bigint DEFAULT NULL,
  `stock_id` varchar(255) DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `total_value` bigint DEFAULT NULL,
  PRIMARY KEY (`portfolio_id`),
  KEY `FKmwtnb3njfb523dkx6yd2i8i2d` (`user_user_id`),
  KEY `FKclxr2fqkko1a1kjw4pvijvej3` (`user_id`),
  CONSTRAINT `FKclxr2fqkko1a1kjw4pvijvej3` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKmwtnb3njfb523dkx6yd2i8i2d` FOREIGN KEY (`user_user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `portfolio`
--

LOCK TABLES `portfolio` WRITE;
/*!40000 ALTER TABLE `portfolio` DISABLE KEYS */;
INSERT INTO `portfolio` VALUES (1,NULL,NULL,0,0,NULL,NULL,4,'Tech',112);
/*!40000 ALTER TABLE `portfolio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `portfolio_stock`
--

DROP TABLE IF EXISTS `portfolio_stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `portfolio_stock` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quantity` int DEFAULT NULL,
  `portfolio_id` int DEFAULT NULL,
  `stock_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7x1vm1n2o0sho0korvsx8jw1c` (`portfolio_id`),
  KEY `FKk8vxnr0qa04w89a65y8j42rw` (`stock_id`),
  CONSTRAINT `FK7x1vm1n2o0sho0korvsx8jw1c` FOREIGN KEY (`portfolio_id`) REFERENCES `portfolio` (`portfolio_id`),
  CONSTRAINT `FKk8vxnr0qa04w89a65y8j42rw` FOREIGN KEY (`stock_id`) REFERENCES `stocks` (`stock_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `portfolio_stock`
--

LOCK TABLES `portfolio_stock` WRITE;
/*!40000 ALTER TABLE `portfolio_stock` DISABLE KEYS */;
INSERT INTO `portfolio_stock` VALUES (1,5,1,1),(2,1,1,2);
/*!40000 ALTER TABLE `portfolio_stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `portfolio_stocks`
--

DROP TABLE IF EXISTS `portfolio_stocks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `portfolio_stocks` (
  `portfolio_id` int NOT NULL,
  `stock_id` int NOT NULL,
  KEY `FKfj6ref2viw08erqb73vwybbod` (`stock_id`),
  KEY `FKtk51v3hx87ksixbruw2rnhfw9` (`portfolio_id`),
  CONSTRAINT `FKfj6ref2viw08erqb73vwybbod` FOREIGN KEY (`stock_id`) REFERENCES `stocks` (`stock_id`),
  CONSTRAINT `FKtk51v3hx87ksixbruw2rnhfw9` FOREIGN KEY (`portfolio_id`) REFERENCES `portfolio` (`portfolio_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `portfolio_stocks`
--

LOCK TABLES `portfolio_stocks` WRITE;
/*!40000 ALTER TABLE `portfolio_stocks` DISABLE KEYS */;
INSERT INTO `portfolio_stocks` VALUES (1,1);
/*!40000 ALTER TABLE `portfolio_stocks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stocks`
--

DROP TABLE IF EXISTS `stocks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stocks` (
  `current_price` int DEFAULT NULL,
  `day_before_price` int DEFAULT NULL,
  `stock_id` int NOT NULL AUTO_INCREMENT,
  `market_cap` bigint DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `stock_sym` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`stock_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stocks`
--

LOCK TABLES `stocks` WRITE;
/*!40000 ALTER TABLE `stocks` DISABLE KEYS */;
INSERT INTO `stocks` VALUES (20,30,1,2000,'Apple','AAPL'),(12,15,2,1000,'Google','GOOGL');
/*!40000 ALTER TABLE `stocks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stocks_portfolio`
--

DROP TABLE IF EXISTS `stocks_portfolio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stocks_portfolio` (
  `portfolio_portfolio_id` int NOT NULL,
  `stocks_stock_id` int NOT NULL,
  KEY `FKgx8d55xo7a3y7c00yt1e6jysi` (`portfolio_portfolio_id`),
  KEY `FK99s24h2atedenpkn5t8d21efq` (`stocks_stock_id`),
  CONSTRAINT `FK99s24h2atedenpkn5t8d21efq` FOREIGN KEY (`stocks_stock_id`) REFERENCES `stocks` (`stock_id`),
  CONSTRAINT `FKgx8d55xo7a3y7c00yt1e6jysi` FOREIGN KEY (`portfolio_portfolio_id`) REFERENCES `portfolio` (`portfolio_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stocks_portfolio`
--

LOCK TABLES `stocks_portfolio` WRITE;
/*!40000 ALTER TABLE `stocks_portfolio` DISABLE KEYS */;
/*!40000 ALTER TABLE `stocks_portfolio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction` (
  `qty` int DEFAULT NULL,
  `t_id` int NOT NULL AUTO_INCREMENT,
  `price` bigint DEFAULT NULL,
  `timestamp_t` datetime(6) DEFAULT NULL,
  `stock_sym` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `portfolio_id` int DEFAULT NULL,
  `stock_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`t_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
INSERT INTO `transaction` VALUES (1,1,20,'2026-02-03 14:47:37.795000',NULL,'ADD',1,1,4),(1,2,20,'2026-02-03 15:24:58.327000',NULL,'ADD',1,1,4),(4,3,20,'2026-02-03 15:32:32.712000',NULL,'ADD',1,1,4),(2,4,12,'2026-02-03 15:35:21.157000',NULL,'ADD',1,2,4),(1,5,12,'2026-02-03 15:37:53.237000',NULL,'REMOVE',1,2,4);
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `transaction_t_id` int DEFAULT NULL,
  `user_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `FKg4q59stob5i4rtcka43nnwm13` (`transaction_t_id`),
  CONSTRAINT `FKg4q59stob5i4rtcka43nnwm13` FOREIGN KEY (`transaction_t_id`) REFERENCES `transaction` (`t_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (NULL,1,'2026-02-03 13:51:07.362000','sumeet@gmail.com','string','sumeet'),(NULL,2,'2026-02-03 13:54:40.483000','sumeet@gmail.com','string','sumeet'),(NULL,4,'2026-02-03 13:58:42.173000','sumeet@gmail.com','string','sumeet');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-04  4:48:58

-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 20, 2024 at 03:09 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `agenda_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `agenda`
--

CREATE TABLE `agenda` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `date` date NOT NULL,
  `time` time NOT NULL,
  `description` text DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `agenda`
--

INSERT INTO `agenda` (`id`, `title`, `date`, `time`, `description`, `image_path`) VALUES
(4, 'Percobaan crud', '2024-11-18', '19:38:00', 'mencoba crud dan gambar', 'C:\\Users\\ASUS\\Pictures\\Screenshots\\fithub.png'),
(11, 'contoh 2', '2024-11-18', '17:26:00', 'contoh ke 2\n', NULL),
(12, 'contoh 3', '2024-11-18', '17:26:00', 'contoh ke 3\n', NULL),
(13, 'contoh 4', '2024-11-18', '17:26:00', 'contoh ke 4\n', NULL),
(14, 'contoh 5', '2024-11-18', '17:26:00', 'contoh ke 5\n', NULL),
(15, 'CONTOH 6', '2024-11-18', '17:30:00', 'CONTOH KE 6 INI', NULL),
(16, 'contoh 7', '2024-11-18', '19:16:00', 'Contoh ke 7 dengqan menggunakan gamabr', 'C:\\Users\\ASUS\\Pictures\\Screenshots\\program barang.png');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `agenda`
--
ALTER TABLE `agenda`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `agenda`
--
ALTER TABLE `agenda`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

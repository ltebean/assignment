CREATE TABLE `order` (
  `order_id` int(11) NOT NULL,
  `items` int(11) NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
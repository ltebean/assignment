CREATE TABLE `order` (
  `order_id` int(11) NOT NULL,
  `items` int(11) NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `item`(
  `order_id` int(11) NOT NULL,
  `name`  varchar(50) not null,
  `number` int(11)  not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

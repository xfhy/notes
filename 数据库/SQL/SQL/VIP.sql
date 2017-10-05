
use VIP;
go
create table student1
(
   id int primary key identity(1000,1),
   name nvarchar(20) not null,   --姓名
   sex nvarchar(1) not null,     --性别    1位
   num nvarchar(11) not null,    --学号   10位
   grade int,                    --年级
   acad nvarchar(20),            --院系
   tel nvarchar(11) not null,    --电话  11位
   qq nvarchar(15),              --qq
)
select * from student1;
go
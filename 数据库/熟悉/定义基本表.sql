use STU;

create table Student          --学生关系
(
   sno char(10) primary key,  --学号,主键
   sname varchar(20) not null, --姓名
   sage smallint,              --年龄(比较小,直接用smallint)
   ssex char(2),               --性别
   sdept varchar(20)             --院系
)

create table Course            --课程关系
(
   cno char(10),               --课程号(主键)
   primary key(cno),           --
   cname varchar(20),          --课程名
   cpno char(10),              --先行课
   credit smallint             --学分(比较小,直接用smallint)
)

create table SC                --选修关系
( 
   sno char(10),               --学号
   cno char(10),               --课程号
   grade smallint,             --成绩
   primary key(sno,cno)        --学号和课程号(主键)
)

--删除关系,表中的数据也会被删除
drop table Student;   --一旦一个表被删除,数据将不能被恢复

--增加表中的属性
--allow null (新添加的属性要允许为空)
alter table Student add phone char(16);

--修改表中的某属性
alter table Student alter column sdept varchar(100) not null;

--删除表中的某属性
alter table Student drop column sage;

--1、创建Student，Course，SC三张表（暂时不建立外码和唯一码Unique）。
--2、为Student表添加籍贯（50个长度的变长字符串）列，查看表结构。
alter table Student add guanji varchar(50);

--3、将Student表中的“籍贯”列的类型精度改为100，查看表结构。
alter table Student alter column guanji varchar(100);
--4、删除Student表的“籍贯”列。
alter table Student drop column guanji;
--5、删除这三张表。
drop table Student;
drop table SC;
drop table Course;

--按关系模式的属性顺序
insert into Student values('01001','张三',27,'M','CS','10');
--按指定的属性顺序,也可以添加部分属性(非null属性为必需)
insert into Student(sno,sage,sname) values('01002',20,'李四');

--删除单个元组
delete from Student where sno='1';

--删除多个元组
delete from Student where ssex='F';

--删除整个关系中的所有数据
delete from Student;

--删除符合条件的某个(某些)元组的属性值
--例:将001学生转入MA系
update Student set sdept='MA',sage=sage+1 where sno='001';
--所有的学生年龄加1
update Student set sage=sage+1;

     /*----------------练习-------------------*/
--1、为Student（10行以上），Course（8行以上），SC（25行以上）表添加记录。
insert into Student values('01001','qq',27,'M','CS');
insert into Student values('01002','ww',20,'M','IS');
insert into Student values('01003','ee',22,'F','CS');
insert into Student values('01004','rr',18,'M','MA');
insert into Student values('01005','tt',26,'M','CS');
insert into Student values('01006','yy',20,'F','IS');
insert into Student values('01007','uu',25,'M','CS');
insert into Student values('01008','ii',21,'F','CS');
insert into Student values('01009','oo',22,'F','MA');
insert into Student values('010010','pp',21,'M','CS');

insert into Course values('1','数据结构','5',5);
insert into Course values('2','数学','4',1);
insert into Course values('3','英语','2',5);
insert into Course values('4','语文','1',4);
insert into Course values('5','C语言','4',2);
insert into Course values('6','C++','4',1);
insert into Course values('7','Java','1',2);
insert into Course values('8','Android','7',4);
insert into Course values('9','C#','8',5);

insert into SC values('01001','1',70);
insert into SC values('01001','2',80);
insert into SC values('01001','3',40);
insert into SC values('01001','4',45);
insert into SC values('01001','5',61);
insert into SC values('01002','7',61);
insert into SC values('01002','5',61);
insert into SC values('01002','8',61);
insert into SC values('01002','1',61);
insert into SC values('01003','5',61);
insert into SC values('01003','8',61);
--2、为Student表添加列“班级号” （10个长度定长字符串）。
alter table Student add classnum char(10);
alter table Student alter column classnum varchar(100);
--3、为学生填写班级号（数字）。
update Student set classnum='10'; 
--4、将每个同学的班级号前面/后面加上“T”。
--update 表名 set 字段名=字段名+'要添加字符串'
update Student set classnum='T'+classnum;
--5、删除班级号前面/后面的“T”。
--update 表 set 字段=substring(字段,2,len(字段)-1) 就可以了
--substring 是截取字符串 2 是从第二个截取  len(字段)-1 是截取多少个
update Student set classnum=SUBSTRING(classnum,2,len(classnum)-1);
--6、删除班级号为空的学生。
delete from Student where classnum is null;
--7、删除成绩低于50分的学生的选课信息。
delete from SC where grade<50;

       /*-----------数据查询------------*/
select sname from Student;
--select子句的缺省情况是保留重复元素(all),可用distinct取出重复元组
--去除重复元组时:费时
select all sdept from Student;
select distinct sdept from Student;
select distinct sdept,ssex from Student;
--星号*:按关系模式中属性的顺序排列
select * from Student;

--select子句--更名
--为结果集中的某个属性改名,使其更具可读性

select sno as '学号',cno as 课程号,grade as 成绩 from SC; --这句显示出来的效果是原本学号那列显示的是sno,现在显示学号
--把学号对应的出生年显示出来,并且那列列名是birthyear
select sno,YEAR(GETDATE())-sage as birthyear from Student; 

--where子句
--比较：<、<=、>、>=、=、<> 等
--确定范围：
--	Between  A  and  B、Not Between A and B
--确定集合：IN、NOT IN
--字符匹配：LIKE，NOT LIKE
--空值：IS NULL、IS NOT NULL
--多重条件：AND、OR、NOT

--where子句--like
--字符匹配:like,not like 
--1.通配符
   --%   匹配任意字符串
   --_   匹配任意一个字符
--2.大小写敏感
--例:列出姓张的学生的学号,姓名
select sno as '学号',sname as '姓名' from Student where sname like '张%'
--例：列出张姓且单名(2个字)的学生的学号、姓名。
select sno,sname from Student where sname like '张_'

--where子句----转义符escape
--例:列出课程名称中带有'_'的课程号及课程名
select cno,cname from Course where cname like '%\_%' escape '\';

--    from子句
--列出将要被查询的关系(表)
--例：列出所有学生的学号、姓名、课号、成绩。
select Student.sno,sname,cno,grade from Student,SC where Student.sno=SC.sno;--这里需要连接2张表

--    order by子句
--指定结果集中组的排列次序
--耗时
--ASC升序(缺省),DESC(降序)
--例：列出CS系中的男生的学号、姓名、性别和年龄，并按年龄进行排列（降序）
select sno,sname,ssex,sage from Student where sdept='CS' and ssex='M' order by sage desc;

--检索选修C01或C02的学生学号
select sno from SC where cno='C01' or cno='C02';

--检索成绩在70分至80分之间的学生学号,课程号和成绩
select sno,cno,grade from SC where grade<=80 and grade>=70;  --OK
select * from SC where grade between 70 and 80;              --OK

--检索学号为001,003,004的同学的姓名,年龄(年龄按升序排列)
select sname,sage from Student where sno='001' or sno='003' or sno='004' order by sage; --OK
select sno,sage from Student where sno in('001','003','004') order by sage;             --very good

--检索张三同学所学课程的课程号及成绩
select cno,grade from SC,Student where 
SC.sno = Student.sno and sname='张三';

--检索所有学生的姓名、选课名称和成绩
select sname,cname,grade from Student,SC,Course where 
Student.sno=SC.sno and SC.cno=Course.cno;

--查询选修‘c05’课程，并且年龄不大于26岁的学生的学号和成绩，并按成绩降序排列。 
select Student.sno,grade from Student,SC where 
Student.sno=SC.sno and cno='c05' and sage<=26 order by grade DESC;


         /*------------练习--select-------------*/
--1、查询MA系的女同学。
select * from Student where sdept='MA' and ssex='F';
--2、查询CS系姓李的学生选修的课程，列出学号，课程号和成绩。
select Student.sno as '学号',cno as '课程号',grade as '成绩',sname from Student,SC where Student.sno=SC.sno and sdept='CS' and sname like '李%';
--3、查询选修了数据库课程的学生的学号，成绩，按成绩降序排列。
select Student.sno,grade from Student,SC,Course where Student.sno=SC.sno and SC.cno=Course.cno and cname='数据库' order by grade DESC;
--4、找出学分为4分以上的课程的选修情况，列出学号，课程名，成绩。
select Student.sno as '学号',sname as '课程名',grade as '成绩' from Student,SC,Course
where Student.sno=SC.sno and SC.cno=Course.cno and credit>4;
--5、检索数据库的成绩在90分以上的学生的学号和姓名。
select Student.sno,sname from Student,SC,Course where Student.sno=SC.sno and SC.cno=Course.cno and grade>90 and cname='数据库';

--         子查询(Subquery)
--子查询是嵌套在另一查询中的select-from-where 表达式(where/having)
--SQL允许多层嵌套,由内而外德进行分析,子查询的结果作为父查询的查找条件
--可以用多个简单查询来构成复杂查询,以增强SQL的查询能力
--子查询中不适用order by子句,order by子句只能对最终查询结果进行排序

--返回单值的子查询，只返回一行一列
--父查询与单值子查询之间用比较运算符进行连接

--找出与001同龄的学生
select * from Student where sage=
(select sage from Student where sno='001');

--子查询返回多行一列
--运算符：In、All、Some(或Any)、Exists

--若值与子查询返回集中的某一个相等，则返回true
-- IN 被用来测试多值中的成员
--例：查询选修’C01’课程的学生的学号、姓名

select * from Student where sno in(select sno from SC where cno='C01');

--子查询——多值成员In
--例： 查询选修了 ‘数据库’的学生的学号和姓名
select sno,sname from Student where sno in
(select sno from SC where cno in
(select cno from Course where cname='数据库'));

--检索选修课程C02的学生中成绩最高的学生的学号
select sno from SC where cno='C02' and grade>=all
(select grade from SC where cno='C02');

--子查询——多值比较Some/Any
--多值比较：多行一列
--父查询与多值子查询之间的比较需用Some/Any来连接
--值s比子查询返回集R中的某一个都大时返回 Ture
--s > Some R为True  或 
--s > Any R为True 
--Some(早期用Any)表示某一个（任意一个）
-- > some、< some、<=some、>=some、<> some
--= some 等价于 in、<> some 不等价于 not in 

--查询比任意一个女同学年龄大的男同学
select sname from Student where sage>some(select sage from Student where ssex='f')
and ssex='m';



































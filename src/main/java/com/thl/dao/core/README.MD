注意：
1.	在实体类所在的包中创建	类名  + .thl.properties 的配置文件 ，参考 Hibernate 的配置方法
	内容为  类中字段名  =  字段对应的数据库的类型
	如果数据库类型是  datetime ，则使用本工具类中的  com.thl.utlis.entity.DateTime 类

2.	本工具类中提供填充简单的插入与更新语句的方法，使用方法如下：
	(1).实例化本工具包中的  com.thl.utils.DaoUtil 类；
	(2).创建 com.thl.utils.entity.SQLEntity 类，
			插入语句：在构造方法中传入数据库表名与插入字段、
			更新语句：在构造方法中传入数据库表名、更新字段与更新条件；
		其中插入字段、更新字段与更新条件都为实体类，不需要的字段设置为空，
		为了避免 基本数据类型自动初始化造成的影响，
		尽量将实体类中的字段都设置为基本数据类型对应的普通类，或者在基本数据类型中传入原本的值
	(3).调用 com.thl.utils.DaoUtil.fullSQLEntity(
				com.thl.utils.entity.SQLEntity entity, 
				java.lang.Class clazz) 
		方法再次取得 com.thl.utils.entity.SQLEntity 实体类，然后取出其中的 SQL 语句以及参数列表 args

3.	本工具类中已经提供数据连接池来维护连接，通过配置文件  THLDaoUtil.properties 类进行配置，
	如果没有该配置文件，则使用默认的配置文件进行连接池的创建

4.	本工具类中
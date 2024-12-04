use [master];


GO 

---------------------------------------------------------------------------------------------------
use NBS_MSGoute;

if  exists (select * from sys.database_principals where name = N'nbs_ods')
  drop user [nbs_ods];
go

create user nbs_ods for login nbs_ods;  

exec sp_addrolemember 'db_owner', 'nbs_ods';

go  

---------------------------------------------------------------------------------------------------
use NBS_ODSE;

if  exists (select * from sys.database_principals where name = N'nbs_ods')
  drop user [nbs_ods];
go

create user nbs_ods for login nbs_ods;  

exec sp_addrolemember 'db_owner', 'nbs_ods';

go  

---------------------------------------------------------------------------------------------------
use [NBS_SRTE];

if  exists (select * from sys.database_principals where name = N'nbs_ods')
  drop user [nbs_ods];
go

create user nbs_ods for login nbs_ods;  

exec sp_addrolemember 'db_owner', 'nbs_ods';

go  

if  exists (select * from sys.database_principals where name = N'srte_admin')
  drop user [srte_admin];
go

create user srte_admin for login srte_admin;  

exec sp_addrolemember 'db_owner', 'srte_admin';

go  

---------------------------------------------------------------------------------------------------
use [rdb];

if  exists (select * from sys.database_principals where name = N'nbs_rdb')
  drop user [nbs_rdb];
go

create user nbs_rdb for login nbs_rdb;  

exec sp_addrolemember 'db_owner', 'nbs_rdb';

go  

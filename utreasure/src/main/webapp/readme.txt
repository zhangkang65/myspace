地域划分(南京)
问题一：网页怎么获取用户的位置
问题二：手机用户直接获取用户位置


商品发布时间：
浏览次数：
交易状态：
近期价格走势：（图表）
该用户记录（求购，发布）：
详情描素：

用户表:-------------------------------------------------------------------------
		create table UTR_USER
		(
		  id             VARCHAR2(20) not null,
		  username       VARCHAR2(20),
		  loginname      VARCHAR2(20) not null,
		  sex            VARCHAR2(1),
		  age            NUMBER,
		  password       VARCHAR2(30) not null,
		  createtime     DATE,
		  updatetime     DATE,
		  icon           VARCHAR2(30),
		  address        VARCHAR2(30),
		  identitycardid VARCHAR2(20),
		  telephone      VARCHAR2(20),
		  email          VARCHAR2(30),
		  usertype       VARCHAR2(10),
		  shbat1         VARCHAR2(20)
		)
		tablespace TS_TAB_WG_TOPTEA_C1
		  pctfree 10
		  initrans 1
		  maxtrans 255
		  storage
		  (
		    initial 64
		    minextents 1
		    maxextents unlimited
		  );
		-- Create/Recreate primary, unique and foreign key constraints 
		alter table UTR_USER
		  add constraint ID primary key (ID)
		  using index 
		  tablespace TS_TAB_WG_TOPTEA_C1
		  pctfree 10
		  initrans 2
		  maxtrans 255
		  storage
		  (
		    initial 64K
		    minextents 1
		    maxextents unlimited
		  );
	
-----------------------------------------------------------------------------------------	
	
	
	
	
	
	
	置换模块
	赠送模块
	求赠模块
	求购模块
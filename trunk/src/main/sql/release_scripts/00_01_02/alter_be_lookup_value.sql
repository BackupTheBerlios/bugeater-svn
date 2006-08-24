alter table be_lookup_value drop constraint be_lookup_value_pkey;

create table be_lookup_value_new
(
	value_id int8 not null,
	type int4 not null,
	value varchar(255) not null,
	constraint be_lookup_value_pkey primary key (value_id)
);

insert into
	be_lookup_value_new
(
	value_id, type, value
) select
	issue_id, type, value
from
	be_lookup_value
;

drop table be_lookup_value;

alter table be_lookup_value_new rename to be_lookupvalue;

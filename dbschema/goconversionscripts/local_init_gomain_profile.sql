drop table gomain_profile;
create table gomain_profile
as select go_number,
			end_date as workstations_end_date,
			num_workstations,
			end_date as buildings_end_date,
			num_buildings,
			end_date as population_end_date,
			population,
			end_date as enrollment_end_date,
			enrollment
from goservice where 0 > 0;
insert into gomain_profile (
	go_number,
	workstations_end_date,
	num_workstations,
	buildings_end_date,
	num_buildings,
	enrollment_end_date,
	enrollment,
	population_end_date,
	population
) select go_number,max(end_date),0,max(end_date),0,max(end_date),0,max(end_date),0
from goservice group by go_number;
update gomain_profile
	set workstations_end_date = (
		select max(end_date) as use_date from goservice
			where gomain_profile.GO_NUMBER = goservice.go_number
			and num_workstations > 0
			and end_date is not null
		)
	;
update gomain_profile
	set num_workstations = (
		select max(num_workstations) as num_workstations from goservice
			where gomain_profile.GO_NUMBER = goservice.go_number
			and num_workstations > 0
			and end_date is not null
			and end_date = gomain_profile.workstations_end_date
		)
	;
update gomain_profile
	set buildings_end_date = (
		select max(end_date) as use_date from goservice
			where gomain_profile.GO_NUMBER = goservice.go_number
			and num_buildings > 0
			and end_date is not null
		)
	;
update gomain_profile
	set num_buildings = (
		select max(num_buildings) as num_buildings from goservice
			where gomain_profile.GO_NUMBER = goservice.go_number
			and num_buildings > 0
			and end_date is not null
			and end_date = gomain_profile.buildings_end_date
		)
	;
update gomain_profile
	set enrollment_end_date = (
		select max(end_date) as use_date from goservice
			where gomain_profile.GO_NUMBER = goservice.go_number
			and enrollment > 0
			and end_date is not null
		)
	;
update gomain_profile
	set enrollment = (
		select max(enrollment) as enrollment from goservice
			where gomain_profile.GO_NUMBER = goservice.go_number
			and enrollment > 0
			and end_date is not null
			and end_date = gomain_profile.enrollment_end_date
		)
	;
update gomain_profile
	set population_end_date = (
		select max(end_date) as use_date from goservice
			where gomain_profile.GO_NUMBER = goservice.go_number
			and population > 0
			and end_date is not null
		)
	;
update gomain_profile
	set population = (
		select max(population) as population from goservice
			where gomain_profile.GO_NUMBER = goservice.go_number
			and population > 0
			and end_date is not null
			and end_date = gomain_profile.population_end_date
		)
	;

	
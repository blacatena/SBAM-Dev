drop table gonote_blob;
create table gonote_blob as
select go_number,go_note_id,group_concat(note_line SEPARATOR ‘\n’ ) as note from 
(select go_number,go_note_id,note_line,seq_no from gonote order by go_number,go_note_id,seq_no) notelines
group by go_number,go_note_id 
order by go_number,go_note_id;
insert into gonote_blob
(go_number,
go_note_id,
note)
select go_number,go_note_id,group_concat(note_line SEPARATOR '\n' ) as note
from 
(select go_number,go_note_id,note_line,seq_no from gonote order by go_number,go_note_id,seq_no) notelines
group by go_number,go_note_id
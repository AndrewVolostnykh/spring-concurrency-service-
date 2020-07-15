create or replace function hemmingMatchPercent(IN hash1 bigint, IN hash2 bigint)
returns real as $$
    declare
        local_hash1 bigint := hash1;
        local_hash2 bigint := hash2;
        hashesXor bigint := local_hash1 # local_hash2;
        numberOfOne int := 0;
        resultVar real := 0.0;
    begin
        if hashesXor < 0 then
            hashesXor := hashesXor * (-1);
        end if;

        while(hashesXor > 0)
            loop
                numberOfOne := numberOfOne + (hashesXor & 1);
                hashesXor := hashesXor >> 1;
            end loop;

        resultVar := 1 - cast(numberOfOne as double precision)/64;
        return resultVar;
    end; $$
language plpgsql;
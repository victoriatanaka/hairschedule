package com.example.cronogramacapilar;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface TreatmentDao {
    @Query("INSERT INTO treatments (type, last_date, next_date, repeats, repeats_unit, observations) VALUES (:type, :lastDate, :nextDate, :repeats, :repeatsUnit, :observations)")
    void create(String type, Date lastDate, Date nextDate, int repeats, String repeatsUnit, String observations);

    @Query("SELECT * FROM treatments ORDER BY next_date, type")
    List<Treatment> getAll();

    @Query("UPDATE treatments SET last_date = :lastDate, next_date = :nextDate, repeats = :repeats, repeats_unit = :repeatsUnit, observations = :observations WHERE id = :id")
    void save(Date lastDate, Date nextDate, int repeats, String repeatsUnit, String observations, long id);

    @Query("DELETE FROM treatments WHERE id = :id")
    void delete(long id);

    @Query("SELECT * FROM treatments WHERE id = :id")
    Treatment get(long id);

    @Query("DELETE FROM treatments")
    void deleteAll();
}

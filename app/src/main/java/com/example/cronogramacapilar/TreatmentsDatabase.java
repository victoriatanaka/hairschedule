package com.example.cronogramacapilar;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Treatment.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class TreatmentsDatabase extends RoomDatabase {
    public abstract TreatmentDao treatmentDao();
}

